package com.ibm.contracts.advisor.FieldGlass.DYNAMIC;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;
//import com.ibm.contracts.advisor.util.IdentifyDate;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
 









import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
public class TreeHandler implements Constants{

	public static void main(String[] args) {
		StringBuilder contentBuilder = new StringBuilder();
		try {
			BufferedReader inputHTML = new BufferedReader(new FileReader("C:\\Users\\IBM_ADMIN\\Desktop\\demo_user2_1515645955446_CSCOTQ00109377_MS_Great.html")); 
			//C:\\Users\\IBM_ADMIN\\Desktop\\FieldGlass\\FieldGlass\\FieldGlassHTML\\CSCOTQ00109377.pdf_Converted.html
			//C:\\Users\\IBM_ADMIN\\Desktop\\IBM\\CISCO\\cisco-samples\\MS\\MS V5 - 201947668-great.docx
			//ColorCoded_SOW Analyzer MP CSCOTQ00140502(1).pdf
			//5500008247.pdf_Converted.html
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
		
		//PaymentMethodAlert(str); 
		//SpecialTermAlert(str); 
		//OtherResourcesAlert(str); 
		
		//GeneralTerms(str);
		//TypeValidation(str); //HTML needs to be correct. with id SOW
		//WorkLocation(str);
		//ThirdParty(str);
		//Expenses(str);
		//WorkProduct(str);
		//SLA(str);
		//CiscoPreexistingTerms(str);	
		//TermSOW(str);
		//TrialExpenses(str);
		//Scope(str);
		//TypeValidationNONFG(str);
		//TrialAcceptanceCriteria(str);
		
		
		//SpecialTermAlertFG(str);
		//PaymentMethodAlertFG(str);
		//SLACreditTermAlertFG(str);
		//SLAAlertFG(str);
		//TermSowFG(str);
		//ExpensesFG(str);
		//PaymentMethodFG(str);
		//ScopeFG(str);
		//SLAFG(str);
		//trialWho(str);
		//WorkProductFG(str);
		//LiquidatedDamagesFG(str);
		//LiquidatedDamages(str);
		//trialPayment(str);
		//trialWP(str);
		//SKPFG(str);
		//GeneralFG(str);
	}
	
	public static String GeneralFG(String s){
		String who = "";
		int startWho = s.indexOf("SOW Supplier Oracle Name");
		int endWho = 0;
		//start
		for (int j=startWho; ; j++){
			if(s.substring(j, j+7).equals("GENERAL")){
				endWho = j;
				break;
			}
		}
		String textWho = s.substring(startWho, endWho);
		//System.out.println(textWho);
		Pattern r = Pattern.compile(Pattern.quote("SOW Supplier Oracle Name") + "(.*?)" + Pattern.quote("1)"));
		Matcher t = r.matcher(textWho);
		while (t.find()) {
			who = t.group(1).replaceAll("\\<.*?>","").trim(); 
			System.out.println(who);
		}
		return who;
		
	}
			
	
	
private static void TypeValidationNONFG(String s) {
		s = s.toLowerCase();
		Document doc = Jsoup.parse(s);
		Elements alle = doc.getElementsByTag("h1");
		System.out.println(alle.size());
		alle.addAll(doc.getElementsByTag("h2"));
		System.out.println(alle.size());

		int isMP1 = 0, isMS1 = 0;
		for (Element e : alle) {
			if (e.ownText().contains("sow")) {
				System.out.println(e.text());
				e = e.nextElementSibling();
				if (e.text().contains("managed project"))
					isMP1++;
				if (e.text().contains("managed service"))
					isMS1++;
			}

			if (e.ownText().contains("acceptance criteria")) {
				isMP1++;
			}
			if (e.ownText().contains("service level agreement")) {
				isMS1++;
			}

		}

		System.out.println(s.length());

		if (isMP1 == 0 && isMS1 == 0) {
			if (s.contains("managed project")
					&& s.contains("acceptance criteria"))
				System.out.println("MP");
			else
				System.out.println("MS");
		}

		if (isMP1 > isMS1)
			System.out.println("MP");
		else
			System.out.println("MS");
	}

	
	
	private static void SKPFG(String k){
		
		int flagcontact=0;
		int flagname=0;
		int flagposition=0;
		String finaldisplay="";
		String temp="";
		try {
			int start1 = k.indexOf("<p>Supplier Key Personnel");
			int end1 = 0;
			for (int i=start1; ; i++){
				if(k.substring(i, i+5).equals("</tr>")){
					end1 = i;
					break;
				}
			}

			String text =k.substring(start1, end1);
			System.out.println(text);
			
			text = text.replaceAll("\\<.*?>","").trim();
			
			Pattern a = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b"); 
			System.out.println(text);
			Matcher b = a.matcher(text);
			String tempemail="";
			while(b.find()){
					System.out.println(b.group());
					String email = b.group();
					if (!email.equals("")){
						flagcontact =1;
						tempemail=tempemail+ " " + email;
						
					}
				}
			finaldisplay =finaldisplay +" Email ID : " + tempemail + ";";
			String tempphone="";
			 Pattern p = Pattern.compile("\\d+");
		        Matcher m = p.matcher(text);
		        while(m.find()) {
		            System.out.println(m.group());
		            String phone = m.group();
		            if (!phone.equals("")){
						flagcontact =1;
						tempphone = tempphone + " " + phone;
					}
		        }

				finaldisplay =finaldisplay + " Phone no : " + tempphone + ";";
				 //String serializedClassifier = "C://Users//IBM_ADMIN//Downloads//stanford-ner-2014-01-04//stanford-ner-2014-01-04//classifiers//english.all.3class.distsim.crf.ser.gz";
		        String serializedClassifier="english.all.3class.distsim.crf.ser.gz";
		       
		        CRFClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
		        List<List<CoreLabel>> classify = classifier.classify(text);
		        for (List<CoreLabel> coreLabels : classify)
		        {
		        for (CoreLabel coreLabel : coreLabels)
		        {
		        String word = coreLabel.word();
		        String category = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
		        if(!"O".equals(category))
		        {
		        	
		        	if(category.equalsIgnoreCase("PERSON")){
		        		temp = temp +" "+ word;
		        		flagname=1;
		        	}
		        }
		        }
		        }
		        finaldisplay = finaldisplay + " Name(s): " +  temp;
		        
		        String[] list = {"Program Manager","Service Delivery Manager","account manager","Senior Procurement Manager","Procurement Manager","Client Delivery Manager","Delivery Manager","Senior Manager","Supplier Engagement Manager","Project Manager", "Manager","Sr. Mgr.","EVP Operations","PM","Associate Partner","Practice Leader", "Partner", "Project Management Leads","Project Lead","Supplier Lead","Lead","Senior Consultant","Consultant"};
		        for (int i=0;i<list.length;i++){
		        	text=text.toLowerCase();
		        	if(text.contains(list[i])){
		        		flagposition=1;
		        		finaldisplay = finaldisplay + " Designation: " + list[i];
		        		System.out.println(list[i]);
		        		break;
		        	}
		        }
		        System.out.println(finaldisplay);
		        System.out.println(flagname + " "+ flagcontact + " "+ flagposition);
		        if((flagposition==1)&&(flagname==1)&&(flagcontact==1)){

			        System.out.println("Supplier Key Personnel class is green");
		        }
		        else{

			        System.out.println("Supplier Key Personnel Class is red");
		        }
        }		
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}
		
		    
	}
	
	private static void trialWP(String s){
		int flag = 0;
		int alert = 0;
		int flag2 = 0;
		String finaldisplay = "";
		String tempdisplay = "";
		int count = 0;
		String temp1 = "";
		String temp2 = "";
		String temp3 = "";
		String temp4 = "";
		String temp11 = "";
		int start = 0;
		
		
		
		
		
	int counttable=0;
	int countval=0;
	String finalstring="";
		
		int start1 = s.indexOf("id=\"WorkProduct\"");
		int end1 = 0;
		try{
		for (int i=start1; ; i++){
			if(s.substring(i, i+25).equals("WorkProductSpecifications")){
				end1 = i;
				break;
			}
		}

		String text = s.substring(start1, end1);
		System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote("<tbody>") + "(.*?)" + Pattern.quote("</tbody>")); 
		Matcher n = q.matcher(text);
		while(n.find()){
			String temp = n.group(1);
			temp = temp.replaceAll("\\<.*?>","").trim();
			counttable = counttable +1;
			System.out.println(temp);
			System.out.println(counttable);	
		}
		if(counttable>1){
			Pattern a = Pattern.compile(Pattern.quote("<p>") + "(.*?)" + Pattern.quote("</p>")); 
			Matcher b = a.matcher(text);
			while(b.find()){
				temp2 = b.group(1);
				temp2 = temp2.trim();
				System.out.println(temp2);
				if(!temp2.equals(""))
				{
					countval = countval+1;
					finalstring = finalstring + "  "+temp2;
					System.out.println(countval);
				}
			}
			if (countval>4)
			{
				finalstring = "Atleast one row is completely populated. Table Content:" + "\n" + finalstring;
				System.out.println("WP green");
				System.out.println(finalstring);
			}
			else{
				System.out.println("WP class red");
			}	
		}
		else{
			//Do non nested working
			int starttemp = s.indexOf("id=\"WorkProduct\"");
			while (starttemp >= 0) {
				if (starttemp > -1)
					start = starttemp;
				starttemp = s.indexOf("id=\"WorkProduct\"", start + 1);
			}
			int end=0;
			int firstcount = 0;
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h2")) {
					end = i;
					break;
				}
			}
			String textwp = s.substring(start, end);
			// text = removeSpecialCharacters(text);
			Pattern x = Pattern.compile(Pattern.quote("<tr>") + "(.*?)"
					+ Pattern.quote("</tr>"));
			Matcher y = x.matcher(textwp);

