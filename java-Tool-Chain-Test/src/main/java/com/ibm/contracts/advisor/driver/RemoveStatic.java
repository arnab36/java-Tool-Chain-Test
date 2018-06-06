package com.ibm.contracts.advisor.driver;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.constants.SplitPropSet;
import com.ibm.contracts.advisor.util.Util;

public class RemoveStatic {

	public static JSONObject MP_TemplateJson = null;
	public static Set<String> idKey = new HashSet<String>();
	static JSONObject MPTemplateJSONObject = null;
	public static ArrayList<String> removeit = new ArrayList<String>();

	public static Document removeAllStatic(JSONObject temp, String html) {
		MP_TemplateJson = temp;
		html = html.replaceAll("<b>|</b>", "");
		MPTemplateJSONObject = docConvert_V3.createJSONObject(MP_TemplateJson);

		Document doc = Jsoup.parse(html);

		for (Object idName : MPTemplateJSONObject.keySet()) {
			System.out.println(idName);
			idKey.add(idName.toString());
		}

		htmlTags(doc.children());
		System.out
				.println("=========================================================="
						+ removeit.size());
		//removeTags(doc.children());
		return doc;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException, ParseException {
		
		System.out
				.println("=========================================================");
		MP_TemplateJson = Util
				.getJSONFromFile("C:/CISCO-v2-MP/config/MP_Static_Template_V5.json");

		MPTemplateJSONObject = docConvert_V3.createJSONObject(MP_TemplateJson);
		String FileDetail = "C:/Users/IBM_ADMIN/Documents/testnow/cisco-samples/MP HTML/5150019550-great.html";

		String s = new String(Files.readAllBytes(Paths.get(FileDetail)));
		s = s.replaceAll("<b>|</b>", "");
		Document doc = Jsoup.parse(s);

		for (Object idName : MPTemplateJSONObject.keySet()) {
			System.out.println(idName);
			idKey.add(idName.toString());
		}

		htmlTags(doc.children());
		System.out
				.println("=========================================================="
						+ removeit.size());
		// removeTags(doc.children());

		FileWriter file2 = new FileWriter(FileDetail + "_StaticRemoved.html");
		file2.write(doc.toString());
		file2.close();

	}

	private static void removeTags(Elements elements) {
		for (Element el : elements) {
			if (removeit.size() == 0)
				return;
			String tmp = removeit.get(0);
			if (el.ownText().equals(tmp)) {
				System.out.println("=======removing:::" + el.ownText());
				el.remove();
				removeit.remove(0);
				continue;
			}
			removeTags(el.children());
		}
	}

	private static void htmlTags(Elements elements) {
		try {
			for (Element el : elements) {
				String idSOW = el.attr("id");
				if (idKey.contains(idSOW)) {
					System.out.println("id matching" + idSOW);
					gatherStatic(idSOW, el);
					continue;
				}
				htmlTags(el.children());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private static void gatherStatic(String idSOW, Element el) {
		System.out.println("coming for ::" + idSOW);
		ArrayList<String> arr = (ArrayList<String>) MPTemplateJSONObject
				.get(idSOW);

		while (el.hasText()) {
			el = el.nextElementSibling();
			if (el == null)
				break;
			if (el.tagName().equalsIgnoreCase("h2")
					|| el.tagName().equalsIgnoreCase("h1"))
				break;
			System.out.println("Checking for text::" + el.ownText());
			catchStatc(arr, el);
		}

	}

	public static void catchStatc(ArrayList<String> arr, Element ela) {
		String[] allSen = ela.ownText().split(SplitPropSet.patternSplitSentence);
		List<String> sentList = new LinkedList<String>();
		for (int a = 0; a < allSen.length; a++) {
			sentList.add(allSen[a]);
		}
		for (int a = 0; a < allSen.length; a++) {
			if (allSen[a].length() < 6)
				continue;
			for (String temText : arr) {
				int ratio = FuzzySearch.tokenSetRatio(temText, allSen[a]);
				if (ratio > 90) {
					System.out.println(ratio + ":::" + allSen[a]);
					System.out.println("with ::" + temText);
					try {
						sentList.remove(allSen[a]);
						/*
						 * if(removeit.size()==0 ){ removeit.add(el.ownText());
						 * continue; } if(
						 * !removeit.get(removeit.size()-1).equalsIgnoreCase
						 * (el.ownText())) removeit.add(el.ownText());
						 */

						System.out
								.println("removed -----------------------------");
					} catch (Exception e) {
						System.out.println(e.toString());
					}
				}

			}
		}
		if (sentList.size() == 0)
			ela.text("");
		else {
			String putit = "";
			for (String s : sentList)
				putit = putit + ". " + s;
			ela.text(putit);
		}

	}

}
