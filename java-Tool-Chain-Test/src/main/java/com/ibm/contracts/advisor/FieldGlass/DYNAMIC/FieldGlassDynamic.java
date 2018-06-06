package com.ibm.contracts.advisor.FieldGlass.DYNAMIC;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ibm.contracts.advisor.FieldGlass.FGUtils.UtilityFunctions;
import com.ibm.contracts.advisor.constants.APICredentials;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.SplitPropSet;
import com.ibm.contracts.advisor.parser.CiscoVcapUtils;
import com.ibm.contracts.advisor.parser.VcapSetupParser;
import com.ibm.contracts.advisor.vo.FieldGlassOPOBJ;
import com.ibm.contracts.advisor.vo.IndexPairVO;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;



public class FieldGlassDynamic implements Constants{

	private static List<FieldGlassOPOBJ> fgop = new ArrayList<FieldGlassOPOBJ>();
	
	private static JSONObject dynamicResult = null;
	
//	FieldGlassOPOBJ supplierNameOBJ = new FieldGlassOPOBJ();
//	FieldGlassOPOBJ companyCodeOBJ = new FieldGlassOPOBJ();
//	FieldGlassOPOBJ acceptanceCriteriaOBJ = new FieldGlassOPOBJ();
//	FieldGlassOPOBJ workLocationOBJ = new FieldGlassOPOBJ();
//	FieldGlassOPOBJ thirdPartyPropertyOBJ = new FieldGlassOPOBJ();
//	FieldGlassOPOBJ scopeOBJ = new FieldGlassOPOBJ();
//	FieldGlassOPOBJ scopeOBJ = new FieldGlassOPOBJ();
	
