package com.ibm.contracts.advisor.driver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

import com.ibm.contracts.advisor.FieldGlass.STATIC.FieldGlassStatic;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.handler.DocumentConversionHTMLHandler;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;
import com.ibm.contracts.advisor.util.CSVUtils;
import com.ibm.contracts.advisor.util.Util;
import com.ibm.contracts.advisor.vo.ConvertReturn;
import com.ibm.contracts.advisor.vo.IndexSentencePair;
import com.ibm.contracts.advisor.vo.TagNameIdReturn;
import com.ibm.watson.developer_cloud.document_conversion.v1.model.Answers;

public class docConvert {

	private static String removeRegex = "((?=\\S)[\\*]+(?<=\\S))|((?=\\S)[\\(]?[\\d]+[\\)]?(?<=\\S))|((?=\\S)[\\(]?[A-Za-z][\\)](?<=\\S))";
	private static String[] headerTagList = { "h1", "h2", "h3", "h4", "h5",
			"h6" };

	// public static String patternSplitSentence =
	// "(((\\s+\\(?)([Ii][Xx]|[Ii][Vv]|[Vv]?[Ii]{1,3})[.\\)](\\s+))|([.;:])(\\s+)|((\r|\n|\\s){2,})|((\\s+\\()[a-zA-Z0-9][\\).](\\s+)(?=[A-Z]))|((\\s+)[a-zA-Z0-9][\\).](\\s+)))";
	public static String patternSplitSentence = "([.;\\?])(\\s+)|((\r|\n|\\s){2,})";

	public static void main(String[] args) throws FileNotFoundException,
			IOException, InterruptedException, ExecutionException,
			ParseException {
		String str = "All Work Product prepared by Supplier will be produced using Cisco standard desktop, Oracle Development, and Training tools (i.e., MS Word, MS Excel, MS PowerPoint, MS Visio, Standard Survey, and Tools, etc.). All Work Product prepared by Supplier will be subject to Cisco's acceptance in accordance with Section 3 of this SOW in order to determine whether they comply in all material respects with the specifications agreed upon by the parties in this SOW";
		
		String secondRegex = "(?<=[a-z]|[A-Z])\\.\\s+|:\\s+|\\.\\s+";
		
		String[] splittedText = str
				.split(FieldGlassStatic.patternSplitSentence);
		
		/*
		String[] splittedText = new String[100];
		int count1 = 0;
		Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$|:\\s+)", Pattern.MULTILINE | Pattern.COMMENTS);
		Matcher reMatcher = re.matcher(str);
		while (reMatcher.find()) {
			
		    splittedText[count1] = reMatcher.group();
		    count1++;   
		}*/
		
				
		for(int i =0; i < splittedText.length; i++){
			System.out.println("splittedText :: " + splittedText[i]);
			String[] furtherSplit = splittedText[i].split(secondRegex);
			for(int j =0; j < furtherSplit.length; j++){
				System.out.println("furtherSplit :: "+furtherSplit[j]);
			}
		}
		System.exit(0);
		
		File file = new File(
				"C:/Users/IBM_ADMIN/Documents/testnow/cisco-samples/201951972-good.pdf");
		System.out.println(file);

		JSONObject ClasstemplateJson = Util
				.getJSONFromFile("C:/CISCO-v2-MP/config/Template_MP_Classes.json");

		JSONObject MP_TemplateJson = Util
				.getJSONFromFile("C:/CISCO-v2-MP/config/MP_Static_Template_V5.json");

		String docHTML = null;

		if (file != null) {
			// Document conversion
			ConvertReturn answers = DocumentConversionHTMLHandler
					.convertFile(file);
			// file = null;

			docHTML = answers.getHtml().toString();
			Document doc = Jsoup.parse(docHTML);
			doc.select("table").remove();

			// System.out.println(doc);
			// System.exit(0);

			int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, c8 = 0, c9 = 0, c10 = 0;

			Elements elements = doc.select("*");
			// Elements elements = doc
			int count = 0, totalCount = 0;

			for (Element element : elements) {

				if (element.tagName().matches("(p|h[1-5])") == true) {
					Elements children = element.children();
					int numberOfChld = children.size();

					count = 0;
					for (Element chld1 : children) {
						count++;
					}

					/*
					 * if(element.tagName().equalsIgnoreCase("h4")){
					 * System.out.println("Founf h4");
					 * System.out.println(element.tagName());
					 * System.out.println(element.text()); }
					 */

					System.out.println(totalCount + ") numberOfChld = "
							+ numberOfChld + "\t" + count);
					System.out.println(element);
					totalCount++;

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
									chld.before(el);
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
									chld.before(el);
									chld.remove();
									c10++;
								}

							}

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
								/*
								 * element.ownText().replaceAll(".*",
								 * parentTagText);
								 */
								element.text(parentTagText);
								c2++;
							} else {
								element.tagName("p");
								/*
								 * element.ownText().replaceAll(".*",
								 * parentTagText);
								 */
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
									/*
									 * element.ownText().replaceAll(".*",
									 * parentTagText);
									 */
									element.text(parentTagText);
									c5++;
								} else {
									/*
									 * element.ownText().replaceAll(".*",
									 * parentTagText);
									 */
									element.text(parentTagText);
									c6++;
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

			// System.out.println(doc);

			JSONObject docJsonObject = Html2Jsonobject(doc);

			printResultJSON(file.getName(), docJsonObject);

			// System.out.println(docJsonObject);
			// System.exit(0);

			double score = templateMatchFunction(docJsonObject,
					MP_TemplateJson, file.getName());
			System.out.println("Score is :: " + score);

		}

	}

