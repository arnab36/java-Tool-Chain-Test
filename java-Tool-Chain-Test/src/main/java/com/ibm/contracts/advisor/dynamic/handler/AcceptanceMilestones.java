package com.ibm.contracts.advisor.dynamic.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.util.IdentifyDate;

public class AcceptanceMilestones {

	public static void main(String[] args) {
		
		
		StringBuilder contentBuilder = new StringBuilder();
		String fileLoc="C:/Users/IBM_ADMIN/Documents/project 8/cisco/new/";
		try {
			BufferedReader inputHTML = new BufferedReader(new FileReader(fileLoc+"5550039437done.docx_Converted.html")); //201937907-OK-AS1.html //6240053615-great.html
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
		
		
		
		System.out.println(checkAcceptanceMilestone(str,new JSONObject()));
//Payment
		//AcceptanceMilestones
	}
	
	public static int found=0;
	
	 public static void returnDates(Elements elements, ArrayList<String> sb,String id) {
		  if(found==3)
			  return;
		  
		        for(Element el:elements) {
		        	
		        	
		           
		            if(el.tagName().equalsIgnoreCase("table") && found==1){
		            	System.out.println(el.ownText()+"==start search");
		            	found=2;//start search inside table
		            	//System.out.println(el.childNodeSize());
		            	 returnDates(el.children(), sb,id);
		            	 break;
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
		            	
		            
		            if(found==2){
		            	if(el.tagName().equals("td")){
		            			System.out.println("check date here::"+el.text().trim());
			            		String date=IdentifyDate.extractDate(el.text().trim());
			            		/*if(date==null)
			            			date=IdentifyDate.extractDateWOyear(el.text().trim());*/
			            		if(date!=null)
			            			sb.add(date);
			            		
			            		continue;
				           
			 	            }
		            	else{
		            	if(el.ownText().trim().length()>6){
		            		
		            		System.out.println("check date here::"+el.ownText().trim());
		            		String date=IdentifyDate.extractDate(el.ownText().trim());
		            		/*if(date==null)
		            			date=IdentifyDate.extractDateWOyear(el.ownText().trim());*/
		            		if(date!=null)
		            			sb.add(date);
		            	
		 	            }
	            		}
	            		
	            			
	                }
		            	
		           
		            returnDates(el.children(), sb,id);
		          
		        }
		    }
		
		public static boolean checkAcceptanceMilestone(String s, JSONObject dynamicPopUpContent){//return true if all matches means green
			 Document doc = Jsoup.parse(s);
			 ArrayList<String> payment=new ArrayList<String>();
			 ArrayList<String> milestone=new ArrayList<String>();
			 found=0;
			 
			 
			///s=s.replaceAll("\\<b\\>", replacement)
			 
			 returnDates(doc.children(),payment,"PaymentMethod");
			 found=0;
			 returnDates(doc.children(),milestone,"AcceptanceMilestones");			
			 found=0;
			 for(String p:payment)
				 System.out.println("got date in payment ::"+p);
			 for(String m:milestone)
				 System.out.println("got date in milestone ::"+m);
			 //
			 
			 System.out.println("milestone dates size::"+milestone.size());
			 if(milestone.size()==0){
				 getMileStoneContent(doc,"AcceptanceMilestones",milestone);
				 dynamicPopUpContent.put("AcceptanceMilestones", "Acceptance milestone table is not alligned with payment table. "
					 		);
				 if(milestone.size()==0)
				 return false;
			 }
				
			 if(milestone.size()>payment.size())
			 {
				 for(String p:payment){
					 System.out.println("checking for::"+p);
					 if(!milestone.contains(p)){
						 if(dynamicPopUpContent!=null)
						 dynamicPopUpContent.put("AcceptanceMilestones", "Acceptance milestone table is not alligned with payment table. "
						 		+ "No match found in milestone table for "+p);
						 return false;
					 }
						 
				 }
			 }
			 if(milestone.size()<=payment.size())
			 {
				 for(String m:milestone){
					 System.out.println("checking for::"+m);
					 if(!payment.contains(m)){
						 if(dynamicPopUpContent!=null)
						 dynamicPopUpContent.put("AcceptanceMilestones", "Acceptance milestone table is not alligned with payment table. "
							 		+ "No match found in payment table for "+m);
						 return false;
					 }
						
				 }
			 }
			 
			 if(dynamicPopUpContent!=null)
			 dynamicPopUpContent.put("AcceptanceMilestones", "Table 3c : Acceptance milestone, is alligned with payment table.");	
			 return true;
		}
		
		public static void getMileStoneContent(Document doc, String id, ArrayList<String> milestone) {
			Elements targetSet = doc.getElementsByAttributeValue("id", id);

			String targetText = "";
			Element target = targetSet.get(0);
			//System.out.println(target);
			while (true) {
				target = target.nextElementSibling();
				System.out.println(target.tagName());
				if (target.tagName().equals("h1") || target.tagName().equals("h2"))
					break;
				//if(target.tagName().equals("table"))
					targetText = targetText + target.text();
			}
			System.out.println(":::::"+targetText);
			String[] allDate = targetText.toLowerCase().split("(?=((jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*))");
			
			for(String a:allDate){
				//System.out.println("==========="+a);
				String extracted=IdentifyDate.extractDate(a);
				if(extracted!=null)
					milestone.add(extracted);
			//	System.out.println(extracted);
			}
				
			
		}
		
		private static Date getDate(String s){
			String datestr = "10|6|2017";
			String [] dateParts = datestr.split("\\|");
			String month = dateParts[0];
			String day = dateParts[1];
			String year = dateParts[2];
			System.out.println("month:: " + month +  "day:: " + day + "year:: " + year);

		        Date d1=new Date(); 
		        d1.setDate(Integer.parseInt(day));
		        d1.setMonth(Integer.parseInt(month));
		        d1.setYear(Integer.parseInt(year));

		       return d1;
		}
}
