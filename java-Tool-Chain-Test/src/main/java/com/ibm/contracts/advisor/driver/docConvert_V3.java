package com.ibm.contracts.advisor.driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.constants.APICredentials;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.handler.DocumentConversionHTMLHandler;
import com.ibm.contracts.advisor.util.PostDocumentConversionHTMLProcessing;
import com.ibm.contracts.advisor.util.StaticScoreCalculator;
import com.ibm.contracts.advisor.util.Util;
import com.ibm.contracts.advisor.vo.ConvertReturn;
import com.ibm.contracts.advisor.vo.IndexSentencePair;
import com.ibm.contracts.advisor.vo.JsonAndHTML;
import com.ibm.contracts.advisor.vo.ParagraphObject;
import com.ibm.contracts.advisor.vo.TagNameIdReturn;

public class docConvert_V3 implements Constants {

	private static String removeRegex = "((?=\\S)[\\*]+(?<=\\S))|((?=\\S)[\\(]?[\\d]+[\\)]?(?<=\\S))|((?=\\S)[\\(]?[A-Za-z][\\)](?<=\\S))";
	private static String[] headerTagList = { "h1", "h2", "h3", "h4", "h5",
			"h6" };

	public static String patternSplitSentence = "\\s[\\(]?(iv|v?i{0,3})[\\)]\\s|\\s([\\(]?[a-zA-Z][\\)])\\s";
	public static String patternSplitMulti = "(?i)(?<=[.?!])\\S+(?=[a-zA-Z])(\\s{2,})|(.)(\\s{2,})";
	
	public static String patternRremoveSpchar = "(?=\\S)[\\*]+(?<=\\S)\\s*";
	public static String patternRemoveEnum = "((?=\\S)[\\(]?[\\d]+[\\)]?(?<=\\S))|((?=\\S)[\\(]?[A-Za-z][\\)](?<=\\S)\\s*)";
	public static String RegExEnum = "\\s[\\(]?(iv|v?i{0,3})[\\)]\\s|\\s([\\(]?[a-zA-Z][\\)])\\s";
	public static String pattern_word = "[a-zA-Z_]+";
	public static boolean appendixB = false, expense = false;

