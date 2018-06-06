package com.ibm.contracts.advisor.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.dao.interfaces.CommonDAOInterface;
import com.ibm.contracts.advisor.db.ConnectionProvider;
import com.ibm.contracts.advisor.db.DBPool;
import com.ibm.contracts.advisor.util.Encryption;


public class UserDAO implements CommonDAOInterface, Constants {
	// String key = "dsaihfsdhfis423efw532432"; //this is for encryption
	// decryption
	/* implemented methods */
	
	
	public void save(Object obj) throws Exception {

	}

	public void update(Object obj) throws Exception {

	}

	public void deleteRecord(Object obj) throws Exception {

	}

	public List getAllRecords() throws Exception {
		// TODO Auto-generated method stub
		return null;

	}

	public Object getObjectById(Long id) throws Exception {
		return null;
	}

	public Object getObjectByUserIdPassword(String userid, String password)
			throws Exception {
		return null;
	}

	public static  String[] getPasswordById(String uid) throws NamingException, SQLException {
		// TODO Auto-generated method stub
		
		String[] arr = new String[4];		
		//Connection con = ConnectionProvider.getInstance();			
		String query = "select PASSWORD,FIRSTNAME,LASTNAME,ROLE from "+credentialTableName+" where userid=?";
		if (CISCOVCAP) {
			String schema = SchemaName.schemaName();
			query = "select PASSWORD,FIRSTNAME,LASTNAME,ROLE from "+ schema + "."+credentialTableName+" where userid=?";
			
		}
		System.out.println("QUERY LOGGED BY BILLS REQUEST!");
		System.out.println(query);
		System.out.println("*************************");
		try {
			Connection con = DBPool.getConnection();
			PreparedStatement pstmtA = con.prepareStatement(query);
			pstmtA.setString(1, uid);
			ResultSet rs = pstmtA.executeQuery();
			String pwd=null;			
			 while(rs.next()){
				  pwd=rs.getString("password");
				  arr[0] = pwd;
				  arr[1] = rs.getString("FIRSTNAME");
				  arr[2] = rs.getString("LASTNAME");
				  arr[3] = rs.getString("ROLE");
			 }
			 arr[0]=Encryption.xorMessage(Encryption.base64decode(pwd), ENCRYPTIONKEY) ;		
			 pstmtA.close();
			 DBPool.closeConnection(con);
			// con.close();
		}catch(Exception e){
			e.printStackTrace();
		}		
		return arr;
	}
	
	
	public static void insertIntoCredTablePool(String userId,String fname,String lname, String pwd, String role){
		System.out.println("Calling Query from Pool");
		//String jsonName =  Util.replacingFileToJSONExtension(fileName);
		
		String query = "INSERT into "+credentialTableName+" VALUES (1,'"+userId+"','"+fname+"','"+lname+"','"+pwd+"' , '"+role+"')" ;	
		if (CISCOVCAP) {
			String schema = SchemaName.schemaName();
			query = "INSERT into "+ schema + "."+credentialTableName+" VALUES (1,'"+userId+"','"+fname+"','"+lname+"','"+pwd+"' , '"+role+"')" ;
			
		}
		
		try {
			Connection con = DBPool.getConnection();
			System.out.println("COnnection Object :: "+con);
			PreparedStatement pstmtA = con.prepareStatement(query);
			pstmtA.executeUpdate();
			pstmtA.close();
			DBPool.closeConnection(con);
			System.out.println("Done Query");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static Set<String> getAllUser() throws NamingException, SQLException {
		// TODO Auto-generated method stub
		Set<String> alluser=new HashSet<String>();
		Connection con=ConnectionProvider.getInstance();
		String query = "select * from "+credentialTableName; 
		if (CISCOVCAP) {
			String schema = SchemaName.schemaName();
			query = "select * from "+ schema + "."+credentialTableName;
			
		}
		//PreparedStatement b = con.prepareStatement("select * from "+credentialTableName);
		PreparedStatement b = con.prepareStatement(query);
		ResultSet rs = b.executeQuery();
		String pwd=null;
		 while(rs.next()){
			String  uid=rs.getString("userid");
			alluser.add(uid);
		}
		 b.close();
		 con.close();
		return alluser;
	}
	
	public static void main(String[] a) throws NamingException, SQLException{
		String[] s = getPasswordById("demo_user1@cisco.com");
		System.out.println(s);
	}
}
