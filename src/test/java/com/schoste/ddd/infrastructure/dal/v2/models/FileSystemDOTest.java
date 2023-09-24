package com.schoste.ddd.infrastructure.dal.v2.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.schoste.ddd.testing.v1.GenericObjectTest;

/**
 * Explicitly tests the FileSystemDO class and implicitly test the GenericDataObject class.
 * 
 * FileSystemDO instances are equal if their id and their time stamps match.
 * Data of the instances may be different.
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 *
 */
public class FileSystemDOTest extends GenericObjectTest<FileSystemDO>
{

	@Override
	protected Map<String, Collection<FileSystemDO>> getEqualObjects() 
	{
		Map<String, Collection<FileSystemDO>> equalObjectsSet = new HashMap<String, Collection<FileSystemDO>>();
		Collection<FileSystemDO> sameObjects = new ArrayList<FileSystemDO>();
		FileSystemDO sameObject = new FileSystemDO();
		sameObjects.add(sameObject);
		sameObjects.add(sameObject);

		Collection<FileSystemDO> equalObjects = new ArrayList<FileSystemDO>();
		
		// Objects with same data
		FileSystemDO equalObject1 = new FileSystemDO();
		FileSystemDO equalObject2 = new FileSystemDO();
		
		// Object with different data
		FileSystemDO equalObject3 = new FileSystemDO();

		equalObject1.setId(1);
		equalObject1.setCreatedTimeStamp(1);
		equalObject1.setModifiedTimeStamp(1);
		equalObject1.setData("HELLO".getBytes());

		equalObject2.setId(1);
		equalObject2.setCreatedTimeStamp(1);
		equalObject2.setModifiedTimeStamp(1);
		equalObject2.setData("HELLO".getBytes());

		equalObject3.setId(1);
		equalObject3.setCreatedTimeStamp(1);
		equalObject3.setModifiedTimeStamp(1);
		equalObject3.setData("WORLD".getBytes());

		equalObjects.add(equalObject1);
		equalObjects.add(equalObject2);
		equalObjects.add(equalObject3);


		equalObjectsSet.put("sameObjects", sameObjects);
		equalObjectsSet.put("equalObjects", equalObjects);

		return equalObjectsSet;
	}

	@Override
	protected Map<String, Collection<FileSystemDO>> getNotEqualObjects() 
	{
		Map<String, Collection<FileSystemDO>> notEqualObjectsSet = new HashMap<String, Collection<FileSystemDO>>();
		Collection<FileSystemDO> notEqualObjectsSameTSDifferentId = new ArrayList<FileSystemDO>();
		Collection<FileSystemDO> notEqualObjectsSameIdDifferentTS = new ArrayList<FileSystemDO>();

		// Objects with same data
		FileSystemDO notEqualObject1 = new FileSystemDO();
		FileSystemDO notEqualObject2 = new FileSystemDO();
		
		// Object with different data
		FileSystemDO notEqualObject3 = new FileSystemDO();

		notEqualObject1.setId(1);
		notEqualObject1.setCreatedTimeStamp(1);
		notEqualObject1.setModifiedTimeStamp(1);
		notEqualObject1.setData("HELLO".getBytes());

		notEqualObject2.setId(2);
		notEqualObject2.setCreatedTimeStamp(1);
		notEqualObject2.setModifiedTimeStamp(1);
		notEqualObject2.setData("HELLO".getBytes());

		notEqualObject3.setId(3);
		notEqualObject3.setCreatedTimeStamp(1);
		notEqualObject3.setModifiedTimeStamp(1);
		notEqualObject3.setData("WORLD".getBytes());

		notEqualObjectsSameTSDifferentId.add(notEqualObject1);
		notEqualObjectsSameTSDifferentId.add(notEqualObject2);
		notEqualObjectsSameTSDifferentId.add(notEqualObject3);
		
		notEqualObjectsSet.put("notEqualObjectsSameTSDifferentId", notEqualObjectsSameTSDifferentId);

		// Objects with same id but different time stamps
		FileSystemDO notEqualObject4 = new FileSystemDO();
		FileSystemDO notEqualObject5 = new FileSystemDO();
		FileSystemDO notEqualObject6 = new FileSystemDO();

		notEqualObject4.setId(4);
		notEqualObject4.setCreatedTimeStamp(1);
		notEqualObject4.setModifiedTimeStamp(1);
		notEqualObject4.setData("HELLO".getBytes());

		notEqualObject5.setId(4);
		notEqualObject5.setCreatedTimeStamp(2);
		notEqualObject5.setModifiedTimeStamp(3);
		notEqualObject5.setData("HELLO".getBytes());

		notEqualObject6.setId(4);
		notEqualObject6.setCreatedTimeStamp(2);
		notEqualObject6.setModifiedTimeStamp(4);
		notEqualObject6.setData("WORLD".getBytes());

		notEqualObjectsSameIdDifferentTS.add(notEqualObject4);
		notEqualObjectsSameIdDifferentTS.add(notEqualObject5);
		notEqualObjectsSameIdDifferentTS.add(notEqualObject6);
		
		notEqualObjectsSet.put("notEqualObjectsSameIdDifferentTS", notEqualObjectsSameIdDifferentTS);

		return notEqualObjectsSet;
	}

}
