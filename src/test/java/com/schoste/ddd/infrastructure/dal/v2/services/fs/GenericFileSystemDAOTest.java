package com.schoste.ddd.infrastructure.dal.v2.services.fs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.schoste.ddd.infrastructure.dal.v2.services.GenericDAOTest;
import com.schoste.ddd.infrastructure.dal.v2.services.GenericDataAccessObject;
import com.schoste.ddd.infrastructure.dal.v2.models.GenericFileObject;
import com.schoste.ddd.infrastructure.dal.v2.services.fs.GenericFileSystemDAO;
import com.schoste.ddd.testing.v1.TestFiles;

/**
 * Generic test class to test implementations of the GenericFileSystemDAO class.
 * This class provides standard test methods.
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 *
 * @param <DO> The class of the data object
 * @param <DAO> The class of the data access object
 */
public abstract class GenericFileSystemDAOTest<DO extends GenericFileObject, DAO extends GenericDataAccessObject<DO>> extends GenericDAOTest<DO, DAO>
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
	 * Gets the name of the file in the test resources folder which will be used for testing
	 * 
	 * @return a name of a file in the test resources folder
	 */
	protected abstract String getDefaultResourceName();
	
	/**
	 * Creates copies of a resource file in the storage path of a data access object
	 * 
	 * @param resourceName the name of the resource file to create copies of
	 * @param dataObjectNames the name of the data object files to create
	 * @return the created data object files
	 */
	protected Collection<File> deployDataObjects(String resourceName, Collection<String> dataObjectNames)
	{
		Collection<File> deployedFiles = new ArrayList<File>(dataObjectNames.size());
		
		try
		{
			String storagePath = ((GenericFileSystemDAO<?>)this.getDataAccessObject()).getStoragePath().toString();
			File resourceFile = TestFiles.getTestResourceAsFile(resourceName, "000", "zip");
			
			for (String dataObjectName : dataObjectNames)
			{
				File doFile = Paths.get(storagePath, dataObjectName).toFile();
				Files.copy(Paths.get(resourceFile.toURI()), Paths.get(doFile.toURI()), StandardCopyOption.REPLACE_EXISTING);
				
				doFile.setLastModified(System.currentTimeMillis());
				doFile.deleteOnExit();
				
				deployedFiles.add(doFile);
			}
			
			resourceFile.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
		
		return deployedFiles;
	}

}