	public static void main(String[] ab) {

		long startTime = System.nanoTime();

		String filePath = "C:/Users/arnabisw/Downloads/";
		String fileName = "demo_user2_1515645892859_CSCOTQ00109377_MS_Great.html";

		String supplierName = "";
		String companyCode = "";
		String acceptanceCriteria = "";
		String workLocation = "";
		String thirdPartyProperty = "";
		String scope = "";
		String slaclass="";
		String workproduct="";
		String liquidateddamages="";
		String paymentmethod="";
		String acceptancemilestones="";
		String supplierkey="";

		
		File file = new File(filePath + fileName);
		Document doc = null;
		try {
			doc = Jsoup.parse(file, "UTF-8");
			// System.out.println(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String content = doc.toString();

		// Getting the supplier Name
		try {
			supplierName = getSupplierName(content);
			// companyCode = checkCompanyCode(content);
			// acceptanceCriteria = checkAcceptanceCriteria(content);
			//FieldGlassOPOBJ obj = ProcessFieldGlassDynamic(content);
			//System.out.println(obj);
			//workLocation =  checkWorkLocation(content);
			//thirdPartyProperty = checkThirdpartyProperty(content);
			//ProcessFieldGlassDynamic(content,MSFG);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println("supplierName :: "+ supplierName);
		JSONObject dynamicResult = new JSONObject();
		for(int i = 0; i < fgop.size(); i++){
			FieldGlassOPOBJ temp = fgop.get(i);
			dynamicResult.put(temp.getClassName(), temp.getFlag());
			
			System.out.println("========================================");
			System.out.println(temp.getClassName());
			System.out.println(temp.getFlag());
			System.out.println(temp.getOutput());
		}
		
		System.out.println("dynamicResult \n "+dynamicResult);

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		System.out.println("TIme taken to Calculate Score :: " + duration);

	}

	/*
	 * 
	 * The following function will take input as a String(HTML version of
	 * document). In output it will return a FieldGlassOPOBJ(read Field Glass
	 * Output Object), which is a pair of
	 */
	public static List<FieldGlassOPOBJ> ProcessFieldGlassDynamic(String fileContent, String contractType) {

		String supplierName = "";
		String acceptanceCriteria = "";
		String workLocation = "";
		String thirdPartyProperty  ="";
		String termsOfSOW = "";
		String expenses="";
		String ciscopreexisting="";
		String scope="";
		String slaclass="";
		String workproduct="";
		String liquidateddamages="";
		String paymentmethod="";
		String acceptancemilestones="";
		String supplierkey="";		
		
		if((MPFG).equalsIgnoreCase(contractType)){
			
			try {
				supplierName = getSupplierName(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			try {
				acceptanceCriteria = checkAcceptanceCriteria(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			try {
				workLocation =  checkWorkLocation(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				thirdPartyProperty = checkThirdpartyProperty(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				termsOfSOW = checkTermsOfSOW(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				expenses= checkExpenses(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				ciscopreexisting = checkCiscoPreExisting();
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				scope = checkScope(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				workproduct = checkWorkProduct(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				paymentmethod=checkPaymentMethod(fileContent,"paymentMethod");
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				acceptancemilestones=checkPaymentMethod(fileContent,"acceptanceMilestones");	
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				supplierkey = checkSupplierKey(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		else if((MSFG).equalsIgnoreCase(contractType)){
			
			try {
				supplierName = getSupplierName(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			try {
				acceptanceCriteria = checkAcceptanceCriteria(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			try {
				workLocation =  checkWorkLocation(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				thirdPartyProperty = checkThirdpartyProperty(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				termsOfSOW = checkTermsOfSOW(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				expenses= checkExpenses(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				ciscopreexisting = checkCiscoPreExisting();
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				scope = checkScope(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				slaclass = checkSLAclass(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				workproduct = checkWorkProduct(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}try {
				liquidateddamages = checkLiquidatedDamages(fileContent);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				paymentmethod=checkPaymentMethod(fileContent, "paymentMethod");
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				supplierkey = checkSupplierKey(fileContent);
			}catch(Exception e){	
				e.printStackTrace();
			}		
						
		}
		return fgop;	
		
	}
	private static String checkSupplierKey(String fileContent){
		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("SupplierKeyPersonnel");
		
		JSONObject jsondisplay = new JSONObject();
		
		int flagcontact=0;
		int flagname=0;
		int flagposition=0;
		String finaldisplay="";
		String name="";
		String contact="";
		String role="";
		try {
			int start1 = fileContent.indexOf("<p>Supplier Key Personnel");
			int end1 = 0;
			for (int i=start1; ; i++){
				if(fileContent.substring(i, i+5).equals("</tr>")){
					end1 = i;
					break;
				}
			}

			String text = fileContent.substring(start1, end1);
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
			contact =contact +" Email ID : " + tempemail + ";";
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

		        contact =contact + " Phone no : " + tempphone + ";";
				
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
		        		name = name +" "+ word;
		        		flagname=1;
		        	}
		        }
		        }
		        }
	
		        
		        String[] list = {"producer and director","producer & director","producer","director","program manager","service delivery manager","account manager","senior procurement manager","procurement manager","client delivery manager","delivery manager","resource manager","senior manager","supplier engagement manager","project manager","program manager", "manager","sr. mgr.","evp operations"," pm ","associate partner","practice leader", "partner", "project management leads","project lead","supplier lead","lead","senior consultant","consultant"};
		        for (int i=0;i<list.length;i++){
		        	if (text.contains("PM")){
		        		text=text.replace("PM"," pm ");
		        	}
		        	text=text.toLowerCase();
		        	if(text.contains(list[i])){
		        		flagposition=1;
		        		role = list[i];
		        		System.out.println(list[i]);
		        		break;
		        	}
		        }
		        System.out.println(finaldisplay);
		        System.out.println(flagname + " "+ flagcontact + " "+ flagposition);
		        
		        jsondisplay.put("ENGAGEMENT MANAGER", name);
				jsondisplay.put("ROLE", role);
				jsondisplay.put("CONTACT", contact);
				finaldisplay = jsondisplay.toString();
		 
		        
		        if((flagposition==1)&&(flagname==1)&&(flagcontact==1)){

			        System.out.println("Supplier Key Personnel class is green");
			        System.out.println(finaldisplay);
					 obj.setOutput(finaldisplay);
						obj.setFlag(true);
		        }
		        else{

			        System.out.println("Supplier Key Personnel Class is red");
					 obj.setOutput(finaldisplay);
					 obj.setFlag(false);
		        }
        }		
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");

			 obj.setOutput(finaldisplay);
				obj.setFlag(false);
			}
		fgop.add(obj);
		return null;
	}
	
	private static String checkPaymentMethod(String fileContent, String className){
		
		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("PaymentMethod");
		
		FieldGlassOPOBJ obj1 = new FieldGlassOPOBJ();
		obj1.setClassName("AcceptanceMilestones");
		
		String stringstart="";
		String startDate = null;
		String endDate = null;
		String testDate= null;
		String trtext="";
		String tdtext="";
		String paymentDisplay="";
		String startFilledtext="";
		
		if (fileContent.contains("Statement of Work Payment Characteristics")){
			startFilledtext ="Statement of Work Payment Characteristics";
			stringstart=startFilledtext;
		}
		else if (fileContent.contains("Events")){
			startFilledtext ="Events";
			stringstart=startFilledtext;
		}
		else if (fileContent.contains("Schedules")){
			startFilledtext ="Schedules";
			stringstart=startFilledtext;
		}
		
		

		int start1 = fileContent.indexOf("Statement of Work Period");
		int end1 = 0;
		
		int start2 = fileContent.indexOf(stringstart);
		int end2 = 0;
		
		int startFilled = fileContent.indexOf(startFilledtext);
		int endFilled = 0;

		int flag = 0;
		int counttr=0;
		int flagAmount=0;
		int flagName=0;
		ArrayList<String> DateList = new ArrayList<String>();
		try{
			for (int i=start1; ; i++){
				if(fileContent.substring(i, i+4).equals("</tr")){
					end1 = i;
					break;
				}
			}
			String text = fileContent.substring(start1, end1);
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
					if(fileContent.substring(i, i+7).equals("</html>")){
						end2 = i;
						break;
					}
				}
				String textpayment = fileContent.substring(start2, end2);
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
			if(fileContent.substring(i, i+8).equals("</table>")){
				endFilled = i;
				break;
			}
		}
		String textFilled = fileContent.substring(startFilled, endFilled);
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
					if (((counttd==7)&&(fileContent.contains("Service Level Agreements")))||(counttd==9))
							{ //For amount
					tdtext=b.group(1);
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
	        	DisplayList.add(DateList.get(i) + " ; " + NameList.get(i)+ " ; " + AmountList.get(i) );
	        	FinalDisplayList.add(DisplayList.get(i));
	        }
		  paymentDisplay="";
     	  paymentDisplay="Dates mentioned in the table align with start and end date of TermSOW, Names column is filled and doesn't have duplicate values, Amount column is filled.";
     	  paymentDisplay = paymentDisplay +" " +FinalDisplayList;
     	   System.out.println(paymentDisplay);
	     	
	     	
     	 obj.setOutput(paymentDisplay);
			obj.setFlag(true);
			obj1.setOutput(paymentDisplay);
			obj1.setFlag(true);
		}
		else{
			System.out.println("Payment class fail");
			System.out.println(paymentDisplay);
			 obj.setOutput(paymentDisplay);
				obj.setFlag(false);
				obj1.setOutput(paymentDisplay);
				obj1.setFlag(false);
		}
				}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			 obj.setOutput(paymentDisplay);
				obj.setFlag(false);
				 obj1.setOutput(paymentDisplay);
					obj1.setFlag(false);
			}
		
			if(className.equalsIgnoreCase("paymentMethod")){
				fgop.add(obj);
			}
			
			if(className.equalsIgnoreCase("acceptanceMilestones")){
				fgop.add(obj1);
			}			
			
			return null;
	}
	
	
	private static String checkLiquidatedDamages(String fileContent){
		
		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("LiquidatedDamages");
		
		String ldMissed="";
		String ldCredit="";
		String ldDisplay="";
		int flag1 = 0;
		int flag2 = 0;
		
		int start1 = fileContent.indexOf("SLA Missed Months");
		int end1 = 0;
		try{
		for (int i=start1; ; i++){
			if(fileContent.substring(i, i+4).equals("<tr>")){
				end1 = i;
				break;
			}
		}
		String textMissed = fileContent.substring(start1, end1);
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
		int start2 = fileContent.indexOf("SLA Credit terms");
		int end2 = 0;
		
		for (int i=start2; ; i++){
			if(fileContent.substring(i, i+4).equals("<tr>")){
				end2 = i;
				break;
			}
		}
		String textCredit = fileContent.substring(start2, end2);
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
			 obj.setOutput(ldDisplay);
				obj.setFlag(true);
		}
		else{

			System.out.println("LiquidatedDamages is red");
			 obj.setOutput(ldDisplay);
				obj.setFlag(false);
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			 obj.setOutput("Liquidated Damages exception");
				obj.setFlag(false);
			}
		fgop.add(obj);
		return null;
	}
	
	
	
	
	
	private static String checkWorkProduct(String fileContent){

		JSONObject jsondisplay = new JSONObject();
		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("WorkProduct");
		
		String wpDescription="";
		String wpFormat="";
		String wpLocation="";
		String wpDisplay="";

		String wpFormat1="";
		String wpLocation1="";
		String wpDescription1="";

		int flag1 = 0;
		int flag2 = 0;
		int flag3 = 0;
		int flagMS=0;
		
		if(fileContent.contains("Service Level Agreements")){
			flagMS =1;
			System.out.println(flagMS);
		}
		
		int start1 = fileContent.indexOf("Work Product Description (Required)");
		int end1 = 0;
		try{
		for (int i=start1; ; i++){
			if(fileContent.substring(i, i+37).equals("Work Product Description, continued 2")){
				end1 = i;
				break;
			}
		}
		String textDescription = fileContent.substring(start1, end1);
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
			wpDescription1=wpDescription;
		}
		
		//===========================================================================
		int start2 = fileContent.indexOf("Specifications for Work Product");
		int end2 = 0;
		
		for (int i=start2; ; i++){
			if(fileContent.substring(i, i+42).equals("Additional Specifications for Work Product")){
				end2 = i;
				break;
			}
		}
		String textFormat = fileContent.substring(start2, end2);
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
			wpFormat1=wpFormat;
		}
		//=========================================================================
		
		int start3 = fileContent.indexOf("Primary Work Site");
		int end3 = 0;
		
		for (int i=start3; ; i++){
			if(fileContent.substring(i, i+4).equals("<tr>")){
				end3 = i;
				break;
			}
		}
		String textLocation = fileContent.substring(start3, end3);
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
			wpLocation1= wpLocation;
		}
		//======================================================================
		
		jsondisplay.put("LOCATION", wpLocation1);
		jsondisplay.put("FORMAT", wpFormat1);
		if (flagMS==1){
		jsondisplay.put("SERVICE", wpDescription1);
		}
		else{
		jsondisplay.put("DELIVERABLE", wpDescription1);
		}
		
		wpDisplay = jsondisplay.toString();

		if ((flag1==1)&&(flag2==1)&&(flag3==1)){
			System.out.println("Work Product is green");
			System.out.println(wpDisplay);
	        obj.setOutput(wpDisplay);
			obj.setFlag(true);
		}
		else{
			System.out.println("Work Product is red");
			obj.setOutput(wpDisplay);
			obj.setFlag(false);
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
	        obj.setOutput("Work Product Class not Found");
			obj.setFlag(false);
			}

        fgop.add(obj);
		return null;
	}
	
	
	
	private static String checkSLAclass(String fileContent){
		
		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("SLA");
		
		String display = "";
	
		String finaldisplay = "";
		String target = "";
		int targetcheck=0;
		int start = fileContent.indexOf("c) Service Level Agreements");
		int end = 0;
		int freqcheck=0;
		int count = 0;
		int k = 0;
	try{
		for (int i=start; ; i++){
			if(fileContent.substring(i, i+16).equals("SLA Credit terms")){
				end = i;
				break;
			}
		}
		String[] list = {"annual","annually","monthly","daily","weekly","hourly"};
		String text = fileContent.substring(start, end);
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
			display = display + "Service Level Agreement #" + temp;
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
	        obj.setOutput("One of the criteria is not met in one of the two mandatory SLAs :: " + display.replaceAll("\\<.*?>",""));
			obj.setFlag(false);
		}
		else {
			System.out.println("SLA class green");
	        obj.setOutput(display.replaceAll("\\<.*?>",""));
			obj.setFlag(true);
		}
	}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("SLA class exception"); // return class red
	        obj.setOutput("SLA class not found");
			obj.setFlag(false);
			}

        fgop.add(obj);
		return null;
		}

	
	
	
	private static String checkScope(String fileContent) {
		 
		String finalDisplay="";
		JSONObject jsondisplay = new JSONObject();
 
		
		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("Scope");
		
		String finalText="";
		String CONVERSATION_USERNAME = null;
		String CONVERSATION_PASSWORD =null;
		String CONVERSATION_URL = null;
		
		int flag1 = 0;
		int flag2 = 0;
		int flag3 = 0;
		String how="";
		String what="";
		String why="";
		String who="";

		if(CISCOVCAP){
			//CiscoVcapUtils.getConversationCredentials(CiscoVcapUtils.VCAPUtils());
			 CONVERSATION_USERNAME = CiscoVcapUtils.convUsername;
			 CONVERSATION_PASSWORD = CiscoVcapUtils.convPassword;
			 CONVERSATION_URL = CiscoVcapUtils.convUrl;
		}else{
			if (VCAPCONVERSATION) {
				
				/*VcapSetupParser.setConversationCredentials(VcapSetupParser
						.VCAPUtils());
				VcapSetupParser.setEnvironmentVariable();
				*/
				
				CONVERSATION_USERNAME = VcapSetupParser.VCAPCONVERSATION_userID;
				CONVERSATION_PASSWORD = VcapSetupParser.VCAPCONVERSATION_password;
				CONVERSATION_URL = VcapSetupParser.ENV_SCOPE_CONVERSATION_WORKSPACEID;
			} else {
				
				/*CONVERSATION_USERNAME = "eb88d8fc-94d6-4f0d-a2c3-83ed3347102b";
				CONVERSATION_PASSWORD = "JGGdWPlhDqNO";
				CONVERSATION_URL = "db7521b1-f9f8-490a-8a38-c6b89606d8e4";*/
				
				CONVERSATION_USERNAME = APICredentials.CONVERSATION_USERNAME;
				CONVERSATION_PASSWORD = APICredentials.CONVERSATION_PASSWORD;
				CONVERSATION_URL = APICredentials.SCOPE_CONVERSATION_WORKSPACEID;
			}
		}
		
		int start = fileContent.indexOf("SCOPE OF SERVICES AND WORK PRODUCT");
		int end = 0;
		try
		{
					for (int i=start; ; i++)
					{
							if(fileContent.substring(i, i+15).equals("b) Work Product"))
							{
							end = i;
							break;
							}
					}
					String text = fileContent.substring(start, end);
					//System.out.println(text);
					Pattern q = Pattern.compile(Pattern.quote("Scope Description") + "(.*?)" + Pattern.quote("<tr>")); //Extracts start date
					Matcher n = q.matcher(text);
					while (n.find()) 
					{
						finalText = n.group(1);
						finalText = finalText.replaceAll("(Required)","").replaceAll(", continued","").replaceAll("\\<.*?>","").trim(); 
						System.out.println(finalText);
					
						String[] sent=finalText.split(SplitPropSet.patternSplitSentence);
						ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2017_02_03);
						service.setUsernameAndPassword(CONVERSATION_USERNAME, CONVERSATION_PASSWORD);

						for(String sentence:sent)
						{
							MessageRequest newMessage = new MessageRequest.Builder().alternateIntents(true).inputText(sentence).build();
							MessageResponse response = null;
							response = service.message(CONVERSATION_URL, newMessage).execute();
							String resp = response.toString();
							System.out.println(response);
							JSONParser parser = new JSONParser();
							JSONObject json = (JSONObject) parser.parse(resp);
							System.out.println(json.get("intents"));

							for (int i=0; i < ((JSONArray)json.get("intents")).size(); i++)
							{
									if (((JSONObject)((JSONArray)json.get("intents")).get(i)).get("intent").toString().equals("What"))
									{
										if (Double.parseDouble(((JSONObject)((JSONArray)json.get("intents")).get(i)).get("confidence").toString()) >= 0.7)
										{
											flag1 = 1;
											what = what + sentence;
										}else
										{
											//jsondisplay.put("What", "Intent absent");
										}
									}
									if (((JSONObject)((JSONArray)json.get("intents")).get(i)).get("intent").toString().equals("Why"))
									{
										if (Double.parseDouble(((JSONObject)((JSONArray)json.get("intents")).get(i)).get("confidence").toString()) >= 0.7)
										{
											flag2 = 1;
											why = why + sentence;
											}
										else{
											//jsondisplay.put("Why", "Intent absent");
										}
									}
									if (((JSONObject)((JSONArray)json.get("intents")).get(i)).get("intent").toString().equals("How"))
									{
										if (Double.parseDouble(((JSONObject)((JSONArray)json.get("intents")).get(i)).get("confidence").toString()) >= 0.7)
										{
											flag3 = 1;
											how = how + sentence;
										}else{
											//jsondisplay.put("How", "Intent absent");
										}
									}
									
							}
						}
					}
		//==================================
		
					int startWho = fileContent.indexOf("SOW Supplier Oracle Name");
					int endWho = 0;
					//start
					for (int j=startWho; ; j++){
						if(fileContent.substring(j, j+7).equals("GENERAL")){
							endWho = j;
							break;
						}
					}
					String textWho = fileContent.substring(startWho, endWho);
					System.out.println(textWho);
					Pattern r = Pattern.compile(Pattern.quote("SOW Supplier Oracle Name") + "(.*?)" + Pattern.quote("1)")); 
					Matcher t = r.matcher(textWho);
					while (t.find()) {
						who = t.group(1).replaceAll("\\<.*?>","").trim(); 
						System.out.println(who);
					}		
					//end
		//==================================
							
		jsondisplay.put("WHAT", what);
		jsondisplay.put("WHY", why);
		jsondisplay.put("WHO", who);
		jsondisplay.put("HOW", how);
		finalDisplay = jsondisplay.toString();
 
	        if((flag1==1)&&(flag2==1)&&(flag3==1))
	        {
	            System.out.println("Green");
	            System.out.println(finalDisplay);
	            obj.setOutput(finalDisplay);
				obj.setFlag(true);
	        }
	        else{
	            System.out.println("Red");
	            System.out.println(finalDisplay);
	            obj.setOutput(finalDisplay);
				obj.setFlag(false);
	                        
	        }    
	    }
		catch(Exception e){
	            e.printStackTrace();
	            System.out.println("catch exception");
	            obj.setOutput("Scope class not found");
				obj.setFlag(false);
	        }  
	        
	        fgop.add(obj);
			return null;
	}

	
	/**
	 * Start Date & End Date for Statement of Work Period (Applicable to MP & MS)
	 * Presence of dates or should we tie this back to Payment characteristics table? Still not clear.
	 * 
	 * */
	private static String checkTermsOfSOW(String fileContent) {
		String startDate = null;
		String endDate = null;
		String output="";
		int start = fileContent.indexOf("Statement of Work Period");
		int end = 0;
		

		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("TermSOW");
		
		try{
		for (int i=start; ; i++){
			if(fileContent.substring(i, i+4).equals("</tr")){
				end = i;
				break;
			}
		}
		String text = fileContent.substring(start, end);
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
		if((!startDate.equals(""))&&(!endDate.equals(""))){
			output = "Start date is: " + startDate + "\n" +" End date is: " + endDate;
			obj.setOutput(output);
			obj.setFlag(true);
		}
		else{
			obj.setOutput("StartDate and EndDate not found");
			obj.setFlag(false);
		}
		}
		catch(Exception e){
			obj.setOutput("StartDate and EndDate not found");
			obj.setFlag(false);
			e.printStackTrace();	
			}
		
		fgop.add(obj);
		return output;
	}

	/**
	 * Return expenses (shall not exceed)
	 * */
	
	private static String checkExpenses(String fileContent) {
		String stringstart = "If no Fees Section exists the maximum liability for all expenses incurred during the execution of this SOW shall not exceed";
		String exceedLimit="";
		String output="";
		int start = fileContent.indexOf("Cisco shall reimburse reasonable,");
		int end = 0;
		
		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("Expenses");
		
		
		try{
		for (int i=start; ; i++){
			if(fileContent.substring(i, i+20).equals("PLACE OF PERFORMANCE")){
				end = i;
				break;
			}
		}
		String text = fileContent.substring(start, end);
		System.out.println(text);
		Pattern q = Pattern.compile(Pattern.quote(stringstart) + "(.*?)" + Pattern.quote("</p>")); //Extracts start date
		Matcher n = q.matcher(text);
		while (n.find()) {
		  exceedLimit = n.group(1);
		  exceedLimit = exceedLimit.replaceAll("\\<.*?>","").trim();
		  System.out.println(exceedLimit);
		  output = stringstart + " " +exceedLimit;
		}
	
		if(exceedLimit.equals("")){
			obj.setOutput("Expenses class does not exist");
			obj.setFlag(false);	
		}
		else{
			obj.setOutput(output);
			obj.setFlag(true);
		}
		}
		catch(Exception e){
			obj.setOutput("Expenses class does not exist");
			obj.setFlag(false);
			e.printStackTrace();	
			System.out.println("catch exception");
			}

		fgop.add(obj);
		return output;
	}
	
	
	/**
	 * Always red
	 * */

	private static String checkCiscoPreExisting() {

		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("CiscoPreExistingProperty");
		obj.setOutput("Cisco Pre-Existing Property is not applicable to Field-Glass");
		obj.setFlag(false);

		fgop.add(obj);
		return null;
	}
	
	/**
	 * Define any supplier third party property, if required for this engagement or N/A if not applicable.
	 * N/A is a pass. Blank is a fail.
	 * 
	 * First "Third Party Property" value in the document is static.
	 * So we have to find the second "Third Party Property" column.
	 * So we find the index of 2nd occurrence of "Third Party Property"
	 * and start our searching from there.
	 * 
	 * The following function will take the fileContent String as input and will 
	 * provide us the Third Party Property as output.
	 * 
	 * */
	/*private static String checkThirdpartyProperty(String fileContent) {
	// TODO Auto-generated method stub
	String key = "Third Party Property";
	String output = "";
	int start = UtilityFunctions.nthIndexOf(fileContent,key,2);
	int outPutStart = 0;
	int outputEnd = 0;
	
	FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
	obj.setClassName("ThirdPartyProperty");
	
	try {
		IndexPairVO vo1 = UtilityFunctions.getOutputIndex(fileContent, "<p>", 3, start);
		outPutStart = vo1.getEndIndex();
		
		IndexPairVO vo2 = UtilityFunctions.getOutputIndex(fileContent, "</p>", 4, outPutStart);
		outPutStart = vo2.getStartIndex();
		outputEnd = vo2.getEndIndex();

		output = fileContent.substring(outPutStart + 3, outputEnd);
		obj.setOutput(output);
		obj.setFlag(true);
	}catch(Exception e){
		obj.setOutput("Third Party Property Does not exists");
		obj.setFlag(false);
		e.printStackTrace();
	}
	
	fgop.add(obj);
	System.out.println(output);
	return output;
}
*/


/**
 * Define any supplier third party property, if required for this engagement or N/A if not applicable.
 * N/A is a pass. Blank is a fail.
 * 
 * First "Third Party Property" value in the document is static.
 * So we have to find the second "Third Party Property" column.
 * So we find the index of 2nd occurrence of "Third Party Property"
 * and start our searching from there.
 * 
 * The following function will take the fileContent String as input and will 
 * provide us the Third Party Property as output.
 * 
 * Third party property is always followed by "5) RESOURCES TO BE PROVIDED BY CISCO"
 *  So our algorithm changes. We check from "Third Party Property" tag to "5) RESOURCES TO BE PROVIDED BY CISCO" tag.
 *  Remove the HTML in between.
 *  Get the text.
 * 
 * */
private static String checkThirdpartyProperty(String fileContent) {
	// TODO Auto-generated method stub
	String key1 = "Third Party Property";
	String key2 = "5) RESOURCES TO BE PROVIDED BY CISCO";
	String output = "";
	int start = UtilityFunctions.nthIndexOf(fileContent,key1,2);
	int end = fileContent.indexOf(key2);
	
	FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
	obj.setClassName("ThirdPartyProperty");

	try {
		String temp = fileContent.substring(start,end);
		temp = temp.replaceAll("<p>|</p>|<td>|</td>|<tr>|</tr>|Third|Party|Property", "");
		temp = temp.trim();
		System.out.println("Temp is :: "+ temp);
		obj.setOutput(temp);
		obj.setFlag(true);
	}catch(Exception e){
		obj.setOutput("Third Party Property Does not exists");
		obj.setFlag(false);
		e.printStackTrace();
	}
	
	fgop.add(obj);
	System.out.println(output);
	return output;
}

	/**
	 * Place of performance/ city/state must be completed (Applicable to MP & MS)
	 * The following function will take the fileContent String as input and will 
	 * provide us the name of work location as output.
	 * */
	private static String checkWorkLocation(String fileContent) {
		// TODO Auto-generated method stub
		String key = "Primary Work Site";
		String output = "";
		int start = fileContent.indexOf(key);
		int outPutStart = 0;
		int outputEnd = 0;

		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("WorkLocation");
		
		try {
			IndexPairVO vo1 = UtilityFunctions.getOutputIndex(fileContent, "<p>", 3, start);
			outPutStart = vo1.getEndIndex();
			
			IndexPairVO vo2 = UtilityFunctions.getOutputIndex(fileContent, "</p>", 4, outPutStart);
			outPutStart = vo2.getStartIndex();
			outputEnd = vo2.getEndIndex();

			output = fileContent.substring(outPutStart + 3, outputEnd);
			obj.setOutput(output);
			obj.setFlag(true);
			
		}catch(Exception e){
			obj.setOutput("Work Location Does not exists");
			obj.setFlag(false);
			e.printStackTrace();
		}
		
		fgop.add(obj);
		System.out.println(output);		
		return output;
	}

	/**
	 * The following function will check two fields of acceptance
	 * criterion(Acceptance Criteria Timeframe and Escalation Process
	 * Development Timeframe) and check whether those two have values greater
	 * than 0. If both are then it is present else absent.
	 * 
	 * */

	private static String checkAcceptanceCriteria(String content) {
		// TODO Auto-generated method stub
		String key = "Acceptance Criteria Timeframe";
		String output = "";
		boolean flag = true;

		List<String> keyList = new ArrayList<String>();
		keyList.add("Acceptance Criteria Timeframe");
		keyList.add("Escalation Process Development Timeframe");

		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("AcceptanceCriteria");
		
		try {
			for (int i = 0; i < keyList.size(); i++) {
				key = keyList.get(i);
				int start = content.indexOf(key);
				int outPutStart = 0;
				int outputEnd = 0;

				IndexPairVO vo1 = UtilityFunctions.getOutputIndex(content, "<p>", 3, start);
				outPutStart = vo1.getEndIndex();

				IndexPairVO vo2 = UtilityFunctions.getOutputIndex(content, "</p>", 4,
						outPutStart);
				outPutStart = vo2.getStartIndex();
				outputEnd = vo2.getEndIndex();

				String temp = content.substring(outPutStart + 3, outputEnd)
						.trim();
				System.out.println("temp is :: " + temp);

				if (temp.equalsIgnoreCase("NA") || temp.equalsIgnoreCase("N/A")
						|| temp.equalsIgnoreCase("") || temp == null) {
					
					output = "Acceptance Criterion is not satisfied.";
					obj.setOutput(output);
					obj.setFlag(false);
					break;
				} else {					
					String[] splited = temp.split(" ");
					int nunmber = Integer.parseInt(splited[0]);
					if (nunmber <= 0) {
						output = "Acceptance Criterion is not satisfied.";
						obj.setOutput(output);
						obj.setFlag(false);
						flag = false;
						break;
					}else {
						output += key +" " +temp+"\n";;
					}
				}
				System.out.println(temp);
			}
			// If both the numbers week and day are greater than 0 then the flag is true
			if(flag){
				obj.setOutput(output);
				obj.setFlag(true);
			}
		} catch (Exception e) {
			obj.setOutput("Acceptance Criterion is not satisfied.");
			obj.setFlag(false);
			e.printStackTrace();
		}
				

		fgop.add(obj);
		
		System.out.println("Acceptance Criteria ==================");
		System.out.println(output);
		return output;
	}

	/**
	 * The following function will return the Company Code. The first column we
	 * search is -> "Company Code/Entity" The output format from first column is
	 * :: Cisco Systems, Inc. (020) where 020 is the code. We have to use
	 * substring method to extract that.
	 * 
	 * We check the same thing from another column -> "Company Code" we get the
	 * value from there too. Then we match those two values.
	 * 
	 * 
	 * */

	private static String checkCompanyCode(String content) {
		// TODO Auto-generated method stub
		String companyCode = "";

		// Checking the first Column
		String key = "Company Code/Entity";
		int start = content.indexOf(key);
		int outPutStart = 0;
		int outputEnd = 0;

		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("CompanyCode");
		
		try {
			IndexPairVO vo1 = UtilityFunctions.getOutputIndex(content, "<p>", 3, start);
			outPutStart = vo1.getEndIndex();

			IndexPairVO vo2 = UtilityFunctions.getOutputIndex(content, "</p>", 4, outPutStart);
			outPutStart = vo2.getStartIndex();
			outputEnd = vo2.getEndIndex();

			String temp = content.substring(outPutStart + 3, outputEnd);
			companyCode = temp.substring(temp.indexOf('(') + 1,
					temp.indexOf(')'));

			System.out.println(companyCode);

			// Checking the second column

			String companyCode2 = "";
			String key2 = "Company Code";
			int start2 = UtilityFunctions.nthIndexOf(content, key2, 2);

			IndexPairVO vo3 = UtilityFunctions.getOutputIndex(content, "<p>", 3, start2);
			outPutStart = vo3.getEndIndex();

			IndexPairVO vo4 = UtilityFunctions.getOutputIndex(content, "</p>", 4, outPutStart);
			outPutStart = vo4.getStartIndex();
			outputEnd = vo4.getEndIndex();

			companyCode2 = content.substring(outPutStart + 3, outputEnd);
			System.out.println(companyCode2);

			if (companyCode.equalsIgnoreCase(companyCode2)) {
				obj.setOutput(companyCode2);
				obj.setFlag(true);
			} else {
				obj.setOutput("Did not match Company code in two places");
				obj.setFlag(false);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			obj.setOutput("Did not match Company code in two places");
			obj.setFlag(false);
			e.printStackTrace();
		}

		fgop.add(obj);
		return companyCode;
	}

	/*
	 * The following function will parse the html doc and will get us back the
	 * supplier name. Lets start with String matching approach. & is coming with
	 * a special character that we have to handle.
	 */

	private static String getSupplierName(String fileContent) {
		FieldGlassOPOBJ obj = new FieldGlassOPOBJ();
		obj.setClassName("General");
		String who="";
		
		try {
			int startWho = fileContent.indexOf("SOW Supplier Oracle Name");
			int endWho = 0;
			//start
			for (int j=startWho; ; j++){
				if(fileContent.substring(j, j+7).equals("GENERAL")){
					endWho = j;
					break;
				}
			}
			String textWho = fileContent.substring(startWho, endWho);
			System.out.println("====textWho:: \n "+textWho);
			System.out.println("==========");
			Pattern r = Pattern.compile(Pattern.quote("SOW Supplier Oracle Name") + "(.*?)" + Pattern.quote("1)"));
			System.out.println("r is "+r);
			Matcher t = r.matcher(textWho);
			System.out.println("t is "+t);
			int i = 0;
			while (t.find()) {
				who = t.group(1).replaceAll("\\<.*?>","").trim(); 
				System.out.println(who);
				System.out.println("i is "+i);
				i++;
			}		
			obj.setOutput(who);
			obj.setFlag(true);
			System.out.println("who :: "+who);
			
		} catch (Exception e) {
			obj.setOutput("Supplier Name not found");
			obj.setFlag(false);
			e.printStackTrace();
		}

		fgop.add(obj);
		return who;
	}

	/*
	 * The following function will parse the html doc and will get us back the
	 * start and end date. Lets start with String matching approach with "statement of work period" till </tr> 
	 * and date is extracted from it and value is checked.
	 */
	

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
		if((startDate=="")&&(endDate=="")){
			System.out.println("termSOW red");
		}
		else{
			System.out.println("termSOW green");
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			}
		}

	
	
}
