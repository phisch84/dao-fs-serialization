package com.schoste.ddd.infrastructure.dal.v2.services.serialization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.schoste.ddd.infrastructure.dal.v2.models.SerializationDO;
import com.schoste.ddd.infrastructure.dal.v2.services.serialization.GenericSerializationDAO;

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
}
