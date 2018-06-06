package com.ibm.contracts.advisor.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.ibm.contracts.advisor.constants.Constants;



public class ConnectionProvider {

	private static ConnectionProvider connectionProvider;
    private DataSource source;
    private static final String SYNC = "SYNC";

    public static Connection getInstance()
        throws NamingException, SQLException
    {
    	Connection a = DriverManager.getConnection(Constants.dashDBURL);
    	//Statement b = a.createStatement();
    //	String key = "dsaihfsdhfis423efw532432";
        if(a == null)
            synchronized(SYNC)
            {
            	System.out.println("------not able to create connection-------");
            	return null;
              //  if(a == null)
                //    connectionProvider = new ConnectionProvider();
            }
        return a;
    }

    private ConnectionProvider()
        throws NamingException
    {
        //String dataSourceJndi = UtilAppConf.getInstance().getProperty("application.jndi.database");
       // source = (DataSource)(new InitialContext()).lookup(dataSourceJndi);
    }

    public Connection getConnection()
        throws SQLException, NamingException
    {
        return source.getConnection();
    }

}
