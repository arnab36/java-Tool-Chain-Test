package com.ibm.contracts.advisor.dynamic.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.util.IdentifyDate;

public class DynamicClassFunctions implements Constants {

	public static JSONObject ProcessDynamicClasses(String dynamicInput,
			String documentType, JSONObject dynamicPopUpContent) {
		JSONObject dynamicResult = new JSONObject();
		dynamicInput = dynamicInput.toLowerCase();

		try {
			dynamicResult.put("General",
					GeneralTerms(dynamicInput, dynamicPopUpContent));
		} catch (Exception e) {
			e.printStackTrace();
			dynamicResult.put("General", false);
		}

		try {
			dynamicResult.put("WorkLocation",
					WorkLocation(dynamicInput, dynamicPopUpContent));
		} catch (Exception e) {
			e.printStackTrace();
			dynamicResult.put("WorkLocation", false);
		}

		try {
			dynamicResult.put("ThirdPartyProperty",
					ThirdParty(dynamicInput, dynamicPopUpContent));
		} catch (Exception e) {
			e.printStackTrace();
			dynamicResult.put("ThirdPartyProperty", false);
		}

		try {
			dynamicResult.put("Expenses",
					Expenses.checkExpense(dynamicInput, dynamicPopUpContent));
		} catch (Exception e) {
			e.printStackTrace();
			dynamicPopUpContent
					.put("Expenses",
							"Either the expenses are not included in the payment table or have not been correctly added.");
			dynamicResult.put("Expenses", false);
		}

		try {
			dynamicResult.put("WorkProduct",
					WorkProduct(dynamicInput, dynamicPopUpContent));
		} catch (Exception e) {
			e.printStackTrace();
			dynamicResult.put("WorkProduct", false);
		}

		try {
			dynamicResult.put("CiscoPreExistingProperty",
					CiscoPreexistingTerms(dynamicInput, dynamicPopUpContent));
		} catch (Exception e) {
			e.printStackTrace();
			dynamicResult.put("CiscoPreExistingProperty", false);
		}

		// Two special classes for MP that are not present in MS
		if (documentType.equalsIgnoreCase(MPNonFG)) {
			try {
				dynamicResult.put("AcceptanceMilestones", AcceptanceMilestones
						.checkAcceptanceMilestone(dynamicInput,
								dynamicPopUpContent));
			} catch (Exception e) {
				e.printStackTrace();
				dynamicResult.put("AcceptanceMilestones", false);
			}

			try {
				dynamicResult.put("AcceptanceCriteria", AcceptenceCriteria
						.isAcpCriMatchin(dynamicInput, dynamicPopUpContent));
			} catch (Exception e) {
				e.printStackTrace();
				dynamicPopUpContent
						.put("AcceptanceCriteria",
								"At least one of the acceptance criteria is not filled");
				dynamicResult.put("AcceptanceCriteria", false);
			}
		} else {// SLA is only for MS
			try {
				dynamicResult
						.put("SLA", SLA(dynamicInput, dynamicPopUpContent));
			} catch (Exception e) {
				e.printStackTrace();
				dynamicPopUpContent.put("SLA",
						"Some fields are missing in minimum one of the SLAs");
				dynamicResult.put("SLA", false);
			}
			
			try{
				dynamicResult
						.put("LiquidatedDamages", LiquidatedDamages(dynamicInput, dynamicPopUpContent));
			} catch (Exception e) {
				e.printStackTrace();
				dynamicPopUpContent.put("LiquidatedDamages",
						"At least one of them (SLA Missed Months, SLA Credit Terms) are not filled");
				dynamicResult.put("LiquidatedDamages", false);
			}
			
			
			
		}

		try {
			dynamicResult.put("PaymentMethod", PaymentMethodClass
					.checkPaymentMethod(dynamicInput, documentType,
							dynamicPopUpContent));
		} catch (Exception e) {
			e.printStackTrace();
			// Commented and modified as per Prashant's instructions on 14.11.2017
			/*dynamicPopUpContent.put("PaymentMethod",
					"Non-Alignment with Milestone Table/Expenses");*/
			dynamicPopUpContent.put("PaymentMethod",
					"Non-Alignment with Payment Table/Expenses");
			dynamicResult.put("PaymentMethod", false);
		}

		try {
			dynamicResult.put("TermSOW",
					TermSOW.getTermSOW(dynamicInput, dynamicPopUpContent));
		} catch (Exception e) {
			e.printStackTrace();

			dynamicPopUpContent
					.put("TermSOW",
							"Either Start Date or End Date is not filled or doesn't match with the Payment Table");
			dynamicResult.put("TermSOW", false);
		}

		try {
			dynamicResult.put("Scope",
					Scope.getScope(dynamicInput, dynamicPopUpContent));
		} catch (Exception e) {
			dynamicResult.put("Scope", false);
			dynamicPopUpContent.put("Scope", "Scope is missing 2");
		}
		return dynamicResult;
	}
	
