package com.schoste.ddd.infrastructure.dal.v2.services.fs;

import com.schoste.ddd.infrastructure.dal.v2.models.FileSystemDO;
import com.schoste.ddd.infrastructure.dal.v2.models.GenericDataObject;
import com.schoste.ddd.infrastructure.dal.v2.services.LazyLoader;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Example file system data object used in unit testing of the GenericFileSystemDAO implementation
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 *
 */
public class FileSystemDAOImpl extends GenericFileSystemDAO<FileSystemDO>
{
	@Autowired
	protected ApplicationContext applicationContext;

	protected FileSystemDO getSafe(int id)
	{
		try
		{
			return super.get(id);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public FileSystemDAOImpl(String storagePath) throws IllegalArgumentException, IllegalStateException, Exception 
	{
		super(storagePath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileSystemDO createDataObject() 
	{
		return (FileSystemDO) this.applicationContext.getBean(FileSystemDO.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LazyLoader<Integer, FileSystemDO> createLazyLoader() throws Exception 
	{
		Function<Integer, GenericDataObject> cv = id -> this.safeDoGet(id);
		LazyLoader<Integer, FileSystemDO> ll = this.applicationContext.getBean(LazyLoader.class, cv, this.storagePath);

		return ll;
	}

	/**
	 * Calls the {@link GenericFileSystemDAO#doGet(int)} method inside a try-catch block
	 * 
	 * @param id the id of the data object to load
	 * @return the data object with the given id, or null if none was found or on error 
	 */
	protected FileSystemDO safeDoGet(Integer id)
	{
		try
		{
			return this.doGet(id);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);

			return null;
		}
	}
}
