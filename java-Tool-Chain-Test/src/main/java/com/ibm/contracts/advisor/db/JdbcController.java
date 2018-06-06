package com.ibm.contracts.advisor.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This is the Database manager for JDBC connections. The purpose of this class
 * is to hide connection handling from the user. A common problem in JDBC
 * database handling is users not closing the connections that they use. With
 * this implementation we manage the connections withing this manager class.
 *
 * @author Atrijit
 */
public class JdbcController {

	//Added to disable instances to be created
	private JdbcController() {
		// TODO Auto-generated constructor stub
	}
	
	/**
     * To run Jdbc command user should pass the JdbcCommand to this method. This
     * method will supply a connection object instance to the command. At the
     * end of the command user need not be required to close the Connection. The
     * framework will take care of this.
     *
     * @param command
     *            to run
     * @return @throws
     *         Exception
     */
	
	public static Object runCommand(JdbcCommandInterface command)
	        throws Exception
	    {
	       
		   Connection connection = null;
		   Object output = null;
		   try {
		       //obtain the connection
		       connection = getConnection();
		       //now execute the database command
		       output = command.execute(connection);
		   } finally { //what ever happens we try to close the connection used
		       closeConnection(connection);
		   }
		   
		   return output;
	    }

	    public static void runUpdateCommand(JdbcUpdateCommandInterface command)
	        throws Exception
	    {
	        Connection connection = null;
	 //       Object output = null;
	        try {
	            //obtain the connection
	            connection = getConnection();
	            //now execute the database command
	            command.executeUpdate(connection);
	        } finally { //what ever happens we try to close the connection used
	            closeConnection(connection);
	        }
	        
	    }
	    
	    /**
	     * Returns the connection objects for internal use. The connection handling
	     * mechanism can be decided by the user of this class. To change the way of
	     * obtaining connections anyone can subclass this and override this method.
	     * In case connection pooling to be done, it should be done externally. In
	     * most cases we better let it managed by app server.
	     *
	     * @return obtained connection
	     */
	    public static Connection getConnection() throws Exception {
	       return ((JdbcController) ConnectionProvider.getInstance()).getConnection();
	    }

	    public static void closeConnection(Connection connection)
	        throws SQLException
	    {
	        if(connection != null && !connection.isClosed())
	        {
	            connection.close();
	        }
	    }
	    
	    public static Object runCommand(JdbcCommandInterface command, Connection connection) throws Exception {
	        
	          	 Object output = null;
	          	 boolean connBool = false;
	          	 try {
	            //Check if the passed connection is null or connection is closed.
	          		 if(connection==null || connection.isClosed()){
	          			 connection = getConnection();
	          			 connBool=true;
	          		 }
	          		 //now execute the database command
	          		 output = command.execute(connection);
	          	 } finally { //if we had had to create a connection inside this then we close it inside this.
	          		 if(connBool){
	          			 closeConnection(connection);
	          		 }
	          	 }
	          	 
	        return output;
	    }
	    
	    public static void runUpdateCommand(JdbcUpdateCommandInterface command, Connection connection) throws Exception {
	         boolean connBool = false;
	        try {
	            //Check if the passed connection is null or is closed
	        	if(connection==null || connection.isClosed()){
	        		connection = getConnection();
	        		connBool=true;
	        	}
	            //now execute the database command
	            command.executeUpdate(connection);
	        	} finally { //if we had had to create a connection inside this then we close it inside this.
	        		if(connBool){
	        			closeConnection(connection);
	        		}
	        	}
	       
	    
	    } 

}
