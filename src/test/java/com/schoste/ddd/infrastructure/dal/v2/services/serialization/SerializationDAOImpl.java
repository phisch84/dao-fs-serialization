package com.schoste.ddd.infrastructure.dal.v2.services.serialization;

import com.schoste.ddd.infrastructure.dal.v2.models.GenericDataObject;
import com.schoste.ddd.infrastructure.dal.v2.models.SerializationDO;
import com.schoste.ddd.infrastructure.dal.v2.services.LazyLoader;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Example file system data object used in unit testing of the GenericSerializationDAO implementation
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 *
 */
public class SerializationDAOImpl extends GenericSerializationDAO<SerializationDO>
{
	@Autowired
	protected ApplicationContext applicationContext;
	
	/**
	 * {@inheritDoc}
	 */
	public SerializationDAOImpl(String storagePath) throws IllegalArgumentException, IllegalStateException, Exception 
	{
		super(storagePath);
	}
	
	/**
	 * Creates a new data object
	 * 
	 * @return an instance to a new data object
	 */
	public SerializationDO createDataObject()
	{
		return (SerializationDO) this.applicationContext.getBean(SerializationDO.class);
	}

	protected SerializationDO getSafe(int id)
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

	@SuppressWarnings("unchecked")
	@Override
	protected LazyLoader<Integer, SerializationDO> createLazyLoader() throws Exception 
	{
		Function<Integer, GenericDataObject> cv = id -> this.safeDoGet(id);
		LazyLoader<Integer, SerializationDO> ll = this.applicationContext.getBean(LazyLoader.class, cv, this.storagePath);

		return ll;
	}

	/**
	 * Calls the {@link GenericSerializationDAO#doGet(int)} method inside a try-catch block
	 * 
	 * @param id the id of the data object to load
	 * @return the data object with the given id, or null if none was found or on error 
	 */
	protected SerializationDO safeDoGet(Integer id)
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
