package com.ibm.contracts.advisor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;

public class Util implements Constants {

	
	public static String[] splitToSent(String regex, String chunk) {
		Pattern pattern = Pattern.compile(regex);
		return pattern.split(chunk);
	}
	
	// Disabled for now with '_' option. The next one is the working version
	/*public static double probConfStdlangClass_1(String className,String text){
		double score=0.0;
		double tmp;
		JSONArray stdLangClass = (JSONArray) PostNLCPropSet.stdLanguage.get(className);
		Iterator i=stdLangClass.iterator();
		
		while(i.hasNext())
		{
			String stdText=((JSONObject) i.next()).get("text").toString();
			//System.out.println("----------------------");
			//System.out.println(stdText);
			tmp=FuzzySearch.tokenSetRatio(text,stdText)/100.0;
			if(tmp>score)
				score=tmp;
		}
		
		return score;
	}*/
	
	/**
	 * Prob conf stdlang class: get the highest score from standard language json
	 *
	 * @param className the class name
	 * @param text the text
	 * @return the double
	 */
	// The new version
	public static double probConfStdlangClass(String className,String text){
		double score=0.0;
		double tmp;
		System.out.println("class name ::"+className);
		JSONArray stdLangClass = (JSONArray) PostNLCPropSet.stdLanguage.get(className);
		Iterator i=stdLangClass.iterator();

		while(i.hasNext())
		{
			String stdText=((JSONObject) i.next()).get("text").toString();
			//System.out.println("----------------------");
			//System.out.println(stdText);
			tmp=((double)FuzzySearch.tokenSetRatio(text,stdText)+Constants.fuzzeThreshold)/100.0;
			if(tmp>score)
				score=tmp;
		}

		return score;
	} 
	
	/**
	 * Gets the JSON from file in the local storage
	 *
	 * @param filePath the file path
	 * @return the JSON from file
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public static JSONObject getJSONFromFile(String filePath) throws FileNotFoundException, IOException, ParseException{
        JSONParser jsonParser = new JSONParser();

        File file = new File(filePath);

        Object object = jsonParser.parse(new FileReader(file));

        JSONObject jsonObject = (JSONObject) object;
        return jsonObject;
		
	}
	
	/**
	 * Word count: count the number of word in the sentence according to reguler expression
	 *
	 * @param regx the regx
	 * @param sent the sentence
	 * @return the int
	 */
	public static int wordCount(String regx, String sent) {
		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher(sent);

		int count = 0;
		while (matcher.find())
			count++;
		return count;
	}
	
	/**
	 * Gets the JSON object: create a json object from the string
	 *
	 * @param jsonString the json string
	 * @return the JSON object
	 */
	public static JSONObject getJSONObject(String jsonString){
		JSONParser parser = new JSONParser();
		JSONObject splitedJSON=null;
		try {
			splitedJSON = (JSONObject) parser.parse(jsonString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return splitedJSON;
	}

	public static String replacingFileExtension(String fileName, String extension)
	{
        String strFileName = fileName;
		String strFileExtension = FilenameUtils.getExtension(strFileName);
		strFileName = strFileName.replaceAll(strFileExtension, extension);
		return strFileName;
	}
	
	public static String replacingFileToJSONExtension(String fileName)
	{
        String strFileName = fileName;
		String strFileExtension = FilenameUtils.getExtension(fileName);
		strFileName = strFileName.replaceAll(strFileExtension, "json");
		return strFileName;
	}
	
	public static String replacingFileToHTMLExtension(String fileName)
	{
        String strFileName = fileName;
		String strFileExtension = FilenameUtils.getExtension(fileName);
		strFileName = strFileName.replaceAll(strFileExtension, "html");
		return strFileName;
	}
	
	
	/**
	 * Search section title: search for the title's corresponding class searching in template list and fix the class confidence and quality
	 * and alternative class as well 
	 *
	 * @param titleText the title text
	 * @return the JSON object
	 */
	public static JSONObject searchSectionTitle(String titleText) {
		int wordCount=wordCount(patternWords,titleText);
		//HashMap<String, String> secContentClass =(HashMap<String, String>) ((HashMap<String, String>)PostNLCPropSet.getTitleClass()).clone();
		JSONObject secContentClass=new JSONObject();
		JSONArray tmp=new JSONArray();
		
		//System.out.println("-----searching for title text :--"+titleText);		
		 if(wordCount>1 || titleText.length()>5){
			 TreeMap<Integer, String> titleClasses=FuzzyMatch.getNearest(titleText,PostNLCPropSet.templateList);
				Entry<Integer, String> pair = titleClasses.pollLastEntry();
				secContentClass.put("CLASS",pair.getValue() );
				secContentClass.put("confidence",Double.toString((double)pair.getKey()/100.0) );
					String titleClass=null;
					pair = titleClasses.pollLastEntry();
					JSONObject tmp2=new JSONObject();
					//JSONObject alt=new JSONObject();
					try{
					titleClass=templateMap.get(pair.getValue());
					tmp2.put("CLASS",pair.getValue());
					tmp2.put("confidence",Double.toString((double)pair.getKey()/100.0));
					 }
						catch(Exception e){
							tmp2.put("CLASS",titleClass);
							tmp2.put("confidence","0");
						}
					tmp.add(tmp2);

					pair = titleClasses.pollLastEntry();
					JSONObject tmp3=new JSONObject();

					try{
					titleClass=templateMap.get(pair.getValue());
					tmp3.put("CLASS",pair.getValue());
					tmp3.put("confidence",Double.toString((double)pair.getKey()/100.0));
					 }
					catch(Exception e){
							tmp3.put("CLASS",titleClass);
							tmp3.put("confidence","0");
						}
					tmp.add(tmp3);

				secContentClass.put("class_alternative", tmp);
			
		}
		else{
			secContentClass.put("CLASS", "BLANK");
			secContentClass.put("Quality", "0");
		}
		//System.exit(0);
		return secContentClass;
	}
	
	
	
	/**
	 * Populate template title.
	 *
	 * @return the list
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public static List populateTemplateTitle() throws FileNotFoundException, IOException, ParseException {
		List<String> templateTitles=new LinkedList<String>();
		
		JSONObject template=PostNLCPropSet.templatefile;
		
		for(int i=0;i<((JSONArray)template.get("section_titles")).size();i++){
			String titleTem=((JSONObject)((JSONArray)template.get("section_titles")).get(i)).get("title").toString();
			String classTem=((JSONObject)((JSONArray)template.get("section_titles")).get(i)).get("class_name").toString();
			templateTitles.add(titleTem);
			templateMap.put(titleTem, classTem);
		}
			
		
		return templateTitles;
	}
	
	
	/**
	 * Creates the template OP: it will create the jsonobject from text list of 3 class name and its corresponding confidence
	 *
	 * @param text the text
	 * @param className the class name
	 * @param conf the conf
	 * @return the JSON object
	 */
	public static JSONObject createTemplateOP(String text,List<String> className,List<String> conf){
		JSONObject tmp=new JSONObject();
		tmp.put("text", text);
		JSONArray cls=new JSONArray();
		for(int k=0;k<3;k++){
			JSONObject mainClas=new JSONObject();
			try{
			mainClas.put("confidence", conf.get(k));
			mainClas.put("class_name", className.get(k));
			}
			catch(Exception ex){
				mainClas.put("confidence", "0.0");
				mainClas.put("class_name", "BLANK");
			}
			cls.add(mainClas);
		}
		tmp.put("classes", cls);
		return tmp;
	} 

}
