package com.ibm.contracts.advisor.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.handler.ApacheTikaHandler;


/**
 *  The following class is about creating a general json from HTML for CISCO NLC or Conversation classifier.
 */
public class PrepareGeneralJSON {

	private Document doc;
	private static String[] tagList = { "h1", "h2", "h3", "h4", "h5",
	"h6", "p"};
	private static int wordLimit = 5;
	
	/*
	 *  The following function will take-
	 *  @args  - Document as HTML 
	 *  It will process the document and will return a JSONObject 
	 * */
	public static JSONObject HTMLToGeneralJSON(Document doc) {
		
		JSONObject output = new JSONObject();
		JSONArray arr = new JSONArray();
		
		// Tables are extracted and put inside <p> tag
		doc = docConvert_V4.extractTableForGeneral(doc, null);		
		Elements elements = doc.select("*");
		System.out.println(" ========================================================== ");
		for (Element element : elements) {		
			// Checking for tag name, only certain predefined tags are processed
			if(tagMatch(element.tagName())) {	
				String[] countWord = splitForGeneral(element.text(), element);				
				for(String str : countWord) {
					// Each sentence needs at least 5 words
					if(str.split(" ").length > wordLimit) {
						JSONObject temp = new JSONObject();
						temp.put("text", str);
						arr.add(temp);
					}
				}						
			}			
		}
		System.out.println("array size :: "+arr.size());
		output.put("answer_units", arr);		
		return output;
	}
	
	
	
	private static String[] splitForGeneral(String text, Element cTag) {
		// TODO Auto-generated method stub
		String[] temp;
		ArrayList<String> secText = new ArrayList<String>();
		String cText = cTag.text().trim();
		cText = cText.replaceAll("^\"+", "").replaceAll("\"+$", "");
		cText = cText.replaceAll(
				"[^a-zA-Z0-9_?.:;,\"'=()/\\#@$%&!]", " ");
				
		if (docConvert_V4.countWords(cText) > 0) {
			String[] splitSent = docConvert_V4.javaSplitMulti(cText);
			int size = splitSent.length;
			for (int i = 0; i < size; i++) {
				
				if (docConvert_V4.countWords(splitSent[i]) > 0) {
					
					String[] text_split_enum = splitSent[i]
							.split(docConvert_V4.patternSplitSentence);
					for (int j = 0; j < text_split_enum.length; j++) {
						
						secText.add(text_split_enum[j]);
					}
				}
			}
		}
		
		temp = new String[secText.size()];
		for(int  i = 0; i < secText.size(); i++) {
			temp[i] = secText.get(i);
		}
		
		return temp;
	}



	/*
	 *  The following function will take an input String as tagName.
	 *  It will check whether that name is in the tagList.
	 *  Depending on that it will return a boolean response.
	 */
	private static boolean tagMatch(String tagName) {
		// TODO Auto-generated method stub
		for(String str:tagList) {
			if(str.equals(tagName)) {
				return true;
			}
		}
		return false;
	}




	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String fileName = "CSCOTQ00140502_MP.pdf";
		String filePath = "C:\\Users\\IBM_ADMIN\\Documents\\testnow\\FieldGlass\\MP\\";
		File file = new File(filePath+fileName);
	//	Document doc = readFromFIle(filePath+fileName);
	//	Document doc  = ApacheTikaHandler.docConversionDocument(file);
	//	docConvert_V4.WriteToFile(doc,file.getName(),filePath);
	//	JSONObject json = HTMLToGeneralJSON(doc);
	//	PrepareStaticDocument.WriteJSONToFile(json,fileName,filePath);
		String doc = ApacheTikaHandler.docConversion(file);
		System.out.println(doc);
		
	}

	
	


	private static Document readFromFIle(String absPath) throws Exception  {
		// TODO Auto-generated method stub
		String docHTML = null;
		FileReader fr = new FileReader(absPath);
		BufferedReader br =  new BufferedReader(fr);
		String line;
		while((line = br.readLine()) != null){
			docHTML += line; 
		}
		br.close();
		fr.close();
		return Jsoup.parse(docHTML);
	}

}
