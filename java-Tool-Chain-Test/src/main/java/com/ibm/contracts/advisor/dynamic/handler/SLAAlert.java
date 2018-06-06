package com.ibm.contracts.advisor.dynamic.handler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.util.Util;

public class SLAAlert implements Constants{
	
	public static boolean getSLAAlert(String sowHTML,JSONObject template){
		int start = sowHTML.indexOf("Service Level Agreement #1");
		int end = 0;
		int warning=0;
		for (int i=start; ; i++){
			if(sowHTML.substring(i, i+18).equals("AcceptanceCriteria")){
				end = i;
				break;
			}
		}
		String text = sowHTML.substring(start, end);
		if ((text.toLowerCase().contains("on-going"))||(text.toLowerCase().contains("on going"))||(text.toLowerCase().contains("ongoing"))){
			warning=1;
		}
		
		
		JSONArray ansUnit=(JSONArray)template.get("answer_units");
		JSONArray cont=null;
		for(int i=0;i<ansUnit.size();i++){
			if(((JSONObject)ansUnit.get(i)).get("title").toString().contains("Service Level Agreement")){
				cont=(JSONArray)((JSONObject)ansUnit.get(i)).get("content");
			}
		}
		System.out.println(cont.toJSONString());
		ArrayList<String> tempList=new ArrayList<String>();
		
		String temt="";
		boolean take=false;
		for(int i=0;i<cont.size();i++){
			String temtext=((JSONObject)cont.get(i)).get("text").toString();
			temtext=temtext.replaceAll("\\(optional\\)", "");
			if(take==true)
				temt=temt+temtext;
			if(FuzzySearch.ratio(temtext,"Service Level Agreement #1:")>90)
				take=true;
			if(temtext.contains("Frequency") || temtext.contains("frequency")){
				take=false;
				System.out.println("gathering::"+temt);
				tempList.add(temt);
				temt="";
			}
			//System.out.println(temtext+"=====");
			
		}
		
		String[] ele=SLAAlertJsoup(sowHTML);//fetching specific part containing SLA
	System.out.println(ele.length);
	//System.out.println(tempList.size());
	String retVal=checkOneByOne(ele,tempList);//checkAllTag
	System.out.println(retVal+"||||||");
	SLAPresent=false;
	found=0;
	
	if((retVal==null )&&(warning==0)){
		return false;//false mean no alert
	}
	else
		return true;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		/*StringBuilder contentBuilder = new StringBuilder();
		String fileLoc="C:/Users/IBM_ADMIN/Documents/project 8/cisco/MS/MS V4 - 1230360045381-good";
		//MS V4 - 1230360045381-good.html_StaticRemoved
		try {
			BufferedReader inputHTML = new BufferedReader(new FileReader(fileLoc+".html_StaticRemoved.html")); //201937907-OK-AS1.html //6240053615-great.html
			String str;
			while((str = inputHTML.readLine())!= null)
			{
				contentBuilder.append(str);
			}
			inputHTML.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String str = contentBuilder.toString();
		
		//extracting the SLA part from the template . This will be removed and arnab code will be added
		JSONObject tem=Util.getJSONFromFile("C:/Users/IBM_ADMIN/Documents/project 8/cisco/template/MS_Static_Template_V5.json");
		System.out.println("===========================================");*/
		
		String str= new String(Files.readAllBytes(Paths.get("C:/Users/IBM_ADMIN/Documents/project 8/cisco/MS/1150042181 MS.pdfstaticRemoved.html")));
		
		String path="C:/Users/IBM_ADMIN/Documents/project 8/cisco/";
		PostNLCPropSet.staticTemplatefile = Util
				.getJSONFromFile(path+"/template/MS_Static_Template_V5.json");
		
		
		//System.out.println(str);
		System.out.println(getSLAAlert(str,PostNLCPropSet.staticTemplatefile));
	
			}
	
	public static boolean SLAPresent=false;
	
	private static String checkOneByOne(String[] ele, ArrayList<String> tempList){
		boolean start=false;
		boolean foundSLA=false;
		String appendT="";
		for(int i=0;i<ele.length;i++){
			ele[i]=ele[i].replaceAll("\\(optional\\)", "");
			
			if(start==true){
				appendT=appendT+ele[i];
			}
			if(ele[i].contains("Frequency") || ele[i].contains("frequency")){
				start=false;
				int match=0;
				String high="";
				for(String temText :tempList){
					int matchPer=FuzzySearch.tokenSetRatio(temText.toLowerCase(),appendT);
					if(matchPer >match){
						match=matchPer;
						high=temText;
						System.out.println(match+"====="+temText);
					}
					if(match>=80)
						break;
				}
				if(match<80){
					System.out.println("final match::"+match);
					System.out.println("template::"+high);
					
					return ele[i];
				}
				appendT="";
			}
			
			
			if(FuzzySearch.ratio(ele[i],"Service Level Agreement #1:")>80){
				start=true;
				System.out.println("start taking::"+ele[i]);
				foundSLA=true;
			}
		}
		if(foundSLA==false)
			return "SLA not found";
		return null;
	}
	
	
	public static int found=0;
	
  private static void htmlTags(Elements elements, ArrayList<String> sb) {
	  if(found==2)
		  return;
	  
	        for(Element el:elements) {
	        	//System.out.println(el.ownText());
	           
	            if(el.tagName().equalsIgnoreCase("table") && found==1)
	            	continue;
	            if(el.tagName().equalsIgnoreCase("h2") && found==1){
	            	found=2;
	            	return ;
	            }
	            	
	            if(el.attr("id").equalsIgnoreCase("SLA"))
	            	found=1;
	            
	            if(found==1){
	            	if(el.ownText().trim().length()>10){
	            		String[] tmp=el.ownText().trim().split("[.;:]\\s+");
	            		for(String s:tmp)
	            		sb.add(s.toLowerCase());
		            
	            	
	 	            }
	            }
	            	
	           
	            htmlTags(el.children(), sb);
	          
	        }
	    }
	
	private static String[] SLAAlertJsoup(String s){
		 Document doc = Jsoup.parse(s);
		 ArrayList<String> sb=new ArrayList<String>();
		 htmlTags(doc.children(),sb);
		 String[] sendit=new String[sb.size()];
		 for(int i=0;i<sb.size();i++)
			 sendit[i]=sb.get(i);
		 return sendit;
	}
	
	
	
	
}
