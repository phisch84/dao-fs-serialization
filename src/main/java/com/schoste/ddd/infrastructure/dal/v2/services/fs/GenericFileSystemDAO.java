package com.schoste.ddd.infrastructure.dal.v2.services.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.schoste.ddd.infrastructure.dal.v2.services.GenericDAO;
import com.schoste.ddd.infrastructure.dal.v2.services.GenericDataAccessObject;
import com.schoste.ddd.infrastructure.dal.v2.exceptions.DALException;
import com.schoste.ddd.infrastructure.dal.v2.models.GenericDataObject;
import com.schoste.ddd.infrastructure.dal.v2.models.GenericFileObject;

/**
 * Version 2 implementation of the GenericDataAccessObject interface to persist data objects
 * to the file system.
 * 
 * When files are saved, their content is completely overwritten and never appended.
 * 
 * Note that the maximum storage capacity is Integer.MAX_VALUE bytes.
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 *
 * @param <T> the class of the data object to persist
 */
public abstract class GenericFileSystemDAO<T extends GenericFileObject> extends GenericDAO<T> implements GenericDataAccessObject<T> 
{
	/**
	 * The directory where the files of the data objects are located
	 */
	protected final Path storagePath;
	
	protected synchronized void tryAddToCollectionIfNewer(Collection<T> newerFiles, Path fileToAdd)
	{
		try
		{
			String fileName = fileToAdd.getFileName().toString();
			int id = Integer.valueOf(fileName);
			BasicFileAttributes attr = Files.readAttributes(fileToAdd, BasicFileAttributes.class);
			long lastModified = attr.lastModifiedTime().to(TimeUnit.MILLISECONDS);
			
			if (this.latestModificationTimeStamp >= lastModified) return;
			
			T dataObject = this.get(id);
			
			newerFiles.add(dataObject);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
	}
	
	protected synchronized void updateTimeStamp(T fileObject, Path pathToFile)
	{
		try
		{
			BasicFileAttributes attr = Files.readAttributes(pathToFile, BasicFileAttributes.class);
	
			fileObject.setCreatedTimeStamp(attr.creationTime().to(TimeUnit.MILLISECONDS));
			fileObject.setModifiedTimeStamp(attr.lastModifiedTime().to(TimeUnit.MILLISECONDS));
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
	}
	
	protected synchronized int getNewFileId()
	{
		int now = Math.toIntExact(System.currentTimeMillis() / 1000);
		Path fileToCheck = Paths.get(this.storagePath.toString(), String.valueOf(now));
		
		while (Files.exists(fileToCheck)) fileToCheck = Paths.get(this.storagePath.toString(), String.valueOf(++now));
		
		return now;
	}
	
	/**
	 * Initializes the instance of a file system DAO with the path
	 * to the folder where all data objects are stored. If the path
	 * does not exist the method will try to create it. If it fails an
	 * exception is thrown.
	 * 
	 * @param storagePath the path to the folder where all data objects are stored
	 * @throws IllegalArgumentException if the provided storage path is null or it exists but is no directory.
	 * @throws IllegalStateException if files cannot be either created or deleted
	 * @throws DALException re-throws every exception as {@see DALException} (e.g. when directory creation fails)
	 */
	public GenericFileSystemDAO(String storagePath) throws IllegalArgumentException, IllegalStateException, DALException
	{
		try
		{
			if (storagePath == null) throw new IllegalArgumentException("storagePath");

			this.storagePath = Paths.get(storagePath);

			if (!Files.exists(this.storagePath)) Files.createDirectories(this.storagePath);
			if (!Files.isDirectory(this.storagePath)) throw new IllegalArgumentException("storagePath");

			Path testFile = Paths.get(this.storagePath.toString(), this.getClass().getSimpleName());

			Files.createFile(testFile);

			if (!Files.exists(testFile)) throw new IllegalStateException();

			Files.delete(testFile);

			if (Files.exists(testFile)) throw new IllegalStateException();
		}
		catch (IllegalArgumentException e)
		{
			throw e;
		}
		catch (IllegalStateException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DALException(e);
		}
	}
	
	/**
	 * Gets the path where data objects are stored
	 * 
	 * @return the path where data objects are stored
	 */
	public Path getStoragePath()
	{
		return this.storagePath;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized T doGet(int id) throws IllegalStateException, Exception
	{
		Path pathToFile = Paths.get(this.storagePath.toString(), String.valueOf(id));
		
		if (!Files.exists(pathToFile)) return null;
		if (!Files.isRegularFile(pathToFile)) throw new IllegalStateException();
		if (!Files.isReadable(pathToFile)) throw new IllegalStateException();
		
		File file = pathToFile.toFile();
		
		if (file.length() > Integer.MAX_VALUE) throw new IllegalStateException();
		
		int fileLength = Math.toIntExact(file.length());
		T fileObject = this.createDataObject();
		  fileObject.setId(id);
		  
		try (FileInputStream fis = new FileInputStream(file))
		{
			byte[] data = new byte[fileLength];
			
			fis.read(data, 0, fileLength);
			fileObject.setData(data);
		}
		catch (Exception e)
		{
			throw e;
		}
		
		this.updateTimeStamp(fileObject, pathToFile);
		
		return fileObject;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized Collection<T> doReloadAll() throws Exception
	{
		this.latestModificationTimeStamp = 0;
		
		return this.getAll();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Collection<T> doGet(int[] ids) throws Exception 
	{
		Collection<T> newDataObjects = new ArrayList<T>();

		if (ids != null)
		{
			for (int id : ids) newDataObjects.add(this.doGet(id));

			return newDataObjects;
		}

		try (Stream<Path> files = Files.walk(this.storagePath)) 
		{
		    files
		        .filter(Files::isRegularFile)
		        .forEach(file -> this.tryAddToCollectionIfNewer(newDataObjects, file));
		}

		T latestModifiedObject = newDataObjects.stream().max(Comparator.comparing(GenericDataObject::getModifiedTimeStamp)).orElseGet(() -> null);
		
		if (latestModifiedObject != null) this.latestModificationTimeStamp = latestModifiedObject.getModifiedTimeStamp();
		
		return newDataObjects;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void doSave(T fileObject) throws Exception
	{
		if (fileObject == null) throw new IllegalArgumentException("fileObject");

		int fileId = (fileObject.getId() > 0) ? fileObject.getId() : this.getNewFileId();
		
		Path pathToFile = Paths.get(this.storagePath.toString(), String.valueOf(fileId));
		
		try (FileOutputStream fos = new FileOutputStream(pathToFile.toFile()))
		{
			fos.write(fileObject.getData());
		}
		catch (Exception e)
		{
			throw e;
		}
		
		fileObject.setId(fileId);
		
		this.updateTimeStamp(fileObject, pathToFile);
	}

	@Override
	protected void doSave(Collection<T> fileObjects) throws Exception
	{
		for (T fileObject : fileObjects) this.doSave(fileObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void doDelete(T dataObject) throws Exception 
	{
		if (dataObject == null) throw new IllegalArgumentException("dataObject");
		
		Path pathToFile = Paths.get(this.storagePath.toString(), String.valueOf(dataObject.getId()));
		
		if (Files.exists(pathToFile, LinkOption.NOFOLLOW_LINKS)) Files.delete(pathToFile);
		
		dataObject.setIsDeleted(!Files.exists(pathToFile, LinkOption.NOFOLLOW_LINKS));
	}

	@Override
	protected void doDelete(Collection<T> fileObjects) throws Exception
	{
		for (T fileObject : fileObjects) this.doDelete(fileObject);
	}

	@Override
	protected void doDelete(int[] fileObjectIds) throws Exception
	{
		for (int fileObjectId : fileObjectIds)
		{
			Path pathToFile = Paths.get(this.storagePath.toString(), String.valueOf(fileObjectId));
			
			if (Files.exists(pathToFile, LinkOption.NOFOLLOW_LINKS)) Files.delete(pathToFile);			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doClear() throws Exception 
	{
		try (Stream<Path> files = Files.walk(this.storagePath)) 
		{
		    files
		        .filter(Files::isRegularFile)
		        .forEach(file -> safeDelete(file));
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}		
	}

	private static void safeDelete(Path file)
	{
		try
		{
			Files.delete(file);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
	}
}
