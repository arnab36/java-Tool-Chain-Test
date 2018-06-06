package com.ibm.contracts.advisor.dynamic.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.constants.SplitPropSet;

public class AlertHandler implements Constants {
	public static JSONObject HandleAlerts(String s, String contractType) {
		JSONObject dynamicAlert = new JSONObject();

		String specialTermAlert = null, paymentMethodAlert = null, otherResourcesAlert = null;
		try {
			specialTermAlert = SpecialTermAlert(s);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			paymentMethodAlert = PaymentMethodAlert(s);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			otherResourcesAlert = OtherResourcesAlert(s);
		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean slaAlert = false;

		try {
			if (contractType.equalsIgnoreCase(MSNonFG)) {
				slaAlert = SLAAlert.getSLAAlert(s,PostNLCPropSet.staticTemplatefile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String type = DynamicClassFunctions.TypeValidation(s);

		System.out.println("======== Indisde Alert Handler ===============");
		System.out.println("Validation type is :: " + type);
		System.out.println("Contract type is :: " + contractType);

		boolean typeMatch = false;
		if (type.equalsIgnoreCase("MP")
				&& contractType.equalsIgnoreCase(MPNonFG)) {
			typeMatch = true;

		}
		if (type.equalsIgnoreCase("MS")
				&& contractType.equalsIgnoreCase(MSNonFG)) {
			typeMatch = true;
		}
		
		System.out.println("type is typeMatch:::" + typeMatch);
		// boolean slaAlert= false;
		if (typeMatch == false){
			JSONObject temp1 = new JSONObject();
			temp1.put("flag", true);
			temp1.put("output", "Document class is not correct.");
			dynamicAlert.put("ClassificationAlert", temp1);
		}else {
			JSONObject temp1 = new JSONObject();
			temp1.put("flag", false);
			temp1.put("output", "Document class is correct.");
			dynamicAlert.put("ClassificationAlert", temp1);
		}
			
		if (specialTermAlert != null){
			JSONObject temp1 = new JSONObject();
			temp1.put("flag", true);
			temp1.put("output", specialTermAlert);
			dynamicAlert.put("SpecialTermAlert", temp1);
		}else {
			JSONObject temp1 = new JSONObject();
			temp1.put("flag", false);
			temp1.put("output", "Special term does not exist.");
			dynamicAlert.put("SpecialTermAlert", temp1);
		}
			
		if (paymentMethodAlert != null){
			JSONObject temp1 = new JSONObject();
			temp1.put("flag", true);
			temp1.put("output", paymentMethodAlert);
			dynamicAlert.put("PaymentMethodAlert", temp1);
		}else {
			JSONObject temp1 = new JSONObject();
			temp1.put("flag", false);
			temp1.put("output", "Prepayment term doesn't exist.");
			dynamicAlert.put("PaymentMethodAlert", temp1);
		}
			
		if (otherResourcesAlert != null){
			JSONObject temp1 = new JSONObject();
			temp1.put("flag", true);
			temp1.put("output", otherResourcesAlert);
			dynamicAlert.put("SupplierResourcesAlert", temp1);
		}else {
			JSONObject temp1 = new JSONObject();
			temp1.put("flag", false);
			temp1.put("output", "Table is not populated.");
			dynamicAlert.put("SupplierResourcesAlert", temp1);
		}
			
		
		if(contractType.equalsIgnoreCase(MSNonFG)){
			if (slaAlert){
				JSONObject temp1 = new JSONObject();
				temp1.put("flag", true);
				temp1.put("output", "SLA Alert to be raised.");
				dynamicAlert.put("SLAAlert",temp1);
			}else {
				JSONObject temp1 = new JSONObject();
				temp1.put("flag", false);
				temp1.put("output", "SLA Alert need not be raised.");
				dynamicAlert.put("SLAAlert",temp1);
			}
		}
		
			
		return dynamicAlert;
	}

	private static String PaymentMethodAlert(String s) {
		int count = 0;
		int start = s.indexOf(PaymentMethod);
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h2")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			// text = removeSpecialCharacters(text);
			Pattern p = Pattern.compile(alert3regex);
			Matcher m = p.matcher(text);
			while (m.find()) {
				String temp1 = m.group(1);
				String temp = Jsoup.parse(temp1).text().trim().toLowerCase();
				/*
				if (temp.contains("prepayment") || temp.contains("pre-payment")
						|| temp.contains("pre payment")) {
					count = count + 1;
					break;
						
				}*/
				if (temp.contains("prepayment") || temp.contains("pre-payment")
						|| temp.contains("pre payment") || temp.contains("early payment") || temp.contains("earlypayment") || temp.contains("early-payment")
						|| temp.contains("advance payment") || temp.contains("advancepayment") || temp.contains("advance-payment")
						|| temp.contains("advance fund") || temp.contains("advancefund") || temp.contains("advance-fund")
						|| temp.contains("deposit")) {
					count = count + 1;
					break;
						
				}
			}
			if (count > 0) {
				System.out
						.println("Prepayment word exists. Prepayment alert to be raised");
				return (Alert3);
			} else {
				System.out
						.println("Prepayment word doesn't exist. Prepayment alert not to be raised");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			return null;
		}
	}

	private static String SpecialTermAlert_prev(String s) {
		int count = 0;
		int start = s.indexOf(SpecialTerms);

		// For MS if anything is there(even if the following statements), then
		// it is an alert
		String stdText1 = "Enter any additional terms or conditions applicable to this SOW (this is uncommon).";
		String stdText2 = "If requested by Cisco, Supplier resources shall record hours spent on tasks within a Cisco based task tracking system. "
				+ "All hours tracked will be Cisco Confidential Information and will not be used in any way for payment of Supplier.";
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h1")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			Pattern p = Pattern.compile(alert1regex);
			Matcher m = p.matcher(text);
			while (m.find()) {

				System.out.println("Before :: " + m.group(1));
				String temp = m.group(1).replace("dir=\"ltr\">", "")
						.replace(">", "").trim();

				temp = temp.replaceAll("[^\\x00-\\x7F]", "");
				temp = temp.replaceAll("(&nbsp|\\.)", "");
				temp = temp.trim();

				float prob1 = FuzzySearch.ratio(temp, stdText1);
				float prob2 = FuzzySearch.ratio(temp, stdText2);

				if (temp.equalsIgnoreCase("N/A")
						|| temp.equalsIgnoreCase("n/a")
						|| temp.equalsIgnoreCase("none")
						|| temp.equalsIgnoreCase("NA") || temp.length() < 1) {
					System.out.println(" 1) Count became 1 ");
					count = 1;
					break; // there are NA terms
				} else if (prob1 > 70) {
					count = 1;
					System.out.println(" 2) Count became 1 ");
					break;
				} else if (prob2 > 70) {
					continue;
				}
			}
			System.out.println("Count = " + count);
			if (count > 0) {
				System.out
						.println("Special term does not exist. Special term alert not to be raised");
				return null;
			} else {
				System.out
						.println("Special term exists. Special term alert to be raised");
				return Alert1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			return null;
		}
	}

	private static String SpecialTermAlert_deb(String s) {
		int count = 0;
		int start = s.indexOf(SpecialTerms);

		// For MS if anything is there(even if the following statements), then
		// it is an alert
		String stdText1 = "Enter any additional terms or conditions applicable to this SOW (this is uncommon).";
		String stdText2 = "If requested by Cisco, Supplier resources shall record hours spent on tasks within a Cisco based task tracking system. "
				+ "All hours tracked will be Cisco Confidential Information and will not be used in any way for payment of Supplier.";
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h1")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			// text=text.replaceAll("(<b>|</b>)", "");

			// System.out.println("text is:::"+text);
			Pattern p = Pattern.compile(alert1regex);
			Matcher m = p.matcher(text);
			while (m.find()) {

				System.out.println("Before :: " + m.group(1));
				String temp = m.group(1).replace("dir=\"ltr\">", "")
						.replace(">", "").trim();
				// System.out.println("middle::"+temp);
				temp = temp.replaceAll("[^\\x00-\\x7F]", "");
				// System.out.println("middle::"+temp);
				temp = temp.replaceAll("(&nbsp|\\.)", "");
				temp = temp.trim();
				// System.out.println("middle::"+temp);
				float prob1 = FuzzySearch.ratio(temp, stdText1);
				float prob2 = FuzzySearch.ratio(temp, stdText2);
				System.out.println("===" + temp);
				// System.exit(0);
				if (temp.equalsIgnoreCase("n/a")
						|| temp.equalsIgnoreCase("None")
						|| temp.equalsIgnoreCase("NA") || temp.length() < 1) {
					// System.out.println(" 1) Count became 1 ");
					count = 1;
					// System.out.println("count becoming 1 NA::");
					break; // there are NA terms
				} else if (prob1 > 70) {
					count = 1;
					System.out.println(" 2) Count became 1 ");
					break;
				} else if (prob2 > 70) {
					continue;
				}
			}
			System.out.println("Count = " + count);
			if (count > 0) {
				System.out
						.println("Special term does not exist. Special term alert not to be raised");
				return null;
			} else {
				System.out
						.println("Special term exists. Special term alert to be raised");
				return Alert1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			return null;
		}
	}

	private static String SpecialTermAlert(String s) {
		int count = 0;
		int start = s.indexOf(SpecialTerms);
		// For MS if anything is there(even if the following statements), then
		// it is an alert
		String stdText1 = "Enter any additional terms or conditions applicable to this SOW (this is uncommon).";
		String stdText2 = "If requested by Cisco, Supplier resources shall record hours spent on tasks within a Cisco based task tracking system";
		String stdText3 = "All hours tracked will be Cisco Confidential Information and will not be used in any way for payment of Supplier.";
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h1")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			// text=text.replaceAll("(<b>|</b>)", "");
			// System.out.println("text is:::"+text);
			Pattern p = Pattern.compile(alert1regex);
			Matcher m = p.matcher(text);
			String alltext = "";
			while (m.find()) {
				System.out.println("Before :: " + m.group(1));
				String temp = m.group(1).replace("dir=\"ltr\">", "")
						.replace(">", "").trim();
				// System.out.println("middle::"+temp);
				temp = temp.replaceAll("[^\\x00-\\x7F]", "");
				// System.out.println("middle::"+temp);
				temp = temp.replaceAll("(&nbsp|\\.)", "");
				temp = temp.trim();

				alltext = alltext + " " + temp;

			}

			String[] sent = alltext.split(SplitPropSet.patternSplitSentence);

			for (int i = 0; i < sent.length; i++) {
				System.out.println("sentence is::::" + sent[i]);
				int prob1 = FuzzySearch.tokenSetRatio(sent[i], stdText1);
				int prob2 = FuzzySearch.tokenSetRatio(sent[i], stdText2);
				int prob3 = FuzzySearch.tokenSetRatio(sent[i], stdText3);
				System.out.println(prob1 + "==" + prob2 + "====" + prob3);
				if (prob1 > 90 || prob2 > 90 || prob3 > 90)
					alltext = alltext.replace(sent[i], "").trim();
			}
			alltext = alltext.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
			if (alltext.equalsIgnoreCase("n/a")
					|| alltext.equalsIgnoreCase("None")
					|| alltext.equalsIgnoreCase("NA") || alltext.length() < 3)
				count++;

			System.out
					.println(alltext + "====================Count = " + count);
			if (count > 0) {
				System.out
						.println("Special term does not exist. Special term alert not to be raised");
				return null;
			} else {
				System.out
						.println("Special term exists. Special term alert to be raised");
				return Alert1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			return null;
		}
	}

	private static String OtherResourcesAlert(String s) {
		int count = 0;
		int start = s.indexOf(OtherSupplierResources);
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h2")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			// text = removeSpecialCharacters(text);
			text = text.replaceAll("[^\\x00-\\x7F]", " ");
			Pattern p = Pattern.compile(alert2regex);
			Matcher m = p.matcher(text);
			while (m.find()) {
				String temp1 = m.group(1);
				// System.out.println(temp1);
				String temp = Jsoup.parse(temp1).text().trim();
				// System.out.println(temp);
				if (temp.equalsIgnoreCase("Primary Location")
						|| temp.equalsIgnoreCase("Participation")
						|| temp.equalsIgnoreCase("Role")
						|| temp.equalsIgnoreCase("Name")
						|| temp.equalsIgnoreCase("Key Personnel")
						|| temp.equalsIgnoreCase("NA")
						|| temp.equalsIgnoreCase("N/A")
						|| temp.equalsIgnoreCase("None")
						|| temp.equalsIgnoreCase("")
						|| temp.equalsIgnoreCase("?????")) {
					// Do nothing System.out.println("safe");
				} else {
					// System.out.println("uhoh");
					count = count + 1; // Table is populated
				}

			}
			if (count > 0) {

				System.out
						.println("Table is populated. Other supplier resource alert to be raised");
				return (Alert2);
			} else {
				System.out
						.println("Table is not populated. Other supplier resource alert not to be raised");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
			return null;
		}
	}

	/*
	 * private static String removeSpecialCharacters(String text) { // TODO
	 * Auto-generated method stub Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
	 * Matcher match = pt.matcher(text); while (match.find()) { String s =
	 * match.group(); text = text.replaceAll("\\" + s, ""); }
	 * System.out.println(text); return text; }
	 */

	public static void main(String[] args) {
		StringBuilder contentBuilder = new StringBuilder();
		try {
			BufferedReader inputHTML = new BufferedReader(new FileReader(
					"C:/Users/IBM_ADMIN/Downloads/test.html"));
			// BufferedReader inputHTML = new BufferedReader(new
			// FileReader("C:/Users/IBM_ADMIN/Documents/SametimeFileTransfers/5150019550-great.html"));
			String str;
			while ((str = inputHTML.readLine()) != null) {
				contentBuilder.append(str);
			}
			inputHTML.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// String def = PaymentMethodAlert(s);
		// String ghi = SpecialTermAlert(str);

		// System.out.println(def);

	}
}