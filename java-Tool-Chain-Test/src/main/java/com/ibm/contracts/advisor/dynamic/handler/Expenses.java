package com.ibm.contracts.advisor.dynamic.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.util.IdentifyDate;

public class Expenses {

	public static boolean checkExpense(String str,
			JSONObject dynamicPopUpContent) {
		str = str.replaceAll("<b>|</b>", "");
		Document doc = Jsoup.parse(str);
		String paymentTab = getTableContent(doc, "paymentmethod").replaceAll(
				"[\\.,]", "");
		String expanses = getExpanseContent(doc, "expenses",
				dynamicPopUpContent).replaceAll("[\\.,]", "");
		IdentifyDate.getSpecialString(paymentTab, "");
		System.out.println(expanses);
		System.out.println(paymentTab);
		// Bug fix point no 9 following Cisco Testing Iteration 2_8NOV .xlsx - Atrijit Dasgupta
		if ((Double.parseDouble(expanses.trim()) == 0.0)||(Double.parseDouble(expanses.trim()) == 0.00)||(Double.parseDouble(expanses.trim()) == 0)) {
			System.out.println("got 0 expanses");
			return true;
		}
		if (expanses.equalsIgnoreCase(paymentTab)) {

			return true;
		} else {
			dynamicPopUpContent
					.put("Expenses",
							"Either the expenses are not included in the payment table or have not been correctly added.");
			return false;
		}

		// JSONObject j=new JSONObject();
	}

	public static void main(String[] args) {
		String docType = "MP";
		StringBuilder contentBuilder = new StringBuilder();
		String fileLoc = "C:/Users/IBM_ADMIN/Documents/project 8/cisco/SOWs(1)/";
		try {
			BufferedReader inputHTML = new BufferedReader(new FileReader(
					fileLoc + "5350005260-great.html_StaticRemoved.html")); // 201937907-OK-AS1.html
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
		System.out.println(checkExpense(str, j));

		// System.out.println(ProcessDynamicClasses(str,j));

		// System.out.println(Expenses(str,j));

	}

	private static String getExpanseContent(Document doc, String id,
			JSONObject dynamicPopUpContent) {
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
		System.out.println("::::::" + targetText);
		String temp = new String(targetText);
		targetText = targetText.substring(targetText
				.indexOf("shall not exceed")+16);//+16 newly added
		dynamicPopUpContent.put("Expenses", temp);
		System.out.println("from expanses section::"+targetText);
		if(IdentifyDate.getSpecialString(targetText,"(\\.[0-9]+)")==null){
			targetText=targetText+".00";//newly added
		}
			
		/*return IdentifyDate.getSpecialString(targetText,
				"((\\d*,?)+\\d+\\.?\\d*)");*/
		return targetText.replaceAll("[^\\d]", "");

	}

	public static String getTableContent(Document doc, String id) {
		Elements targetSet = doc.getElementsByAttributeValue("id", id);

		String targetText = "";
		Element target = targetSet.get(0);
		System.out.println(target);
		while (true) {
			target = target.nextElementSibling();
			System.out.println(target.tagName());
			if (target.tagName().equals("h1") || target.tagName().equals("h2"))
				break;
			if (target.tagName().equals("table"))
				targetText = targetText + target.text();
		}
		System.out.println(":::::" + targetText);
		
		String prefix=IdentifyDate.getSpecialString(targetText, "(expenses \\(total from section 8c\\))|(subtotal of 8c)");
		
		targetText = targetText.substring(
				targetText.indexOf(prefix)
						+ prefix.length(),
				targetText.indexOf("total of 8b and 8c"));
		
		
		/*targetText = targetText.substring(
				targetText.indexOf("expenses (total from section 8c)")
						+ "expenses (total from section 8c)".length(),
				targetText.indexOf("total of 8b and 8c"));*/
		return IdentifyDate.getSpecialString(targetText,
				"((\\d*,?)+\\d+\\.?\\d*)");
	}

}
