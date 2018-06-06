package com.ibm.contracts.advisor.dynamic.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ibm.contracts.advisor.util.IdentifyDate;

public class AcceptenceCriteria {

	public static void main(String[] args) {
		StringBuilder contentBuilder = new StringBuilder();
		String fileLoc="C:/Users/IBM_ADMIN/Documents/project 8/cisco/SOWs(1)/";
		try {
			BufferedReader inputHTML = new BufferedReader(new FileReader(fileLoc+"5050015774MP.pdf_staticRemoved.html"));//201937907-OK-AS1.html //6240053615-great.html
			//201997017.docx_Converted.html //5500008247done.pdf_Converted.html //201997239.pdf_Converted.html
			String str;
			while((str = inputHTML.readLine())!= null)
			{
				contentBuilder.append(str);
			}
			inputHTML.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String str = contentBuilder.toString().toLowerCase();
		Document doc = Jsoup.parse(str);
		
		String targetText=TermSOW.getHContent(doc,"AcceptanceCriteria");
		System.out.println("===========================");
		System.out.println(targetText);
		System.out.println(validateDates(targetText,new JSONObject()));
	}
	
	public static boolean isAcpCriMatchin(String str, JSONObject dynamicPopUpContent){
		Document doc = Jsoup.parse(str);
		String targetText=TermSOW.getHContent(doc,"AcceptanceCriteria");
		System.out.println("===========================");
		System.out.println(targetText);
		return validateDates(targetText,dynamicPopUpContent);
		
	}

	private static boolean validateDates(String targetText, JSONObject dynamicPopUpContent) {
		System.out.println(targetText);
		List<String> allDay = IdentifyDate.getAllSpecialString(targetText, "((will have)|(have)|(within)|(first))[^\"]{3,20}((business)|(after)|(working)|(week)|(weeks))");
		
		if(allDay.size()<3){
			
			System.out.println(allDay.size()+ " " + "less than 3");
			for(int i=0;i<allDay.size();i++){
			    System.out.println(allDay.get(i));
			} 
			dynamicPopUpContent.put("AcceptanceCriteria", "At least one of the acceptance criteria is not filled");
			return false;
		}
			
		for(String s:allDay){
			s=s.replaceAll("(have)|(within)|(business)|(working)|(first)|(week)|(weeks)", "").trim();
			System.out.println(s);
			if(s.contains("zero") | s.equals("0")){
				dynamicPopUpContent.put("AcceptanceCriteria", "At least one of the acceptance criteria is not filled");
				return false;
			}
				
		}

		dynamicPopUpContent.put("AcceptanceCriteria",targetText);
		System.out.println(targetText);
		return true;
		
	}

}
