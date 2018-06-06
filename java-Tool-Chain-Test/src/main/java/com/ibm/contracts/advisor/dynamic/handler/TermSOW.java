package com.ibm.contracts.advisor.dynamic.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.util.IdentifyDate;

public class TermSOW {

	
	public static boolean getTermSOW(String str, JSONObject dynamicPopUpContent) {
		System.out.println(str);
		Document doc = Jsoup.parse(str);
		String format=IdentifyDate.formatDetect(doc.getAllElements());
		ArrayList<String> alldates = new ArrayList<String>();

		AcceptanceMilestones.returnDates(doc.children(), alldates,
				"PaymentMethod");
		AcceptanceMilestones.found=0;
		String targetText = getHContent(doc, "TermSOW");
		System.out.println(alldates.size()+":::::::size::::::::::::"+alldates.size());
		return validateDate(alldates.get(0), alldates.get(alldates.size() - 1),
				targetText,dynamicPopUpContent,format);

	}

	public static void main(String[] args) {

		StringBuilder contentBuilder = new StringBuilder();
		String fileLoc = "C:/Users/IBM_ADMIN/Documents/project 8/cisco/SOWs(1)/";
		try {
			BufferedReader inputHTML = new BufferedReader(new FileReader(
					fileLoc + "5150020843.docx_staticRemoved.html")); // 201937907-OK-AS1.html
																			// //6240053615-great.html
			String str;
			while ((str = inputHTML.readLine()) != null) {
				contentBuilder.append(str);
			}
			inputHTML.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String str = contentBuilder.toString().toLowerCase();
		str=str.replaceAll("<b>|</b>", "");
		Document doc = Jsoup.parse(str);
		String format=IdentifyDate.formatDetect(doc.getAllElements());
		
		ArrayList<String> alldates = new ArrayList<String>();

		AcceptanceMilestones.returnDates(doc.children(), alldates,
				"paymentmethod");
		AcceptanceMilestones.found = 0;
		for (String d : alldates)
			System.out.println("paymentmethod--------------" + d);

		String targetText = getHContent(doc, "termsow");
		System.out.println(alldates.size()+":::::::size::::::::::::"+alldates.size());
		JSONObject j=new JSONObject();
		System.out.println(validateDate(alldates.get(0), alldates.get(alldates.size() - 1),
				targetText,j,format)); 

	}

	public static String getHContent(Document doc, String id) {
		Elements targetSet = doc.getElementsByAttributeValue("id", id);

		String targetText = "";
		Element target = targetSet.get(0);
		System.out.println(target);
		while (true) {
			target = target.nextElementSibling();
			System.out.println(target.tagName());
			if (target.tagName().equals("h1") || target.tagName().equals("h2"))
				break;
			targetText = targetText + target.text();
		}
		return targetText;
	}

	private static boolean validateDate(String start, String end,
			String targetText, JSONObject dynamicPopUpContent, String format) {
		System.out.println("start::" + start + " end::" + end);
		String[] txtChunk = targetText.split("the");
		ArrayList<String> termDate = new ArrayList<String>();
		try {
			for (int i = 0; i < txtChunk.length; i++) {
				// System.out.println(ch);
				String date = IdentifyDate.extractDate(txtChunk[i]);
				/*if (date == null)
					date = IdentifyDate.extractDateWOyear(txtChunk[i]);*/
				if (date != null)
					termDate.add(date);

			}
			/*if (termDate.size() >= 2) {
				for (String d : termDate) {
					System.out.println("got::" + d);
					if (d.equals(start))
						start = "";
					else if (d.equals(end))
						end = "";
				}
			}*/
			/*System.out.println(termDate.get(0)+"||||||"+termDate.get(1));
			Date termStart=getDate(termDate.get(0));
			Date termEnd=getDate(termDate.get(1));
			System.out.println("payment ::"+termStart+" termDate::"+termEnd);*/
			if(termDate.size()<2){
				dynamicPopUpContent.put("TermSOW", "Didnt find date in termSOW section");
				return false;
			}
				
			System.out.println("termsow date::"+termDate);
			if(getDate(start,format).compareTo(getDate(termDate.get(0),format))>=0)
				start="";
			
			if(getDate(termDate.get(1),format).compareTo(getDate(end,format))>=0)
				end="";
			
			System.out.println("start::"+start+" end::"+end);
			
			if (start.equals("") && end.equals("")){
				dynamicPopUpContent.put("TermSOW", targetText);
				return true;
			}
			else{
				dynamicPopUpContent.put("TermSOW", "Either Start Date or End Date is not filled or doesn't match with the Payment Table");
				return false;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			dynamicPopUpContent.put("TermSOW", "Either Start Date or End Date is not filled or doesn't match with the Payment Table");
			return false;	
		}
		
		
	}
	
	/*private static int getDate(String s,String s2){
		System.out.println("convert ::"+s);
		String [] dateParts = s.split("\\|");
		String month1 = dateParts[0];
		String day1 = dateParts[1];
		String year1 = dateParts[2];
		//System.out.println("month:: " + month +  "day:: " + day + "year:: " + year);

		String [] dateParts2 = s2.split("\\|");
		String month2 = dateParts2[0];
		String day2 = dateParts2[1];
		String year2 = dateParts2[2];  
		
		if(year2>year1)
			return 1;
		if(year2<year1)
			return -1;
		else{
			
		}
				
	       
	       return 1;
	}
	*/
	private static Date getDate(String s, String format2) throws ParseException{
		//System.out.println("convert ::"+s);
		String [] dateParts = s.split("\\|");
		String day1 = dateParts[0];
		String month1 = dateParts[1];
		String year1 = dateParts[2];
		if(day1.length()==1)
			day1="0"+day1;
		if(month1.length()==1)
			month1="0"+month1;
		
		if(format2.equalsIgnoreCase("IND")){
			SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
			Date parsedDate = formatter.parse(month1+"-"+day1+"-"+year1);
			return parsedDate;
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Date parsedDate = formatter.parse(month1+"-"+day1+"-"+year1);
		return parsedDate;
	}

}
