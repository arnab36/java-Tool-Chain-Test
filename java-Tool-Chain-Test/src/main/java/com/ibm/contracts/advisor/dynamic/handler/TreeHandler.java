package com.ibm.contracts.advisor.dynamic.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import org.jsoup.Jsoup;

import com.ibm.contracts.advisor.constants.Constants;

public class TreeHandler implements Constants {

	public static void main(String[] args) {
		StringBuilder contentBuilder = new StringBuilder();
		try {
			BufferedReader inputHTML = new BufferedReader(
					new FileReader(
							"C:\\Users\\IBM_ADMIN\\Desktop\\cisco-samples\\MP_HTML\\201937907-OK-AS1.html")); // 201937907-OK-AS1.html
																												// //6240053615-great.html
			String str;
			while ((str = inputHTML.readLine()) != null) {
				contentBuilder.append(str);
			}
			inputHTML.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String str = contentBuilder.toString();

		PaymentMethodAlert(str);
		SpecialTermAlert(str);
		OtherResourcesAlert(str);

		GeneralTerms(str);
		TypeValidation(str); // HTML needs to be correct. with id=SOW
		// WorkLocation(str);
		// ThirdParty(str);
		// Expenses(str);
		// WorkProduct(str);
		// CiscoPreexistingTerms(str);
		TermSOW(str);
	}

	private static void PaymentMethodAlert(String s) {
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
			Pattern p = Pattern.compile(alert3regex);
			Matcher m = p.matcher(text);
			while (m.find()) {
				String temp1 = m.group(1);
				String temp = Jsoup.parse(temp1).text().trim();
				if (temp.contains("Prepayment") || temp.contains("prepayment")
						|| temp.contains("pre-payment")
						|| temp.contains("Pre-Payment")
						|| temp.contains("Pre-payment")) {
					count = count + 1;
					break;
					// Raise alert
				}
			}
			if (count > 0) {
				System.out
						.println("Prepayment word exists. Prepayment alert to be raised");
			} else {
				System.out
						.println("Prepayment word doesn't exist. Prepayment alert not to be raised");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
		}
	}

	private static void SpecialTermAlert(String s) {
		int count = 0;
		int start = s.indexOf(SpecialTerms);
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h1")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			// String text1 = removeSpecialCharacters(text);
			Pattern p = Pattern.compile(alert1regex);
			Matcher m = p.matcher(text);
			while (m.find()) {
				String temp = m.group(1).replace("dir=\"ltr\">", "")
						.replace(">", "").trim();
				// System.out.println(temp);
				if (temp.contains("N/A") || temp.contains("n/a")
						|| temp.contains("none") || temp.contains("None")
						|| temp.contains("NA") || temp.equals("")) {
					count = 1;
					break; // there are NA terms
				}
			}
			if (count > 0) {
				System.out
						.println("Special term does not exist. Special term alert not to be raised");
			} else {
				System.out
						.println("Special term exists. Special term alert to be raised");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
		}
	}

	private static void OtherResourcesAlert(String s) {
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
			} else {
				System.out
						.println("Table is not populated. Other supplier resource alert not to be raised");// return
																											// null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
		}
	}

