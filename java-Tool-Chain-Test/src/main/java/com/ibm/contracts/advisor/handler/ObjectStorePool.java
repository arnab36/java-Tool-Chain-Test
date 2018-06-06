package com.ibm.contracts.advisor.handler;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.parser.CiscoVcapUtils;
import com.ibm.contracts.advisor.parser.VcapSetupParser;
import com.ibm.contracts.advisor.util.SOP;

public class ObjectStorePool implements Constants{
	
		
	public ObjectStorePool() {
		// TODO Auto-generated constructor stub
	}
	/*public static OSClientV3 getCOSClientV3(){
		OSClientV3 cos = getCiscoV3Vcap();
		return cos;
	}*/
	
	
	public static AmazonS3Client getCOSClientV3(){
		AmazonS3Client cos = getAmazonS33Vcap();
		return cos;
	}
	
	private static OSClientV3 getCiscoV3Vcap() {
		CiscoVcapUtils.getObjectStorageCredentials(CiscoVcapUtils.VCAPUtils());
		String accessKey = CiscoVcapUtils.accessKeyID;
		String secretKey = CiscoVcapUtils.secretAccessKey;
		String endpoint = CiscoVcapUtils.endpointUrl;
		/*
		OSClientV3 os = OSFactory.builderV3()
                .endpoint(VcapSetupParser.VCAPauth_url)
                .credentials(accessKey, secretKey)
                .scopeToProject(projectIdent, domainIdent)
                .authenticate();
                */
		OSClientV3 os = OSFactory.builderV3()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .authenticate();
		
		
		return os;
	}
	
	
	private static AmazonS3Client getAmazonS33Vcap(){
		CiscoVcapUtils.getObjectStorageCredentials(CiscoVcapUtils.VCAPUtils());
		String accessKey = CiscoVcapUtils.accessKeyID;
		String secretKey = CiscoVcapUtils.secretAccessKey;
		String endpoint = CiscoVcapUtils.endpointUrl;
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AmazonS3Client cos = new AmazonS3Client(credentials);
		cos.setEndpoint(endpoint);
		return cos;
	}
	
	
	public static OSClientV3 getOSClientV3(){
		OSClientV3 os = null;
		if(VCAPOBJECTSTORAGE){
			os = getOSClientV3Vcap();
		}
		else{
			os = getOSClientV3NOVcap();
		}
		return os;
		
	}
	
	private static OSClientV3 getOSClientV3NOVcap() {
		Identifier domainIdent = Identifier.byName(domain);
	    Identifier projectIdent = Identifier.byName(project);
		OSClientV3 os = OSFactory.builderV3()
                .endpoint(auth_url)
                .credentials(userId, password)
                .scopeToProject(projectIdent, domainIdent)
                .authenticate();
		return os;
	}
	
	private static OSClientV3 getOSClientV3Vcap() {
		VcapSetupParser.getObjectStorageCredentials(VcapSetupParser.VCAPUtils());
		
		Identifier domainIdent = Identifier.byName(VcapSetupParser.VCAPObjdomainName);
	    Identifier projectIdent = Identifier.byName(VcapSetupParser.VCAPProject);
	   // System.out.println("Inside getOSClientV3Vcap \n ============== VCAP Project is :: "+ VcapSetupParser.VCAPProject);
	    SOP.printSOPSmall("aaaa VCAPDomain Name :: "+VcapSetupParser.VCAPObjdomainName);
		SOP.printSOPSmall("aaaa VCAPProject :: "+VcapSetupParser.VCAPProject);
		SOP.printSOPSmall("aaaa VCAPauth_url :: "+VcapSetupParser.VCAPauth_url);
		SOP.printSOPSmall("aaaa VCAPObjUserId :: "+VcapSetupParser.VCAPObjUserId);
		SOP.printSOPSmall("aaaa VCAPdbPassword :: "+VcapSetupParser.VCAPObjPassword);
		OSClientV3 os = OSFactory.builderV3()
                .endpoint(VcapSetupParser.VCAPauth_url)
                .credentials(VcapSetupParser.VCAPObjUserId, VcapSetupParser.VCAPObjPassword)
                .scopeToProject(projectIdent, domainIdent)
                .authenticate();
		
		
		return os;
	}

}
