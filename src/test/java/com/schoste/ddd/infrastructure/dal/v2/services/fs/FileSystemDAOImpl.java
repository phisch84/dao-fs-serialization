package com.schoste.ddd.infrastructure.dal.v2.services.fs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.schoste.ddd.infrastructure.dal.v2.models.FileSystemDO;
import com.schoste.ddd.infrastructure.dal.v2.services.fs.GenericFileSystemDAO;

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
}
