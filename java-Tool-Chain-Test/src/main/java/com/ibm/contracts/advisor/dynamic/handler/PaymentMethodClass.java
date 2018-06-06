package com.ibm.contracts.advisor.dynamic.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.util.IdentifyDate;

public class PaymentMethodClass {

	//MP : date validation with milestone table  AND Expenses (total from section 8c) amount match AND 8b+8c=total 

	//MS Both start and end|due date present in payment AND Expenses (total from section 8c) amount match AND 8b+8c=total
	
	public static boolean checkPaymentMethod(String str,String docType, JSONObject dynamicPopUpContent){
		//System.out.println(getDates(str));
		str=str.replaceAll("(<b>|</b>)", "");
				boolean dateCheck=false;
				String msg="";
				if(docType.equalsIgnoreCase("Managed Services")){
					Document doc = Jsoup.parse(str);
					returnDates(doc.children(),"PaymentMethod");
					System.out.println("got text ::"+searchHere);
					if(searchHere.indexOf("start date")>-1 && (searchHere.indexOf("end date")>-1 || searchHere.indexOf("due date")>-1)){
						dateCheck=true;	
						msg=searchHere;
					}
											
				}
				else{
					 dateCheck=AcceptanceMilestones.checkAcceptanceMilestone(str,dynamicPopUpContent);
					System.out.println("milestone::"+dateCheck);
				}
				
				boolean expenses=Expenses.checkExpense(str,dynamicPopUpContent);
				boolean sumMatch=checkSum(str);
				
				System.out.println("expenses::"+expenses);
				System.out.println("milestone::"+dateCheck);
				System.out.println("summation ::"+sumMatch);
				found=0;
				searchHere=null;
				if(expenses==true && dateCheck==true && sumMatch==true){
					if(msg.equals(""))
						dynamicPopUpContent.put("PaymentMethod", "Expenses mentioned in the section align with Payment Table.");
					else
						dynamicPopUpContent.put("PaymentMethod", msg);
					return true;
				}
				else{
				// Commented and modified as per Prashant's instructions on 14.11.2017
					//as per 27.12
					if (docType.equalsIgnoreCase("Managed Services")){
				dynamicPopUpContent.put("PaymentMethod", "Non-Alignment with Expenses");
					}
					else{
						dynamicPopUpContent.put("PaymentMethod", "Dates are not aligned with the Milestone Table.");
					}
				
		return false;
				}
	}
	
	public static void main(String[] args) {
		//String docType="MS";
		StringBuilder contentBuilder = new StringBuilder();
		String fileLoc="C:/Users/IBM_ADMIN/Documents/project 8/cisco/SOWs(1)/";
		try {
			BufferedReader inputHTML = new BufferedReader(new FileReader(fileLoc+"5150020491done.pdf_Converted.html")); //201937907-OK-AS1.html //6240053615-great.html
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
		
		//System.out.println(checkPaymentMethod(str,docType,null));
		//System.out.println(checkSum(str));
		JSONObject j=new JSONObject();
		System.out.println(checkPaymentMethod(str,"Managed Projects",j));
		
		
		
	}
	
	public static int found=0;
	
	public static String searchHere=null;

	 private static void returnDates(Elements elements, String id) {
		 //this is for MS only to check if start date due date present
		  if(found==3)
			  return ;
		  
		        for(Element el:elements) {
		        	
		        	
		           
		            if(el.tagName().equalsIgnoreCase("table") && found==1){
		            	System.out.println(el.ownText()+"==start search");
		            	
		            	searchHere= el.text();
		            	found=3;
		            	
		            }
		            	
		            if(el.tagName().equalsIgnoreCase("h2") && found!=0){
		            	System.out.println(el.ownText()+"==stop search search");
		            	found=3;//stop search
		            	return ;
		            }
		            	
		            if(el.attr("id").equalsIgnoreCase(id)){
		            	found=1;
		            	System.out.println(el.ownText()+"==found id");
		            }
		            	
		            
		            	
		           
		             returnDates(el.children(),id);
		          
		        }
		    }
	
	private static boolean checkSum(String str){
		str=str.replaceAll("<b>|</b>", "");
		Document doc = Jsoup.parse(str);
		 return matchTableContent(doc,"paymentmethod");
	
	
	}
	public static boolean matchTableContent(Document doc, String id) {
		Elements targetSet = doc.getElementsByAttributeValue("id", id);

		String targetText = "";
		Element target = targetSet.get(0);
		//System.out.println(target);
		while (true) {
			target = target.nextElementSibling();
			//System.out.println(target.tagName());
			if (target.tagName().equals("h1") || target.tagName().equals("h2"))
				break;
			if(target.tagName().equals("table"))
				targetText = targetText + target.text();
		}
		
		String part1= targetText.substring(targetText.indexOf("subtotal of 8b")+"subtotal of 8b".length(), targetText.indexOf("expenses "));
		System.out.println("part1:::::"+part1);
		List<String> allAmt = IdentifyDate.getAllSpecialString(part1, "((\\d*,?)+\\d+\\.?\\d+)");
		int sum1=0;
		for(String amt:allAmt){
			sum1=sum1+Integer.parseInt(amt.replaceAll("[\\.,]",""));
		}
		
		String part2= targetText.substring(targetText.indexOf("expenses (total from section 8c)")+"expenses (total from section 8c)".length(), targetText.indexOf("total of 8b and 8c"));
		System.out.println("part2:::::"+part2);
		List<String> allAmt2 = IdentifyDate.getAllSpecialString(part2, "((\\d*,?)+\\d+\\.?\\d+)");
		int sum2=0;
		for(String amt:allAmt2){
			sum2=sum2+Integer.parseInt(amt.replaceAll("[\\.,]",""));
		}
			
		String part3= targetText.substring(targetText.indexOf("total of 8b and 8c")+"total of 8b and 8c".length());
		System.out.println("part3:::::"+part3);
		List<String> allAmt3 = IdentifyDate.getAllSpecialString(part3, "((\\d*,?)+\\d+\\.?\\d+)");
		int sum3=0;
		for(String amt:allAmt3){
			sum3=sum3+Integer.parseInt(amt.replaceAll("[\\.,]",""));
		}
		if(sum1+sum2==sum3)
			return true;
			
		return false;
	}
}
