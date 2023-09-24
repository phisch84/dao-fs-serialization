package com.schoste.ddd.infrastructure.dal.v2.services.fs;

import java.io.File;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.schoste.ddd.infrastructure.dal.v2.models.FileSystemDO;

/**
 * Test class of the FileSystemDAOImpl implementation
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 */
@ContextConfiguration(locations = { "file:src/test/resources/unittest-beans.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class FileSystemDAOImplTest extends GenericFileSystemDAOTest<FileSystemDO, FileSystemDAOImpl>
{
	@Autowired
	protected FileSystemDAOImpl fileSystemDAOImpl;

	/**
	 * Executed before every test is executed.
	 * Clears the DAO and stores new data objects into it.
	 * 
	 * @throws Exception re-throws every exception
	 */
	@Before
	public void initializeTest() throws Exception
	{
		this.clearAndSetUpExistingDataObjects();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FileSystemDAOImpl getDataAccessObject() 
	{
		return this.fileSystemDAOImpl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FileSystemDO createDataObject(Object... parameters) 
	{
		FileSystemDO dataObject = this.fileSystemDAOImpl.createDataObject();
		
		if ((parameters == null) || (parameters.length < 1)) return dataObject;	
		if (parameters.length > 0) dataObject.setId((Integer) parameters[0]);
		
		return dataObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultResourceName() 
	{
		return "fs-dao-testfile.zip";
	}
	
	/**
	 * Asserts that the DAO can be created with nested paths
	 * 
	 * @throws Exception re-throws every exception
	 */
	@Test
	public void testConstructorWithNestedPaths() throws Exception
	{
		String daoPath = String.format("%s%s%s%s%s", System.getenv("java.io.tmpdir"), File.separator, System.nanoTime(), File.separator, "TEST");
		FileSystemDAOImpl dao = new FileSystemDAOImpl(daoPath);
		
		Assert.notNull(dao, "");
	}

	@Override
	protected void modifyDataObject(FileSystemDO dataObject, Object... parameters) 
	{
		return;
	}

	@Override
	protected boolean clearRepositorySucceeded() throws Exception 
	{
		this.deployDataObjects(this.getDefaultResourceName(), Arrays.asList(new String[] {"1"}));
		this.getDataAccessObject().clear();

		return (this.getDataAccessObject().reloadAll().size() == 0);
	}

	@Override
	protected boolean assertDefaultGetListenersBeforeGet(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean assertDefaultGetListenersAfterGet(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean assertDefaultSaveListenersBeforeSave(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean assertDefaultSaveListenersAfterSave(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean assertDefaultDeleteListenersBeforeDelete(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean assertDefaultDeleteListenersAfterDelete(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean assertDefaultReloadListenersAfterReload(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return false;
	}
}
