package com.ibm.contracts.advisor.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

import snaq.db.ConnectionPool;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.parser.VcapSetupParser;
import com.ibm.contracts.advisor.util.SOP;
import com.ibm.nosql.json.api.BasicDBList;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;

public class DBPool implements Constants{
	private static DBPool dbpool; 
	private static ConnectionPool pool;
	private static Connection con;
	
	public static DBPool getInstance(){
		
		if(CISCOVCAP){
			dbpool = getCiscoInstanceVcap();
			return dbpool;
		}
		
		if(VCAPDASHDB){
			dbpool = getInstanceVcap();
		}else{
			dbpool = getInstanceNoVcap();
		}
		
		return dbpool;
	}
	
	private static DBPool getCiscoInstanceVcap(){
		Connection conn = null;
		BasicDBObject bludb = null;
		String VCAP_SERVICES = "";
		String jdbcurl = null;
		String schema = "";
		if(dbpool==null){
			dbpool = new DBPool();
			try {
				VCAP_SERVICES = System.getenv("VCAP_SERVICES");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (VCAP_SERVICES != null) {
				BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
				String thekey = null;
				Set<String> keys = obj.keySet();
				
				for (String eachkey : keys) {
					//writer.println("Key is: " + eachkey);
					// The old name for this service was AnalyticsWarehouse
					if (eachkey.contains("user-provided")) {
						thekey = eachkey;
					}
				}
				if (thekey == null) {
					//writer.println("Cannot find any dashDB service in the VCAP; exiting");
					return null;
				}
				BasicDBList list = (BasicDBList) obj.get(thekey);
				bludb = (BasicDBObject) list.get("0");
				bludb = (BasicDBObject) bludb.get("credentials");
				try {
					Class.forName(dbClassName);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jdbcurl =  (String) bludb.get("jdbcurl");
				schema = (String) bludb.get("schema");
				String jdbcurlwithschema = jdbcurl+":currentSchema="+schema;
				String user = (String) bludb.get("username");
			    String password = (String) bludb.get("password");
			    try {
					conn = DriverManager.getConnection(jdbcurlwithschema, user,password);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    pool = new ConnectionPool(poolName,
						minPool, maxPool, maxSize, idleTimeout, jdbcurlwithschema, user, password);
			}
		}
		return dbpool;
	}
	
	private static DBPool getInstanceVcap(){
		// TODO Auto-generated constructor stub
				if(dbpool==null){
					dbpool = new DBPool();
					VcapSetupParser.getDashDBCredential(VcapSetupParser.VCAPUtils());
					try {
						Class c = Class.forName(dbClassName);
						Driver driver = (Driver)c.newInstance();
						DriverManager.registerDriver(driver);
						String url = VcapSetupParser.VCAPdbjdbcurl;
						pool = new ConnectionPool(poolName,
								minPool, maxPool, maxSize, idleTimeout, url, VcapSetupParser.VCAPdbUsername, VcapSetupParser.VCAPdbPassword);
						SOP.printSOPSmall( "asasas VCAPdbUsername :: "+VcapSetupParser.VCAPdbUsername);
						SOP.printSOPSmall( "frfrfr VCAPdbPassword :: "+VcapSetupParser.VCAPdbPassword);
						SOP.printSOPSmall( "tytyty VCAPdbjdbcurl :: "+VcapSetupParser.VCAPdbjdbcurl);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return dbpool;
	}
	
	private static DBPool getInstanceNoVcap(){
		// TODO Auto-generated constructor stub
				if(dbpool==null){
					dbpool = new DBPool();
					try {
						Class c = Class.forName(dbClassName);
						Driver driver = (Driver)c.newInstance();
						DriverManager.registerDriver(driver);
						String url = dbUrl;
						pool = new ConnectionPool(poolName,
								minPool, maxPool, maxSize, idleTimeout, url, dbUsername, dbPassword);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return dbpool;
	}

	private DBPool() {
		
	}
	
	public static Connection getConnection() throws SQLException{
		return DBPool.getInstance().pool.getConnection();
	}
	
	public static Connection getConnectionOlder() throws SQLException{
		if(CISCOVCAP){
			Connection conn = null;
			BasicDBObject bludb = null;
			String VCAP_SERVICES = "";
			String jdbcurl = null;
			try {
				VCAP_SERVICES = System.getenv("VCAP_SERVICES");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (VCAP_SERVICES != null) {
				BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
				String thekey = null;
				Set<String> keys = obj.keySet();
				
				for (String eachkey : keys) {
					//writer.println("Key is: " + eachkey);
					// The old name for this service was AnalyticsWarehouse
					if (eachkey.contains("user-provided")) {
						thekey = eachkey;
					}
				}
				if (thekey == null) {
					//writer.println("Cannot find any dashDB service in the VCAP; exiting");
					return null;
				}
				BasicDBList list = (BasicDBList) obj.get(thekey);
				bludb = (BasicDBObject) list.get("0");
				//writer.println("Service found: " + bludb.get("name"));
				bludb = (BasicDBObject) bludb.get("credentials");
				
				try {
					// Class.forName("com.ibm.db2.jcc.DB2Driver");
					Class.forName(dbClassName);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String schema = (String) bludb.get("schema");
				//jdbcurl =  (String) bludb.get("jdbcurl");
				//jdbcurl="jdbc:db2://dashdb-txn-lrg-cisco1-dal09-01.ciscodev.us-south.bluemix.net:50000/BLUDB:currentSchema=IBM_TEST_PROJ_TEST1_DEV";
				jdbcurl=(String) bludb.get("jdbcurl")+":currentSchema="+schema;
				String user = (String) bludb.get("username");
			    String password = (String) bludb.get("password");
			    // String schema = (String) bludb.get("schema");
			    System.out.println("#############################################");
			    System.out.println("SCHEMA IS "+schema);
			    conn = DriverManager.getConnection(jdbcurl, user,password);
				
			}else{
				System.out.println("VCAP not OK. Connection NOT created.");
			}
			
			return conn;
		}
		return DBPool.getInstance().pool.getConnection();
	}
	
	public static Connection getConnection_Alt() throws SQLException{
		return  DriverManager.getConnection(Constants.dashDBURL);
	}
	
	public static void closeConnection(Connection conn){
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void release(){
		if(pool!=null){
			pool.release();
		}
	}

}
