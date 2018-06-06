package com.ibm.contracts.advisor.dao.interfaces;

import java.util.List;

public interface CommonDAOInterface {

	public void save(Object obj) throws Exception;
	
	public void update(Object obj) throws Exception;

	public void deleteRecord(Object obj) throws Exception;

	public List getAllRecords() throws Exception;

	public Object getObjectById(Long id) throws Exception;
	

}
