package com.ibm.contracts.advisor.FieldGlass.STATIC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.ibm.contracts.advisor.FieldGlass.FGUtils.UtilityFunctions;
import com.ibm.contracts.advisor.util.Util;
import com.ibm.contracts.advisor.vo.IndexPairVO;

public class FieldGlassStatic {

	private static JSONObject FGStaticDocumentJSON = new JSONObject();
	// /public static String patternSplitSentence =
	// "\\s[\\(]?(iv|v?i{0,3})[\\)]\\s|\\s([\\(]?[a-zA-Z][\\)])\\s";
	public static String patternSplitSentence = "(((\\s+\\(?)([Ii][Xx]|[Ii][Vv]|[Vv]?[Ii]{1,3})[.\\)](\\s+))|(?<=[^(i.e)])([.;:])(\\s+)|((\r|\n|\\s){2,})|((\\s+\\()[a-zA-Z0-9][\\).](\\s+)(?=[A-Z]))|((\\s+)[a-zA-Z0-9][\\).](\\s+)(?=[A-Z])))";

	public static void main(String[] ab) {

		String sowFilePath = "C:/Users/IBM_ADMIN/Documents/testnow/FieldGlass/FG HTML MP/";
		String sowFileName = "sow_10_27_2016_CSCOTQ00105402.pdf_Converted.html";

		// Getting the SOW file(in HTML format)
		File file = new File(sowFilePath + sowFileName);

		// Converting to HTML
		Document doc = null;
		try {
			doc = Jsoup.parse(file, "UTF-8");
			// System.out.println(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Getting the template file
		String classTemplate = "C:/Users/IBM_ADMIN/Documents/Phase2/CISCO-Phase 3/Template_MP_FG_Classes.json";
		JSONObject ClasstemplateJson = null;
		try {
			ClasstemplateJson = Util.getJSONFromFile(classTemplate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// The following function will create the initial structure of the
		// template which we are going to generate
		// createInitialStructure();
		JSONObject staticTemplate = createStaticTemplateFG(doc,
				ClasstemplateJson);
		System.out.println(" ========== Static template is ::  ============\n "
				+ staticTemplate);
	}

	
	/*
	 * The following function will put some additional fields to the static
	 * template we are going to create.
	 * 
	 * It will work on the private JSOn object "FGStaticDocumentJSON"
	 */
	private static void createInitialStructure() {
		// TODO Auto-generated method stub
		JSONArray arr = new JSONArray();
		JSONArray arr1 = new JSONArray();

		FGStaticDocumentJSON.put("answer_units", arr);
		FGStaticDocumentJSON.put("metadata", arr1);
	}
	

	/**
	 * The following function will take two arguments.
	 * 
	 * 1) HTML document of SOW 2) JSON Object of header - className mapping
	 * 
	 * and will create a static template for this HTML document.
	 * 
	 * The output is that static template as JSON Object.
	 * 
	 * @return
	 * 
	 * */
	public static JSONObject createStaticTemplateFG(Document doc,
			JSONObject classtemplateJson) {
		// TODO Auto-generated method stub
		JSONObject staticTemplate = new JSONObject();
		String content = null;
		createInitialStructure();

		
		/* String title = "Statement of Work Payment Characteristics"; 
		 
		 if(title.equalsIgnoreCase("Statement of Work Payment Characteristics")){
			 String tableContent = UtilityFunctions.CheckPaymentMethodTable(title,doc.toString());
			 addToFGStaticDocumentJSON("PaymentMethod", tableContent);			 
		 }else {
			 content = getContentWithStringInput(doc.toString(), title.trim(),"WorkProductSpecifications");				 
		 }*/
			

		JSONArray arr = (JSONArray) classtemplateJson.get("section_titles");
		int size = arr.size();
		for (int i = 0; i < size; i++) {
			JSONObject individualElement = (JSONObject) arr.get(i);
			if(individualElement.get("title").toString().trim().equalsIgnoreCase("Statement of Work Payment Characteristics")){
				String tableContent = UtilityFunctions.CheckPaymentMethodTable(individualElement.get("title").toString().trim(),doc.toString());
				addToFGStaticDocumentJSON(individualElement.get("class_name").toString(), tableContent);		
			}else {
				content = getContentWithStringInput(doc.toString(),
						individualElement.get("title").toString().trim(),
						individualElement.get("class_name").toString());
			}
			
		}		 
		 
		return FGStaticDocumentJSON;
	}

	

	/**
	 * The following function takes two arguments,-
	 * 
	 * 1) Document as String 2) Class header as String
	 * 
	 * It will return the content of that class as output String
	 * */

	private static String getContentWithStringInput(String doc, String title,
			String className) {

		// TODO Auto-generated method stub
		String classContent = "", tagContent = "";

		try {
			doc = doc.replaceAll(
					"&lt;b&gt;|&lt;/b&gt;|&lt;br&gt;|&lt;|&gt|&nbsp;", "");

			IndexPairVO titleIndex = UtilityFunctions.GetCurrentTagIndex(doc,
					"<p>", "</p>", title);
			
			// This if condition is the scenario where title itself is not found
			if(titleIndex.getStartIndex() == titleIndex.getEndIndex()){
				addToFGStaticDocumentJSON(className, classContent);
				return classContent;
			}else {

				tagContent = doc.substring(titleIndex.getStartIndex() + 3,
						titleIndex.getEndIndex()).trim();
				
				System.out.println("char at " + titleIndex.getStartIndex() + " :: "
						+ doc.charAt(titleIndex.getStartIndex()));
				System.out.println("char at " + titleIndex.getEndIndex() + " :: "
						+ doc.charAt(titleIndex.getEndIndex()));
				tagContent = tagContent.replaceAll(
						"&lt;b&gt;|&lt;/b&gt;|&lt;br&gt;|&lt;|&gt|<p>|</p>", "");
				
				System.out.println("tagContent :: "+ tagContent);
			}


			/*
			 * There are two possible scenarios.
			 * 
			 * 1) The title is properly structured 2) The title is not properly
			 * structured
			 * 
			 * For 1st case we get the content of the next <td></td> tag For
			 * others we still have to write a custom algorithm.
			 */
			if (title.equalsIgnoreCase(tagContent)) {
				System.out.println(title + " :: Found");				
				classContent = processproperlyStructuredTag(titleIndex,doc);				
			} else {
				classContent = UtilityFunctions.ProcessUnstructured(titleIndex,
						doc, title);
				System.out.println(title + " :: Not Found");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		addToFGStaticDocumentJSON(className, classContent);
		return classContent;
	}

	

	private static String processproperlyStructuredTag(IndexPairVO titleIndex,
			String doc) {
		// TODO Auto-generated method stub
		
		String classContent = "";
		String tagType = UtilityFunctions.checkTagTypeOfTittletext(titleIndex,doc);
		
		System.out.println("tag Type :: " + tagType);
		//System.exit(0);
		
		// Checking required
		if(tagType.equalsIgnoreCase("P")){
			classContent = UtilityFunctions.GetPTagToTableTagContent(titleIndex, doc);
		}
		
		// Done
		if(tagType.equalsIgnoreCase("TD")){
			classContent = UtilityFunctions.getNextTDText(titleIndex, doc);
		}
		
		return classContent;
	}


	/**
	 * @param className
	 *            - Name of the class
	 * @param classContent
	 *            - Content of the class
	 * 
	 *            This will put the content of the class into the template.
	 */

	private static void addToFGStaticDocumentJSON(String className,
			String classContent) {
		// TODO Auto-generated method stub

		boolean foundFlag = false;
		JSONObject classJSON = new JSONObject();
		JSONArray contentArray = new JSONArray();

		System.out.println("classContent :: " + classContent);

		// Processing the class content
		if (!("").equalsIgnoreCase(classContent)) {

			String[] splittedText = callSplitFunction(classContent);
			// String[] splittedText = callSplitFunction1(classContent);

			System.out.println("text_split_enum Length :: "
					+ splittedText.length);

			for (int j = 0; j < splittedText.length; j++) {
				JSONObject temp = new JSONObject();
				temp.put("text", splittedText[j]);
				contentArray.add(temp);
				System.out.println(splittedText[j]);
			}
		} else {
			JSONObject temp = new JSONObject();
			temp.put("text", classContent);
			contentArray.add(temp);
		}
		
		System.out.println("Content Array :: "+ contentArray);

		// As we have same class in multiple header so we are checking whether
		// the same class
		// already exists or not. If it exists then we add the content to the
		// already existing content
		JSONArray arr = (JSONArray) FGStaticDocumentJSON.get("answer_units");
		for (int i = 0; i < arr.size(); i++) {
			JSONObject tempObj = (JSONObject) arr.get(i);
			System.out.println("class is :: "+ tempObj.get("CLASS").toString());
			if (className.equalsIgnoreCase(tempObj.get("CLASS").toString())) {
				foundFlag = true;
				System.out.println("True Found");
				JSONArray oldContent = (JSONArray) tempObj.get("content");
				System.out.println("***********88 oldContent \n ==== "+oldContent);
				for (int j = 0; j < contentArray.size(); j++) {
					oldContent.add(contentArray.get(j));
				}
				break;
			}
		}

		System.out.println("Found Flag :: "+ foundFlag);
		// If this is a new class
		if (!foundFlag) {
			classJSON.put("CLASS", className);
			classJSON.put("content", contentArray);
			arr.add(classJSON);
		}
	}

	private static String[] callSplitFunction1(String classContent) {
		// TODO Auto-generated method stub
		String[] splittedText = new String[100];
		String[] output;
		int count1 = 0;
		Pattern re = Pattern
				.compile(
						"[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$|:\\s+)",
						Pattern.MULTILINE | Pattern.COMMENTS);
		Matcher reMatcher = re.matcher(classContent);
		while (reMatcher.find()) {
			splittedText[count1] = reMatcher.group();
			count1++;
		}
		output = new String[count1];
		for (int i = 0; i < count1; i++) {
			output[i] = splittedText[i];
		}
		return output;
	}

	
	private static String[] callSplitFunction(String classContent) {
		// TODO Auto-generated method stub
		/*
		  String[] splittedText = classContent
		  .split("(?<=[a-z]|[A-Z])\\.\\s+|:\\s+");
		 */

		/*String[] splittedText = classContent
				.split("(?<=[a-z]|[A-Z])\\.\\s+|:\\s+|\\.\\s+");*/
		
		String secondRegex = "(?<=[a-z]|[A-Z])\\.\\s+|:\\s+|\\.\\s+";
		
		
		String[] splittedText = classContent
				.split(patternSplitSentence);
		
		List<String> list = new ArrayList<String>();
		for(int i =0; i < splittedText.length; i++){			
			String[] furtherSplit = splittedText[i].split(secondRegex);
			for(int j =0; j < furtherSplit.length; j++){
				list.add(furtherSplit[j]);
			}
		}
		
		String[] output = new String[list.size()];
		for(int  i = 0; i < list.size(); i++){
			output[i] = list.get(i);
		}
		
		return output;
	}

}
