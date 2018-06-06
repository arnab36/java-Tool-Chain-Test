package com.ibm.contracts.advisor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.driver.docConvert_V3;

public class HTML2JSONObject implements Constants {

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
		
		System.out.println(doc);

		String find_next_tag = "p|li|h\\d+";
		Elements elements = doc.select("*");
		for (Element element : elements) {

			ArrayList<String> secText = new ArrayList<String>();
			System.out.println("Sec text:: "+secText);
			Element cTag = element;
			if (element.tagName().matches("(h[1-5])") == true) {
				while (true) {

					cTag = getNextElement(cTag.nextElementSibling(), find_next_tag);					
				//	cTag = docConvert_V3.getNextElement(cTag, find_next_tag);
				//	cTag = docConvert_V3.getNextElementAyan(cTag, find_next_tag);

					if (cTag == null) {
						break;
					}

					if (inHeaderTag(cTag.tagName())) {
						break;
					}

					String cText = cTag.text().trim();
					cText = cText.replaceAll("^\"+", "").replaceAll("\"+$", "");
					cText = cText.replaceAll(
							"[^a-zA-Z0-9_?.:;,\"'=()/\\#@$%&!]", " ");

					// if (cText.split("[a-zA-Z_]").length > 0) {
					if (countWords(cText) > 0) {
						String[] splitSent = javaSplitMulti(cText);
						int size = splitSent.length;
						for (int i = 0; i < size; i++) {
							if (countWords(splitSent[i]) > 0) {
								String[] text_split_enum = splitSent[i]
										.split(patternSplitSentence);
								for (int j = 0; j < text_split_enum.length; j++) {
									secText.add(text_split_enum[j]);
								}
							}
						}
					}
				}
				System.out.println("secText in Html2Jsonobject" + secText);
				JSONObject SecAnsUnit = CreateAnswerUnit(element, secText);
				newArray.add(SecAnsUnit);
			}

		}

		Html2Json.put("answer_units", newArray);
		return Html2Json;
	}

	private static int countWords(String sentence) {
		int count = 0;
		String pattern_word = "[a-zA-Z_]+";
		Pattern regExWord = Pattern.compile(pattern_word);
		Matcher matcher = regExWord.matcher(sentence);
		while (matcher.find())
			count++;

		return count;
	}

	private static String[] javaSplitMulti(String paragraph) {
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

	private static Element getNextElementWOWrittenReportFix(Element child,
			String find_next_tag) {
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

	private static Element getNextElement(Element child, String find_next_tag) {
		
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
			System.out.println(":::::::: " + e.tagName());
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
		
		System.out.println(element);
		System.out.println(secText);

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

	private static boolean inHeaderTag(String tagName) {
		boolean flag = false;
		for (int i = 0; i < headerTagList.length; i++) {
			if (headerTagList[i].equalsIgnoreCase(tagName)) {
				return true;
			}
		}
		return flag;
	}

}
