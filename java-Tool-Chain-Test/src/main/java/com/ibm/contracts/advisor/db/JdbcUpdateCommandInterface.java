package com.ibm.contracts.advisor.db;

import java.sql.Connection;

/**
 * This is the interface for Jdbc command. Users who wish to use Jdbc through
 * "JdbcController" may use this interface to implement Jdbc calls.
 *
 * @author Atrijit
 */

public interface JdbcUpdateCommandInterface {
	
	/**
     * The JdbcController will call this method giving an connection instance as
     * the parameter. User can use this connection instance to perform any
     * database operation. At the end of the database call the JdbcController
     * will take care of closing any unclosed connection, hence the developer
     * is made free of the connection management issues.
     */
	public void executeUpdate(Connection connection) throws Exception;
}
