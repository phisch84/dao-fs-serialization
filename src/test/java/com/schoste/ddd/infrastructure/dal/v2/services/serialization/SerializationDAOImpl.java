package com.schoste.ddd.infrastructure.dal.v2.services.serialization;

import java.util.Spliterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.schoste.ddd.infrastructure.dal.v2.models.SerializationDO;

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

	@Override
	protected Spliterator<SerializationDO> createLazyLoader() throws Exception 
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'createLazyLoader'");
	}
}
