package com.schoste.ddd.infrastructure.dal.v2.services.serialization;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.schoste.ddd.infrastructure.dal.v2.models.SerializationDO;
import com.schoste.ddd.infrastructure.dal.v2.services.serialization.SerializationDAOImpl;

/**
 * Test class of the SerializationDAOImpl implementation
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 */
@ContextConfiguration(locations = { "file:src/test/resources/unittest-beans.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class SerializationDAOImplTest extends GenericSerializationDAOTest<SerializationDO, SerializationDAOImpl>
{
	@Autowired
	protected SerializationDAOImpl serializationDAOImpl;
	
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
	protected SerializationDAOImpl getDataAccessObject() 
	{
		return this.serializationDAOImpl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SerializationDO createDataObject(Object... parameters) 
	{
		SerializationDO dataObject = this.serializationDAOImpl.createDataObject();
		
		if ((parameters == null) || (parameters.length < 1)) return dataObject;	
		if (parameters.length > 0) dataObject.setId((Integer) parameters[0]);
		if (parameters.length <= 1) return dataObject;
		if (!(parameters[1] instanceof String)) throw new IllegalArgumentException("parameters");
		
		String callingMethod = (String) parameters[1];
		
		switch (callingMethod)
		{
			// This test method requires two identical DOs
			case "testSaveExisting":
				dataObject.setId(1);
				dataObject.setExampleStringProperty(String.format("%s%s", parameters[0], parameters[1]));
				break;
				
			default: dataObject.setExampleStringProperty((String) parameters[1]);
		}
		
		return dataObject;
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
		SerializationDAOImpl dao = new SerializationDAOImpl(daoPath);
		
		Assert.notNull(dao, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void modifyDataObject(SerializationDO dataObject, Object... parameters) 
	{
		if ((parameters == null) || (parameters.length < 1)) return;
		
		dataObject.setExampleStringProperty((String) parameters[0]);
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
		return true;
	}
}
