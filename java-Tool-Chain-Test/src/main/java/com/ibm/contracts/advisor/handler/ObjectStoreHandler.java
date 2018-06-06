/**
 * 
 */
package com.ibm.contracts.advisor.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.storage.ObjectStorageService;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.openstack.storage.object.domain.SwiftObjectImpl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.parser.CiscoVcapUtils;

/**
 * @author Atrijit
 * 
 */
public class ObjectStoreHandler implements Constants {

	public static void objectStore(InputStream targetStream, String fileName,
			String container) {
		if (!CISCOVCAP) {
			OSClientV3 os = ObjectStorePool.getOSClientV3();
			try {
				ObjectStorageService objectStorage = os.objectStorage();
				objectStorage.objects().put(container, fileName,
						Payloads.create(targetStream));
			} catch (Exception e) {
				// System.out.println("------------------------------------------------");
				e.printStackTrace();
			}
		} else {
			AmazonS3Client cos = ObjectStorePool.getCOSClientV3();
			CiscoVcapUtils.getObjectStorageCredentials(CiscoVcapUtils
					.VCAPUtils());
			String accessKey = fileName;
			/*PutObjectRequest request = new PutObjectRequest(container,
					accessKey, new File(fileName));*/
			PutObjectRequest request = new PutObjectRequest(container,
					accessKey, targetStream, new ObjectMetadata());
			PutObjectResult result = cos.putObject(request);
		}
	}

	// ALTERNATE WITH FILE INSTEAD OF INPUTSTREAM
	public static void objectStoreAlt(String filePath, String fileName,
			HttpServletRequest request, HttpServletResponse response,
			String container) {
		OSClientV3 os = ObjectStorePool.getOSClientV3();
		ObjectStorageService objectStorage = os.objectStorage();
		objectStorage.objects().put(container, filePath,
				Payloads.create(new File(filePath)));

	}

	/*
	 * public static String getFileStr(String param, String mycontainer) throws
	 * Exception{ OSClientV3 os = null; if(!CISCOVCAP){ os =
	 * ObjectStorePool.getOSClientV3(); }else{ os =
	 * ObjectStorePool.getCOSClientV3(); }
	 * 
	 * List objs = os.objectStorage().objects().list(mycontainer);
	 * 
	 * //TODO THIS PART NEEDS SOME CHANGE
	 * 
	 * SwiftObjectImpl soione = null;
	 * 
	 * for(int i = 0; i < objs.size();i++){ SwiftObjectImpl soi =
	 * (SwiftObjectImpl)objs.get(i); String str = soi.getName();
	 * if(str.equalsIgnoreCase(param)){ soione = soi; break; }
	 * 
	 * }
	 * 
	 * InputStream soinput = soione.download().getInputStream();
	 * 
	 * BufferedReader in = new BufferedReader(new InputStreamReader(soinput));
	 * 
	 * StringBuilder response = new StringBuilder(); String inputLine;
	 * 
	 * while ((inputLine = in.readLine()) != null) response.append(inputLine);
	 * 
	 * in.close(); String reply = response.toString(); //
	 * out.println("String "+reply); return reply;
	 * 
	 * }
	 */

	public static String getFileStr(String fileName, String mycontainer)
			throws Exception {
		String reply = null;

		if (!CISCOVCAP) {
			OSClientV3 os = ObjectStorePool.getOSClientV3();
			List objs = os.objectStorage().objects().list(mycontainer);

			// TODO THIS PART NEEDS SOME CHANGE
			SwiftObjectImpl soione = null;
			for (int i = 0; i < objs.size(); i++) {
				SwiftObjectImpl soi = (SwiftObjectImpl) objs.get(i);
				String str = soi.getName();
				if (str.equalsIgnoreCase(fileName)) {
					soione = soi;
					break;
				}

			}

			InputStream soinput = soione.download().getInputStream();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					soinput));

			StringBuilder response = new StringBuilder();
			String inputLine;

			while ((inputLine = in.readLine()) != null){
				response.append(inputLine);
			}				

			in.close();
			reply = response.toString();			
			return reply;
		} else {
			// For CISCO VCAP using Amazon S3 Client
			AmazonS3Client cos = ObjectStorePool.getCOSClientV3();
			try {
				S3Object object = cos.getObject(new GetObjectRequest(
						mycontainer, fileName));
				InputStream objectData = object.getObjectContent();

				reply = IOUtils.toString(objectData, "UTF-8");

				objectData.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return reply;
		}
	}

	
	public static void delete(String containerName, String objectName) {
		if (!CISCOVCAP) {
			OSClientV3 os = ObjectStorePool.getOSClientV3();
			try {
				os.objectStorage().objects().delete(containerName, objectName);
			} catch (Exception e) {
				System.out.println("Object Storage deletion problem.");
				e.printStackTrace();
			}
		} else {
			// For CISCO VCAP using Amazon S3 Client
			AmazonS3Client cos = ObjectStorePool.getCOSClientV3();
			try {
				cos.deleteObject(new DeleteObjectRequest(containerName, objectName));
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
			} 
			
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
