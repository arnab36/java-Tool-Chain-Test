package com.ibm.contracts.advisor.FieldGlass.Alert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.vo.FieldGlassOPOBJ;

public class FGDynamicAlert implements Constants{
	
	
	public static FieldGlassOPOBJ SLAAlertFG(
			String content){
		String statement1="timeliness of work products. the supplier shall be timely with their work product. this will be measured by the number of days late for project management work product.";
		String statement2="quality of work product. the supplier shall deliver quality work product. this will be measured by the completeness of project management work product.";
		String statement3="change management. the supplier shall provide version control management of project related documentation as identified by cisco pm. the supplier shall manage change management activities and shall ensure version control management will be in place for all project related documentation, including the rmt release playbook, performance/uat playbook, gate review docs, document templates, or as identified by cisco pm.";
		String statement4="project impact assessment. the supplier shall provide an impact assessment for any changes to upstream or downstream systems (applications, databases, servers, etc.) as identified by cisco pm.";
		
		int start = content.indexOf("Service Level Agreements"); //Taking all data between id "Payment term" and next header
		int end = 0;
		int fail=0;
		int pass=0;
		
		FieldGlassOPOBJ result = new FieldGlassOPOBJ();
		
		try{
		for (int i=start; ; i++){
			if(content.substring(i, i+16).equals("SLA Credit terms")){
				end = i;
				break;
			}
		}
		String text = content.substring(start, end);
		System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote("Service Level Agreement #") + "(.*?)" + Pattern.quote("<tr>")); //Extracts start date
		Matcher m = q.matcher(text);
		int count = 1;
		while(m.find())
		{
			String temp = m.group(1).toLowerCase().replaceAll("\\<.*?>","").trim();
			System.out.println(temp);
			count=count+1;
			int score1 = FuzzySearch.tokenSetRatio(statement1, temp); //even partialRatio can be used
			int score2 = FuzzySearch.tokenSetRatio(statement2, temp); //even partialRatio can be used
			int score3 = FuzzySearch.tokenSetRatio(statement3, temp); //even partialRatio can be used
			int score4 = FuzzySearch.tokenSetRatio(statement4, temp); //even partialRatio can be used
			System.out.println(score1 +" "+ score2 +" "+ score3 +" "+ score4);
			if((score1<90)&&(score2<90)&&(score3<90)&&(score4<90))
			{
				fail = fail+1;
				break;
			}
			else{
				pass = pass+1;
			}
		}
		result.setClassName("SLAAlert");
		if((fail>0)||(pass<2)){
			result.setFlag(true);
			result.setOutput("Service Level Agreements not properly populated");
		
		}
		else{
			result.setFlag(false);
			result.setOutput("Service Level Agreement properly populated");
		}
		}catch(Exception e){
			result.setClassName("SLAAlert");
			result.setFlag(true);
			result.setOutput("Alert raised :: Exception occured");
			e.printStackTrace();
		}	
		return result;
	}
	
	
	
	
	public static FieldGlassOPOBJ SLACreditTermAlertFG(
			String content){
		String doc="";
		String toMatch="Where Supplier fails to resolve the problem by the time defined in any corrective action plan, Supplier will issue a credit of of the monthly charge for each month that the SLA remains out of range. If Supplier continues to fail to resolve the problem, Supplier is in breach of contract and Cisco retains the right to terminate for cause.";
		toMatch = toMatch.toLowerCase().trim();
		int score  = 0;
		int start = content.indexOf("SLA Credit terms"); //Taking all data between id "Payment term" and next header
		int end = 0;
		
		FieldGlassOPOBJ result = new FieldGlassOPOBJ();
		
		try{
		for (int i=start; ; i++){
			if(content.substring(i, i+3).equals("<tr")){
				end = i;
				break;
			}
		}
		String text = content.substring(start, end);
		System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote("SLA Credit terms") + "(.*?)" + Pattern.quote("</tr>")); //Extracts start date
		Matcher m = q.matcher(text);
		while(m.find())
		{
			String temp = m.group(1).replaceAll("\\<.*?>","").trim();
			doc =temp.toLowerCase().trim();
			score = FuzzySearch.ratio(toMatch, doc); //even weightedRatio can be used
			System.out.println(score);
			
		}
		result.setClassName("SLACreditTerm");
		if (score<90)
		{

			result.setFlag(true);
			result.setOutput("There is a deviation in SLA Credit Term text. Please cross check");
		}
		else {

			result.setFlag(false);
			result.setOutput("No deviation in SLA Credit Term text.");
		
		}
		}catch(Exception e){
			result.setClassName("SLACreditTerm");
			result.setFlag(true);
			result.setOutput("Alert raised :: Exception occured");
			e.printStackTrace();
		}	
		return result;
	}
	
	public static FieldGlassOPOBJ PaymentMethodAlertFG(
			String content){
		int count  = 0;
		int start = content.indexOf("Statement of Work Payment Characteristics"); //Taking all data between id "Payment term" and next header
		int end = 0;
		
		FieldGlassOPOBJ result = new FieldGlassOPOBJ();
		
		try{
		for (int i=start; ; i++){
			if(content.substring(i, i+6).equals("</html")){
				end = i;
				break;
			}
		}
		String text = content.substring(start, end);
		System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote("Statement of Work Payment Characteristics") + "(.*?)" + Pattern.quote("</body>")); //Extracts start date
		Matcher m = q.matcher(text);
		while(m.find())
		{
			String temp = m.group(1);
			temp=temp.replaceAll("\\<.*?>","").trim();
			String temp1 =temp.toLowerCase();
			System.out.println(temp1);
			
			if(temp.contains("advance funds")||temp.contains("prepayment")||temp.contains("pre-payment"))
			{
				count = count + 1;
				break;
				//Raise alert
			}
		}
		result.setClassName("PrepaymentTermAlert");
		if (count>0)
		{
			result.setFlag(true);
			result.setOutput("Prepayment terms exist.");
		}
		else {
			result.setFlag(false);
			result.setOutput("Prepayment terms do not exist.");
		}
		}catch(Exception e){
			result.setClassName("PrepaymentTermAlert");
			result.setFlag(true);
			result.setOutput("Alert raised, Prepayment terms exist");
			e.printStackTrace();
		}	
		return result;
	}
	
	
	public static FieldGlassOPOBJ SpecialTermAlertFG(String content)
	{
		int count = 0;
		int start = content.indexOf("SPECIAL TERMS"); //Checks between id SpecialTerms and next header
		int end = 0;
		
		FieldGlassOPOBJ result = new FieldGlassOPOBJ();
		
		try {
			for (int i=start; ; i++){
				if(content.substring(i, i+3).equals("<tr")){
					end = i;
					break;
				}
			}
			String text = content.substring(start, end);
			//System.out.println(text);
			//String text1 = removeSpecialCharacters(text);
			Pattern q = Pattern.compile(Pattern.quote("SPECIAL TERMS") + "(.*?)" + Pattern.quote("</tr>")); //Extracts start date
			Matcher m = q.matcher(text);
			while(m.find())
			{
				String temp = m.group(1);
				temp=temp.replaceAll("\\<.*?>","").trim();
				System.out.println(temp);
				if(temp.contains("N/A") || temp.contains("n/a")|| temp.contains("none") || temp.contains("None") || temp.contains("NA") || temp.equals(""))
				{
					count = 1;
					break; //there are NA terms
				}
			}

			result.setClassName("SpecialTermAlert");
			if (count>0){
				result.setFlag(false);
				result.setOutput("Special term does not exist.");
			}
			else{
				result.setFlag(true);
				result.setOutput("Special terms exist.");
			}
		}catch(Exception e){
			result.setClassName("SpecialTermAlert");
			result.setFlag(true);
			result.setOutput("Alert raised :: Exception occured");
			}	
		return result;
	}	

	
}