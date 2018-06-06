package com.ibm.contracts.advisor.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.dao.interfaces.CommonDAOInterface;
import com.ibm.contracts.advisor.db.DBPool;
import com.ibm.contracts.advisor.util.SOP;
import com.ibm.contracts.advisor.util.Util;

public class DocumentsDAO implements CommonDAOInterface, Constants {
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

	public void insertIntoDocumentTablePool(String fileName, String userId,
			String containerName) {
		System.out.println("Calling Query from Pool");
		String jsonName = Util.replacingFileToJSONExtension(fileName);
		
		String query = "INSERT into "+documentTableName+"(USERID,DOCUMENTUNIQUENAME,JSONOBJECTUNIQUENAME,CONTAINER,CONVERTFLAG,ANALYZEFLAG,TIMESTAMP) VALUES ('"
				+ userId
				+ "', '"
				+ fileName
				+ "','"
				+ jsonName
				+ "','"
				+ containerName + "', '1', '0','anything')";
		if(CISCOVCAP){
			String schema = SchemaName.schemaName();
			query = "INSERT into "+schema+"."+documentTableName+"(USERID,DOCUMENTUNIQUENAME,JSONOBJECTUNIQUENAME,CONTAINER,CONVERTFLAG,ANALYZEFLAG,TIMESTAMP) VALUES ('"
					+ userId
					+ "', '"
					+ fileName
					+ "','"
					+ jsonName
					+ "','"
					+ containerName + "', '1', '0','anything')";
		}
		try {
			Connection con = DBPool.getConnection();
			System.out.println("COnnection Object :: " + con);
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

	public void insertIntoDocumentTable(String fileName, String userId,
			String containerName) {
		System.out.println("Calling Query from Normal");
		String jsonName = Util.replacingFileToJSONExtension(fileName);
		// String query =
		// "INSERT into DOCUMENTS(USERID,DOCUMENTUNIQUENAME,JSONOBJECTUNIQUENAME,CONTAINER,CONVERTFLAG,ANALYZEFLAG) VALUES ('"+userId+"', '"+fileName+"','"+jsonName+"','"+containerName+"', '1', '0')"
		// ;
		String query = "INSERT into "+documentTableName+"(USERID,DOCUMENTUNIQUENAME,JSONOBJECTUNIQUENAME,CONTAINER,CONVERTFLAG,ANALYZEFLAG,TIMESTAMP)"
				+ " VALUES (?, ?, ?, ?, ?, ?,?)";
		if(CISCOVCAP){
			String schema = SchemaName.schemaName();
			query = "INSERT into "+schema+"."+documentTableName+"(USERID,DOCUMENTUNIQUENAME,JSONOBJECTUNIQUENAME,CONTAINER,CONVERTFLAG,ANALYZEFLAG,TIMESTAMP)"
					+ " VALUES (?, ?, ?, ?, ?, ?,?)";
			
		}
		
		try {
			// Connection con=ConnectionProvider.getInstance();
			Connection con = DBPool.getConnection();
			PreparedStatement pstmtA = con.prepareStatement(query);
			pstmtA.setString(1, userId);
			pstmtA.setString(2, fileName);
			pstmtA.setString(3, jsonName);
			pstmtA.setString(4, containerName);
			pstmtA.setString(5, "1");
			pstmtA.setString(6, "0");
			pstmtA.setString(7, "Anything");
			pstmtA.executeUpdate();
			pstmtA.close();			
			DBPool.closeConnection(con);
			System.out.println("Done Query");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// After the NLC is run successfully, the table is updated
	public void updateAfterNLC(String jsonObjectName, String userId) {
		String query = "UPDATE "+documentTableName+" set ANALYZEFLAG ='1' where userid='"
				+ userId + "' AND JSONOBJECTUNIQUENAME='" + jsonObjectName
				+ "'";
		if(CISCOVCAP){
			String schema = SchemaName.schemaName();
			query = "UPDATE "+schema+"."+documentTableName+" set ANALYZEFLAG ='1' where userid='"
					+ userId + "' AND JSONOBJECTUNIQUENAME='" + jsonObjectName
					+ "'";
			
			
		}
		System.out.println("Query is :: " + query);
		try {
			// Connection con=ConnectionProvider.getInstance();
			Connection con = DBPool.getConnection();
			PreparedStatement pstmtA = con.prepareStatement(query);
			pstmtA.executeUpdate();
			pstmtA.close();
			DBPool.closeConnection(con);
			System.out.println("Done Query");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public List<JSONObject> getObjectStorageInfo(String userId) {
	public List<String> getObjectStorageInfo(String userId, String fileName) {
		List<String> result = new ArrayList<String>();
		String objectId, jsonObjectName, container, tempFileName = null;
		int counter = 0;
		SOP.printSOPSmall("File inside  getObjectStorageInfo :: " + fileName);
		SOP.printSOPSmall("userId inside  getObjectStorageInfo :: " + userId);
		// String query =
		// "SELECT * from DOCUMENTS where USERID='"+userId+"' AND CONVERTFLAG='"+1+"' AND ANALYZEFLAG='"+0+"'"
		// ;
		String query = "SELECT * from "+documentTableName+" where USERID=? AND CONVERTFLAG=? AND ANALYZEFLAG=?";
		if(CISCOVCAP){
			String schema = SchemaName.schemaName();
			query = "SELECT * from "+schema+"."+documentTableName+" where USERID=? AND CONVERTFLAG=? AND ANALYZEFLAG=?";
			
		}
		try {
			// Connection con=ConnectionProvider.getInstance();
			Connection con = DBPool.getConnection();
			PreparedStatement pstmtA = con.prepareStatement(query);
			pstmtA.setString(1, userId);
			pstmtA.setString(2, "1");
			pstmtA.setString(3, "0");
			ResultSet rs = pstmtA.executeQuery();
			SOP.printSOPSmall("rs is :: " + rs);
			while (rs.next()) {
				objectId = rs.getString("OBJECTID");
				jsonObjectName = rs.getString("JSONOBJECTUNIQUENAME");
				container = rs.getString("CONTAINER");
				tempFileName = rs.getString("DOCUMENTUNIQUENAME");
				// SOP.printSOPSmall("------DOCUMENTUNIQUENAME :: "+rs.getString("DOCUMENTUNIQUENAME"));
				// SOP.printSOPSmall("------OBJECTUNIQUENAME :: "+rs.getString("JSONOBJECTUNIQUENAME"));
				// SOP.printSOPSmall("** tempFileName :: "+ tempFileName);
				if (tempFileName.equals(fileName)) {
					result.add(jsonObjectName);
					SOP.printSOPSmall("+++++++++++++++++++= Final JSONOBJECTNAME ++++++++++++++++++++"
							+ jsonObjectName);
				}
				counter++;
			}
			pstmtA.close();
			DBPool.closeConnection(con);
		} catch (Exception e) {
			SOP.printSOPSmall("===============Exception occured in getObjectStorageInfo =========================");
			e.printStackTrace();
		}
		return result;
	}
}