	public static void main(String[] args) throws FileNotFoundException,
			IOException, InterruptedException, ExecutionException,
			ParseException {
		System.out.println("----------------------");
		File file = new File(
				"C:/Users/IBM_ADMIN/Documents/testnow/cisco-samples/MP/5150019550-great.docx");
		
		JSONObject ClasstemplateJson = Util
				.getJSONFromFile("C:/CISCO-v2-MP/config/Template_MP_Classes.json");

		JSONObject MP_TemplateJson = Util
				.getJSONFromFile("C:/CISCO-v2-MP/config/MP_Static_Template_V5.json");

		PostNLCPropSet.templatefile = Util.getJSONObject(ClasstemplateJson
				.toJSONString());

		APICredentials.CLASS_WORKSPACE_ID = APICredentials.MP_WORKSPACE_ID;

		try {
			PostNLCPropSet.templateList = Util.populateTemplateTitle();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// The following will read the static template and then will convert
		// that to JSon object
		JSONObject MPTemplateJSONObject = createJSONObject(MP_TemplateJson);
		String docHTML = null;

		if (file != null) {
			// Document conversion
			ConvertReturn answers = DocumentConversionHTMLHandler
					.convertFile(file);

			docHTML = answers.getHtml().toString();
			Document doc = Jsoup.parse(docHTML);			
			
			
			/*JsonAndHTML preProcessedInput = PostDocumentConversionHTMLProcessing
					.ProcessHTML(docHTML);			
			System.out.println(preProcessedInput.getStaticInput());
			System.exit(0);			
			*/
			
			
			doc = extractTableForGeneral(doc, ClasstemplateJson);
			//doc = extractTable(doc, ClasstemplateJson);
			doc.select("table").remove();
			
			doc = processOLTag(doc);
			
			//System.out.println(doc);
			
			int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, c8 = 0, c9 = 0, c10 = 0;

			Elements elements = doc.select("*");
			// Elements elements = doc
			int count = 0, totalCount = 0;

			for (Element element : elements) {

				if (element.tagName().matches("(p|h[1-5])") == true) {
					Elements children = element.children();
					
					// Child calculation is different in Rambau's
					// Version(Python) and Arnab's Version(Java)
					int numberOfChld = calculateRambabuChild(element) - 1;
					count++;
					count = 0;
					if (numberOfChld > 0) {
						for (Element chld : children) {
							if (chld.tagName().equalsIgnoreCase("b")) {
								c7++;
								String childtag_text = chld.text();
								TagNameIdReturn tagNameID = compareTitles(
										childtag_text, ClasstemplateJson);
								if (!tagNameID.getTagName()
										.equalsIgnoreCase("")) {
									Element el = new Element(
											tagNameID.getTagType());
									el.attr("id", tagNameID.getTagName());
									el.text(chld.ownText());
									// The following was Arnab's mistake
									// chld.before(el);
									element.before(el);
									chld.remove();
									c8++;
								}

							} else if (inHeaderTag(chld.tagName())) {
								c9++;
								String childtag_text = chld.text();
								TagNameIdReturn tagNameID = compareTitles(
										childtag_text, ClasstemplateJson);
								if (!tagNameID.getTagName()
										.equalsIgnoreCase("")) {
									Element el = new Element(
											tagNameID.getTagType());
									el.attr("id", tagNameID.getTagName());
									el.text(chld.ownText());
									// The following was Arnab's mistake
									// chld.before(el);
									element.before(el);
									chld.remove();
									c10++;
								}

							}

						}
						// looking for other than b and p tags
						String parentTagText = element.text().trim();
						ParagraphObject po = new ParagraphObject();
						po = title_searchInParagraph(parentTagText,
								ClasstemplateJson);
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
							c1++;
							TagNameIdReturn tagNameID = compareTitles(
									parentTagText, ClasstemplateJson);
							if (!tagNameID.getTagName().equalsIgnoreCase("")) {
								element.attr("id", tagNameID.getTagName());
								element.tagName(tagNameID.getTagType());
								element.text(parentTagText);
								c2++;
							} else {
								element.tagName("p");
								element.text(parentTagText);
								c3++;
							}
						} else if (element.tagName().equalsIgnoreCase("p")) {
							if (parentTagText.split(" ").length > 0
									&& parentTagText.split(" ").length < 20) {
								c4++;
								TagNameIdReturn tagNameID1 = compareTitles(
										parentTagText, ClasstemplateJson);

								if (!tagNameID1.getTagName().equalsIgnoreCase(
										"")) {
									element.attr("id", tagNameID1.getTagName());
									element.tagName(tagNameID1.getTagType());
									element.text(parentTagText);
									c5++;
								} else {
									ParagraphObject po1 = new ParagraphObject();
									po1 = title_searchInParagraph(
											parentTagText, ClasstemplateJson);
									if (po1.getHtagName() != null
											&& !("").equalsIgnoreCase(po1
													.getHtagName())) {
										Element el = new Element(
												po1.getHtagName());
										el.text(po1.getTtagString());
										el.attr("id", po1.getHtagID());
										element.text(po1.getPtagString());
										element.before(el);
									} else {
										element.text(parentTagText);
									}
									c6++;
								}
							} else {
								ParagraphObject po1 = new ParagraphObject();
								po1 = title_searchInParagraph(parentTagText,
										ClasstemplateJson);
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
							continue;
						}
					}

				}

			}

			Element titleTag = new Element("h1");
			titleTag.text("SOW");
			titleTag.attr("id", "");

			doc.body().insertChildren(0, titleTag);			
			
			
			JSONObject docJsonObject = Html2Jsonobject(doc);
			
			System.out.println(docJsonObject);
			System.exit(0);
			
			/*
			double score = templateMatchFunction(docJsonObject,
					MP_TemplateJson, file.getName());*/
			
			double score = StaticScoreCalculator.templateMatchFunction(docJsonObject,
					MP_TemplateJson, file.getName());
			
			System.out.println("Score is :: " + score);			
			System.out.println(docJsonObject);
		}

	}

	private static Document RemoveStaticText(Document doc,
			JSONObject mPTemplateJSONObject) {
		// TODO Auto-generated method stub
		Elements elements = doc.select("h1,h2,h3,h4,h5");
		for (Element element : elements) {
			StringBuilder sb = new StringBuilder(element.toString());

			Element next = element.nextElementSibling();
			while (next != null && !next.tagName().startsWith("h")) {
				sb.append(next.toString()).append("\n");
				next = next.nextElementSibling();
			}
			//System.out.println(sb);
			break;
		}

		return doc;
	}

	public static JSONObject createJSONObject(JSONObject mPTemplateJSONObject) {
		// TODO Auto-generated method stub
		JSONArray answerUnit = (JSONArray) mPTemplateJSONObject
				.get("answer_units");
		JSONObject outputJSON = new JSONObject();
		int size = answerUnit.size();
		for (int i = 0; i < size; i++) {
			JSONObject eachObj = (JSONObject) answerUnit.get(i);
			JSONArray content = (JSONArray) eachObj.get("content");
			if ((((JSONObject) content.get(0)).get("text").toString() != null)
					&& (!("").equalsIgnoreCase(((JSONObject) content.get(0))
							.get("text").toString()))) {
				ArrayList arr = new ArrayList();
				int size1 = content.size();
				for (int j = 0; j < size1; j++) {
					arr.add(((JSONObject) content.get(j)).get("text"));
				}
				outputJSON.put(eachObj.get("CLASS").toString(), arr);
			}
		}
		return outputJSON;
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

	// if (element.tagName() != null && element.tagName().matches("ol|ul") ==
	// true){
	// Processing of OL tags
	public static Document processOLTag(Document doc) {
		// TODO Auto-generated method stub
		//Elements elements = doc.select("[ol],[ul]");
		//Elements elements = doc.getElementsByTag("ol,ul");
		//Elements elements = doc.select("ol").select("ul");
		Elements elements = doc.select("ol");
		for (Element element : elements) {
			if (element.tagName() != null) {
				Elements liElement = element.select("li");
				for (Element li : liElement) {
					String liText = li.text().trim();
					Element el = new Element("p");
					el.text(liText);
					element.before(el);
					element.remove();
				}
			}
		}	
		
		return doc;
	}

	// Processing the table tag
	private static Document extractTable(Document doc,
			JSONObject classtemplateJson) {
		// TODO Auto-generated method stub
		int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c41 = 0;
		Elements elements = doc.select("table");
		for (Element element : elements) {
			c1++;
			if (element.tagName() != null) {
				c2++;
				Elements rows = element.select("tr");

				for (Element tr : rows) {
					c3++;
					ArrayList<String> text_data = new ArrayList<String>();
					Elements tableData = tr.select("td");
					for (Element td : tableData) {
						c4++;
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

					c41 += 1;
					int countWords = tr_text.split(" ").length;
					if (countWords > 0 && countWords < 20) {
						c5++;
						TagNameIdReturn tagNameID = compareTitles(tr_text,
								classtemplateJson);

						if (!tagNameID.getTagType().toString()
								.equalsIgnoreCase("none")) {
							c6++;
							Element el = new Element("p");
							el.text(tr_text);
							element.before(el);
						}
					}

				}
			}

		}

		/*
		 * System.out.println("c1 = " + c1); System.out.println("c2 = " + c2);
		 * System.out.println("c3 = " + c3); System.out.println("c4 = " + c4);
		 * System.out.println("c5 = " + c5); System.out.println("c6 = " + c6);
		 */
		return doc;
	}

	
	// Processing the table tag
	public static Document extractTableForGeneral(Document doc,
			JSONObject classtemplateJson) {
		// TODO Auto-generated method stub		
		Elements elements = doc.select("table");
		for (Element element : elements) {						
			Elements rows = element.select("tr");
			for (Element tr : rows) {				
				ArrayList<String> text_data = new ArrayList<String>();
				Elements tableData = tr.select("td");
				for (Element td : tableData) {					
					String tdText = td.text().trim();
					tdText = tdText.replaceAll("^\"+", "").replaceAll("\"+$",
							"");
					tdText = tdText.replaceAll("[^a-zA-Z0-9_-]", " ");
					text_data.add(tdText);
				}				
				String tr_text = "";
				for (int i = 0; i < text_data.size(); i++) {
					tr_text = tr_text + text_data.get(i) + " ";
				}															
				Element el = new Element("p");
				el.text(tr_text);
				element.before(el);					
				}				
			}		
		return doc;
	}
	
	
	// Same method as above just the AppendixB taken care separately
	public static Document extractTableForGeneralWithABFixed(Document doc,
			JSONObject classtemplateJson) {
		// TODO Auto-generated method stub		
		Elements elements = doc.select("table");
		for (Element element : elements) {						
			Elements rows = element.select("tr");
			for (Element tr : rows) {				
				ArrayList<String> text_data = new ArrayList<String>();
				Elements tableData = tr.select("td");
				for (Element td : tableData) {					
					String tdText = td.text().trim();
					tdText = tdText.replaceAll("^\"+", "").replaceAll("\"+$",
							"");
					tdText = tdText.replaceAll("[^a-zA-Z0-9_-]", " ");
					text_data.add(tdText);
				}				
				String tr_text = "";
				for (int i = 0; i < text_data.size(); i++) {
					tr_text = tr_text + text_data.get(i) + " ";
				}															
				Element el = new Element("p");
				el.text(tr_text);
				element.before(el);					
				}				
			}		
		return doc;
	}
	
	

	private static void printResultJSON(String fileName,
			JSONObject docJsonObject) {
		// TODO Auto-generated method stub

		String filePath = "C:/Users/IBM_ADMIN/Documents/Phase2/Result/MP-V2/";
		try (FileWriter file = new FileWriter(filePath + fileName + ".json")) {

			file.write(docJsonObject.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JSONObject Html2Jsonobject(Document doc) {
		// TODO Auto-generated method stub
		JSONObject Html2Json = new JSONObject();
		Html2Json.put("source_document_id", "");
		Html2Json.put("media_type_detected", "");
		Html2Json.put("timestamp", "");
		Html2Json.put("warnings", "");
		Html2Json.put("metadata", "");

		JSONArray metadata = new JSONArray();
		JSONObject obj1 = new JSONObject();
		obj1.put("content", "text/html; charset=UTF-8");
		obj1.put("name", "Content-Type");

		JSONObject obj2 = new JSONObject();
		obj2.put("content", "Angela O'Connor");
		obj2.put("name", "author");

		metadata.add(obj1);
		metadata.add(obj2);
		Html2Json.replace("metadata", metadata);

		JSONArray newArray = new JSONArray();

		String find_next_tag = "p|li|h\\d+";

		int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, c8 = 0, c9 = 0;

		Elements elements = doc.select("*");
		for (Element element : elements) {
			c1++;
			ArrayList<String> secText = new ArrayList<String>();
			Element cTag = element;
			if (element.tagName().matches("(h[1-5])") == true) {
				
				// Fix for Appendix B
				if(element.attr("id").equalsIgnoreCase("AppendixB")){
					//secText = handleAppendix(element,secText);
					if(cTag.parent().tagName().matches("h[1-5]")){
						cTag = cTag.parent();
					}	
					removeNextH2Tag(element,doc);
				}
				
				while (true) {
					c2++;
					/*cTag = getNextElement(cTag.nextElementSibling(),
							find_next_tag);*/
					
					cTag =  getNextElementAyan(cTag.nextElementSibling(),
							find_next_tag);

					if (cTag == null) {
						c3++;
						break;
					}

					if (inHeaderTag(cTag.tagName())) {
						c4++;
						break;
					}

					String cText = cTag.text().trim();
					cText = cText.replaceAll("^\"+", "").replaceAll("\"+$", "");
					cText = cText.replaceAll(
							"[^a-zA-Z0-9_?.:;,\"'=()/\\#@$%&!]", " ");
					c5++;
					// if (cText.split("[a-zA-Z_]").length > 0) {
					if (countWords(cText) > 0) {
						c6++;
						// String[] splitSent = cText.split(patternSplitMulti);
						String[] splitSent = javaSplitMulti(cText);
						// String[] splitSent = cText.split(".\\s+");
						int size = splitSent.length;
						for (int i = 0; i < size; i++) {
							c7++;
							// System.out.println(c7 + ") " + splitSent[i]);
							// if (splitSent[i].split("[a-zA-Z_]").length > 0) {
							if (countWords(splitSent[i]) > 0) {
								c8++;
								String[] text_split_enum = splitSent[i]
										.split(patternSplitSentence);
								for (int j = 0; j < text_split_enum.length; j++) {
									c9++;
									secText.add(text_split_enum[j]);
								}
							}
						}
					}
				}
				JSONObject SecAnsUnit = CreateAnswerUnit(element, secText);
				newArray.add(SecAnsUnit);
			}
		}
		Html2Json.put("answer_units", newArray);
		return Html2Json;
	}

	public static String[] javaSplitMulti(String paragraph) {
		// TODO Auto-generated method stub
		Pattern re = Pattern
				.compile(
						"[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)",
						Pattern.MULTILINE | Pattern.COMMENTS);
		Matcher reMatcher = re.matcher(paragraph);
		List<String> allMatches = new ArrayList<String>();

		while (reMatcher.find()) {
			// System.out.println(reMatcher.group());
			allMatches.add(reMatcher.group());
		}

		int size = allMatches.size();
		String[] individualSentences = new String[size];
		for (int i = 0; i < size; i++) {
			individualSentences[i] = allMatches.get(i);
		}
		return individualSentences;
	}

	public static int countWords(String sentence) {
		int count = 0;
		Pattern regExWord = Pattern.compile(pattern_word);
		Matcher matcher = regExWord.matcher(sentence);
		while (matcher.find())
			count++;

		return count;
	}

	public static JSONObject CreateAnswerUnit(Element element,
			ArrayList<String> secText) {
		// TODO Auto-generated method stub
		JSONObject SecAnsUnit = new JSONObject();
		SecAnsUnit.put("direction", "ltr");
		SecAnsUnit.put("title", element.text().trim());
		SecAnsUnit.put("parent_id", "");
		SecAnsUnit.put("type", element.tagName());
		SecAnsUnit.put("id", "");
		String tagId;
		try {
			tagId = element.attr("id");
		} catch (Exception e) {
			tagId = "";
		}

		if (!("").equalsIgnoreCase(tagId))
			SecAnsUnit.put("CLASS", tagId);
		else
			SecAnsUnit.put("CLASS", "");

		int size = secText.size();
		JSONArray currentArray = new JSONArray();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				JSONObject temp = new JSONObject();
				temp.put("media_type", "text/plain");
				temp.put("text", secText.get(i));
				currentArray.add(temp);
			}
		} else {
			JSONObject temp = new JSONObject();
			temp.put("media_type", "text/plain");
			temp.put("text", "");
			currentArray.add(temp);
		}
		SecAnsUnit.put("content", currentArray);
		return SecAnsUnit;
	}

	public static Element getNextElement(Element child, String find_next_tag) {
		// TODO Auto-generated method stub

		if (child == null) {
			return null;
		}

		if (child.tagName().matches(find_next_tag)) {
			return child;
		}

		Elements children = child.children();
		for (Element e : children) {
			// System.out.println("Childre tag is :: "+ e.tagName());
			try {
				if (e.tagName().matches(find_next_tag)) {
					return e;
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		return null;
	}
	
	public static Element getNextElementAyan(Element child, String find_next_tag) {
		
		//System.out.println(child+"====================");
		if(child == null){
			return null;
		}
		
		if (child.tagName().matches(find_next_tag)) {
			return child;
		}
		
		Element brother = child.nextElementSibling();
		if (brother == null) {
			if (child.parent().nextElementSibling() != null)
				return child.parent().nextElementSibling();
			return null;
		}

		if (brother.tagName().matches(find_next_tag)) {
			return brother;
		}

		Elements children = brother.children();
		if (children.size() == 0) {
			return brother.parent().nextElementSibling();

		}

		for (Element e : children) {
			//System.out.println(":::::::: " + e.tagName());
			try {
				if (e.tagName().matches(find_next_tag)) {
					return e;
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		return null;
	}


	// Matching the template to calculate score
	public static double templateMatchFunction(JSONObject docJsonObject,
			JSONObject MP_TemplateJson, String fileName) {
		// TODO Auto-generated method stub
		JSONObject template_classes = new JSONObject();
		ArrayList<String> template_class_list = new ArrayList<String>();
		JSONObject static_text_template_classes = new JSONObject();
		ArrayList<String> static_text_template_class_list = new ArrayList<String>();

		JSONObject modifiedJson = new JSONObject();
		modifiedJson = (JSONObject) docJsonObject.clone();
		// modifiedJson = docJsonObject;

		JSONArray answerUnitTemplate = (JSONArray) MP_TemplateJson
				.get("answer_units");
		int size = answerUnitTemplate.size();

		int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, c8 = 0, c9 = 0, c10 = 0, c11 = 0, c12 = 0;

		for (int i = 0; i < size; i++) {
			JSONObject jObj = (JSONObject) answerUnitTemplate.get(i);
			String className = jObj.get("CLASS").toString();
			template_classes.put(className, 0);
			template_class_list.add(className);
			c1++;

			JSONArray contentTemplate = (JSONArray) jObj.get("content");
			JSONObject individualElement = (JSONObject) contentTemplate.get(0);
			String textElement = individualElement.get("text").toString();

			if (contentTemplate.size() > 0
					&& !("").equalsIgnoreCase(textElement)) {
				static_text_template_classes.put(className, 0);
				static_text_template_class_list.add(className);
				c2++;
			}
		}

		JSONObject template_content_classes = (JSONObject) template_classes
				.clone();

		JSONArray answerUnitResultDocument = (JSONArray) modifiedJson
				.get("answer_units");
		size = answerUnitResultDocument.size();
		for (int i = 0; i < size; i++) {
			c3++;
			JSONObject tempObj = (JSONObject) answerUnitResultDocument.get(i);
			String docClass = tempObj.get("CLASS").toString();

			JSONArray contentResultDocument = (JSONArray) tempObj
					.get("content");
			JSONObject firstElement = (JSONObject) contentResultDocument.get(0);
			String textOfFirstElement = firstElement.get("text").toString();

			// System.out.println("Bug 2");

			if (!("").equalsIgnoreCase(docClass)
					&& template_classes.containsKey(docClass)) {
				template_classes.put(docClass, 1);
				// SectionConfLevel.put("confidence_title", 1);
				c4++;
				if (contentResultDocument.size() > 0
						&& !("").equalsIgnoreCase(textOfFirstElement)) {
					IndexSentencePair isp = SectionstaticTextScore(docClass,
							contentResultDocument, MP_TemplateJson);

					tempObj.put("TotalStaticSentences",
							isp.getTotalStaticSentences());
					tempObj.put("MatcheIndex", isp.getMatchIndex());

					c5++;
					if (isp.getTotalStaticSentences() > 0) {
						float StaticTextMatchIndex = ((float) isp
								.getMatchIndex() / (float) isp
								.getTotalStaticSentences()) * 100;
						c6++;
						
						if (StaticTextMatchIndex > 100) {
							StaticTextMatchIndex = 100;
							c7++;
						}

						template_content_classes.put(docClass,
								(int) (StaticTextMatchIndex));
					} else {
						// SectionConfLevel.put("content_match_percentage",
						// 100);
						template_content_classes.put(docClass, 100);
						c8++;
					}
				} else {
					tempObj.put("TotalStaticSentences", 0);
					tempObj.put("MatcheIndex", 0);
					template_content_classes.put(docClass, 100);
					
				}				
			} else {
				c10++;
				// There are no text in that particular class of the document
				template_content_classes.put(docClass, 0);
				tempObj.put("TotalStaticSentences", 0);
				tempObj.put("MatcheIndex", 0);
				
			}

			if (Float.parseFloat(template_content_classes.get(docClass)
					.toString()) >= 90) {
				tempObj.put("Found", 1);
			} else {
				tempObj.put("Found", 0);
			}
		}

		// System.out.println("===============================================");

		float tempSum = 0;
		Iterator iterator = template_content_classes.keySet().iterator();
		String itrKey;
		while (iterator.hasNext()) {
			itrKey = iterator.next().toString();
			c11++;
			float matchPer = Float.parseFloat(template_content_classes.get(
					itrKey).toString());
			// System.out.println(itrKey+" \t "+matchPer);
			if (matchPer > 90) {
				tempSum++;
				c12++;
			}
			// System.out.println("Bug 5");
		}

		// System.out.println("===============================================");
		
		  System.out.println("tempSum = " + tempSum);
		  System.out.println("template_class_list = " +
		  template_class_list.size());
		 
		
		float TemplateMatchingScore = ((float) (tempSum) / ((float) (template_class_list
				.size()))) * 100;

		/*CSVUtils.writeCSV(fileName, template_class_list, template_classes,
				template_content_classes);*/
		
		//System.out.println(docJsonObject);
		return TemplateMatchingScore;
	}

	public static  boolean checkForSentenceInStaticTemplate(String docClass,
			JSONArray answerUnitTemplate) {
		// TODO Auto-generated method stub
		boolean flag = false;
		int size = answerUnitTemplate.size();
		for(int i = 0; i < size; i++){
			JSONObject temp = (JSONObject)answerUnitTemplate.get(i);
			if(temp.get("CLASS").toString().equalsIgnoreCase(docClass)){
				JSONArray arr = (JSONArray)temp.get("content");
				if(!((JSONObject)arr.get(0)).get("text").toString().equalsIgnoreCase("")){
					return true;
				}
				break;
			}
		}		
		return flag;
		
	}

	private static void temptesting(JSONObject MP_TemplateJson) {
		String docClass = "blah";
		JSONArray answerUnitTemplate = (JSONArray) MP_TemplateJson
				.get("answer_units");
		int size = answerUnitTemplate.size();
		// System.out.println("Size  = " + size);
		// System.exit(0);
		JSONArray staticTextSection = null;
		for (int i = 0; i < size; i++) {
			JSONObject jObj = (JSONObject) answerUnitTemplate.get(i);
			// System.out.println("jObj :: =========== \n " + jObj);
			try {
				if (jObj.get("CLASS").toString().equalsIgnoreCase(docClass)) {
					staticTextSection = (JSONArray) jObj.get("content");
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private static IndexSentencePair SectionstaticTextScore(String docClass,
			JSONArray contentResultDocument, JSONObject MP_TemplateJson) {
		// TODO Auto-generated method stub
		int TotalStaticSentences, MatchIndex = 0;
		float highestProbability = 0;

		JSONArray answerUnitTemplate = (JSONArray) MP_TemplateJson
				.get("answer_units");
		int size = answerUnitTemplate.size();

		JSONArray staticTextSection = null;

		for (int i = 0; i < size; i++) {
			JSONObject jObj = (JSONObject) answerUnitTemplate.get(i);
			// System.out.println("jObj :: =========== \n " + jObj);
			try {
				if (jObj.get("CLASS").toString().equalsIgnoreCase(docClass)) {
					staticTextSection = (JSONArray) jObj.get("content");
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		JSONObject individualElement = (JSONObject) staticTextSection.get(0);
		String textElement = individualElement.get("text").toString();
		if (staticTextSection.size() == 1 && ("").equalsIgnoreCase(textElement)) {
			TotalStaticSentences = 0;
		} else {
			TotalStaticSentences = staticTextSection.size();
		}

		size = contentResultDocument.size();
		for (int i = 0; i < size; i++) {
			JSONObject eachElement = (JSONObject) contentResultDocument.get(i);
			String secText = eachElement.get("text").toString();
			int newSize = staticTextSection.size();
			for (int j = 0; j < newSize; j++) {
				JSONObject tempObj = (JSONObject) staticTextSection.get(j);
				String tText = tempObj.get("text").toString();
				//float probMatch = FuzzySearch.partialRatio(secText, tText);
				float probMatch = FuzzySearch.tokenSetPartialRatio(secText, tText);
				//float probMatch = FuzzySearch.tokenSetRatio(secText, tText);
				if (probMatch > highestProbability) {
					highestProbability = probMatch;
				}
			}
			if (highestProbability >= 90) {
				MatchIndex = MatchIndex + 1;
			}
			eachElement.put("HProb", (highestProbability / 100));
			highestProbability = 0;
		}

		// System.out.println("TotalStaticSentences = " + TotalStaticSentences);
		// System.out.println("MatchIndex = " + MatchIndex);

		IndexSentencePair isp = new IndexSentencePair();
		isp.setMatchIndex(MatchIndex);
		isp.setTotalStaticSentences(TotalStaticSentences);
		return isp;
	}

	// Changed from private to public during legacy defect fix
	public static boolean inHeaderTag(String tagName) {
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
			//double probMatch = FuzzySearch.tokenSetRatio(title, stringToMatch);

			if (probMatch > 90) {
				//AppendixB Fix
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
				tempObj.setTagType(jObj.get("type").toString());
				tempObj.setTagName(jObj.get("class_name").toString());
				return tempObj;
			}

		}
		tempObj.setTagType(tagType);
		tempObj.setTagName(tagName);
		return tempObj;
	}

	// The following function will check whether a document is MP or MS
	private static String IsManagedProjectORManagedServiceSOW(
			JSONObject docJsonObject) {
		String sowClass = "";
		int countMP = 0, countMS = 0;
		JSONArray answerUnit = (JSONArray) docJsonObject.get("answer_units");
		int sizeOfAnsUnit = answerUnit.size();
		for (int i = 0; i < sizeOfAnsUnit; i++) {
			JSONObject tempObject = (JSONObject) answerUnit.get(i);
			if (tempObject.get("title").toString().equalsIgnoreCase("SOW")) {
				JSONArray content = (JSONArray) tempObject.get("content");
				int sizeOfContent = content.size();
				for (int j = 0; j < sizeOfContent; j++) {
					String contentString = ((JSONObject) content.get(j)).get(
							"text").toString();
					if (!("").equalsIgnoreCase(contentString)
							&& contentString != null) {
						countMP = countMP + filterMPSOW(contentString, "MP");
						countMS = countMS + filterMPSOW(contentString, "MS");
					}
				}
			}
		}

		if (countMP > countMS) {
			sowClass = "Managed Project";
		} else {
			sowClass = "Managed Service";
		}

		return sowClass;
	}
	
	

	/**
	 * The following function has been introduced in Legacy Fixing stage.
	 * It is specially written for Appendix B class.
	 * It will check if there is any header tag that exists after this class.
	 * If it is then it will remove that header tag completely and will proceed
	 * @param doc 
	 * */
	private static void removeNextH2Tag(Element element, Document doc) {
		// TODO Auto-generated method stub
		try {
			Element nextTag = element.nextElementSibling();
			if(nextTag.tagName().matches("(h[1-5])")){
				nextTag.remove();
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}

	private static int filterMPSOW(String contentString, String flag) {
		// TODO Auto-generated method stub
		String regMP = "\\bManaged\\sProject(s)?\\b";
		String regMS = "\\bManaged\\sService(s)?\\b";
		Pattern pattern = null;
		int count = 0;

		if (flag.equalsIgnoreCase("MP")) {
			Pattern.compile(regMP);
		} else {
			Pattern.compile(regMS);
		}

		Matcher matcher = pattern.matcher(contentString);
		while (matcher.find())
			count++;

		return count;
	}

}