	private static void GeneralTerms(String s) {
		String temp1 = null;
		int start = s.indexOf("General");
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h1")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);

			Pattern q = Pattern.compile(Pattern.quote(by) + "(.*?)"
					+ Pattern.quote(under));
			Matcher n = q.matcher(text);
			while (n.find()) {
				temp1 = n.group(1).trim();
			}
			if (temp1.equalsIgnoreCase("N/A") || temp1.equalsIgnoreCase("None")
					|| temp1.equalsIgnoreCase("NA")
					|| temp1.equalsIgnoreCase("")) {
				System.out.println("General Term does not exist"); // Red node
			} else {
				System.out.println("General Term does exist"); // Green node
			}
		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("catch exception");
		}
	}

	private static void TypeValidation(String s) {
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
			} else if ((startCheckMS > -1) && (termMS > 0)) {
				// MS document
				System.out.println("MS document");
			}
		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("catch exception");
		}
	}

	private static void WorkLocation(String s) {
		String sent1 = null;
		String sent2 = null;
		String sent3 = null;
		String sent4 = null;
		int start = s.indexOf("WorkLocation");
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h1")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);

			Pattern case1 = Pattern.compile(Pattern.quote("by Supplier at")
					+ "(.*?)" + Pattern.quote("at:"));
			Matcher test1 = case1.matcher(text);
			while (test1.find()) {
				sent1 = test1.group(1).trim();
			}

			Pattern case2 = Pattern.compile(Pattern.quote("by Supplier at")
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
			} else {
				System.out.println("Work Location Red");
			}
		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("catch exception");
		}
	}

	private static void ThirdParty(String s) {
		int flag = 0;
		String temp1 = null;
		int start = s.indexOf("ThirdPartyProperty");
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
				String temp = m
						.group()
						.replace("dir=\"ltr\">", "")
						.replace(">", "")
						.replace("&nbsp;", "")
						.replace(
								"Define any supplier third party property, if required for this engagement or N/A if not applicable.",
								"")
						.replace(
								"Supplier shall provide and pay for the following third party property to complete the Work:",
								"").replace(" ", "");
				temp1 = temp.trim();
				// System.out.println(temp1);
				if (temp1.length() > 1) {
					flag = 1;
					break;
				}
			}
			if (flag == 1) {
				System.out.println("Third party property green");
			} else {
				System.out.println("Third party property red");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
		}
	}

	private static void Expenses(String s) {
		int alert = 0;
		int temp1 = 0;
		int start = s.indexOf("id=\"Expenses\"");
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 2).equals("<h")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			Pattern q = Pattern.compile(Pattern.quote("<b>") + "(.*?)"
					+ Pattern.quote("</b>"));
			Matcher n = q.matcher(text);
			while (n.find()) {
				temp1 = Integer.parseInt(n.group(1).replaceAll("[^0-9]", "")
						.trim());
				// System.out.println(temp1);
			}

			int startPay = s.indexOf("PaymentMethod");
			int endPay = 0;
			// System.out.println(startPay);
			for (int j = startPay;; j++) {
				if (s.substring(j, j + 3).equals("<h2")) {
					endPay = j;
					break;
				}
			}
			String textPay = s.substring(startPay, endPay);
			Pattern p = Pattern
					.compile("(?<=\\<tbody\\>)(.+?)(?=\\<\\/tbody\\>)");
			Matcher m = p.matcher(textPay);
			text = "";
			while (m.find()) {
				text += m.group();
			}
			// System.out.println(text);
			startPay = text.indexOf("Expenses");
			endPay = 0;
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}
			q = Pattern.compile(Pattern.quote("<b>") + "(.*?)"
					+ Pattern.quote("</b>"));
			n = q.matcher(text.substring(startPay, endPay));
			ArrayList<Integer> expen = new ArrayList<Integer>();
			while (n.find()) {
				expen.add(Integer.parseInt(n.group(1).replaceAll("[^0-9]", "")
						.trim()));
			}

			startPay = text.indexOf("Subtotal of 8b");
			endPay = 0;
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}
			q = Pattern.compile(Pattern.quote("<b>") + "(.*?)"
					+ Pattern.quote("</b>"));
			n = q.matcher(text.substring(startPay, endPay));
			ArrayList<Integer> subtotal = new ArrayList<Integer>();
			while (n.find()) {
				if (n.group(1).replaceAll("[^0-9]", "").trim().equals(""))
					continue;
				else
					subtotal.add(Integer.parseInt(n.group(1)
							.replaceAll("[^0-9]", "").trim()));
			}

			startPay = text.indexOf("Total of 8b and 8c");
			endPay = 0;
			for (int j = startPay;; j++) {
				if (text.substring(j, j + 4).equals("</tr")) {
					endPay = j;
					break;
				}
			}
			q = Pattern.compile(Pattern.quote("<b>") + "(.*?)"
					+ Pattern.quote("</b>"));
			n = q.matcher(text.substring(startPay, endPay));
			ArrayList<Integer> total = new ArrayList<Integer>();
			while (n.find()) {
				total.add(Integer.parseInt(n.group(1).replaceAll("[^0-9]", "")
						.trim()));
			}

			int sum = 0;
			for (int i : expen) {
				sum += i;
			}
			// System.out.println(sum==temp1);
			int size = expen.size();
			for (int i = 0; i < size; i++) {
				if (subtotal.get(i) + expen.get(i) != total.get(i)) {
					alert = 1;
					break;
				}
			}
			if ((alert == 0) && (sum == temp1)) {
				System.out.println("Expense class green");
			} else {
				System.out.println("Expense class red");
			}
		} catch (Exception e) {
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
		int start = 0;
		int starttemp = s.indexOf("id=\"WorkProduct\"");
		while (starttemp >= 0) {
			System.out.println(start);
			if (starttemp > -1)
				start = starttemp;
			starttemp = s.indexOf("id=\"WorkProduct\"", start + 1);
		}
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h2")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			Pattern q = Pattern.compile(Pattern.quote("<tr>") + "(.*?)"
					+ Pattern.quote("</tr>"));
			Matcher n = q.matcher(text);
			while (n.find()) {
				temp1 = n.group().trim();
				Pattern p = Pattern.compile(Pattern.quote("<td>") + "(.*?)"
						+ Pattern.quote("</td>"));
				Matcher m = p.matcher(temp1);
				flag = 0;
				flag2 = 0;
				// System.out.println("FLAGS" + flag + flag2 + n.group(1));
				while (m.find()) {
					temp2 = m.group(1).replace("?????", "pass")
							.replace("&nbsp;", "pass").trim(); // ????? being
																// used
					temp3 = Jsoup.parse(temp2).text();
					if (temp3.equals("")) {
						flag = 1;
						// System.out.println(temp3 + " " + flag);
					} else {
						flag2 = 2;
						// System.out.println(temp3 + " " + flag2);
					}
				}
				if (flag == 1 && flag2 == 2) {
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

		catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
		}
	}

	private static void CiscoPreexistingTerms(String s) {
		int alert = 0;
		int flag = 0;
		int flag2 = 0;
		String temp1 = "";
		String temp2 = "";
		String temp3 = "";
		int start = s.indexOf("CiscoPreExistingProperty");
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h2")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);
			Pattern q = Pattern.compile(Pattern.quote("<tr>") + "(.*?)"
					+ Pattern.quote("</tr>"));
			Matcher n = q.matcher(text);
			while (n.find()) {
				temp1 = n.group().trim();
				Pattern p = Pattern.compile(Pattern.quote("<td>") + "(.*?)"
						+ Pattern.quote("</td>"));
				Matcher m = p.matcher(temp1);
				flag = 0;
				flag2 = 0;
				// System.out.println("FLAGS" + flag + flag2 + n.group(1));
				while (m.find()) {
					temp2 = m.group(1).trim();
					temp3 = Jsoup.parse(temp2).text();
					if (temp3.equalsIgnoreCase("yes")) {
						flag = 1;
						System.out.println(temp3 + " " + flag);
					} else {
						flag2 = 2;
						System.out.println(temp3 + " " + flag2);
					}
				}
				if (flag == 1 && flag2 == 2) {
					alert = 1;
					break;
				}
			}
			if (alert == 1) {
				System.out.println("Cisco Preexisting table red"); // Work
																	// Product
																	// table is
																	// not
																	// populated
			} else {
				System.out.println("Cisco Preexisting table is green"); // Work
																		// Product
																		// table
																		// is
																		// populated
			}
		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("catch exception");
		}
	}

	private static void TermSOW(String s) {
		String startDate = null;
		String endDate = null;
		int start = s.indexOf("id=\"TermSOW\"");
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (s.substring(i, i + 3).equals("<h1")) {
					end = i;
					break;
				}
			}
			String text = s.substring(start, end);

			Pattern q = Pattern.compile(Pattern.quote("begin on") + "(.*?)"
					+ Pattern.quote("and remain"));
			Matcher n = q.matcher(text);
			while (n.find()) {
				startDate = n.group(1).trim();
				System.out.println(startDate);
			}
			Pattern p = Pattern.compile(Pattern.quote("Work Product,")
					+ "(.*?)" + Pattern.quote(", or"));
			Matcher m = p.matcher(text);
			while (m.find()) {
				endDate = m.group(1).trim();
				System.out.println(endDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception");
		}
	}
}
// int result = FuzzySearch.ratio("mysmilarstring","mysimilarstring");
// System.out.println(result);