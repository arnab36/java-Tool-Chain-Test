package com.ibm.contracts.advisor.dao.implementation;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;

public class SchemaName implements Constants{

	public static String schemaName() {
		// TODO Auto-generated constructor stub
		/*BasicDBObject bludb = null;
		String VCAP_SERVICES = "";
		String schema = "";
		try {
			VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (VCAP_SERVICES != null) {
			BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
			bludb = (BasicDBObject) bludb.get("credentials");
			schema = (String) bludb.get("schema");
		}
		return schema;*/
		return DBSCHEMANAME;
	}

}
