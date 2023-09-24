package com.schoste.ddd.infrastructure.dal.v2.services.serialization;

import java.util.HashMap;
import java.util.Map;

import com.schoste.ddd.infrastructure.dal.v2.models.GenericDataObject;
import com.schoste.ddd.infrastructure.dal.v2.services.GenericDAOTest;
import com.schoste.ddd.infrastructure.dal.v2.services.GenericDataAccessObject;

/**
 * Generic test class to test implementations of the GenericSerializationDAO class.
 * This class provides standard test methods.
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 *
 * @param <DO> The class of the data object
 * @param <DAO> The class of the data access object
 */
public abstract class GenericSerializationDAOTest <DO extends GenericDataObject, DAO extends GenericDataAccessObject<DO>> extends GenericDAOTest<DO, DAO>
{
	protected Map<Integer, Integer> cachedDataObjects = new HashMap<>();

	/**
	 * Creates a new data object and saves it with a given id so it can be
	 * obtained later by {@link this#getExistingDataObject(int)}.
	 * 
	 * Override it if you need a different functionality in your tests.
	 * 
	 * @param id the id of the object to retrieve it
	 * @param properties properties to be passed on to {@link this#createDataObject(Object...)}
	 * @throws Exception re-throws every exception
	 */
	protected void cacheDataObject(int id, Object ...properties) throws Exception
	{
		Object[] parameters = (properties == null) ? new Object[1] : new Object[properties.length +1];
		
		parameters[0] = 0;
		
		for (int i=0; i<((properties == null) ? 0 : properties.length); i++)
		{
			parameters[1+i] = properties[i];
		}
		
		DO dataObject = this.createDataObject(parameters);
		
		this.getDataAccessObject().save(dataObject);
		this.cachedDataObjects.put(id, dataObject.getId());
	}

	/**
	 * Creates a set of existing data objects stored with an id from 1-10.
	 * Override it if you need a different functionality in your tests.
	 * 
	 * @throws Exception re-throws every exception
	 */
	protected void clearAndSetUpExistingDataObjects() throws Exception
	{
		this.cachedDataObjects.clear();
		this.getDataAccessObject().clear();
		
		this.cacheDataObject(1, "testSaveExisting");
		this.cacheDataObject(2, "testGetAll1");
		this.cacheDataObject(3, "testGetAll2");
		this.cacheDataObject(4, "testGetAll3");
		this.cacheDataObject(5, "testGetAll4");
		this.cacheDataObject(6, "testGetAll5");
		this.cacheDataObject(7, "testGetAll6");
		this.cacheDataObject(8, "testGetAll7");
		this.cacheDataObject(9, "testGetAll8");
		this.cacheDataObject(10, "testGetAll9");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DO getExistingDataObject(int id) 
	{
		try
		{
			if (this.cachedDataObjects.containsKey(id))
			{
				int cachedId = this.cachedDataObjects.get(id);
				
				return this.getDataAccessObject().get(cachedId);
			}
			else
			{
				return this.getDataAccessObject().get(id);				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean clearRepositorySucceeded() throws Exception 
	{
		this.getDataAccessObject().save(this.createDataObject());
		this.getDataAccessObject().clear();

		return (this.getDataAccessObject().reloadAll().size() == 0);
	}
}
