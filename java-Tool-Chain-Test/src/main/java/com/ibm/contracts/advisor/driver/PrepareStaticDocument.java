package com.ibm.contracts.advisor.driver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.constants.APICredentials;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.handler.DocumentConversionHTMLHandler;
import com.ibm.contracts.advisor.util.PostDocumentConversionHTMLProcessing;
import com.ibm.contracts.advisor.util.Util;
import com.ibm.contracts.advisor.vo.ConvertReturn;

public class PrepareStaticDocument {
	
	private static String[] headerTagList = { "h1", "h2", "h3", "h4", "h5",
	"h6" };
	
	public static String pattern_word = "[a-zA-Z_]+";
	
	public static String patternSplitSentence = "\\s[\\(]?(iv|v?i{0,3})[\\)]\\s|\\s([\\(]?[a-zA-Z][\\)])\\s";


	public static void main(String[] args) throws FileNotFoundException,
			IOException, ParseException {
		// TODO Auto-generated method stub
		String outputFilePath = "C:/Users/IBM_ADMIN/Documents/testnow/FieldGlass/Defect Document HTML/";
		String fileName = "5500008422MP.docx";
		File file = new File(
				"C:/Users/IBM_ADMIN/Documents/testnow/cisco-samples/MP/"
						+ fileName);
		// System.out.println(file);

		JSONObject ClasstemplateJson = Util
				.getJSONFromFile("C:/CISCO-v2-MP/config/Template_MP_Classes.json");

		JSONObject MP_TemplateJson = Util
				.getJSONFromFile("C:/CISCO-v2-MP/config/MP_Static_Template_V5.json");

		PostNLCPropSet.templatefile = Util.getJSONObject(ClasstemplateJson
				.toJSONString());

		APICredentials.CLASS_WORKSPACE_ID = APICredentials.MP_WORKSPACE_ID;
		
		
		boolean useOldFile = true;
		boolean writeHTML = false;
		boolean writeJSON = true;

		try {
			PostNLCPropSet.templateList = Util.populateTemplateTitle();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String docHTML = "";

		if (file != null) {

			/*------------------ The Beginning of creating static document -------------------	 */
			JSONObject staticJsonObject;
			Document staticDoc= null;
			if(!useOldFile){
				ConvertReturn answers = DocumentConversionHTMLHandler
						.convertFile(file);
				docHTML = answers.getHtml().toString();

				Document doc = Jsoup.parse(docHTML);

				/*staticDoc = docConvert_V3.extractTableForGeneral(doc,
						ClasstemplateJson);
				*/
				staticDoc = extractTableForGeneral(doc,
						ClasstemplateJson);

				staticDoc.select("table").remove();

				//staticDoc = docConvert_V3.processOLTag(staticDoc);
				staticDoc = processOLTag(staticDoc);

				staticDoc = PostDocumentConversionHTMLProcessing
						.ProcessHTMLAndAssignTagID(staticDoc, ClasstemplateJson);

				staticDoc = PostDocumentConversionHTMLProcessing
						.addTitle(staticDoc);
				
				staticJsonObject = Html2Jsonobject(staticDoc);
			}else {
				String absPath = outputFilePath + fileName + "_Converted.HTML";
				FileReader fr = new FileReader(absPath);
				BufferedReader br =  new BufferedReader(fr);
				String line;
				while((line = br.readLine()) != null){
					docHTML += line; 
				}
				br.close();
				fr.close();
				
				staticJsonObject = Html2Jsonobject(Jsoup.parse(docHTML));
			}
			
		

			/*if(writeHTML){
				docConvert_V4.WriteToFile(staticDoc, file.getName(), outputFilePath);
			}*/
			
			if(writeJSON){
				WriteJSONToFile(staticJsonObject, file.getName(), outputFilePath);
			}
			
			System.exit(0);

			/*---------- The End of creating static document ------ */
			
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
				if (element.attr("id").equalsIgnoreCase("AppendixB")) {
					if (cTag.parent().tagName().matches("h[1-5]")) {
						cTag = cTag.parent();
					}
					removeNextH2Tag(element,doc);
				}

				while (true) {
					c2++;
					/*cTag = docConvert_V3.getNextElementAyan(
							cTag.nextElementSibling(), find_next_tag);*/
					cTag = getNextElementAyan(
							cTag.nextElementSibling(), find_next_tag);

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
					if (countWords(cText) > 0) {
						c6++;
						/*String[] splitSent = docConvert_V3
								.javaSplitMulti(cText);*/
						String[] splitSent = javaSplitMulti(cText);
						int size = splitSent.length;
						for (int i = 0; i < size; i++) {
							c7++;
							/*if (docConvert_V3.countWords(splitSent[i]) > 0) {*/
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
				/*JSONObject SecAnsUnit = docConvert_V3.CreateAnswerUnit(element,
						secText);*/
				JSONObject SecAnsUnit = CreateAnswerUnit(element,
						secText);
				newArray.add(SecAnsUnit);
			}
		}
		Html2Json.put("answer_units", newArray);
		return Html2Json;
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
		Element nextTag = element.nextElementSibling();
		System.out.println("nextTag :: "+ nextTag);
		System.out.println("nextTag Name :: "+ nextTag.tagName());
		if(nextTag.tagName().matches("(h[1-5])")){
			nextTag.remove();
		}
		
	}
	
	
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

	
	public static void WriteJSONToFile(JSONObject doc, String fileName,
			String filePath) {
		// TODO Auto-generated method stub
		String absPath = filePath + fileName + "_Converted.JSON";
		File file = new File(absPath);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(doc.toString());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	
	//Changed from private to public during legacy defect fix
	public static boolean inHeaderTag(String tagName) {
		boolean flag = false;
		for (int i = 0; i < headerTagList.length; i++) {
			if (headerTagList[i].equalsIgnoreCase(tagName)) {
				return true;
			}
		}
		return flag;
	}
	
	public static int countWords(String sentence) {
		int count = 0;
		Pattern regExWord = Pattern.compile(pattern_word);
		Matcher matcher = regExWord.matcher(sentence);
		while (matcher.find())
			count++;

		return count;
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


}
