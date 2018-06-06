package com.ibm.contracts.advisor.FieldGlass.Alert;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ibm.contracts.advisor.FieldGlass.FGUtils.UtilityFunctions;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.vo.FieldGlassOPOBJ;
import com.ibm.contracts.advisor.vo.IndexPairVO;

public class ClassificationAlert implements Constants{

	
	public static void main(String[] ab) {

		long startTime = System.nanoTime();

		String filePath = "C:/Users/IBM_ADMIN/Documents/testnow/FieldGlass/FieldGlassHTML/";
		String fileName = "CSCOTQ00104609.pdf_Converted.html"; 
		
		String documentClass = "";
		File file = new File(filePath + fileName);
		
		Document doc = null;
		try {
			doc = Jsoup.parse(file, "UTF-8");
			// System.out.println(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String content = doc.toString();
		
		// Calling the actual function
		try {
			documentClass = checkDocumentClass(content);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("documentClass :: "+ documentClass);
		
	}
	
	
	/*
	 * This function is getting called from FGAlertHandler
	 */
	public static FieldGlassOPOBJ CheckClassificationAlert(
			String content, String contractType) {
		// TODO Auto-generated method stub
		FieldGlassOPOBJ result = new FieldGlassOPOBJ();
		// Calling the actual function
		String documentClass = "";
		try {
			documentClass = checkDocumentClass(content);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		result.setClassName("ClassificationAlert");
		if(contractType.equalsIgnoreCase(documentClass)){
			result.setFlag(false);
			result.setOutput("Document Class is Correct.");			
		}else {
			result.setFlag(true);
			result.setOutput("Wrong Document Uploaded.");	
		}
		
		return result;
	}
	
	
	
	
	/**
	 *  The following function will parse the document and will return the class of the document(MP/MS) based on,- 
	 *  
	 *  "SoW Assignment Category" - 40% weightage(category)
	 *   For "Acceptance of Milestones" - for MP with 60% (Existing class)
	 *   For "Service Level Agreements" - for MS with 60% (Existing class)
	 *    
	 *    Score = (40% * category) + (60 * class)
	 *    if MP score > MS score then CLASS = MP 
	 *    else CLASS = MS
	 *    
	 * */
	
	
	private static String checkDocumentClass(String content) {
		
		String name = getDocumentClassName(content);		
		double MPScore = 0, MSScore = 0;		
		
		if(name.equalsIgnoreCase("Managed Service")){
			MSScore = MSScore + 0.4;
		}else if(name.equalsIgnoreCase("Managed Project")){
			MPScore = MPScore + 0.4;
		}else {
			System.out.println("Class Name not found");
		}
		
		System.out.println("MPScore :: "+ MPScore);
		System.out.println("MSScore :: "+ MSScore);
		
		MPScore = MPScore + checkClassExistance(content,"Acceptance of Milestones");
		MSScore = MSScore + checkClassExistance(content,"Service Level Agreements");
		
		System.out.println("MPScore :: "+ MPScore);
		System.out.println("MSScore :: "+ MSScore);
		
		if(MPScore >= MSScore){
			return MPFG;
		}else {
			return	MSFG;
		}
		
	}
	
	
	/*
	 * The following function will serach a key in the content.
	 * If found then score is 0.6 else 0 
	 * */
	private static double checkClassExistance(String content, String key) {
		// TODO Auto-generated method stub
		double score = 0;
		
		try {
			int index  = content.indexOf(key);			
			if(index > 0){
				score = 0.6;
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
		
		return score;
	}



	/*
	 * The following will return the class name
	 * */
	private static String getDocumentClassName(String content) {
		// TODO Auto-generated method stub
		String key = "SOW Assignment Category";

		int start = content.indexOf(key);
		
		System.out.println("start:: "+ start);
		System.out.println(content.substring(start,start+ 20));
		
		int outPutStart = 0;
		int outputEnd = 0;
		
		String className = "";
		
		try {
			IndexPairVO vo1 = UtilityFunctions.getOutputIndex(content, "<p>", 3, start);
			outPutStart = vo1.getEndIndex();
			
			IndexPairVO vo2 = UtilityFunctions.getOutputIndex(content, "</p>", 4, outPutStart);
			outPutStart = vo2.getStartIndex();
			outputEnd = vo2.getEndIndex();
			
			System.out.println("outPutStart :: "+ outPutStart);
			System.out.println("outputEnd :: "+ outputEnd);
			
			String vendorName = content.substring(outPutStart + 3, outputEnd);
			className = vendorName.substring(vendorName.indexOf('.')+1, vendorName.length()).trim();
			
			System.out.println("vendorName :: "+ vendorName );		
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("className :: "+ className);
		return className;
	}


}