	private static boolean LiquidatedDamages(String s, JSONObject dynamicPopUpContent){
		String ldMissed="";
		String ldCredit="";
		String ldDisplay="";
		int flag1 = 0;
		int flag2 = 0;
		
		int start1 = s.indexOf("id=\"sla\"");
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
		Pattern q = Pattern.compile(Pattern.quote("if supplier performance fails to meet an sla for") + "(.*?)" + Pattern.quote("consecutive months due to factors")); //Extracts start date
		Matcher n = q.matcher(text);
		while (n.find()) {
			ldMissed = n.group(1);
			ldMissed=ldMissed.replaceAll("&lt;","<").replaceAll("&gt;",">").trim();
			ldMissed=ldMissed.replaceAll("\\<.*?>","").trim();
			System.out.println(ldMissed);
		}
		if ((!ldMissed.equals(""))&&(!ldMissed.equals("na"))&&(!ldMissed.equals("n/a"))&&(!ldMissed.equals("none"))){
			flag1 = 1;
			System.out.println(flag1);
			ldDisplay=ldDisplay+ "SLA Missed Months: " + ldMissed;
		}
		
		//===========================================================================
		
		
		Pattern a = Pattern.compile(Pattern.quote("supplier will issue a credit of") + "(.*?)" + Pattern.quote("of the monthly charge")); //Extracts start date
		Matcher b = a.matcher(text);
		while (b.find()) {
			ldCredit = b.group(1);
			ldCredit=ldCredit.replaceAll("&lt;","<").replaceAll("&gt;",">").trim();
			ldCredit=ldCredit.replaceAll("\\<.*?>","").trim();
			System.out.println(ldCredit);
		}
		if ((!ldCredit.equals(""))&&(!ldCredit.equals("na"))&&(!ldCredit.equals("n/a"))&&(!ldCredit.equals("none"))){
			flag2 = 1;
			System.out.println(flag2);
			ldDisplay=ldDisplay+ "\n"+"SLA Credit Terms: " + ldCredit;
		}
		
		
		//======================================================================
		if ((flag1==1)&&(flag2==1)){
			System.out.println("LiquidatedDamages is green");
			System.out.println(ldDisplay);
			dynamicPopUpContent.put("LiquidatedDamages", ldDisplay);
			return true;
		}
		else{

			System.out.println("LiquidatedDamages is red");
			dynamicPopUpContent.put("LiquidatedDamages", ldDisplay);
			return false;
		}
		}
		catch(Exception e){
			e.printStackTrace();	
			System.out.println("catch exception");
			dynamicPopUpContent.put("LiquidatedDamages", "LiquidatedDamages class exception");
			return false;
			}

	}
	

	private static boolean GeneralTerms(String s, JSONObject dynamicPopUpContent) {
		s=s.toLowerCase();
		String tempdisplay = "";
		String temp1 = null;
		int start = s.indexOf("id=\"general\"");
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h1")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			text = removeSpecialCharacters(text);

			Pattern q = Pattern.compile(Pattern.quote(by) + "(.*?)"
					+ Pattern.quote(under));
			Matcher n = q.matcher(text);
			while (n.find()) {
				temp1 = n.group(1).trim();
				System.out.println(temp1);
			}
			if (temp1.equalsIgnoreCase("N/A") || temp1.equalsIgnoreCase("None")
					|| temp1.equalsIgnoreCase("NA")
					|| temp1.equalsIgnoreCase("")) {
				System.out.println("General Term does not exist"); // Red node
				dynamicPopUpContent.put("General", "Supplier name is missing.");
				return false;
			} else {
				System.out.println("General Term does exist"); // Green node
				dynamicPopUpContent.put("General",
						temp1.replaceAll("(p\\s)", ". "));
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			dynamicPopUpContent.put("General", "Supplier name is missing.");
			return false;
		}
	}

	public static String TypeValidation(String s) {
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
				return "MP";
			else
				return "MS";
		}

		if (isMP1 > isMS1)
			return "MP";
		else
			return "MS";

	}

	// It will show whether the document is MP/MS
	// We have to add id="SOW" and pass it to this function
	public static String TypeValidation_prev(String s) {
		int termMP = 0;
		int termMS = 0;

		int startCheckMP = s.indexOf("AcceptanceMilestones");
		int startCheckMS = s.indexOf("SLAandTesting");

		int start = s.indexOf("id=\"SOW\"");
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 7).equals("General")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			text = removeSpecialCharacters(text);
			Pattern p = Pattern.compile("(?<=\\<b\\>)(.+?)(?=\\<\\/b\\>)");
			Matcher m = p.matcher(text);
			while (m.find()) {
				String temp = (m.group(1).trim());
				boolean tempMP = Pattern
						.compile(Pattern.quote("Managed Project"),
								Pattern.CASE_INSENSITIVE).matcher(temp).find();
				boolean tempMS = Pattern
						.compile(Pattern.quote("Managed Service"),
								Pattern.CASE_INSENSITIVE).matcher(temp).find();
				if (tempMP == true) {
					termMP = termMP + 1;
				} else if (tempMS == true) {
					termMS = termMS + 1;
				}
			}
			if ((startCheckMP > -1) && (termMP > 0)) {
				// MP document
				System.out.println("MP document");
				return "MP";
			} else if ((startCheckMS > -1) && (termMS > 0)) {
				// MS document
				System.out.println("MS document");
				return "MS";
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			return "OTHER";
		}
		return "OTHER";
	}

	public static void main(String[] args) {
		JSONObject j = new JSONObject();
		System.out
				.println(WorkLocation(
						"<h1  id='WorkLocation'>* PLACE OF PERFORMANCE</h1> he Work shall be performed by Supplier at "
								+ "Cisco�s facility at: Stockholm, Sweden.</p>    </p>    <h1>* TERM OF STATEMENT OF WORK</h1>",
						j));
		System.out.println("JSON String " + j.toJSONString());

	}

	private static boolean WorkLocation(String s, JSONObject dynamicPopUpContent) {
		String sent1 = null;
		String sent2 = null;
		String sent3 = null;
		String sent4 = null;
		s = s.toLowerCase();
		int start = s.indexOf("worklocation");
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h1")) {
					end = i;
					break;
				}
			}
			String text1 = s.substring(start, end);
			String finaldisplay = Jsoup.parse(text1).text()
					.replaceAll("worklocation\">", "").replaceAll("\">", "");
			String text = text1.replace("&nbsp", "");

			// text = removeSpecialCharacters(text);
			Pattern case1 = Pattern.compile(Pattern.quote("by supplier at")
					+ "(.*?)" + Pattern.quote("at:"));
			Matcher test1 = case1.matcher(text);
			while (test1.find()) {
				sent1 = test1.group(1).trim();
			}

			Pattern case2 = Pattern.compile(Pattern.quote("by supplier at")
					+ "(.*?)" + Pattern.quote("in"));
			Matcher test2 = case2.matcher(text);
			while (test2.find()) {
				sent2 = test2.group(1).trim();
			}

			Pattern case3 = Pattern.compile(Pattern.quote("at:") + "(.*?)"
					+ Pattern.quote("</p>"));
			Matcher test3 = case3.matcher(text);
			while (test3.find()) {
				sent3 = test3.group(1).trim();
			}
			Pattern case4 = Pattern.compile(Pattern.quote("in") + "(.*?)"
					+ Pattern.quote("</p>"));
			Matcher test4 = case4.matcher(text);
			while (test4.find()) {
				sent4 = test4.group(1).trim();
			}
			if (((sent1 != null) || (sent2 != null))
					&& ((sent3 != null) || (sent4 != null))) {
				System.out.println("Work Location Green");
				dynamicPopUpContent.put("WorkLocation", finaldisplay);
				return true;
			} else {
				System.out.println("Work Location Red");
				dynamicPopUpContent.put("WorkLocation",
						"Place of performance/city/state is not completed");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			dynamicPopUpContent.put("WorkLocation",
					"Place of performance/city/state is not completed");
			return false;
		}
	}

	private static boolean WorkLocation_prev19(String s,
			JSONObject dynamicPopUpContent) {
		String sent1 = null;
		String sent2 = null;
		String sent3 = null;
		String sent4 = null;
		int start = s.indexOf("worklocation");
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h1")) {
					end = i;
					break;
				}
			}
			String text1 = s.substring(start, end);
			String text = text1.replace(" ", "");
			Pattern case1 = Pattern.compile(Pattern.quote("by supplier at")
					+ "(.*?)" + Pattern.quote("at:"));
			Matcher test1 = case1.matcher(text);
			while (test1.find()) {
				sent1 = test1.group(1).trim();
			}

			Pattern case2 = Pattern.compile(Pattern.quote("by supplier at")
					+ "(.*?)" + Pattern.quote("in"));
			Matcher test2 = case2.matcher(text);
			while (test2.find()) {
				sent2 = test2.group(1).trim();
			}

			Pattern case3 = Pattern.compile(Pattern.quote("at:") + "(.*?)"
					+ Pattern.quote("</p>"));
			Matcher test3 = case3.matcher(text);
			while (test3.find()) {
				sent3 = test3.group(1).trim();
			}
			Pattern case4 = Pattern.compile(Pattern.quote("in") + "(.*?)"
					+ Pattern.quote("</p>"));
			Matcher test4 = case4.matcher(text);
			while (test4.find()) {
				sent4 = test4.group(1).trim();
			}
			if (((sent1 != null) || (sent2 != null))
					&& ((sent3 != null) || (sent4 != null))) {
				System.out.println("Work Location Green");
				dynamicPopUpContent.put("WorkLocation", text);
				return true;
			} else {
				System.out.println("Work Location Red");
				dynamicPopUpContent.put("WorkLocation",
						"work location not present");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			dynamicPopUpContent
					.put("WorkLocation", "work location not present");
			return false;
		}
	}

	private static boolean ThirdParty(String s, JSONObject dynamicPopUpContent) {
		int flag = 0;
		String temp1 = null;
		String stdText1 = "define any supplier third party property, if required for this engagement or n/a if not applicable.";
		String stdText2 = "supplier shall provide and pay for the following third party property to complete the work:";
String finaldisplay="";
		int start = s.indexOf("thirdpartyproperty");
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 2).equals("<h")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			Pattern p = Pattern.compile("(?<=\\<p)(.+?)(?=\\<\\/p\\>)");
			Matcher m = p.matcher(text);
			while (m.find()) {
				String temp = m.group().replaceAll("\\<.*?>","").replace(".", "").replace("dir=\"ltr\">", "")
						.replace(">", "").replace("&nbsp;", "")
						.replace(stdText1, "").replace(stdText2, "")
						.replace(" ", "");

			temp = temp.replaceAll("[^\\x00-\\x7F]", "");
			temp1 = temp.trim();
							// System.out.println(temp1);
				if (temp1.length() > 1) {
		finaldisplay=finaldisplay + temp1;
					flag = 1;
				}
			}
			if (flag == 1) {
				System.out.println("Third party property green");
				dynamicPopUpContent.put("ThirdPartyProperty", finaldisplay);
				return true;
			} else {
				System.out.println("Third party property red");
				dynamicPopUpContent
						.put("ThirdPartyProperty",
								"No mention of any Supplier third party property for this engagement");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			dynamicPopUpContent
					.put("ThirdPartyProperty",
							"No mention of any Supplier third party property for this engagement");
			return false;
		}
	}

	public static boolean Expenses(String s, JSONObject dynamicPopUpContent) {
		int temp1 = 0;
		int start = s.indexOf("cisco's maximum liability for all expenses");
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 2).equals("<h")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			System.out.println(text);
			Pattern q = Pattern.compile(Pattern.quote("shall not exceed ")
					+ "(.*?)" + Pattern.quote("</p>")); // Removes value from
														// expense section
			Matcher n = q.matcher(text);
			while (n.find()) {
				temp1 = Integer.parseInt(n.group(1).replaceAll("[^0-9]", "")
						.trim());
				System.out.println(temp1);
			}
			
			if ((temp1==0)||(temp1==0.00)){
				System.out.println(temp1);
				System.out.println("Expenses class green");
				dynamicPopUpContent.put("Expenses",	"Expenses are: " + temp1);
			return true;
			}
			
			else{
			int startPay = s.indexOf("paymentmethod");
			int endPay = 0;
			// System.out.println(startPay);
			for (int j = startPay;; j++) {
				if (s.substring(j, j + 3).equals("<h2")) {
					endPay = j;
					break;
				}
			}
			String textPay = s.substring(startPay, endPay);
			Pattern p = Pattern.compile(Pattern.quote("<tr>") + "(.*?)"
					+ Pattern.quote("</tr>"));
			Matcher m = p.matcher(textPay);
			text = "";
			while (m.find()) {
				text += m.group();
			}
			// System.out.println(text);
			startPay = text.indexOf("expenses (total from section 8c)");
			endPay = 0;
			// System.out.println(startPay + " " + text.length());
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}
			System.out.println("text::" + text);
			q = Pattern.compile(Pattern.quote("<p(\\sdir\\=\"ltr\")>")
					+ "(.*?)" + Pattern.quote("</p>"));
			n = q.matcher(text.substring(startPay, endPay));
			ArrayList<Integer> expen = new ArrayList<Integer>();
			while (n.find()) {
				expen.add(Integer.parseInt(n.group(1).replaceAll("[^0-9]", "")
						.trim()));
			}

			int sum = 0;
			for (int i : expen) {
				sum += i;
			}
			System.out.println("payment table" + sum);
			if (sum == temp1) {
				System.out.println("Expense class green");
				if (dynamicPopUpContent != null)
					dynamicPopUpContent.put("Expenses",
							text.replaceAll("\\<.*\\>", ""));
				return true;
			} else {
				System.out.println("Expense class red");
				if (dynamicPopUpContent != null)
					dynamicPopUpContent
							.put("Expenses",
									"Either the expenses are not included in the payment table or have not been correctly added.");
				return false;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			if (dynamicPopUpContent != null) {
				dynamicPopUpContent
						.put("Expenses",
								"Either the expenses are not included in the payment table or have not been correctly added.");
			}
			return false;
		}
	}

	public static boolean Expenses_ayan(String s, JSONObject dynamicPopUpContent) {
		int alert = 0;
		int temp1 = 0;
		int start = s.indexOf("cisco's maximum liability for all expenses"
				.toLowerCase());
		int end = 0;
		System.out.println("start string::" + start);
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 2).equals("<h")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			System.out.println(text);
			Pattern q = Pattern.compile(Pattern.quote("shall not exceed ")
					+ "(.*?)" + Pattern.quote("</p>")); // Removes value from
														// expense section
			Matcher n = q.matcher(text);
			while (n.find()) {
				temp1 = Integer.parseInt(n.group(1).replaceAll("[^0-9]", "")
						.trim());
				System.out.println(temp1);
			}
			int startPay = s.indexOf("payment method");
			int endPay = 0;
			System.out.println(startPay);
			for (int j = startPay;; j++) {
				if (s.substring(j, j + 3).equals("<h2")) {
					endPay = j;
					break;
				}
			}
			String textPay = s.substring(startPay, endPay);
			System.out.println("textPay::" + textPay);
			System.out.println("start index:::" + textPay.indexOf("<tbody>")
					+ "  end index:::" + textPay.indexOf("</tbody>"));
			text = textPay.substring(textPay.indexOf("<tbody>"),
					textPay.indexOf("</tbody>"));
			/*
			 * Pattern p =
			 * Pattern.compile("(?<=\\<tbody\\>)(.+?)(?=\\<\\/tbody\\>)");
			 * Matcher m = p.matcher(textPay); text = ""; while (m.find()) {
			 * text += m.group(); }
			 */
			// System.out.println(text);
			startPay = text.indexOf("expenses (total from section 8c)");
			endPay = 0;
			System.out.println(startPay + "|||||||||" + text.length());
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}
			System.out.println(":::::::" + text.substring(startPay, endPay));
			ArrayList<Integer> expen = new ArrayList<Integer>();
			System.out.println("_____________"
					+ text.substring(startPay, endPay));
			List<String> amt = IdentifyDate.getAllSpecialString(
					text.substring(startPay, endPay), "(\\d+,?\\d*\\.\\d+)");
			System.out
					.println("+++++++++++++++++++++++++++++++++++++++++++++=");
			for (String tmp : amt) {
				System.out.println("-------------" + tmp);
				tmp = tmp.replaceAll("[,\\.]", "");

				if (tmp.indexOf(".") > -1)
					expen.add(Integer.parseInt(tmp.substring(0,
							tmp.indexOf("."))));
				else
					expen.add(Integer.parseInt(tmp));
			}
			startPay = text.indexOf("subtotal of 8b");
			endPay = 0;
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}
			System.out.println("+++++" + text.substring(startPay, endPay));
			String tot2 = text.substring(startPay, endPay);
			ArrayList<Integer> subtotal = new ArrayList<Integer>();
			List<String> tot3 = IdentifyDate
					.getAllSpecialString(tot2,
							"(\\s*(ï¿½|$|ï¿½|(GBP))?(\\d+\\,\\d+\\,?\\d+)|(\\d+)\\s+)");
			for (String temp : tot3) {
				temp = temp.replaceAll(",", "").trim();
				if (temp.contains("."))
					temp.replace(".", "");
				else
					temp = temp + "00";
				subtotal.add(Integer.parseInt(temp));
			}
			startPay = text.indexOf("total of 8b and 8c");
			endPay = 0;
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}
			String tot = text.substring(startPay, endPay);
			ArrayList<Integer> total = new ArrayList<Integer>();
			System.out.println(tot + ":::");
			List<String> tot1 = IdentifyDate
					.getAllSpecialString(tot,
							"(\\s*(ï¿½|$|ï¿½|(GBP))?(\\d+\\,\\d+\\,?\\d+)|(\\d+)\\s+)");
			for (String temp : tot1) {
				System.out.println("got money::" + temp);
				temp = temp.replaceAll(",", "").trim();
				if (temp.contains("."))
					temp.replace(".", "");
				else
					temp = temp + "00";
				total.add(Integer.parseInt(temp));
			}
			int sum = 0;
			for (int i : expen) {
				sum += i;
			}
			// System.out.println(sum==temp1);
			int size = expen.size();
			System.out.println("size is " + size);
			for (int i = 0; i < size; i++) {
				System.out.println("subtotal " + subtotal.get(i) + " exp:: "
						+ expen.get(i) + " total.get(i) is " + total.get(i));
				if (subtotal.get(i) + expen.get(i) != total.get(i)) {
					alert = 1;
					break;
				}
			}
			System.out.println("sum::" + sum + " temp1::" + temp1);
			if ((alert == 0) && (sum == temp1)) {
				System.out.println("Expense class green");
				if (dynamicPopUpContent != null)
					dynamicPopUpContent.put("Expenses",
							tot.replaceAll("\\<.*\\>", ""));
				return true;
			} else {
				System.out.println("Expense class red");
				if (dynamicPopUpContent != null)
					dynamicPopUpContent.put("Expenses",
							"Expanses are not matching");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			if (dynamicPopUpContent != null)
				dynamicPopUpContent
						.put("Expenses", "Expanses are not matching");
			return false;
		}
	}
	
	private static boolean WorkProduct(String s, JSONObject dynamicPopUpContent){
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
		
		s = s.toLowerCase();
		
		
		
		
	int counttable=0;
	int countval=0;
	String finalstring="";
		
		int start1 = s.indexOf("id=\"workproduct\"");
		int end1 = 0;
		try{
		for (int i=start1; ; i++){
			if(s.substring(i, i+25).equals("workproductspecifications")){
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
				dynamicPopUpContent.put("WorkProduct", finalstring);
				return true;

			}
			else{
				System.out.println("WP class red");
				dynamicPopUpContent.put("WorkProduct", "Work Product table is not correctly populated");
				return false;

			}	
		}
		else{
			//Do non nested working
			int starttemp = s.indexOf("id=\"workproduct\"");
			while (starttemp >= 0) {
				if (starttemp > -1)
					start = starttemp;
				starttemp = s.indexOf("id=\"workproduct\"", start + 1);
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
							|| temp7.contains("deliverable name")
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
					dynamicPopUpContent.put("WorkProduct", "Work Product table is not properly populated");
					return false;


				} else {
					System.out.println("Work Product is green"); // Work Product
																	// table is
																	// populated
					dynamicPopUpContent.put("WorkProduct", finaldisplay);
					return true;

				}
			}
		}
		catch(Exception e)
			{
			e.printStackTrace();	
			System.out.println("catch exception");
			dynamicPopUpContent.put("WorkProduct", "Work Product Table is not properly populated");
			return false;
			}
	}

	private static boolean WorkProductOld(String s, JSONObject dynamicPopUpContent) {
		int flag = 0;
		int alert = 0;
		int flag2 = 0;
		String finaldisplay = "";
		String tempdisplay = "";
		int count = 0;
		String temp1 = "";
		String temp2 = "";
		String temp3 = "";
		int start = 0;
		s = s.toLowerCase();
		int starttemp = s.indexOf("id=\"workproduct\"");
		while (starttemp >= 0) {
			if (starttemp > -1)
				start = starttemp;
			starttemp = s.indexOf("id=\"workproduct\"", start + 1);
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
			// text = removeSpecialCharacters(text);
			Pattern q = Pattern.compile(Pattern.quote("<tr>") + "(.*?)"
					+ Pattern.quote("</tr>"));
			Matcher n = q.matcher(text);

			while (n.find()) {

				temp1 = n.group().trim();
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
					temp2 = m.group(1).replaceAll("&nbsp;", "").trim();
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
				dynamicPopUpContent.put("WorkProduct",
						"Not all fields are filled in the work product table");
				return false;

			} else {
				System.out.println("Work Product is green"); // Work Product
																// table is
																// populated
				dynamicPopUpContent.put("WorkProduct", finaldisplay);
				return true;
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			dynamicPopUpContent.put("WorkProduct",
					"Not all fields are filled in the work product table");
			return false;

		}
	}

	private static boolean CiscoPreexistingTerms(String s,
			JSONObject dynamicPopUpContent) {
		int alert = 0;
		int flag = 0;
		int flag2 = 0;
		String temp1 = "";
		String temp2 = "";
		String temp3 = "";
		int start = s.indexOf("ciscopreexistingproperty");
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h2")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			text = removeSpecialCharacters(text);
			Pattern q = Pattern.compile(Pattern.quote("<tr>") + "(.*?)"
					+ Pattern.quote("</tr>"));
			Matcher n = q.matcher(text);
			int i = 0;
			int j = 0;
			while (n.find()) {
				if (alert == 1) {
					break;
				}
				if (i == 0) {
					i++;
					continue;
				}
				temp1 = n.group().trim();
				Pattern p = Pattern.compile(Pattern.quote("<td>") + "(.*?)"
						+ Pattern.quote("</td>"));
				Matcher m = p.matcher(temp1);
				flag = 0;
				flag2 = 0;
				j = 0;

				// System.out.println("FLAGS" + flag + flag2 + n.group(1));
				while (m.find()) {
					if (j == 0) {
						temp2 = m.group(1).trim();
						temp3 = Jsoup.parse(temp2).text().trim();
						if (temp3.equals("")) {
							flag2 = 1;
						}
						j++;
					} else if (j == 1) {
						temp2 = m.group(1).trim();
						temp3 = Jsoup.parse(temp2).text().trim();

						if (temp3.equals("") && flag2 == 0) {
							alert = 1;
							break;
						} else {
							if (flag2 == 1 && !temp3.equals("")) {
								alert = 1;
								break;
							} else if (temp3.equalsIgnoreCase("yes")) {
								flag = 1;
							} else if (temp3.equalsIgnoreCase("no")) {
								flag = 2;
							}
						}
						j++;
					} else if (flag == 1) {
						temp2 = m.group(1).trim();
						temp3 = Jsoup.parse(temp2).text().trim();
						temp3 = temp3.replace("(select)", "")
								.replace("(select)", "").replace("select", "")
								.replace("select", "");
						if (temp3.equals("")) {
							alert = 1;
							break;
						}
						j++;
					} else if (flag == 2) {
						j++;
					} else {
						if (flag2 == 1) {
							temp2 = m.group(1).trim();
							temp3 = Jsoup.parse(temp2).text().trim();
							if (temp3.equals("")) {

							} else {
								alert = 1;
								break;
							}
						}
						j++;
					}
				}
				i++;
			}
			if (alert == 1) {
				System.out.println("Cisco preexisting property node red");
				dynamicPopUpContent
						.put("CiscoPreExistingProperty",
								"Some fields are missing for Pre-Exisiting Property to be used to complete the work ");
				return false;
			} else {
				System.out.println("Cisco preexisting property node green");
				dynamicPopUpContent
						.put("CiscoPreExistingProperty",
								"All fields are present in Table 5a : Pre-Exisiting Property, to be used to complete the work ");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Cisco preexisting property node red");
			dynamicPopUpContent
					.put("CiscoPreExistingProperty",
							"Some fields are missing for Pre-Exisiting Property to be used to complete the work ");
			return false;
		}
	}

	private static boolean SLA(String s, JSONObject dynamicPopUpContent) {
		String measurement = null;
		String target = null;
		String frequency = null;
		int frequencycheck = 0;
		int targetcheck = 0;
		int measurementcheck = 0;
		int finalcheck = 0;
		int start = s.indexOf("id=\"sla\"");
		int end = 0;
		int warning = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h2")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			ArrayList<Integer> a = new ArrayList<Integer>();
			Pattern b = Pattern.compile("service level agreement #");
			Matcher b1 = b.matcher(text);
			while (b1.find()) {
				a.add(b1.start());
			}
			a.add(text.length());
			String finaldisplay = "";
			for (int i = 0; i < a.size() - 1; i++) {
				String temp = text.substring(a.get(i), a.get(i + 1));
				
			if (temp.toString().toLowerCase().contains("ongoing") || temp.toString().toLowerCase().contains("on-going") || temp.toString().toLowerCase().contains("on going") )  {
				System.out.println("Ongoing term present");
				warning =1;
			}
			else
			{	
				Pattern r = Pattern.compile(Pattern.quote("measurement:")
						+ "(.*?)" + Pattern.quote("</p>"));
				
					Matcher o = r.matcher(temp);
					while (o.find()) {
						measurement = o.group(1).replaceAll("_", "").trim();
						finaldisplay = finaldisplay + o.group() + "\n";
						System.out.println(measurement);
						if (!measurement.equals("")) {
							measurementcheck = 1;
						}
					}
				
				Pattern q = Pattern.compile(Pattern.quote("target:") + "(.*?)"
						+ Pattern.quote("</p>"));
				Matcher n = q.matcher(temp);
				while (n.find()) {
					target = n.group(1).replaceAll("_", "").trim();
					finaldisplay = finaldisplay + n.group() + "\n";
					System.out.println(target);
					if (!target.equals("")) {
						targetcheck = 1;
					}
				}
				Pattern p = Pattern.compile(Pattern.quote("frequency:")
						+ "(.*?)" + Pattern.quote("</p>"));
				Matcher m = p.matcher(temp);
				while (m.find()) {
					frequency = m.group(1).replaceAll("_", "").trim();
					finaldisplay = finaldisplay + m.group() + "\n";
					System.out.println(frequency);
					if (!frequency.equals("")) {
						frequencycheck = 1;
					}
				}
				System.out.println(measurementcheck + "" + targetcheck + ""
						+ frequencycheck);
				if ((frequencycheck == targetcheck)
						&& (targetcheck == measurementcheck)
						&& (measurementcheck == 1)) {
					finalcheck = finalcheck + 1;
				}
				frequencycheck = targetcheck = measurementcheck = 0;
				System.out.println(finalcheck);
			}
			}
			if ((finalcheck >= 2)&&(warning==0)) {
				System.out.println("SLA class green"); // return class green
				dynamicPopUpContent.put("SLA", finaldisplay);
				return true;
			} else {
				System.out.println("SLA class red"); // return class red
				dynamicPopUpContent.put("SLA",
						"Some fields are missing in minimum one of the SLAs or On-going term was present");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("SLA class red"); // return class red
			dynamicPopUpContent.put("SLA",
					"Some fields are missing in minimum one of the SLAs");
			return false;
		}
	}

	public static boolean Expenses_prev2(String s,
			JSONObject dynamicPopUpContent) {
		int alert = 0;
		int temp1 = 0;
		int start = s.indexOf("cisco's maximum liability for all expenses"
				.toLowerCase());
		int end = 0;
		System.out.println("start string::" + start);
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 2).equals("<h")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			System.out.println(text);
			Pattern q = Pattern.compile(Pattern.quote("shall not exceed <b>")
					+ "(.*?)" + Pattern.quote("</b>")); // Removes value from
														// expense section
			Matcher n = q.matcher(text);
			while (n.find()) {
				temp1 = Integer.parseInt(n.group(1).replaceAll("[^0-9]", "")
						.trim());
				System.out.println(temp1);
			}

			int startPay = s.indexOf("payment method");
			int endPay = 0;
			System.out.println(startPay);
			for (int j = startPay;; j++) {
				if (s.substring(j, j + 3).equals("<h2")) {
					endPay = j;
					break;
				}
			}
			String textPay = s.substring(startPay, endPay);
			System.out.println("textPay::" + textPay);
			System.out.println("start index:::" + textPay.indexOf("<tbody>")
					+ "  end index:::" + textPay.indexOf("</tbody>"));
			text = textPay.substring(textPay.indexOf("<tbody>"),
					textPay.indexOf("</tbody>"));
			/*
			 * Pattern p =
			 * Pattern.compile("(?<=\\<tbody\\>)(.+?)(?=\\<\\/tbody\\>)");
			 * Matcher m = p.matcher(textPay); text = ""; while (m.find()) {
			 * text += m.group(); }
			 */
			// System.out.println(text);
			startPay = text.indexOf("expenses (total from section 8c)");
			endPay = 0;
			System.out.println(startPay + "|||||||||" + text.length());
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}
			System.out.println(":::::::" + text.substring(startPay, endPay));
			ArrayList<Integer> expen = new ArrayList<Integer>();

			List<String> amt = IdentifyDate.getAllSpecialString(
					text.substring(startPay, endPay), "(\\d+.\\d+)");
			for (String tmp : amt) {
				System.out.println("---" + tmp);
				if (tmp.indexOf(".") > -1)
					expen.add(Integer.parseInt(tmp.substring(0,
							tmp.indexOf("."))));
				else
					expen.add(Integer.parseInt(tmp));
			}

			startPay = text.indexOf("subtotal of 8b");
			endPay = 0;
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}
			System.out.println("+++++" + text.substring(startPay, endPay));

			String tot2 = text.substring(startPay, endPay);
			ArrayList<Integer> subtotal = new ArrayList<Integer>();
			List<String> tot3 = IdentifyDate.getAllSpecialString(tot2,
					"(\\s*(�|$|�|(GBP))?(\\d+\\,\\d+\\,?\\d+)|(\\d+)\\s+)");
			for (String temp : tot3) {
				temp = temp.replaceAll(",", "").trim();
				subtotal.add(Integer.parseInt(temp));
			}

			startPay = text.indexOf("total of 8b and 8c");
			endPay = 0;
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}

			String tot = text.substring(startPay, endPay);
			ArrayList<Integer> total = new ArrayList<Integer>();
			System.out.println(tot + ":::");
			List<String> tot1 = IdentifyDate.getAllSpecialString(tot,
					"(\\s*(�|$|�|(GBP))?(\\d+\\,\\d+\\,?\\d+)|(\\d+)\\s+)");
			for (String temp : tot1) {
				System.out.println("got money::" + temp);
				temp = temp.replaceAll(",", "").trim();
				total.add(Integer.parseInt(temp));
			}

			int sum = 0;
			for (int i : expen) {
				sum += i;
			}
			// System.out.println(sum==temp1);
			int size = expen.size();
			System.out.println("size is " + size);
			for (int i = 0; i < size; i++) {
				System.out.println("total.get(i) is " + total.get(i));
				if (subtotal.get(i) + expen.get(i) != total.get(i)) {
					alert = 1;
					break;
				}
			}
			if ((alert == 0) && (sum == temp1)) {
				System.out.println("Expense class green");
				if (dynamicPopUpContent != null)
					dynamicPopUpContent.put("Expenses",
							tot.replaceAll("\\<.*\\>", ""));
				return true;
			} else {
				System.out.println("Expense class red");
				if (dynamicPopUpContent != null)
					dynamicPopUpContent.put("Expenses",
							"Expanses are not matching");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			if (dynamicPopUpContent != null)
				dynamicPopUpContent
						.put("Expenses", "Expanses are not matching");
			return false;
		}
	}

	public static boolean Expenses_prev3(String s,
			JSONObject dynamicPopUpContent) {
		int alert = 0;
		int temp1 = 0;
		int start = s.indexOf("cisco's maximum liability for all expenses"
				.toLowerCase());
		int end = 0;
		System.out.println("start string::" + start);
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 2).equals("<h")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			System.out.println(text);
			Pattern q = Pattern.compile(Pattern.quote("shall not exceed <b>")
					+ "(.*?)" + Pattern.quote("</b>")); // Removes value from
														// expense section
			Matcher n = q.matcher(text);
			while (n.find()) {
				temp1 = Integer.parseInt(n.group(1).replaceAll("[^0-9]", "")
						.trim());
				System.out.println(temp1);
			}
			int startPay = s.indexOf("payment method");
			int endPay = 0;
			System.out.println(startPay);
			for (int j = startPay;; j++) {
				if (s.substring(j, j + 3).equals("<h2")) {
					endPay = j;
					break;
				}
			}
			String textPay = s.substring(startPay, endPay);
			System.out.println("textPay::" + textPay);
			System.out.println("start index:::" + textPay.indexOf("<tbody>")
					+ "  end index:::" + textPay.indexOf("</tbody>"));
			text = textPay.substring(textPay.indexOf("<tbody>"),
					textPay.indexOf("</tbody>"));
			/*
			 * Pattern p =
			 * Pattern.compile("(?<=\\<tbody\\>)(.+?)(?=\\<\\/tbody\\>)");
			 * Matcher m = p.matcher(textPay); text = ""; while (m.find()) {
			 * text += m.group(); }
			 */
			// System.out.println(text);
			startPay = text.indexOf("expenses (total from section 8c)");
			endPay = 0;
			System.out.println(startPay + "|||||||||" + text.length());
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}
			System.out.println(":::::::" + text.substring(startPay, endPay));
			ArrayList<Integer> expen = new ArrayList<Integer>();
			System.out.println("_____________"
					+ text.substring(startPay, endPay));
			List<String> amt = IdentifyDate.getAllSpecialString(
					text.substring(startPay, endPay), "(\\d+,?\\d*\\.\\d+)");
			System.out
					.println("+++++++++++++++++++++++++++++++++++++++++++++=");
			for (String tmp : amt) {
				System.out.println("-------------" + tmp);
				tmp = tmp.replaceAll("[,\\.]", "");

				if (tmp.indexOf(".") > -1)
					expen.add(Integer.parseInt(tmp.substring(0,
							tmp.indexOf("."))));
				else
					expen.add(Integer.parseInt(tmp));
			}
			startPay = text.indexOf("subtotal of 8b");
			endPay = 0;
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}
			System.out.println("+++++" + text.substring(startPay, endPay));
			String tot2 = text.substring(startPay, endPay);
			ArrayList<Integer> subtotal = new ArrayList<Integer>();
			List<String> tot3 = IdentifyDate
					.getAllSpecialString(tot2,
							"(\\s*(ï¿½|$|ï¿½|(GBP))?(\\d+\\,\\d+\\,?\\d+)|(\\d+)\\s+)");
			for (String temp : tot3) {
				temp = temp.replaceAll(",", "").trim();
				if (temp.contains("."))
					temp.replace(".", "");
				else
					temp = temp + "00";
				subtotal.add(Integer.parseInt(temp));
			}
			startPay = text.indexOf("total of 8b and 8c");
			endPay = 0;
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}
			String tot = text.substring(startPay, endPay);
			ArrayList<Integer> total = new ArrayList<Integer>();
			System.out.println(tot + ":::");
			List<String> tot1 = IdentifyDate
					.getAllSpecialString(tot,
							"(\\s*(ï¿½|$|ï¿½|(GBP))?(\\d+\\,\\d+\\,?\\d+)|(\\d+)\\s+)");
			for (String temp : tot1) {
				System.out.println("got money::" + temp);
				temp = temp.replaceAll(",", "").trim();
				if (temp.contains("."))
					temp.replace(".", "");
				else
					temp = temp + "00";
				total.add(Integer.parseInt(temp));
			}
			int sum = 0;
			for (int i : expen) {
				sum += i;
			}
			// System.out.println(sum==temp1);
			int size = expen.size();
			System.out.println("size is " + size);
			for (int i = 0; i < size; i++) {
				System.out.println("subtotal " + subtotal.get(i) + " exp:: "
						+ expen.get(i) + " total.get(i) is " + total.get(i));
				if (subtotal.get(i) + expen.get(i) != total.get(i)) {
					alert = 1;
					break;
				}
			}
			System.out.println("sum::" + sum + " temp1::" + temp1);
			if ((alert == 0) && (sum == temp1)) {
				System.out.println("Expense class green");
				if (dynamicPopUpContent != null)
					dynamicPopUpContent.put("Expenses",
							tot.replaceAll("\\<.*\\>", ""));
				return true;
			} else {
				System.out.println("Expense class red");
				if (dynamicPopUpContent != null)
					dynamicPopUpContent.put("Expenses",
							"Expenses are not matching");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			if (dynamicPopUpContent != null)
				dynamicPopUpContent
						.put("Expenses", "Expenses are not matching");
			return false;
		}
	}

	private static String removeSpecialCharacters(String text) {
		// TODO Auto-generated method stub
		Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
		Matcher match = pt.matcher(text);
		while (match.find()) {
			String s = match.group();
			text = text.replaceAll("\\" + s, " ");
		}
		System.out.println(text);
		return text;
	}

	public static void mainOld(String[] args) {
		String docType = "MP";
		StringBuilder contentBuilder = new StringBuilder();
		String fileLoc = "C:/Users/IBM_ADMIN/Documents/project 8/cisco/MS/";
		try {
			BufferedReader inputHTML = new BufferedReader(
					new FileReader(
							fileLoc
									+ "demo_user2_1505286255891_MS V4 - 201921277-great.html")); // 201937907-OK-AS1.html
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
		JSONObject j = new JSONObject();
		System.out.println(ProcessDynamicClasses(str, "Managed Services", j));
		System.out.println(j.toJSONString());

	}

}