			while (y.find()) {

				temp1 = y.group().trim();
				Pattern c = Pattern.compile(Pattern.quote("<td>") + "(.*?)"
						+ Pattern.quote("</td>"));
				Matcher d = c.matcher(temp1);
				while (d.find()) {
					System.out.println(d.group(1));
					String temp7 = d.group(1).trim().toLowerCase();
					if (temp7.contains("phase/gate")
							|| temp7.contains("deliverable")
							|| temp7.contains("format")
							|| temp7.contains("location")) {
						firstcount = firstcount + 1;
					}
				}
				System.out.println(firstcount);
				Pattern p = Pattern.compile(Pattern.quote("<td>") + "(.*?)"
						+ Pattern.quote("</td>"));
				Matcher m = p.matcher(temp1);
				flag = 0;
				flag2 = 0;

				// System.out.println("FLAGS" + flag + flag2 + n.group(1));
				while (m.find()) {
					tempdisplay = Jsoup.parse(m.group(1)).text() + "\t\t\t"; // for
																				// table
																				// format
					finaldisplay = finaldisplay + tempdisplay;
					count = count + 1;
					if ((count % 4) == 0) {
						finaldisplay = finaldisplay + "\n";
					}
					temp2 = m.group(1).replaceAll("<p dir=\"ltr\">&nbsp;</p>", "").replaceAll("&nbsp;", "").trim();
					temp2 = temp2.replaceAll(" ", "").trim(); // ????? being
																// used
					System.out.println("Before :: " + temp2);
					temp2 = temp2.replaceAll("[^\\x00-\\x7F]", "");
					temp2 = temp2.trim();
					temp3 = Jsoup.parse(temp2).text().toLowerCase();
					System.out.println("After :: " + temp3);
					if (temp3.equals("") || temp3.equals("na")
							|| temp3.equals("n/a") || temp3.equals("none")) {
						flag = 1;
						// System.out.println(temp3 + " " + flag);
					} else {
						flag2 = 2;
						// System.out.println(temp3 + " " + flag2);
					}
				}
				if ((flag == 1 && flag2 == 2) || (firstcount != 4)) {
					alert = 1;
					break;
				}
			}

				if (alert == 1) {
					System.out.println("Work Product is red"); // Work Product table
																// is not populated

				} else {
					System.out.println("Work Product is green"); // Work Product
																	// table is
																	// populated
				}
			}

	
			}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}
	

	}	
	
	
	
	
