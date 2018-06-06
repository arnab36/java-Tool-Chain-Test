package com.ibm.contracts.advisor.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.driver.RemoveStatic;
import com.ibm.contracts.advisor.driver.docConvert_V3;
import com.ibm.contracts.advisor.vo.JsonAndHTML;
import com.ibm.contracts.advisor.vo.ParagraphObject;
import com.ibm.contracts.advisor.vo.TagNameIdReturn;

public class PostDocumentConversionHTMLProcessing {

	// private static String removeRegex =
	// "((?=\\S)[\\*]+(?<=\\S))|((?=\\S)[\\(]?[\\d]+[\\)]?(?<=\\S))|((?=\\S)[\\(]?[A-Za-z][\\)](?<=\\S))";
	private static String patternRemoveEnum = "((?=\\S)[\\(]?[\\d]+[\\)]?(?<=\\S))|((?=\\S)[\\(]?[A-Za-z][\\)](?<=\\S)\\s*)";
	private static String patternRremoveSpchar = "(?=\\S)[\\*]+(?<=\\S)\\s*";
	private static String[] headerTagList = { "h1", "h2", "h3", "h4", "h5",
			"h6" };

	public static String classTemplateJson = null;
	public static boolean appendixB = false, expense = false;

	public static JsonAndHTML ProcessHTML(String docHTML)
			throws FileNotFoundException, IOException, ParseException {
		Document staticDoc = null;
		Document dynamicDoc = null;
		// Document generalDoc = null;

		if (("").equalsIgnoreCase(docHTML) || docHTML == null) {
			return null;
		}

		JSONParser parser = new JSONParser();
		JSONObject template;
		/* JSONObject ClasstemplateJson = Util
		  .getJSONFromFile("C:/Users/IBM_ADMIN/Desktop/cisco-samples/Template_MP_Classes.json");*/
		 
		try {
			template = (JSONObject) parser.parse(classTemplateJson);
			// template = ClasstemplateJson;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		staticDoc = Jsoup.parse(docHTML);
		// Cloning will create a separate object in the memory space. Otherwise
		// it will point to same memory location.
		dynamicDoc = staticDoc.clone();

		// System.out.println(template);

		// Table extraction is only for static and general document
		staticDoc = docConvert_V3.extractTableForGeneral(staticDoc, template);

		// The table removal is only for static document
		staticDoc.select("table").remove();

		// OL tag processing is for both static and dynamic
		staticDoc = processOLTag(staticDoc);
		dynamicDoc = processOLTag(dynamicDoc);

		dynamicDoc = ProcessHTMLAndAssignTagID(dynamicDoc, template);
		staticDoc = ProcessHTMLAndAssignTagID(staticDoc, template);
		// generalDoc = ProcessHTMLAndAssignTagID(generalDoc,template);
		
		staticDoc = addTitle(staticDoc);
		// To be uncommented
		dynamicDoc = RemoveStatic.removeAllStatic(
				PostNLCPropSet.staticTemplatefile, dynamicDoc.toString());
		// generalDoc = addTitle(generalDoc);

		/*JSONObject staticJsonObject = HTML2JSONObject
				.Html2Jsonobject(staticDoc);*/		
		
		JSONObject staticJsonObject = docConvert_V3
				.Html2Jsonobject(staticDoc);
		

		System.out.println(staticJsonObject);
		// JSONObject generalJsonObject =
		// HTML2JSONObject.Html2Jsonobject(generalDoc);

		JsonAndHTML jah = new JsonAndHTML();
		jah.setDunamicInput(dynamicDoc);
		jah.setStaticInput(staticJsonObject);
		// jah.setGeneralInput(generalJsonObject);
		return jah;
	}

	// Changed Private to Public Legacy defect fixing
	public static Document addTitle(Document doc) {
		// TODO Auto-generated method stub

		Element titleTag = new Element("h1");
		titleTag.text("SOW");
		titleTag.attr("id", "");
		// doc.insertChildren(0, titleTag);
		doc.body().insertChildren(0, titleTag);
		return doc;
	}

	/**
	 * The following function will process a document and assign TagID based on
	 * template
	 * */

	public static Document ProcessHTMLAndAssignTagID(Document document,
			JSONObject template) {
		expense=false;
		// TODO Auto-generated method stub
		Elements elements = document.select("*");

		for (Element element : elements) {

			if (element.tagName().matches("(p|h[1-5])") == true) {
				Elements children = element.children();

				// Child calculation is different in Rambau's
				// Version(Python) and Arnab's Version(Java)
				int numberOfChld = calculateRambabuChild(element) - 1;

				if (numberOfChld > 0) {
					for (Element chld : children) {
						if (chld.tagName().equalsIgnoreCase("b")) {

							String childtag_text = chld.text();
							TagNameIdReturn tagNameID = compareTitles(
									childtag_text, template);
							if (!tagNameID.getTagName().equalsIgnoreCase("")) {
								Element el = new Element(tagNameID.getTagType());
								el.attr("id", tagNameID.getTagName());
								el.text(chld.ownText());
								// The following was Arnab's mistake
								// chld.before(el);
								element.before(el);
								chld.remove();

							}

						} else if (inHeaderTag(chld.tagName())) {

							String childtag_text = chld.text();
							TagNameIdReturn tagNameID = compareTitles(
									childtag_text, template);
							if (!tagNameID.getTagName().equalsIgnoreCase("")) {
								Element el = new Element(tagNameID.getTagType());
								el.attr("id", tagNameID.getTagName());
								el.text(chld.ownText());
								// The following was Arnab's mistake
								// chld.before(el);
								element.before(el);
								chld.remove();

							}

						}

					}
					// looking for other than b and p tags
					String parentTagText = element.text().trim();
					ParagraphObject po = new ParagraphObject();
					po = title_searchInParagraph(parentTagText, template);
					if (po.getHtagName() != null
							&& !("").equalsIgnoreCase(po.getHtagName())) {
						Element el = new Element(po.getHtagName());
						el.text(po.getTtagString());
						el.attr("id", po.getHtagID());
						element.text(po.getPtagString());
						element.before(el);
					}

				} else {
					String parentTagText = element.text().trim();
					if (inHeaderTag(element.tagName())) {

						TagNameIdReturn tagNameID = compareTitles(
								parentTagText, template);
						if (!tagNameID.getTagName().equalsIgnoreCase("")) {
							element.attr("id", tagNameID.getTagName());
							element.tagName(tagNameID.getTagType());
							element.text(parentTagText);

						} else {
							element.tagName("p");
							element.text(parentTagText);

						}
					} else if (element.tagName().equalsIgnoreCase("p")) {
						if (parentTagText.split(" ").length > 0
								&& parentTagText.split(" ").length < 20) {

							TagNameIdReturn tagNameID1 = compareTitles(
									parentTagText, template);

							if (!tagNameID1.getTagName().equalsIgnoreCase("")) {
								element.attr("id", tagNameID1.getTagName());
								element.tagName(tagNameID1.getTagType());
								element.text(parentTagText);

							} else {
								ParagraphObject po1 = new ParagraphObject();
								po1 = title_searchInParagraph(parentTagText,
										template);
								if (po1.getHtagName() != null
										&& !("").equalsIgnoreCase(po1
												.getHtagName())) {
									Element el = new Element(po1.getHtagName());
									el.text(po1.getTtagString());
									el.attr("id", po1.getHtagID());
									element.text(po1.getPtagString());
									element.before(el);
								} else {
									element.text(parentTagText);
								}

							}
						} else {
							ParagraphObject po1 = new ParagraphObject();
							po1 = title_searchInParagraph(parentTagText,
									template);
							if (po1.getHtagName() != null
									&& !("").equalsIgnoreCase(po1.getHtagName())) {
								Element el = new Element(po1.getHtagName());
								el.text(po1.getTtagString());
								el.attr("id", po1.getHtagID());
								element.text(po1.getPtagString());
								element.before(el);
							} else {
								element.text(parentTagText);
							}
						}
					} else {
						continue;
					}
				}

			}

		}
		return document;
	}

	// This method has to be modified
	private static int calculateRambabuChild(Element element) {
		// TODO Auto-generated method stub
		int sum = 0;
		Elements e = element.select("*");
		for (Element el : e) {
			sum++;
		}
		return sum;
	}

	// The following is the child calculation method Done by Rambabu in python
	private static ParagraphObject title_searchInParagraph(
			String parentTagText, JSONObject classtemplateJson) {
		// TODO Auto-generated method stub
		ParagraphObject po = new ParagraphObject();
		String tagType = "none";
		String tagID = "";
		String pTagString = "";
		String titleString = "";
		String regExTitle = "";

		JSONArray section_titles = (JSONArray) classtemplateJson
				.get("section_titles");
		int size = section_titles.size();
		String secTitle = "";
		for (int i = 0; i < size; i++) {
			secTitle = ((JSONObject) section_titles.get(i)).get("title")
					.toString();
			regExTitle = "([\\(]?\\d{1,}[\\)]\\W*|[a-zA-Z][\\)]\\W*)%s\\s*";
			regExTitle.replaceAll("%s", secTitle);
			Pattern regExTitlePattern = Pattern.compile(regExTitle);
			Matcher m = regExTitlePattern.matcher(secTitle);
			if (m.find()) {
				String ptagText = parentTagText.replaceAll(regExTitle, "");
				po.setHtagName(((JSONObject) section_titles.get(i)).get("type")
						.toString());
				po.setHtagID(((JSONObject) section_titles.get(i)).get(
						"class_name").toString());
				po.setPtagString(ptagText);
				po.setTtagString(secTitle);
			}
		}

		po.setHtagID(tagType);
		po.setHtagName(tagID);
		po.setPtagString(pTagString);
		po.setTtagString(titleString);
		return po;
	}

	// Processing of OL tags
	public static Document processOLTag(Document doc) {
		// TODO Auto-generated method stub
		Elements elements = doc.select("ol");
		for (Element element : elements) {
			if (element.tagName() != null) {
				Elements liElement = element.select("li");
				for (Element li : liElement) {
					String liText = li.text().trim();
					Element el = new Element("p");
					el.text(liText);
					try{
					element.before(el);
					element.remove();
					}catch(Exception e){
						System.out.println(e.toString());
					}
				}
			}
		}
		return doc;
	}

	// Processing the table tag
	private static Document extractTable(Document doc,
			JSONObject classtemplateJson) {
		// TODO Auto-generated method stub
		Elements elements = doc.select("table");
		for (Element element : elements) {
			if (element.tagName() != null) {
				Elements rows = element.select("tr");
				for (Element tr : rows) {
					ArrayList<String> text_data = new ArrayList<String>();
					Elements tableData = tr.select("td");
					for (Element td : tableData) {
						String tdText = td.text().trim();
						tdText = tdText.replaceAll("^\"+", "").replaceAll(
								"\"+$", "");
						tdText = tdText.replaceAll("[^a-zA-Z0-9_-]", " ");
						text_data.add(tdText);
					}
					String tr_text = "";
					for (int i = 0; i < text_data.size(); i++) {
						tr_text = tr_text + text_data.get(i) + " ";
					}
					int countWords = tr_text.split(" ").length;
					if (countWords > 0 && countWords < 20) {
						TagNameIdReturn tagNameID = compareTitles(tr_text,
								classtemplateJson);
						if (!tagNameID.getTagType().toString()
								.equalsIgnoreCase("none")) {
							Element el = new Element("p");
							el.text(tr_text);
							element.before(el);
						}
					}

				}
			}

		}
		return doc;
	}

	private static boolean inHeaderTag(String tagName) {
		boolean flag = false;
		for (int i = 0; i < headerTagList.length; i++) {
			if (headerTagList[i].equalsIgnoreCase(tagName)) {
				return true;
			}
		}
		return flag;
	}

	private static TagNameIdReturn compareTitles(String childtag_text,
			JSONObject ClasstemplateJson) {
		String tagType = "none";
		String tagName = "";

		TagNameIdReturn tempObj = new TagNameIdReturn();

		JSONArray temp = (JSONArray) ClasstemplateJson.get("section_titles");
		int size = temp.size();

		String stringToMatch = childtag_text.replaceAll(patternRemoveEnum, "");
		stringToMatch = stringToMatch.replaceAll(patternRremoveSpchar, "");
		// System.out.println(stringToMatch);
		for (int i = 0; i < size; i++) {
			JSONObject jObj = (JSONObject) temp.get(i);
			String title = jObj.get("title").toString();
			double probMatch = FuzzySearch.ratio(title, stringToMatch);

			if (probMatch > 90) {				
				// For AppendixB Fix.
				System.out.println(expense+"======================"+jObj.get("class_name").toString());
				if(jObj.get("class_name").toString().equalsIgnoreCase("Expenses")){
					if(!expense){
						if(jObj.get("class_name").toString().equalsIgnoreCase("Expenses")){
							expense = true;
						}					
					}else {					
							tempObj.setTagType(tagType);
							tempObj.setTagName(tagName);
							return tempObj;					
					}	
				}	
				
				System.out.println("Got class Name :: "+jObj.get("class_name").toString() );
				tempObj.setTagType(jObj.get("type").toString());
				tempObj.setTagName(jObj.get("class_name").toString());
				return tempObj;
			}

		}
		tempObj.setTagType(tagType);
		tempObj.setTagName(tagName);
		return tempObj;
	}

}