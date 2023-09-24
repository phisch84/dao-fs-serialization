package com.schoste.ddd.infrastructure.dal.v2.services.serialization;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

/**
 * Version 1 implementation of the GenericDataAccessObject interface to persist data objects
 * via serialization.
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 *
 * @param <T> the class of the data object to persist
 */
public abstract class GenericSerializationDAO<T extends GenericDataObject> extends GenericDAO<T> implements GenericDataAccessObject<T> 
{
	/**
	 * The directory where the files are serialized to
	 */
	protected final Path storagePath;
	
	/**
	 * The last time stamp used 
	 */
	private long lastTimeStamp;
	
	private synchronized void tryAddToCollectionIfNewer(Collection<T> newerFiles, Path fileToAdd)
	{
		try
		{
			String fileName = fileToAdd.getFileName().toString();
			int id = Integer.valueOf(fileName);
			BasicFileAttributes attr = Files.readAttributes(fileToAdd, BasicFileAttributes.class);
			long lastModified = attr.lastModifiedTime().to(TimeUnit.MILLISECONDS);
			
			// If the latest mod TS is greater than the file's last mod TS plus one second, it is definitely not newer
			if (this.latestModificationTimeStamp > (lastModified +1000)) return;
			
			T dataObject = this.get(id);

			// File's mod TS was greater or equal, do a precise check on the object's mod TS
			if (this.latestModificationTimeStamp >= dataObject.getModifiedTimeStamp()) return;
			
			newerFiles.add(dataObject);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
	}
	
	/**
	 * This method returns a time stamp that is guaranteed to be greater than the one returned
	 * by the previous call.
	 * 
	 * @return a time stamp
	 */
	private synchronized long getTimeStamp()
	{
		long currentTS = System.currentTimeMillis();
		
		if (currentTS > this.lastTimeStamp) this.lastTimeStamp = currentTS;
		else this.lastTimeStamp++;
		
		return this.lastTimeStamp;
	}
	
	protected int getNewFileId()
	{
		int now = Math.toIntExact(System.currentTimeMillis() / 1000);
		Path fileToCheck = Paths.get(this.storagePath.toString(), String.valueOf(now));
		
		while (Files.exists(fileToCheck)) fileToCheck = Paths.get(this.storagePath.toString(), String.valueOf(++now));
		
		return now;
	}
	
	/**
	 * Initializes the instance of a serialization DAO with the path
	 * to the folder where all data objects are stored. If the path
	 * does not exist the method will try to create it. If it fails an
	 * exception is thrown.
	 * 
	 * @param storagePath the path to the folder where all data objects are stored
	 * @throws IllegalArgumentException if the provided storage path is null or it exists but is no directory.
	 * @throws IllegalStateException if files cannot be either created or deleted
	 * @throws DALException re-throws every exception as {@see DALException} (e.g. when directory creation fails)
	 */
	public GenericSerializationDAO(String storagePath) throws IllegalArgumentException, IllegalStateException, DALException
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
	protected synchronized void doDelete(T dataObject) throws Exception 
	{
		if (dataObject == null) throw new IllegalArgumentException("dataObject");
		
		Path pathToFile = Paths.get(this.storagePath.toString(), String.valueOf(dataObject.getId()));
		
		if (Files.exists(pathToFile, LinkOption.NOFOLLOW_LINKS)) Files.delete(pathToFile);
		
		dataObject.setIsDeleted(!Files.exists(pathToFile, LinkOption.NOFOLLOW_LINKS));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doDelete(Collection<T> dataObjects) throws Exception
	{
		for (T dataObject : dataObjects) this.doDelete(dataObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doDelete(int[] dataObjectIds) throws Exception
	{
		for (int dataObjectId : dataObjectIds)
		{
			Path pathToFile = Paths.get(this.storagePath.toString(), String.valueOf(dataObjectId));
			
			if (Files.exists(pathToFile, LinkOption.NOFOLLOW_LINKS)) Files.delete(pathToFile);			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected synchronized T doGet(int id) throws Exception 
	{
		Path pathToFile = Paths.get(this.storagePath.toString(), String.valueOf(id));
		
		if (!Files.exists(pathToFile)) return null;
		if (!Files.isRegularFile(pathToFile)) throw new IllegalStateException();
		if (!Files.isReadable(pathToFile)) throw new IllegalStateException();
		
		try(FileInputStream fis = new FileInputStream(pathToFile.toFile());
			ObjectInputStream ois = new ObjectInputStream(fis))
		{
			T dataObject = (T) ois.readObject();
			
			return dataObject;
		}
		catch (Exception ex)
		{
			throw ex;
		}
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
	protected synchronized Collection<T> doReloadAll() throws Exception
	{
		this.latestModificationTimeStamp = 0;
		
		return this.getAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void doSave(T dataObject) throws Exception 
	{
		if (dataObject == null) throw new IllegalArgumentException("dataObject");
		if (dataObject.getId() < 1)
		{
			dataObject.setId(this.getNewFileId());
			dataObject.setCreatedTimeStamp(this.getTimeStamp());
		}
		
		int fileId = dataObject.getId();		
		Path pathToFile = Paths.get(this.storagePath.toString(), String.valueOf(fileId));
		
		try (FileOutputStream fos = new FileOutputStream(pathToFile.toFile());
			 ObjectOutputStream oos = new ObjectOutputStream(fos))
		{
			dataObject.setModifiedTimeStamp(this.getTimeStamp());

			oos.writeObject(dataObject);
		}
		catch (Exception e)
		{
			throw e;
		}
		
		pathToFile.toFile().setLastModified(dataObject.getModifiedTimeStamp());
		
		dataObject.setId(fileId);		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSave(Collection<T> dataObjects) throws Exception
	{
		for (T dataObject : dataObjects) this.doSave(dataObject);
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