private static void PaymentMethodFG(String s){
		
		String startDate = null;
		String endDate = null;
		String testDate= null;
		int start1 = s.indexOf("Statement of Work Period");
		int end1 = 0;
		
		String stringstart="Statement of Work Payment Characteristics";
		
		int start2 = s.indexOf(stringstart);
		int end2 = 0;
		
		int flag = 0;
		
		try{
		for (int i=start1; ; i++){
			if(s.substring(i, i+4).equals("</tr")){
				end1 = i;
				break;
			}
		}
		String text = s.substring(start1, end1);
		System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote("Statement of Work Period") + "(.*?)" + Pattern.quote("to")); //Extracts start date
		Matcher n = q.matcher(text);
		while (n.find()) {
		  startDate = n.group(1);
		  startDate = startDate.replaceAll("\\<.*?>","").trim();
		  System.out.println(startDate);
		}
		Pattern p = Pattern.compile(Pattern.quote("to") + "(.*?)" + Pattern.quote("</p>")); //Extracts end date
		Matcher m = p.matcher(text);
		while (m.find()) {
			  endDate = m.group(1);
			  endDate = endDate.replaceAll("\\<.*?>","").trim();
		  System.out.println(endDate);
		  
		}
    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date1 = sdf.parse(startDate);
        Date date2 = sdf.parse(endDate);
        
		 for (int i=start2; ; i++){
				if(s.substring(i, i+7).equals("</html>")){
					end2 = i;
					break;
				}
			}
			String textpayment = s.substring(start2, end2);
			System.out.println(textpayment);
			
			Matcher z = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4}|\\d{1,2}/\\d{1,2})", Pattern.CASE_INSENSITIVE).matcher(textpayment);
		        while (z.find()) {
	        	testDate = z.group(1);
	        	Date date3 = sdf.parse(testDate);
	        	
		            System.out.println(z.group(1));
		           if (((date3.after(date1))||(date3.equals(date1)))&&((date3.before(date2))||(date3.equals(date2)))){
		        	   System.out.println("pass");
		}          
		           else{
		        	   flag=flag+1;
		           }
		  }
		      if(flag>0){
		    	  System.out.println("Payment class red");
		      }
		      else{

		    	  System.out.println("Payment class green");
		      }
		        
		        
			}

		
			
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}

	}

	
	
	private static void trialPayment(String s){
		String stringstart="";
		String startDate = null;
		String endDate = null;
		String testDate= null;
		String trtext="";
		String tdtext="";
		String paymentDisplay="";
		String startFilledtext="";
		
		if (s.contains("Statement of Work Payment Characteristics")){
			startFilledtext ="Statement of Work Payment Characteristics";
			stringstart=startFilledtext;
		}
		else if (s.contains("Events")){
			startFilledtext ="Events";
			stringstart=startFilledtext;
		}
		else if (s.contains("Schedules")){
			startFilledtext ="Schedules";
			stringstart=startFilledtext;
		}

		int start1 = s.indexOf("Statement of Work Period");
		int end1 = 0;
		
		int start2 = s.indexOf(stringstart);
		int end2 = 0;
	
		int startFilled = s.indexOf(startFilledtext);//Statement of Work Payment Characteristics
		int endFilled = 0;
		int flag = 0;
		int counttr=0;
		int flagAmount=0;
		int flagName=0;
		try{
			for (int i=start1; ; i++){
				if(s.substring(i, i+4).equals("</tr")){
					end1 = i;
					break;
				}
			}
			String text = s.substring(start1, end1);
			System.out.println(text);
			Pattern q = Pattern.compile(Pattern.quote("Statement of Work Period") + "(.*?)" + Pattern.quote("to")); //Extracts start date
			Matcher n = q.matcher(text);
			while (n.find()) {
			  startDate = n.group(1);
			  startDate = startDate.replaceAll("\\<.*?>","").trim();
			  System.out.println(startDate);
			}
			Pattern p = Pattern.compile(Pattern.quote("to") + "(.*?)" + Pattern.quote("</p>")); //Extracts end date
			Matcher m = p.matcher(text);
			while (m.find()) {
				  endDate = m.group(1);
				  endDate = endDate.replaceAll("\\<.*?>","").trim();
			  System.out.println(endDate);  
			}
	    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	        Date date1 = sdf.parse(startDate);
	        Date date2 = sdf.parse(endDate);
	        ArrayList<String> DateList = new ArrayList<String>();
			 for (int i=start2; ; i++){
					if(s.substring(i, i+7).equals("</html>")){
						end2 = i;
						break;
					}
				}
				String textpayment = s.substring(start2, end2);
				System.out.println(textpayment);
				
				Matcher z = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4}|\\d{1,2}/\\d{1,2})", Pattern.CASE_INSENSITIVE).matcher(textpayment);
			        while (z.find()) {
		        	testDate = z.group(1);
		        	Date date3 = sdf.parse(testDate);
		        	
			            System.out.println(z.group(1));
			           if (((date3.after(date1))||(date3.equals(date1)))&&((date3.before(date2))||(date3.equals(date2)))){
			        	   System.out.println("pass");
			        	   DateList.add(date3.toString());
			}          
			           else{
			        	   flag=flag+1;
			        	   paymentDisplay=paymentDisplay + "Dates mentioned in the table do not align with start and end date of TermSOW";
			        	   break;
			           }
			  }
			        
			for (int i=startFilled; ; i++){
			if(s.substring(i, i+8).equals("</table>")){
				endFilled = i;
				break;
			}
		}
		String textFilled = s.substring(startFilled, endFilled);
		System.out.println(textFilled);
		Pattern qFilled = Pattern.compile(Pattern.quote("<tr>") + "(.*?)" + Pattern.quote("</tr>")); //Extracts start date
		Matcher nFilled = qFilled.matcher(textFilled);

		ArrayList<String> NameList = new ArrayList<String>();
		ArrayList<String> AmountList = new ArrayList<String>();
		while (nFilled.find()) {
			counttr=counttr+1;
			trtext = nFilled.group(1).replaceAll("colspan=\"2\"","").replaceAll("<td >","<td>");
			if (trtext.contains("Schedule")||trtext.contains("Events")||trtext.contains("Amount")||trtext.contains("Name")||trtext.contains("Total")){
				//Do nothing //pass condition;
			}
			else{
				System.out.println(counttr+"  "+trtext);
				int counttd=0;
				Pattern a = Pattern.compile(Pattern.quote("<td>") + "(.*?)" + Pattern.quote("</td>")); //Extracts start date
				Matcher b = a.matcher(trtext);
				while(b.find()){
					counttd=counttd+1;
					if (((counttd==7)&&(s.contains("Service Level Agreements")))||(counttd==9))
							{ //For amount
					tdtext=b.group(1);
					System.out.println(tdtext);
					tdtext=tdtext.replaceAll("\\<.*?>","").trim();
					AmountList.add(tdtext);
					tdtext=tdtext.replaceAll("\\.","").replaceAll("\\,","").replaceAll("[a-zA-Z]","").trim();
					if (!tdtext.equals("")){
					System.out.println(counttd+"  "+tdtext);
					}
					else{
						//Raise amount not filled flag;
						flagAmount =1;
						paymentDisplay=paymentDisplay + "Amount Column is not completely filled";
					}
					}
					else if (counttd==2){ //For name
						tdtext=b.group(1);
						tdtext=tdtext.replaceAll("\\<.*?>","").trim();
						if (!tdtext.equals("")){
						NameList.add(tdtext);
						System.out.println(counttd+"  "+tdtext);
						}
						else{
							//Name not filled flag to be raised
							flagName=1;
							paymentDisplay=paymentDisplay + "Name Column is not completely filled";
						}
					}
				}
			}
			
		}
		System.out.println(NameList);
		for (int k =0;k<NameList.size();k++){
			for (int l =0;l<NameList.size();l++){
				if ((NameList.get(k).equals(NameList.get(l)))&&(k!=l)){
					flagName=2;
					
				}
			}
		}
		if(flagName==2){
			paymentDisplay=paymentDisplay + "Name Column consists of duplicate values";
		}
		
		
		if (((flagName==0)&&(flagAmount ==0))&&(flag==0)) {
			
			ArrayList<String> DisplayList = new ArrayList<String>();

	        ArrayList<String> FinalDisplayList = new ArrayList<String>();
	        for (int i=0;i<DateList.size();i++){
	        	DisplayList.add(DateList.get(i) + " , " + NameList.get(i)+ " , " + AmountList.get(i));
	        	FinalDisplayList.add(DisplayList.get(i));
	        }
		  paymentDisplay="";
     	  paymentDisplay="Dates mentioned in the table align with start and end date of TermSOW, Names column is filled and doesn't have duplicate values, Amount column is filled.";
     	  paymentDisplay = paymentDisplay +" " +FinalDisplayList;
     	   System.out.println(paymentDisplay);
		}
		else{
			System.out.println("Payment class fail");
			System.out.println(paymentDisplay);
		}
				}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			
			}

	}

	
	
	
	
	
	private static void LiquidatedDamages(String s){
		String ldMissed="";
		String ldCredit="";
		String ldDisplay="";
		int flag1 = 0;
		int flag2 = 0;
		
		int start1 = s.indexOf("id=\"SLA\"");
		int end1 = 0;
		try{
		for (int i=start1; ; i++){
			if(s.substring(i, i+3).equals("<h2")){
				end1 = i;
				break;
			}
		}
		String text = s.substring(start1, end1);
		System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote("If Supplier performance fails to meet an SLA for") + "(.*?)" + Pattern.quote("consecutive months due to factors")); //Extracts start date
		Matcher n = q.matcher(text);
		while (n.find()) {
			ldMissed = n.group(1);
			ldMissed=ldMissed.replaceAll("&lt;","<").replaceAll("&gt;",">").trim();
			ldMissed=ldMissed.replaceAll("\\<.*?>","").trim();
			System.out.println(ldMissed);
		}
		if ((!ldMissed.equals(""))&&(!ldMissed.equals("NA"))&&(!ldMissed.equals("N/A"))&&(!ldMissed.equals("None"))){
			flag1 = 1;
			System.out.println(flag1);
			ldDisplay=ldDisplay+ "SLA Missed Months: " + ldMissed;
		}
		
		//===========================================================================
		
		
		Pattern a = Pattern.compile(Pattern.quote("Supplier will issue a credit of") + "(.*?)" + Pattern.quote("of the monthly charge")); //Extracts start date
		Matcher b = a.matcher(text);
		while (b.find()) {
			ldCredit = b.group(1);
			ldCredit=ldCredit.replaceAll("&lt;","<").replaceAll("&gt;",">").trim();
			ldCredit=ldCredit.replaceAll("\\<.*?>","").trim();
			System.out.println(ldCredit);
		}
		if ((!ldCredit.equals(""))&&(!ldCredit.equals("NA"))&&(!ldCredit.equals("N/A"))&&(!ldCredit.equals("None"))){
			flag2 = 1;
			System.out.println(flag2);
			ldDisplay=ldDisplay+ "\n"+"SLA Credit Terms: " + ldCredit;
		}
		
		
		//======================================================================
		if ((flag1==1)&&(flag2==1)){
			System.out.println("LiquidatedDamages is green");
			System.out.println(ldDisplay);
		}
		else{

			System.out.println("LiquidatedDamages is red");
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}
	
	}

	
	
	
	
	
	
	
	
	
	
	private static void LiquidatedDamagesFG(String s){
		String ldMissed="";
		String ldCredit="";
		String ldDisplay="";
		int flag1 = 0;
		int flag2 = 0;
		
		int start1 = s.indexOf("SLA Missed Months");
		int end1 = 0;
		try{
		for (int i=start1; ; i++){
			if(s.substring(i, i+4).equals("<tr>")){
				end1 = i;
				break;
			}
		}
		String textMissed = s.substring(start1, end1);
		System.out.println(textMissed);
		Pattern q = Pattern.compile(Pattern.quote("SLA Missed Months") + "(.*?)" + Pattern.quote("</tr>")); //Extracts start date
		Matcher n = q.matcher(textMissed);
		while (n.find()) {
			ldMissed = n.group(1);
			ldMissed=ldMissed.replaceAll("&lt;","<").replaceAll("&gt;",">").trim();
			ldMissed=ldMissed.replaceAll("\\<.*?>","").trim();
			System.out.println(ldMissed);
		}
		if ((!ldMissed.equals(""))&&(!ldMissed.equals("NA"))&&(!ldMissed.equals("N/A"))&&(!ldMissed.equals("None"))){
			flag1 = 1;
			System.out.println(flag1);
			ldDisplay=ldDisplay+ "SLA Missed Months: " + ldMissed;
		}
		
		//===========================================================================
		int start2 = s.indexOf("SLA Credit terms");
		int end2 = 0;
		
		for (int i=start2; ; i++){
			if(s.substring(i, i+4).equals("<tr>")){
				end2 = i;
				break;
			}
		}
		String textCredit = s.substring(start2, end2);
		System.out.println(textCredit);
		Pattern a = Pattern.compile(Pattern.quote("Supplier will issue a credit of") + "(.*?)" + Pattern.quote("of the monthly charge")); //Extracts start date
		Matcher b = a.matcher(textCredit);
		while (b.find()) {
			ldCredit = b.group(1);
			ldCredit=ldCredit.replaceAll("&lt;","<").replaceAll("&gt;",">").trim();
			ldCredit=ldCredit.replaceAll("\\<.*?>","").trim();
			System.out.println(ldCredit);
		}
		if ((!ldCredit.equals(""))&&(!ldCredit.equals("NA"))&&(!ldCredit.equals("N/A"))&&(!ldCredit.equals("None"))){
			flag2 = 1;
			System.out.println(flag2);
			ldDisplay=ldDisplay+ "\n"+"SLA Credit Terms: " + ldCredit;
		}
		
		
		//======================================================================
		if ((flag1==1)&&(flag2==1)){
			System.out.println("LiquidatedDamages is green");
			System.out.println(ldDisplay);
		}
		else{

			System.out.println("LiquidatedDamages is red");
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}
	
	}

	

	
	private static void WorkProductFG(String s){
		String wpDescription="";
		String wpFormat="";
		String wpLocation="";
		String wpDisplay="";
		int flag1 = 0;
		int flag2 = 0;
		int flag3 = 0;
		
		int start1 = s.indexOf("Work Product Description (Required)");
		int end1 = 0;
		try{
		for (int i=start1; ; i++){
			if(s.substring(i, i+37).equals("Work Product Description, continued 2")){
				end1 = i;
				break;
			}
		}
		String textDescription = s.substring(start1, end1);
		System.out.println(textDescription);
		Pattern q = Pattern.compile(Pattern.quote("Work Product Description (Required)") + "(.*?)" + Pattern.quote("<tr>")); //Extracts start date
		Matcher n = q.matcher(textDescription);
		while (n.find()) {
			wpDescription = n.group(1);
			wpDescription=wpDescription.replaceAll("&lt;","<").replaceAll("&gt;",">").trim();
			wpDescription=wpDescription.replaceAll("\\<.*?>","").trim();
			System.out.println(wpDescription);
		}
		if (!wpDescription.equals("")){
			flag1 = 1;
			System.out.println(flag1);
			wpDisplay=wpDisplay+ "Deliverable: " + wpDescription;
		}
		
		//===========================================================================
		int start2 = s.indexOf("Specifications for Work Product");
		int end2 = 0;
		
		for (int i=start2; ; i++){
			if(s.substring(i, i+42).equals("Additional Specifications for Work Product")){
				end2 = i;
				break;
			}
		}
		String textFormat = s.substring(start2, end2);
		System.out.println(textFormat);
		Pattern a = Pattern.compile(Pattern.quote("Specifications:") + "(.*?)" + Pattern.quote("All Work Product prepared by Supplier will be subject to Cisco's acceptance in accordance with Section 3")); //Extracts start date
		Matcher b = a.matcher(textFormat);
		while (b.find()) {
			wpFormat = b.group(1);
			wpFormat=wpFormat.replaceAll("&lt;","<").replaceAll("&gt;",">").trim();
			wpFormat=wpFormat.replaceAll("\\<.*?>","").trim();
			System.out.println(wpFormat);
		}
		if((wpFormat.contains("Training tools"))||(wpFormat.contains("MS Word"))||(wpFormat.contains("MS Excel"))||(wpFormat.contains("MS PowerPoint"))||(wpFormat.contains("MS Visio"))){
			flag2 =1;
			System.out.println(flag2);
			wpDisplay=wpDisplay+ "\n"+"Format: " + wpFormat;
		}
		//=========================================================================
		
		int start3 = s.indexOf("Primary Work Site");
		int end3 = 0;
		
		for (int i=start3; ; i++){
			if(s.substring(i, i+4).equals("<tr>")){
				end3 = i;
				break;
			}
		}
		String textLocation = s.substring(start3, end3);
		System.out.println(textLocation);
		Pattern c = Pattern.compile(Pattern.quote("Primary Work Site") + "(.*?)" + Pattern.quote("</tr>")); //Extracts start date
		Matcher d = c.matcher(textLocation);
		while (d.find()) {
			wpLocation = d.group(1);
			wpLocation=wpLocation.replaceAll("&lt;","<").replaceAll("&gt;",">").trim();
			wpLocation=wpLocation.replaceAll("\\<.*?>","").trim();
			System.out.println(wpLocation);
		}
		if(!wpLocation.equals("")){
			flag3 =1;
			System.out.println(flag3);
			wpDisplay=wpDisplay+ "\n"+"Location: " + wpLocation;
		}
		//======================================================================
		if ((flag1==1)&&(flag2==1)&&(flag3==1)){
			System.out.println("Work Product is green");
			System.out.println(wpDisplay);
		}
		else{

			System.out.println("Work Product is red");
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}
	
	}


	
	
	private static void trialWho(String s){
		int startWho = s.indexOf("Statement of Work Payment Characteristics");
		int endWho = 0;
		
		//start
		for (int i=startWho; ; i++){
			if(s.substring(i, i+8).equals("</table>")){
				endWho = i;
				break;
			}
		}
		String textWho = s.substring(startWho, endWho);
		System.out.println(textWho);
		int counttr=0;
		int conditioncheck=0;
		Pattern q = Pattern.compile(Pattern.quote("<tr>") + "(.*?)" + Pattern.quote("</tr>")); //Extracts start date
		Matcher m = q.matcher(textWho);
		String towork = "";
		while(m.find())
		{
			
			String temp = m.group(1).trim();
			if (temp.contains("Schedules")){
				conditioncheck=1;
			}
			else if (temp.contains("Events")){
				conditioncheck=2;
			}
			else if (temp.contains("Total")){
				//Do nothing
			}
			else{
				towork=towork+"  " + temp;
				counttr= counttr+1;
			}
			Pattern k = Pattern.compile(Pattern.quote("<td>") + "(.*?)" + Pattern.quote("</td>")); //Extracts start date
            Matcher l = k.matcher(towork);
			while(l.find())
			{
				
				String tempString = l.group(1).trim();

					System.out.println(tempString);
			
			}
				
			}
		System.out.println(towork);
		}
		
	
	
	
	private static void SLAFG(String s){
		String finaldisplay = "";
		String target = "";
		int targetcheck=0;
		int start = s.indexOf("c) Service Level Agreements");
		int end = 0;
		int freqcheck=0;
		int count = 0;
		int k = 0;
	try{
		for (int i=start; ; i++){
			if(s.substring(i, i+16).equals("SLA Credit terms")){
				end = i;
				break;
			}
		}
		String[] list = {"annual","annually","monthly","daily","weekly","hourly"};
		String text = s.substring(start, end);
		//System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote("Service Level Agreement #") + "(.*?)" + Pattern.quote("<tr>")); //Extracts start date
		Matcher m = q.matcher(text);
		while(m.find())
		{
			count = count + 1;
			String temp = m.group(1).trim();
			System.out.println(temp);
			temp = temp.replaceAll("_", "");
			String tempfreq = temp.toLowerCase();
			finaldisplay = finaldisplay + "Service Level Agreement #"+count + " ";
			k=0;
		    for (int i=0;i<list.length;i++){
		    	
		       	if(tempfreq.contains(list[i])){
		        		freqcheck=freqcheck +1;
		        		finaldisplay = finaldisplay + " Frequency: " + list[i] + " ";
		        		System.out.println(list[i]);
		        		k=1;
		        	}
		        }
		    if (k==0){
		    	finaldisplay = finaldisplay + " No frequency term ";
		    }
		    
		        
				 Pattern p = Pattern.compile("\\d+%");
				 Matcher d = p.matcher(temp);
				 if (d.find()){
					 System.out.println(d.group());
						targetcheck = targetcheck + 1;
						finaldisplay = "\n" + finaldisplay + "Target:" + d.group() + " " ;
				 }
				 else{
					 finaldisplay = finaldisplay + " No target term ";
				 }
		}
		if (targetcheck<2){
			System.out.println("SLA class red");
			System.out.println(finaldisplay);
		}
		else {
			System.out.println("SLA class green");
			System.out.println(finaldisplay);
		}
	}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("SLA class exception"); // return class red
			}
		}

	
	
	
	private static void ScopeFG(String s){

		int start = s.indexOf("SCOPE OF SERVICES AND WORK PRODUCT");
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+15).equals("b) Work Product")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		//System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote("Scope Description") + "(.*?)" + Pattern.quote("<tr>")); //Extracts start date
		Matcher n = q.matcher(text);
		while (n.find()) {
			  String finalText = n.group(1);
			finalText = finalText.replaceAll(", continued","").replaceAll("\\<.*?>","").trim(); 
			System.out.println(finalText);
		}
		
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}

	}

	

	
	
	
	
	private static void ExpensesFG(String s){
		String stringstart = "If no Fees Section exists the maximum liability for all expenses incurred during the execution of this SOW shall not exceed";
		String exceedLimit="";
		String toDisplay="";
		int start = s.indexOf("Cisco shall reimburse reasonable,");
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+4).equals("<tr>")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote(stringstart) + "(.*?)" + Pattern.quote("</p>")); //Extracts start date
		Matcher n = q.matcher(text);
		while (n.find()) {
		  exceedLimit = n.group(1);
		  exceedLimit = exceedLimit.replaceAll("\\s+","");
		  exceedLimit = exceedLimit.trim();
		  System.out.println(exceedLimit + "aaaa");

		  toDisplay = stringstart + " " +exceedLimit;
		}
	
		if(!exceedLimit.equals("")){
			System.out.println("omg text");
		}
		else{
			System.out.println("omg no text");
			System.out.println(toDisplay);
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}

	}
	
	private static void SLAAlertFG(String s){
		String statement1="timeliness of work products. the supplier shall be timely with their work product. this will be measured by the number of days late for project management work product.";
		String statement2="quality of work product. the supplier shall deliver quality work product. this will be measured by the completeness of project management work product.";
		String statement3="change management. the supplier shall provide version control management of project related documentation as identified by cisco pm. the supplier shall manage change management activities and shall ensure version control management will be in place for all project related documentation, including the rmt release playbook, performance/uat playbook, gate review docs, document templates, or as identified by cisco pm.";
		String statement4="project impact assessment. the supplier shall provide an impact assessment for any changes to upstream or downstream systems (applications, databases, servers, etc.) as identified by cisco pm.";
		
		int start = s.indexOf("Service Level Agreements"); //Taking all data between id "Payment term" and next header
		int end = 0;
		int fail=0;
		int pass=0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+16).equals("SLA Credit terms")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
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
		if((fail>0)||(pass<2)){
			System.out.println("SLA alert to be raised");
		}
		else{
			System.out.println("SLA alert not to be raised");
		}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("catch exception");
		}	
	}
	
	
	
	
	private static void SLACreditTermAlertFG(String s){
		String doc="";
		String toMatch="Where Supplier fails to resolve the problem by the time defined in any corrective action plan, Supplier will issue a credit of of the monthly charge for each month that the SLA remains out of range. If Supplier continues to fail to resolve the problem, Supplier is in breach of contract and Cisco retains the right to terminate for cause.";
		toMatch = toMatch.toLowerCase().trim();
		int score  = 0;
		int start = s.indexOf("SLA Credit terms"); //Taking all data between id "Payment term" and next header
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+3).equals("<tr")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
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
		if (score<90)
		{
			System.out.println("SLA Credit Term Alert to be raised as score is less than 0.9");
		}
		else {
			System.out.println("SLA Credit Term Alert not to be raised as score is greater than 0.9");
		}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("catch exception");
		}	
	}
	
	private static void PaymentMethodAlertFG(String s){
		int count  = 0;
		int start = s.indexOf("Statement of Work Payment Characteristics"); //Taking all data between id "Payment term" and next header
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+6).equals("</html")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
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
		if (count>0)
		{
			System.out.println("Prepayment word exists. Prepayment alert to be raised");
		}
		else {
			System.out.println("Prepayment word doesn't exist. Prepayment alert not to be raised");
		}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("catch exception");
		}	
	}
	
	
	private static void SpecialTermAlertFG(String s){
		int count = 0;
		int start = s.indexOf("SPECIAL TERMS"); //Checks between id SpecialTerms and next header
		int end = 0;
		try {
			for (int i=start; ; i++){
				if(s.substring(i, i+3).equals("<tr")){
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
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
			if (count>0){
				System.out.println("Special term does not exist. Special term alert not to be raised");
			}
			else{
				System.out.println("Special term exists. Special term alert to be raised");
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("catch exception");
			}	
	}	

	
	
	private static void TermSowFG(String s){
		String startDate = null;
		String endDate = null;
		int start = s.indexOf("Statement of Work Period");
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+4).equals("</tr")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote("Statement of Work Period") + "(.*?)" + Pattern.quote("to")); //Extracts start date
		Matcher n = q.matcher(text);
		while (n.find()) {
		  startDate = n.group(1);
		  startDate = startDate.replaceAll("\\<.*?>","").trim();
		  System.out.println(startDate);
		}
		Pattern p = Pattern.compile(Pattern.quote("to") + "(.*?)" + Pattern.quote("</p>")); //Extracts end date
		Matcher m = p.matcher(text);
		while (m.find()) {
			  endDate = m.group(1);
			  endDate = endDate.replaceAll("\\<.*?>","").trim();
		  System.out.println(endDate);
		}
		if((startDate!="")&&(endDate!="")){
			System.out.println("termSOW grreen");
		}
		else{
			System.out.println("termSOW red");
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}
		}

	
	
	private static void Scope(String s){
		s=s.toLowerCase();
		int start = s.indexOf("scopeandworkproduct"); //Taking all data between id "Payment term" and next header
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+16).equals("id=\"workproduct\"")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		text = Jsoup.parse(text).text().replaceAll("scopeandworkproduct\">","");
		System.out.println(text);
		String[] sent=text.split(PostNLCPropSet.patternSplitSentence);
		ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2017_02_03);
		service.setUsernameAndPassword("eb88d8fc-94d6-4f0d-a2c3-83ed3347102b", "JGGdWPlhDqNO");
		
		String finalDisplay="";
		JSONObject jsondisplay = new JSONObject();
		int flag1 = 0;
		int flag2=0;
		int flag3=0;
		String how="";
		String what="";
		String why="";
		String who="";
		
		
		for(String sentence:sent){
			MessageRequest newMessage = new MessageRequest.Builder().alternateIntents(true).inputText(sentence).build();
			MessageResponse response = null;
			response = service.message("db7521b1-f9f8-490a-8a38-c6b89606d8e4", newMessage).execute();
			String resp = response.toString();
			//System.out.println(response);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(resp);
			//System.out.println(json.get("intents"));

			for (int i=0; i < ((JSONArray)json.get("intents")).size(); i++){
				if (((JSONObject)((JSONArray)json.get("intents")).get(i)).get("intent").toString().equals("What")){
					if (Double.parseDouble(((JSONObject)((JSONArray)json.get("intents")).get(i)).get("confidence").toString()) >= 0.7){
						String toAvoid = "scope of services and work product supplier shall provide the following services to cisco";
						String toAvoid1= "supplier shall provide the following services and associated work product to cisco";
						if(((FuzzySearch.weightedRatio(toAvoid, sentence))>80)||((FuzzySearch.weightedRatio(toAvoid1, sentence))>80)){
							flag1 = 0;
							System.out.println("omg" + (FuzzySearch.weightedRatio(toAvoid, sentence)) + " "+ (FuzzySearch.weightedRatio(toAvoid, sentence))) ;
							System.out.println("flag :: "+ flag1 +"sentence :: " +sentence );
						}
						else{
						flag1 = 1;
						what = what + sentence;
						}
					}else{
						//jsondisplay.put("What", "Intent absent");
					}
				}
				if (((JSONObject)((JSONArray)json.get("intents")).get(i)).get("intent").toString().equals("Why")){
					if (Double.parseDouble(((JSONObject)((JSONArray)json.get("intents")).get(i)).get("confidence").toString()) >= 0.7){
						flag2 = 1;
						why = why + sentence;
						}
					else{
						//jsondisplay.put("Why", "Intent absent");
					}
				}
				if (((JSONObject)((JSONArray)json.get("intents")).get(i)).get("intent").toString().equals("How")){
					if (Double.parseDouble(((JSONObject)((JSONArray)json.get("intents")).get(i)).get("confidence").toString()) >= 0.7){
						flag3 = 1;
						how = how + sentence;
					}else{
						//jsondisplay.put("How", "Intent absent");
					}
				}
				
			}

		}
		//==================================
		//changes made as per the new logic given by prashant where who is linked to general
		String tempdisplay="";
		int startWho = s.indexOf("id=\"general\"");
		int endWho = 0;
		
			for (int i = startWho;; i++) {
				if (s.substring(i, i + 3).equals("<h1")) {
					endWho = i;
					break;
				}
			}
			String textWho = s.substring(startWho, endWho);

			Pattern a = Pattern.compile(Pattern.quote("this statement of work")
					+ "(.*?)" + Pattern.quote("existing or future sows"));
			Matcher b = a.matcher(textWho);
			while (b.find()) {
				tempdisplay = Jsoup.parse(b.group(1).trim()).text();
				tempdisplay = "This Statement of Work " + tempdisplay
						+ " existing or future SOWs";
				System.out.println(tempdisplay);
			}

			Pattern q = Pattern.compile(Pattern.quote(by) + "(.*?)"
					+ Pattern.quote(under));
			Matcher n = q.matcher(textWho);
			while (n.find()) {
				who = n.group(1).trim();
			}
		
		//==================================
		jsondisplay.put("WHAT", what);
		jsondisplay.put("WHY", why);
		jsondisplay.put("WHO", who);
		jsondisplay.put("HOW", how);
		finalDisplay = jsondisplay.toString();
 
	        if((flag1==1)&&(flag2==1)&&(flag3==1)) //updated as per bug raised by prashant
	        {
		System.out.println("Green");
			System.out.println(finalDisplay);
		}
		else{
			System.out.println("Red");
			System.out.println(finalDisplay);
						
		}
		
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("catch exception");
		}	
	}
	
		
	private static void PaymentMethodAlert(String s){
		int count  = 0;
		int start = s.indexOf(PaymentMethod); //Taking all data between id "Payment term" and next header
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+3).equals("<h2")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		Pattern p = Pattern.compile(alert3regex);//Checks table body
		Matcher m = p.matcher(text);
		while(m.find())
		{
			String temp1 = m.group(1);
			String temp = Jsoup.parse(temp1).text().trim(); //For every instance if it contains the below words count is increased and alert is to be raise if count>0
			if(temp.contains("Prepayment")||temp.contains("prepayment")||temp.contains("pre-payment")||temp.contains("Pre-Payment")||temp.contains("Pre-payment"))
			{
				count = count + 1;
				break;
				//Raise alert
			}
		}
		if (count>0)
		{
			System.out.println("Prepayment word exists. Prepayment alert to be raised");
		}
		else {
			System.out.println("Prepayment word doesn't exist. Prepayment alert not to be raised");
		}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("catch exception");
		}	
	}
	
	private static void SpecialTermAlert(String s){
		int count = 0;
		int start = s.indexOf(SpecialTerms); //Checks between id SpecialTerms and next header
		int end = 0;
		try {
			for (int i=start; ; i++){
				if(s.substring(i, i+3).equals("<h1")){
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			//String text1 = removeSpecialCharacters(text);
			Pattern p = Pattern.compile(alert1regex);
			Matcher m = p.matcher(text);
			while(m.find())
			{
				String temp = m.group(1).replace("dir=\"ltr\">", "").replace(">", "").trim();
				//System.out.println(temp);
				if(temp.contains("N/A") || temp.contains("n/a")|| temp.contains("none") || temp.contains("None") || temp.contains("NA") || temp.equals(""))
				{
					count = 1;
					break; //there are NA terms
				}
			}
			if (count>0){
				System.out.println("Special term does not exist. Special term alert not to be raised");
			}
			else{
				System.out.println("Special term exists. Special term alert to be raised");
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("catch exception");
			}	
	}	

	private static void OtherResourcesAlert(String s){
		int count = 0;
		int start = s.indexOf(OtherSupplierResources); //Checks between id and next header
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+3).equals("<h2")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		Pattern p = Pattern.compile(alert2regex);
		Matcher m = p.matcher(text);
		while(m.find())
		{
			String temp1 = m.group(1);
			//System.out.println(temp1);
			String temp = Jsoup.parse(temp1).text().trim();
			//If string contains below words, then ignore else raise alert
				if(temp.equalsIgnoreCase("Primary Location")||temp.equalsIgnoreCase("Participation")||temp.equalsIgnoreCase("Role")||temp.equalsIgnoreCase("Name")||temp.equalsIgnoreCase("Key Personnel")||temp.equalsIgnoreCase("NA")||temp.equalsIgnoreCase("N/A")||temp.equalsIgnoreCase("None")||temp.equalsIgnoreCase("")||temp.equalsIgnoreCase("?????"))		
					{
					// Do nothing System.out.println("safe");
					}
				else
					{
					//System.out.println("uhoh");
					count = count + 1; //Table is populated
					}

		}
		if (count>0){
			System.out.println("Table is populated. Other supplier resource alert to be raised");
		}
		else {
			System.out.println("Table is not populated. Other supplier resource alert not to be raised");//return null;
		}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("catch exception");
			}	
	}

	
	private static void GeneralTerms(String s){
		String tempdisplay = "";
		String temp1 = null;
		int start = s.indexOf("General");
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+3).equals("<h1")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		
		Pattern q = Pattern.compile(Pattern.quote(by) + "(.*?)" + Pattern.quote(under)); //Finds text between by and under in the general section
		Matcher n = q.matcher(text);
		
		while (n.find()) {
		  temp1 = n.group(1).trim();
		  System.out.println(temp1);
		}
		if(temp1.equalsIgnoreCase("N/A") || temp1.equalsIgnoreCase("None") || temp1.equalsIgnoreCase("NA") || temp1.equalsIgnoreCase(""))
		{
			System.out.println("General Term does not exist"); //Red node
			}
		else 
		{
			System.out.println("General Term does exist");		//Green	node
			System.out.println(tempdisplay);
			}
		}catch(Exception e){
			e.printStackTrace();	

			System.out.println("catch exception");
			}	
		}
	
	private static void TypeValidation(String s){
		int termMP = 0;
		int termMS = 0;
		
		int startCheckMP = s.indexOf("AcceptanceMilestones");		
		int startCheckMS = s.indexOf("SLAandTesting"); 
		
		int start = s.indexOf("id=\"SOW\"");
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+7).equals("General")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		Pattern p = Pattern.compile("(?<=\\<b\\>)(.+?)(?=\\<\\/b\\>)");
		Matcher m = p.matcher(text);
		while(m.find())
		{
			String temp = (m.group(1).trim());
			boolean tempMP = Pattern.compile(Pattern.quote("Managed Project"), Pattern.CASE_INSENSITIVE).matcher(temp).find();
			boolean tempMS = Pattern.compile(Pattern.quote("Managed Service"), Pattern.CASE_INSENSITIVE).matcher(temp).find();
			if (tempMP == true){
			 termMP = termMP + 1;
			}
			else if (tempMS == true){
				termMS = termMS + 1;	
			}
			}
		if ((startCheckMP>-1) && (termMP>0)){
			// MP document
			System.out.println("MP document"); //Return MP score 100
		}
		else if ((startCheckMP>-1) && (termMP==0)){
			//Return MP score 60
		}
		else if ((startCheckMP==-1) && (termMP>0)){
			//Return MP score 40
		}
		else if ((startCheckMS>-1) && (termMS>0)){
		//MS document
			System.out.println("MS document"); //Return MS score 100
		}
		else if ((startCheckMS==-1) && (termMS>0)){
			//MS document
				//Return MS score 40
			}
		else if ((startCheckMS>-1) && (termMS==0)){
			//MS document
		 //Return MS score 60
			}
		}
		catch(Exception e){
			e.printStackTrace();	

			System.out.println("catch exception");
			}
	}
	
	private static void WorkLocation(String s){
		String sent1 = null;
		String sent2 = null;
		String sent3 = null;
		String sent4 = null;
		s= s.toLowerCase();
		int start = s.indexOf("worklocation");
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+3).equals("<h1")){
				end = i;
				break;
			}
		}
		
		String text1 = s.substring(start, end);
		String finaldisplay = Jsoup.parse(text1).text().replaceAll("worklocation\">","").replaceAll("\">","");
		System.out.println(finaldisplay);
		String text = text1.replace("&nbsp;",""); //newly added
		//Checks for 2 cases. If both exists then green else red. Searched in WorkLocation para using below pattern match
		Pattern case1 = Pattern.compile(Pattern.quote("by supplier at") + "(.*?)" + Pattern.quote("at:"));
		Matcher test1 = case1.matcher(text);
		while (test1.find()) {
		sent1  = test1.group(1).trim();
		System.out.println(sent1);
		}
		Pattern case2 = Pattern.compile(Pattern.quote("by supplier at") + "(.*?)" + Pattern.quote("in"));
		Matcher test2 = case2.matcher(text);
		while (test2.find()) {
		sent2 = test2.group(1).trim();
		System.out.println(sent2);
		}
		Pattern case3 = Pattern.compile(Pattern.quote("at:") + "(.*?)" + Pattern.quote("</p>"));
		Matcher test3 = case3.matcher(text);
		while (test3.find()) {
		 sent3 = test3.group(1).trim(); 
		 System.out.println(sent3);
		}
		Pattern case4 = Pattern.compile(Pattern.quote("in") + "(.*?)" + Pattern.quote("</p>"));
		Matcher test4 = case4.matcher(text);
		while (test4.find()) {
		  sent4 = test4.group(1).trim();
		  System.out.println(sent4);
		}
		if(((sent1!=null)||(sent2!=null))&&((sent3!=null)||(sent4!=null))){
			System.out.println("Work Location Green");
		}
		else{
			System.out.println("Work Location Red");
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}
	}

	private static void ThirdParty(String s){
		int flag = 0;
		String temp1 = null;
		int start = s.indexOf("ThirdPartyProperty");
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+2).equals("<h")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		Pattern p = Pattern.compile("(?<=\\<p)(.+?)(?=\\<\\/p\\>)");
		Matcher m = p.matcher(text);
		while(m.find())
		{//Checks if the text contains anything. NA/none or any other thext is gree. Blank is red
			String temp = m.group().replaceAll("\\<.*?>","").replace(".", "").replace("dir=\"ltr\">", "").replace(">", "").replace("*", "").replace("c)", "").replace("&nbsp;", "").replace("Define any supplier third party property, if required for this engagement or N/A if not applicable.", "").replace("Supplier shall provide and pay for the following third party property to complete the Work:", "").replace(" ", "");
			temp1 = temp.trim();
			System.out.println(temp1);
			if(temp1.length()>1){
				flag = 1;
				break;
			}	
	}
		if(flag==1){
			System.out.println("Third party property green");
		}
		else{
			System.out.println("Third party property red");
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}
	}
	
	
	private static void Expenses(String s){
		int alert = 0;
		int temp1 = 0;
		int start = s.indexOf("Cisco shall reimburse");
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+2).equals("<h")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		//System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote("shall not exceed <b>") + "(.*?)" + Pattern.quote("</b>")); //Removes value from expense section
		Matcher n = q.matcher(text);
		while (n.find()) {
		  temp1 = Integer.parseInt(n.group(1).replaceAll("[^0-9]","").trim());
		  text = text.replaceAll("\\<.*?>","").replaceAll("&nbsp;","").trim();
		  System.out.println(text);
		  System.out.println(temp1);
		}
		
		if((temp1==0)||(temp1==0.00)){
			//do nothing;
			
			System.out.println("Expense class green");
		}
				
		else{
		int startPay = s.indexOf("PaymentMethod");
		int endPay = 0;
		System.out.println(startPay);
		for (int j=startPay; ; j++){
			if(s.substring(j, j+3).equals("<h2")){
				endPay = j;
				break;
			}
		}
		String textPay = s.substring(startPay, endPay);
		Pattern p = Pattern.compile("(?<=\\<tbody\\>)(.+?)(?=\\<\\/tbody\\>)");
		Matcher m = p.matcher(textPay);
		text = "";
		while(m.find()){
			text += m.group();	
		}
		System.out.println(text);
		startPay = text.indexOf("Expenses (total from section 8c)");
		endPay = 0;
		System.out.println(startPay + " " + text.length());
		for (int j=startPay; ; j++){
			if(text.substring(j, j+4).equals("</tr")){
				endPay = j;
				break;
			}
		}
		System.out.println(endPay);
		q = Pattern.compile(Pattern.quote("<b>") + "(.*?)" + Pattern.quote("</b>"));
		n = q.matcher(text.substring(startPay, endPay));
		ArrayList<Integer> expen = new ArrayList<Integer>();
		while(n.find()){
			expen.add(Integer.parseInt(n.group(1).replaceAll("[^0-9]","").trim()));
		}
		
		startPay = text.indexOf("Subtotal of 8b");
		endPay = 0;
		for (int j=startPay; ; j++){
			if(text.substring(j, j+4).equals("</tr")){
				endPay = j;
				break;
			}
		}
		q = Pattern.compile(Pattern.quote("<b>") + "(.*?)" + Pattern.quote("</b>"));
		n = q.matcher(text.substring(startPay, endPay));
		ArrayList<Integer> subtotal = new ArrayList<Integer>();
		while(n.find()){
			if(n.group(1).replaceAll("[^0-9]","").trim().equals(""))
				continue;
			else
				subtotal.add(Integer.parseInt(n.group(1).replaceAll("[^0-9]","").trim()));
		}
		
		startPay = text.indexOf("Total of 8b and 8c");
		endPay = 0;
		for (int j=startPay; ; j++){
			if(text.substring(j, j+4).equals("</tr")){
				endPay = j;
				break;
			}
		}
		q = Pattern.compile(Pattern.quote("<b>") + "(.*?)" + Pattern.quote("</b>"));
		n = q.matcher(text.substring(startPay, endPay));
		ArrayList<Integer> total = new ArrayList<Integer>();
		while(n.find()){
			total.add(Integer.parseInt(n.group(1).replaceAll("[^0-9]","").trim()));
		}
		
		int sum=0;
		for (int i: expen) {
	        sum += i;
	    }
		//System.out.println(sum==temp1);
		int size = expen.size();
		for (int i=0; i < size; i++){
			if (subtotal.get(i)+expen.get(i) != total.get(i)){
				alert = 1;
				break;
			}
			}
		if ((alert==0)&&(sum==temp1)){
			System.out.println("Expense class green");
		}
		else {
			System.out.println("Expense class red");
		}
		
		}
		
		
		
		
		
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}
	}

	private static void WorkProduct(String s) {
		int flag = 0;
		int alert = 0;
		int flag2 = 0;
		String temp1 = "";
		String temp2 = "";
		String temp3 = "";
		String tempdisplay="";
		String finaldisplay = "";
		int start = 0;
		int count = 0;
		s=s.toLowerCase();
		int starttemp = s.indexOf("id=\"workproduct\"");
		while (starttemp >= 0) {
			if (starttemp > -1)
				start = starttemp;
			starttemp = s.indexOf("id=\"workproduct\"", start + 1); //To take the second id of work product. Two same id's due to doc conversion
		}
		int end = 0;
		int firstcount = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h2")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			System.out.println(text);
			//text = removeSpecialCharacters(text);
			Pattern q = Pattern.compile(Pattern.quote("<tr>") + "(.*?)"
					+ Pattern.quote("</tr>"));
			Matcher n = q.matcher(text);
			
			while (n.find()) {

				temp1 = n.group().trim();
				//System.out.println(n.group());
				
				Pattern c = Pattern.compile(Pattern.quote("<td>") + "(.*?)"
						+ Pattern.quote("</td>"));
				Matcher d = c.matcher(temp1);
				while(d.find()){
					//System.out.println(d.group());
					String temp7 = d.group(1).trim().toLowerCase();
					if (temp7.contains("phase/gate") || temp7.contains("deliverable name")||temp7.contains("format")|| temp7.contains("location")|| temp7.contains("no.")|| temp7.contains("due date")){
						firstcount = firstcount +1;
					}
				}
				//System.out.println(firstcount);
				Pattern p = Pattern.compile(Pattern.quote("<td>") + "(.*?)"
						+ Pattern.quote("</td>"));
				Matcher m = p.matcher(temp1);
				flag = 0;
				flag2 = 0;
				// System.out.println("FLAGS" + flag + flag2 + n.group(1));
				while (m.find()) {
					//System.out.println(m.group(1));
					tempdisplay = Jsoup.parse(m.group(1)).text() + "\t\t\t"; //for table format
					finaldisplay = finaldisplay + tempdisplay ;
					//System.out.println(finaldisplay);
					count = count + 1;
					if ((count%4)==0){
					finaldisplay = finaldisplay + "\n";	
					}
					
					
					temp2 = m.group(1).replace("?????", "").replace("&nbsp;", "").trim(); // ????? being
																// used
					temp2 = temp2.replaceAll("[^\\x00-\\x7F]", "");
					temp2  = temp2.trim();
					temp3 = Jsoup.parse(temp2).text().toLowerCase();
					if (temp3.equals("")||temp3.equals("na")||temp3.equals("n/a")||temp3.equals("none")) {
						flag = 1;
						 System.out.println(temp3 + " " + flag);
					} else {
						flag2 = 2;
						 System.out.println(temp3 + " " + flag2);
					}
				}
				if ((flag == 1 && flag2 == 2) || (firstcount!=4)) {
					alert = 1;
					System.out.println("Alert raising" + flag + flag2 + firstcount);
					System.out.println(finaldisplay);
					break;
				}
			}
			
			if (alert == 1) {
				System.out.println("Work Product is red"); // Work Product table
															// is not populated
				
			} else {
				System.out.println("Work Product is green"); // Work Product
																// table is
				System.out.println(finaldisplay);											// populated
							}
		}

		catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			
		}
	}
	

	private static void CiscoPreexistingTerms(String s){
		int alert = 0;
		int flag = 0;
		int flag2 = 0;
		String temp1 = "";
		String temp2 = "";
		String temp3 = "";
		int start = s.indexOf("CiscoPreExistingProperty");
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+3).equals("<h2")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		Pattern q = Pattern.compile(Pattern.quote("<tr>") + "(.*?)" + Pattern.quote("</tr>"));
		Matcher n = q.matcher(text);
		int i=0;
		int j=0;
		while (n.find()) {
			if (alert == 1){
				break;
			}
			if (i==0){
				i++;
				continue;
			}
		  temp1 = n.group().trim();
		  Pattern p = Pattern.compile(Pattern.quote("<td>") + "(.*?)" + Pattern.quote("</td>"));
		Matcher m = p.matcher(temp1);
		flag = 0;
		flag2 = 0;
		j = 0;
		
		//System.out.println("FLAGS" + flag + flag2 + n.group(1));
		while(m.find()){
			if (j == 0){
				temp2 = m.group(1).trim();
				temp3= Jsoup.parse(temp2).text().trim();
				if (temp3.equals("")){
					flag2 = 1;
				}
				j++;
			}else if(j == 1){
				temp2 = m.group(1).trim();
				temp3= Jsoup.parse(temp2).text().trim();
				
				if(temp3.equals("") && flag2 == 0){
					alert = 1;
					break;
				}else {
					if (flag2 == 1 && !temp3.equals("")){
						alert = 1;
						break;
					}else if (temp3.equalsIgnoreCase("yes")){
						flag = 1;
					}else if(temp3.equalsIgnoreCase("no")){
						flag = 2;
					}
				}
				j++;
			}else if (flag == 1){
				temp2 = m.group(1).trim();
				temp3= Jsoup.parse(temp2).text().trim();
				temp3 = temp3.replace("(Select)","").replace("(select)","").replace("select","").replace("Select","");
				if (temp3.equals("")){
					alert = 1;
					break;
				}
				j++;
			}else if (flag == 2){
				j++;
			}else{
				if (flag2 == 1){
					temp2 = m.group(1).trim();
					temp3= Jsoup.parse(temp2).text().trim();
					if (temp3.equals("")){
						
					}else{
						alert = 1;
						break;
					}
				}
				j++;
			}
		}
		i++;
		}
			if (alert == 1){
				System.out.println("Cisco preexisting property node red");
			}
			else{
				System.out.println("Cisco preexisting property node green");
			}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("Cisco preexisting property node red");
			}
		}
	
	private static void SLA(String s){
		int flag=0;
		String what ="";
		String sentence="Scope of services and work product *   Supplier shall provide following services to Cisco.:;";
		sentence= sentence.toLowerCase().replaceAll("scope of services and work product","").replace("*","").replace(".","").replaceAll(";","").replaceAll(":", "").toLowerCase().trim();
		String toAvoid="supplier shall provide following services to cisco";
		if(!sentence.equals(toAvoid)){
			System.out.println(sentence);
			System.out.println(" not equal");
		}
		else{
			flag=1;
		System.out.println(sentence);
		what=what+sentence;
		System.out.println("equal");
		}
		System.out.println("what::" + sentence);
		
		
		
		String measurement = null;
		String target = null;
		String frequency = null;
		int frequencycheck = 0;
		int targetcheck=0;
		int measurementcheck = 0;
		int finalcheck = 0;
		int start = s.indexOf("id=\"SLA\"");
		int end = 0;
		int warning=0;
	try{
		for (int i = start; ; i++){
			if(s.substring(i, i+3).equals("<h2")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		ArrayList<Integer> a = new ArrayList<Integer>();
		Pattern b = Pattern.compile("Service Level Agreement #"); //Stores the index of this string and searches for 3 terms(Measurement, Target and frequency) in it and then checks if it is populated.
		Matcher b1 = b.matcher(text);
		while (b1.find()) {
			a.add(b1.start());
		}
		a.add(text.length());
		String finaldisplay = "";
		for(int i=0;i < a.size()-1; i++){
			String temp = text.substring(a.get(i), a.get(i+1));
				

			if (temp.toString().toLowerCase().contains("ongoing") || temp.toString().toLowerCase().contains("on-going") || temp.toString().toLowerCase().contains("on going") )  {
				System.out.println("Ongoing term present");
				warning =1;
			}
			else{
				Pattern r = Pattern.compile(Pattern.quote("Measurement:") + "(.*?)" + Pattern.quote("</p>"));
				Matcher o = r.matcher(temp);
				while (o.find()) {
				measurement = o.group(1).replaceAll("_","").trim();
				finaldisplay = "\n" + finaldisplay + o.group() ;	
				System.out.println(measurement);
				if (!measurement.equals("")){
				  measurementcheck =  1;
				  }
				}
				Pattern q = Pattern.compile(Pattern.quote("Target:") + "(.*?)" + Pattern.quote("</p>"));
				Matcher n = q.matcher(temp);
				while (n.find()) {
				  target = n.group(1).replaceAll("_","").trim();

					finaldisplay = "\n" + finaldisplay + n.group();
				  System.out.println(target);
				  if(!target.equals("")){
				  targetcheck =  1;
				  }
				}
				Pattern p = Pattern.compile(Pattern.quote("Frequency:") + "(.*?)" + Pattern.quote("</p>"));
				Matcher m = p.matcher(temp);
				while (m.find()) {
				
				  frequency = m.group(1).replaceAll("_","").trim();
				finaldisplay = "\n" + finaldisplay + m.group() + "\n";
				  System.out.println(frequency);
				  if (!frequency.equals("")){
					  frequencycheck = 1;
				  }
				}
				System.out.println(measurementcheck + "" + targetcheck + "" + frequencycheck);
				if ((frequencycheck == targetcheck)&& (targetcheck==measurementcheck)&& (measurementcheck == 1))
				{
					finalcheck = finalcheck + 1;
				}
				frequencycheck = targetcheck = measurementcheck = 0;
						System.out.println(finalcheck);
			}
	
			}
			
		if ((finalcheck >=2)&&(warning==0)){
			System.out.println("SLA class green"); 
			finaldisplay = Jsoup.parse(finaldisplay).text();
			System.out.println(finaldisplay); //return class green
		}
		else {
			System.out.println("SLA class red"); //return class red
		}
	}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("SLA class red"); // return class red
			}
		}
	
	private static void TermSOW(String s){
		String startDate = null;
		String endDate = null;
		int start = s.indexOf("id=\"TermSOW\"");
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+3).equals("<h1")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		
		Pattern q = Pattern.compile(Pattern.quote("begin on") + "(.*?)" + Pattern.quote("and remain")); //Extracts start date
		Matcher n = q.matcher(text);
		while (n.find()) {
		  startDate = n.group(1).trim();
		  System.out.println(startDate);
		}
		Pattern p = Pattern.compile(Pattern.quote("Work Product,") + "(.*?)" + Pattern.quote(", or")); //Extracts end date
		Matcher m = p.matcher(text);
		while (m.find()) {
		  endDate = m.group(1).trim();
		  System.out.println(endDate);
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}
		}

	
	private static void TrialExpenses(String s){
		int temp1 = 0;
		int start = s.indexOf("Cisco's maximum liability for all expenses");
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+2).equals("<h")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote("shall not exceed ") + "(.*?)" + Pattern.quote("</p>")); //Removes value from expense section
		Matcher n = q.matcher(text);
		while (n.find()) {
		  temp1 = Integer.parseInt(n.group(1).replaceAll("[^0-9]","").trim());
		  System.out.println(temp1);
		}
				
		int startPay = s.indexOf("PaymentMethod");
		int endPay = 0;
		//System.out.println(startPay);
		for (int j=startPay; ; j++){
			if(s.substring(j, j+3).equals("<h2")){
				endPay = j;
				break;
			}
		}
		String textPay = s.substring(startPay, endPay);
		String regexString = Pattern.quote("Expenses (total") + "(.*?)" + Pattern.quote("Total of");
		textPay = s.substring(startPay, endPay);
		Pattern p = Pattern.compile(Pattern.quote("<tr>") + "(.*?)" + Pattern.quote("</tr>"));
		Matcher m = p.matcher(textPay);
		text = "";
		while(m.find()){
			//System.out.println(m.group(1));
			text += m.group();	
		}
		//System.out.println(text);
		startPay = text.indexOf("Expenses (total from section 8c)");
		endPay = 0;
		//System.out.println(startPay + " " + text.length());
		for (int j=startPay; ; j++){
			if(text.substring(j, j+5).equals("</tr>")){
				endPay = j;
				break;
			}
		}
		//System.out.println(endPay);
		q = Pattern.compile(Pattern.quote("<p(\\sdir\\=\"ltr\")>") + "(.*?)" + Pattern.quote("</p>"));
		n = q.matcher(text.substring(startPay, endPay));
		ArrayList<Integer> expen = new ArrayList<Integer>();
		while(n.find()){
			System.out.println(n.group(1));
			expen.add(Integer.parseInt(n.group(1).replaceAll("[^0-9]","").trim()));
		}
		//System.out.println(expen);
		
		int sum=0;
		for (int i: expen) {
	        sum += i;
	    }
		System.out.println(sum);
		if (sum==temp1){
			System.out.println("Expense class green");
		}
		else {
			System.out.println("Expense class red");
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}
	}
		
}

//int result = FuzzySearch.ratio("mysmilarstring","mysimilarstring");
//System.out.println(result);


// error due to doc conversion in expenses class. 