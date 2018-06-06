package com.ibm.contracts.advisor.dao.implementation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.dao.interfaces.CommonDAOInterface;
import com.ibm.contracts.advisor.db.DBPool;
import com.ibm.contracts.advisor.util.SOP;
import com.ibm.contracts.advisor.vo.StrategyObject;

public class StrategyDAO implements CommonDAOInterface, Constants {

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

	/*
	 * public static HashMap<String, HashMap<String, String>>
	 * selectFromStrategy(String userId, String strategyType, String userRole) {
	 */
	public static JSONObject selectFromStrategy(String userId,
			String strategyType, String userRole) {
		JSONObject result = new JSONObject();
		String strategyName, accessType, userid2 = null;

		JSONObject tempMap1 = new JSONObject();
		JSONObject tempMap2 = new JSONObject();
		String query = "";

		if (userRole.equalsIgnoreCase("A")) {
			query = "select * from " + strategyTableName + " where userid=? ";
			if (CISCOVCAP) {
				String schema = SchemaName.schemaName();
				query = "select * from " + schema + "." + strategyTableName
						+ " where userid=? ";

			}
			try {
				// Connection con=ConnectionProvider.getInstance();
				Connection con = DBPool.getConnection();
				PreparedStatement pstmtA = con.prepareStatement(query);
				pstmtA.setString(1, userId);
				ResultSet rs = pstmtA.executeQuery();
				while (rs.next()) {
					strategyName = rs.getString("strategyName");
					String strategyType_res = rs.getString("strategyType");
					String accessType2 = rs.getString("accessType");
					if (strategyType_res.equalsIgnoreCase(strategyType))
						tempMap1.put(strategyName, accessType2);
				}
				result.put("own", tempMap1);
				pstmtA.close();
				DBPool.closeConnection(con);
			} catch (Exception e) {
				SOP.printSOPSmall("===============Exception occured in feting from userStrategy==========");
				e.printStackTrace();
			}
		}

		query = "";
		query = "select * from " + strategyTableName + " where userid!=?";
		if (CISCOVCAP) {
			String schema = SchemaName.schemaName();
			query = "select * from " + schema + "." + strategyTableName
					+ " where userid!=?";

		}
		System.out.println("query :: " + query);
		try {
			// Connection con=ConnectionProvider.getInstance();
			Connection con = DBPool.getConnection();
			PreparedStatement pstmtB = con.prepareStatement(query);
			pstmtB.setString(1, userId);

			ResultSet rs = pstmtB.executeQuery();
			System.out.println(rs);
			while (rs.next()) {
				strategyName = rs.getString("strategyName");
				userid2 = rs.getString("userid");
				accessType = rs.getString("accessType");
				String strategyType_res = rs.getString("strategyType");
				System.out.println(strategyName);
				System.out.println(accessType);
				System.out.println(strategyType_res);
				if (accessType.equalsIgnoreCase("Pub")
						&& strategyType_res.equalsIgnoreCase(strategyType))
					System.out.println("Putting :: " + userid2);
				tempMap2.put(userid2, strategyName);
			}
			result.put("others", tempMap2);
			pstmtB.close();
			DBPool.closeConnection(con);
		} catch (Exception e) {
			SOP.printSOPSmall("===============Exception occured in feting from userStrategy==========");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param userId
	 * @param role
	 *            => USER(U) or ADMIN("A")
	 * @return => List of MS and MP strategies. For User it is just the public
	 *         strategy of Admin. For Admin it is all his/her strategy.
	 */

	public static HashMap<String, String> getStrategyNamesFromTable(
			String userId, String role) {
		HashMap<String, String> result = new HashMap<String, String>();
		String MP_List = "";
		String MS_List = "";
		String query = "";
		String strategyName, accessType, strategyType;
		if (role.equalsIgnoreCase("A")) {
			query = "select * from " + strategyTableName + " where USERID=?";
			if (CISCOVCAP) {
				String schema = SchemaName.schemaName();
				query = "select * from " + schema + "." + strategyTableName
						+ " where USERID=?";

			}

		} else {
			query = "select * from " + strategyTableName
					+ " where USERID=? and ACCESSTYPE='Pub'";
			if (CISCOVCAP) {
				String schema = SchemaName.schemaName();
				query = "select * from " + schema + "." + strategyTableName
						+ " where USERID=? and ACCESSTYPE='Pub'";

			}
		}

		try {
			Connection con = DBPool.getConnection();
			PreparedStatement pstmtB = con.prepareStatement(query);
			pstmtB.setString(1, "demo_user1@cisco.com");

			ResultSet rs = pstmtB.executeQuery();
			while (rs.next()) {

				strategyName = rs.getString("STRATEGYNAME");
				accessType = rs.getString("ACCESSTYPE");
				strategyType = rs.getString("STRATEGYTYPE");

				if (role.equalsIgnoreCase("A")) {
					if (strategyType.equalsIgnoreCase("MP")) {
						MP_List = MP_List + strategyName + "#";
					} else {
						MS_List = MS_List + strategyName + "#";
					}
				} else {
					if (accessType.equalsIgnoreCase("Pub")) {
						if (strategyType.equalsIgnoreCase("MP")) {
							MP_List = MP_List + strategyName + "#";
						} else {
							MS_List = MS_List + strategyName + "#";
						}
					}

				}

			}
			result.put("MP", MP_List);
			result.put("MS", MS_List);
			pstmtB.close();
			DBPool.closeConnection(con);
		} catch (Exception e) {
			SOP.printSOPSmall("===============Exception occured in feting from userStrategy==========");
			e.printStackTrace();
		}
		return result;
	}

	public static ArrayList<StrategyObject> getStartegyInfoForUser() {
		ArrayList<StrategyObject> strategyInfoList = new ArrayList<StrategyObject>();
		// String query = "Select * from USERSTRATEGYV2 where ACCESSTYPE='Pub'";
		String query = "Select * from USERSTRATEGYV2";
		if (CISCOVCAP) {
			String schema = SchemaName.schemaName();
			query = "Select * from " + schema + "." + "USERSTRATEGYV2";

		}
		try {
			Connection con = DBPool.getConnection();
			PreparedStatement pstmtB = con.prepareStatement(query);
			ResultSet rs = pstmtB.executeQuery();
			while (rs.next()) {
				StrategyObject so = new StrategyObject();
				so.setUserId(rs.getString("USERID"));
				so.setStrategyName(rs.getString("STRATEGYNAME"));
				so.setStrategyType(rs.getString("STRATEGYTYPE"));
				so.setAccessType(rs.getString("ACCESSTYPE"));
				strategyInfoList.add(so);
			}
			pstmtB.close();
			DBPool.closeConnection(con);
		} catch (Exception e) {
			SOP.printSOPSmall("===============Exception occured in feting from userStrategy==========");
			e.printStackTrace();
		}
		return strategyInfoList;
	}

	public static ArrayList<StrategyObject> getStartegyInfoForAdmin(
			String userId) {
		ArrayList<StrategyObject> strategyInfoList = new ArrayList<StrategyObject>();
		// String query = "Select * from USERSTRATEGYV2 where ACCESSTYPE='Pub'";
		String query = "Select * from USERSTRATEGYV2";
		if (CISCOVCAP) {
			String schema = SchemaName.schemaName();
			query = "Select * from " + schema + "." + "USERSTRATEGYV2";

		}
		try {
			Connection con = DBPool.getConnection();
			PreparedStatement pstmtB = con.prepareStatement(query);
			ResultSet rs = pstmtB.executeQuery();
			while (rs.next()) {
				StrategyObject so = new StrategyObject();
				so.setUserId(rs.getString("USERID"));
				so.setStrategyName(rs.getString("STRATEGYNAME"));
				so.setStrategyType(rs.getString("STRATEGYTYPE"));
				so.setAccessType(rs.getString("ACCESSTYPE"));
				strategyInfoList.add(so);
			}
			pstmtB.close();
			DBPool.closeConnection(con);
		} catch (Exception e) {
			SOP.printSOPSmall("===============Exception occured in feting from userStrategy==========");
			e.printStackTrace();
		}
		return strategyInfoList;
	}

	public static void insertIntoStrategyTable(String uid, String strategyName,
			String accessType, String strategyType) {
		System.out.println("insert into strategy table");
		String query = "INSERT into " + strategyTableName
				+ " VALUES (?, ?, ?, ?)";
		if (CISCOVCAP) {
			String schema = SchemaName.schemaName();
			query = "INSERT into " + schema + "." + strategyTableName
					+ " VALUES (?, ?, ?, ?)";

		}
		try {
			// Connection con=ConnectionProvider.getInstance();
			Connection con = DBPool.getConnection();
			PreparedStatement pstmtA = con.prepareStatement(query);
			pstmtA.setString(1, uid);
			pstmtA.setString(2, strategyName);
			pstmtA.setString(3, accessType);
			pstmtA.setString(4, strategyType);

			pstmtA.executeUpdate();
			pstmtA.close();
			DBPool.closeConnection(con);
			System.out.println("Done insertion");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void delFromStrategyTable(String uid, String strategyName,
			String strategyType) {
		System.out.println("delete from strategy table");
		String query = "delete from " + strategyTableName
				+ " where userid=? and STRATEGYNAME=? and STRATEGYTYPE=?";
		if (CISCOVCAP) {
			String schema = SchemaName.schemaName();
			query = "delete from " + schema + "." + strategyTableName
					+ " where userid=? and STRATEGYNAME=? and STRATEGYTYPE=?";

		}
		try {
			// Connection con=ConnectionProvider.getInstance();
			Connection con = DBPool.getConnection();
			PreparedStatement pstmtA = con.prepareStatement(query);
			pstmtA.setString(1, uid);
			pstmtA.setString(2, strategyName);
			pstmtA.setString(3, strategyType);
			pstmtA.executeUpdate();
			pstmtA.close();
			DBPool.closeConnection(con);
			System.out.println("Done deletion");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void delAllStrategyTable() {
		System.out.println("delete from strategy table");
		String query = "delete from " + strategyTableName;
		if (CISCOVCAP) {
			String schema = SchemaName.schemaName();
			query = "delete from " + schema + "." + strategyTableName;

		}
		try {
			// Connection con = ConnectionProvider.getInstance();
			Connection con = DBPool.getConnection();
			PreparedStatement pstmtA = con.prepareStatement(query);
			pstmtA.executeUpdate();
			pstmtA.close();
			DBPool.closeConnection(con);
			System.out.println("Done deletion");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException, ParseException {

		/*
		 * ObjectStoreHandler.delete(Constants.STRATEGY_CONTAINER,
		 * "demo_user1@cisco.com_strategy.json");
		 * ObjectStoreHandler.delete(Constants.STRATEGY_CONTAINER,
		 * "demo_user2@cisco.com_strategy.json");
		 * ObjectStoreHandler.delete(Constants.STRATEGY_CONTAINER,
		 * "demo_user3@cisco.com_strategy.json");
		 * 
		 * 
		 * 
		 * JSONObject tmp=Util.getJSONFromFile(
		 * "C:/Users/IBM_ADMIN/My Documents/SametimeFileTransfers/demo_user2@cisco.com_strategy.json"
		 * );
		 * 
		 * 
		 * 
		 * ObjectStoreHandler.objectStore(new
		 * ByteArrayInputStream(tmp.toJSONString().getBytes()),
		 * "demo_user1@cisco.com_strategy.json",STRATEGY_CONTAINER);
		 * ObjectStoreHandler.objectStore(new
		 * ByteArrayInputStream(tmp.toJSONString().getBytes()),
		 * "demo_user2@cisco.com_strategy.json",STRATEGY_CONTAINER);
		 * ObjectStoreHandler.objectStore(new
		 * ByteArrayInputStream(tmp.toJSONString().getBytes()),
		 * "demo_user3@cisco.com_strategy.json",STRATEGY_CONTAINER);
		 * 
		 * delAllStrategyTable();
		 * 
		 * String strType="MS"; JSONArray mp=(JSONArray) tmp.get(strType);
		 * String uid="demo_user1@cisco.com"; for(int i=0;i<mp.size();i++){
		 * JSONObject s=(JSONObject) mp.get(i); String strName=(String)
		 * s.get("name"); String
		 * accesstype=s.get("access").toString().substring(0, 3);
		 * System.out.println(uid+strName+accesstype); insertIntoStrategyTable(
		 * "demo_user1@cisco.com", strName,accesstype,strType);
		 * insertIntoStrategyTable( "demo_user2@cisco.com",
		 * strName,accesstype,strType); insertIntoStrategyTable(
		 * "demo_user3@cisco.com", strName,accesstype,strType); }
		 * 
		 * 
		 * strType="MP"; mp=(JSONArray) tmp.get(strType); //String
		 * uid="demo_user1@cisco.com"; for(int i=0;i<mp.size();i++){ JSONObject
		 * s=(JSONObject) mp.get(i); String strName=(String) s.get("name");
		 * String accesstype=s.get("access").toString().substring(0, 3);
		 * //System.out.println(uid+strName+accesstype);
		 * insertIntoStrategyTable( "demo_user1@cisco.com",
		 * strName,accesstype,strType); insertIntoStrategyTable(
		 * "demo_user2@cisco.com", strName,accesstype,strType);
		 * insertIntoStrategyTable( "demo_user3@cisco.com",
		 * strName,accesstype,strType); }
		 */

		/*
		 * HashMap<String,String> strategyList =
		 * StrategyDAO.getStrategyNamesFromTable("blah","U");
		 * System.out.println("MP"+ strategyList.get("MP"));
		 * System.out.println("MP"+ strategyList.get("MS"));
		 */

		JSONObject strategyList = selectFromStrategy("demo_user1@cisco.com",
				"MP", "A");
		System.out.println(strategyList);

	}

}