	private static void printResultJSON(String fileName,
			JSONObject docJsonObject) {
		// TODO Auto-generated method stub

		String filePath = "C:/Users/IBM_ADMIN/Documents/Phase2/Result/MS/";
		try (FileWriter file = new FileWriter(filePath + fileName + ".json")) {

			file.write(docJsonObject.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static JSONObject Html2Jsonobject(Document doc) {
		// TODO Auto-generated method stub
		JSONObject Html2Json = new JSONObject();
		Html2Json.put("source_document_id", "");
		Html2Json.put("media_type_detected", "");
		Html2Json.put("timestamp", "");
		Html2Json.put("warnings", "");
		// Html2Json.put("answer_units", "");
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
		Html2Json.put("answer_units", newArray);

		String find_next_tag = "p|li|h\\d+";

		int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, count = 1;

		Elements elements = doc.select("*");
		for (Element element : elements) {
			if (element.tagName().matches("(h[1-5])") == true) {

				c1++;

				ArrayList<String> secText = new ArrayList<String>();
				Element cTag = element;

				// System.out.println("ctag :: " + cTag);

				while (true) {
					c2++;
					cTag = getNextElement(cTag.nextElementSibling(),
							find_next_tag);

					c5++;
					if (cTag == null)
						break;

					c6++;
					if (inHeaderTag(cTag.tagName()))
						break;

					c7++;
					String cText = cTag.text().trim();
					// System.out.println(count +
					// ") "+"L = "+cText.length()+"  " + cText);
					count++;
					if (cText.split("[a-zA-Z_]").length > 1) {
						c3++;
						int count1 = 0;
						String[] splitSent = cText.split(patternSplitSentence);
						// String[] splitSent = cText.split(".");
						for (int i = 0; i < splitSent.length; i++) {
							c4++;
							secText.add(splitSent[i]);
							// System.out.println(count1 +
							// ") "+"L = "+splitSent[i].length()+"  " +
							// splitSent[i]);
							count1++;
						}
					}

				}

				JSONObject SecAnsUnit = CreateAnswerUnit(element, secText);
				JSONArray currentArray = (JSONArray) Html2Json
						.get("answer_units");
				currentArray.add(SecAnsUnit);
				Html2Json.replace("answer_units", currentArray);
			}
		}

		System.out.println("c1 = " + c1);
		System.out.println("c2 = " + c2);
		System.out.println("c3 = " + c3);
		System.out.println("c4 = " + c4);
		System.out.println("c5 = " + c5);
		System.out.println("c6 = " + c6);
		System.out.println("c7 = " + c7);
		return Html2Json;
	}

	private static JSONObject CreateAnswerUnit(Element element,
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

	private static Element getNextElement(Element child, String find_next_tag) {
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

	// Matching the template to calculate score
	private static double templateMatchFunction(JSONObject docJsonObject,
			JSONObject MP_TemplateJson, String fileName) {
		// TODO Auto-generated method stub
		JSONObject template_classes = new JSONObject();
		ArrayList<String> template_class_list = new ArrayList<String>();
		JSONObject static_text_template_classes = new JSONObject();
		ArrayList<String> static_text_template_class_list = new ArrayList<String>();

		JSONObject modifiedJson = new JSONObject();
		modifiedJson = (JSONObject) docJsonObject.clone();

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

			// System.out.println("Bug 1");

			if (contentTemplate.size() > 0
					&& !("").equalsIgnoreCase(textElement)) {
				static_text_template_classes.put(className, 0);
				static_text_template_class_list.add(className);
				c2++;
			}
		}

		JSONObject template_content_classes = (JSONObject) template_classes
				.clone();

		JSONObject SectionConfLevel = new JSONObject();
		SectionConfLevel.put("confidence_title", 0);
		SectionConfLevel.put("content_match_percentage", 0);

		JSONArray answerUnitResultDocument = (JSONArray) modifiedJson
				.get("answer_units");
		size = answerUnitResultDocument.size();
		for (int i = 0; i < size; i++) {
			c3++;
			JSONObject tempObj = (JSONObject) answerUnitResultDocument.get(i);
			String docClass = tempObj.get("CLASS").toString();

			JSONArray contentResultDocument = (JSONArray) tempObj
					.get("content");
			JSONObject individualElement = (JSONObject) contentResultDocument
					.get(0);
			String textElement = individualElement.get("text").toString();

			// System.out.println("Bug 2");

			if (!("").equalsIgnoreCase(docClass)) {
				template_classes.put(docClass, 1);
				SectionConfLevel.put("confidence_title", 1);
				c4++;
				if (contentResultDocument.size() > 0
						&& !("").equalsIgnoreCase(textElement)) {
					IndexSentencePair isp = SectionstaticTextScore(docClass,
							contentResultDocument, MP_TemplateJson);
					c5++;
					if (isp.getTotalStaticSentences() > 0) {
						float StaticTextMatchIndex = ((float) isp
								.getMatchIndex() / (float) isp
								.getTotalStaticSentences()) * 100;
						c6++;
						System.out.println(docClass + "   MatchIndex = "
								+ isp.getMatchIndex()
								+ "    TotalStaticMatchScore = "
								+ isp.getTotalStaticSentences());
						if (StaticTextMatchIndex > 100) {
							StaticTextMatchIndex = 100;
							c7++;
						}
						SectionConfLevel.put("content_match_percentage",
								StaticTextMatchIndex);
						template_content_classes.put(docClass,
								(int) (StaticTextMatchIndex));
					} else {
						SectionConfLevel.put("content_match_percentage", 100);
						template_content_classes.put(docClass, 100);
						c8++;
					}
				} else {
					SectionConfLevel.put("content_match_percentage", 100);
					template_content_classes.put(docClass, 100);
					c9++;
				}

				Iterator iterator = tempObj.keySet().iterator();
				String itrKey;
				while (iterator.hasNext()) {
					itrKey = iterator.next().toString();
					tempObj.replace(itrKey, SectionConfLevel.get(itrKey));
					// System.out.println("Bug 3");
				}

			} else {
				c10++;
				SectionConfLevel.put("content_match_percentage", 0);
				template_content_classes.put(docClass, 0);
				Iterator iterator = tempObj.keySet().iterator();
				String itrKey;
				while (iterator.hasNext()) {
					itrKey = iterator.next().toString();
					tempObj.replace(itrKey, SectionConfLevel.get(itrKey));
					// System.out.println("Bug 4");
				}
			}
		}

		float tempSum = 0;
		Iterator iterator = template_content_classes.keySet().iterator();
		String itrKey;
		while (iterator.hasNext()) {
			itrKey = iterator.next().toString();
			c11++;
			float matchPer = Float.parseFloat(template_content_classes.get(
					itrKey).toString());
			if (matchPer > 90) {
				tempSum++;
				c12++;
			}
			// System.out.println("Bug 5");
		}

		System.out.println("tempSum = " + tempSum);
		System.out.println("template_class_list = "
				+ template_class_list.size());
		System.out.println("c1 = " + c1);
		System.out.println("c2 = " + c2);
		System.out.println("c3 = " + c3);
		System.out.println("c4 = " + c4);
		System.out.println("c5 = " + c5);
		System.out.println("c6 = " + c6);
		System.out.println("c7 = " + c7);
		System.out.println("c8 = " + c8);
		System.out.println("c9 = " + c9);
		System.out.println("c10 = " + c10);
		System.out.println("c11 = " + c11);
		System.out.println("c12 = " + c12);

		float TemplateMatchingScore = ((float) (tempSum) / ((float) (template_class_list
				.size()))) * 100;

		CSVUtils.writeCSV(fileName, template_class_list, template_classes,
				template_content_classes);
		return TemplateMatchingScore;
	}

	private static void temptesting(JSONObject MP_TemplateJson) {
		String docClass = "blah";
		JSONArray answerUnitTemplate = (JSONArray) MP_TemplateJson
				.get("answer_units");
		int size = answerUnitTemplate.size();
		System.out.println("Size  = " + size);
		// System.exit(0);
		JSONArray staticTextSection = null;
		for (int i = 0; i < size; i++) {
			JSONObject jObj = (JSONObject) answerUnitTemplate.get(i);
			System.out.println("jObj :: =========== \n " + jObj);
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
				// float probMatch = FuzzySearch.ratio(secText,tText);
				float probMatch = FuzzySearch.partialRatio(secText, tText);
				if (probMatch > highestProbability) {
					highestProbability = probMatch;
				}
			}
			if (highestProbability > 90) {
				MatchIndex = MatchIndex + 1;
			}
			highestProbability = 0;
		}

		System.out.println("TotalStaticSentences = " + TotalStaticSentences);
		System.out.println("MatchIndex = " + MatchIndex);

		IndexSentencePair isp = new IndexSentencePair();
		isp.setMatchIndex(MatchIndex);
		isp.setTotalStaticSentences(TotalStaticSentences);
		return isp;
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

		for (int i = 0; i < size; i++) {
			JSONObject jObj = (JSONObject) temp.get(i);
			String title = jObj.get("title").toString();
			String stringToMatch = childtag_text.replaceAll(removeRegex, "");

			double probMatch = FuzzySearch.ratio(title, stringToMatch);

			if (probMatch > 90) {
				tempObj.setTagType(jObj.get("type").toString());
				tempObj.setTagName(jObj.get("class_name").toString());
				return tempObj;
			}

		}
		tempObj.setTagType(tagType);
		tempObj.setTagName(tagName);
		return tempObj;
	}

	private static void handleObjectStore(Answers answers, File file) {
		ObjectStoreHandler.objectStore(new ByteArrayInputStream(answers
				.toString().getBytes()), Util.replacingFileToJSONExtension(file
				.getName()), Constants.PROCESSING_CONTAINER);
	}

	private static void handleObjectStore(String string, File file) {
		ObjectStoreHandler.objectStore(
				new ByteArrayInputStream(string.getBytes()),
				Util.replacingFileToJSONExtension(file.getName()),
				Constants.PROCESSING_CONTAINER);
	}

}
