package com.ibm.contracts.advisor.driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.util.Util;
import com.ibm.contracts.advisor.vo.IndexSentencePair;

public class MissingText {

	public static double staticGeneralThreshold = 0.85, staticScoreThreshold = 0.9;

	public static void main(String[] ab) throws FileNotFoundException, IOException, ParseException{		
		
		System.out.println("======================================");
		File file = new File(
				"C:/Users/IBM_ADMIN/Documents/testnow/cisco-samples/MP/6240053615-great.docx");
		// System.out.println(file);

		JSONObject ClasstemplateJson = Util
				.getJSONFromFile("C:/CISCO-v2-MP/config/Template_MP_Classes.json");

		JSONObject MP_TemplateJson = Util
				.getJSONFromFile("C:/CISCO-v2-MP/config/MP_Static_Template_V5.json");

		PostNLCPropSet.templatefile = Util.getJSONObject(ClasstemplateJson
				.toJSONString());
		
		
		JSONObject MPTemplateJSONObject = docConvert_V4.createJSONObject(MP_TemplateJson);
		
		//System.out.println(MPTemplateJSONObject);
		//System.exit(0);
		
		JSONObject docJsonObject = Util.getJSONFromFile("C:/Users/IBM_ADMIN/Documents/Phase2/JSONOutput/201951972-good.json");
		
		//System.out.println(docJsonObject);
		
		double score = templateMatchFunction(docJsonObject,
				MP_TemplateJson, file.getName());	
		
		System.out.println(MP_TemplateJson);
		
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

			JSONArray answerUnitTemplate = (JSONArray) MP_TemplateJson
					.get("answer_units");
			int size = answerUnitTemplate.size();

			for (int i = 0; i < size; i++) {
				JSONObject jObj = (JSONObject) answerUnitTemplate.get(i);
				String className = jObj.get("CLASS").toString();
				template_classes.put(className, 0);
				template_class_list.add(className);

				JSONArray contentTemplate = (JSONArray) jObj.get("content");
				JSONObject individualElement = (JSONObject) contentTemplate.get(0);
				String textElement = individualElement.get("text").toString();

				if (contentTemplate.size() > 0
						&& !("").equalsIgnoreCase(textElement)) {
					static_text_template_classes.put(className, 0);
					static_text_template_class_list.add(className);

				}
			}

			JSONObject template_content_classes = (JSONObject) template_classes
					.clone();

			JSONArray answerUnitResultDocument = (JSONArray) modifiedJson
					.get("answer_units");
			size = answerUnitResultDocument.size();
			for (int i = 0; i < size; i++) {

				JSONObject tempObj = (JSONObject) answerUnitResultDocument.get(i);
				String docClass = tempObj.get("CLASS").toString();

				JSONArray contentResultDocument = (JSONArray) tempObj
						.get("content");
				JSONObject firstElement = (JSONObject) contentResultDocument.get(0);
				String textOfFirstElement = firstElement.get("text").toString();

				if (!("").equalsIgnoreCase(docClass)
						&& template_classes.containsKey(docClass)) {
					template_classes.put(docClass, 1);

					if (contentResultDocument.size() > 0
							&& !("").equalsIgnoreCase(textOfFirstElement)) {
						IndexSentencePair isp = SectionstaticTextScore(docClass,
								contentResultDocument, MP_TemplateJson);

						tempObj.put("TotalStaticSentences",
								isp.getTotalStaticSentences());
						tempObj.put("MatcheIndex", isp.getMatchIndex());

						if (isp.getTotalStaticSentences() > 0) {
							float StaticTextMatchIndex = ((float) isp
									.getMatchIndex() / (float) isp
									.getTotalStaticSentences()) * 100;

							/*System.out.println(docClass + "   MatchIndex = "
									+ isp.getMatchIndex()
									+ "    TotalStaticMatchScore = "
									+ isp.getTotalStaticSentences());*/
							if (StaticTextMatchIndex > 100) {
								StaticTextMatchIndex = 100;
							}
							
							if(previousMatchIndex(template_content_classes,docClass) < StaticTextMatchIndex){
								template_content_classes.put(docClass,
										(int) (StaticTextMatchIndex));
							}
							
						} else {
							// SectionConfLevel.put("content_match_percentage",
							// 100);						
							template_content_classes.put(docClass, 100);

						}
					} else {
						tempObj.put("TotalStaticSentences", 0);
						tempObj.put("MatcheIndex", 0);
						template_content_classes.put(docClass, 100);

					}

					
				} else {

					template_content_classes.put(docClass, 0);
					tempObj.put("TotalStaticSentences", 0);
					tempObj.put("MatcheIndex", 0);
					
				}
				
				if(docClass.equalsIgnoreCase("General")){
					if (Float.parseFloat(template_content_classes.get(docClass)
							.toString()) >= staticGeneralThreshold) {
						tempObj.put("Found", 1);
					} else {
						tempObj.put("Found", 0);
					}
				}else{
					if (Float.parseFloat(template_content_classes.get(docClass)
							.toString()) >= staticScoreThreshold) {
						tempObj.put("Found", 1);
					} else {
						tempObj.put("Found", 0);
					}
				}

				
			}

			float tempSum = 0;
			Iterator iterator = template_content_classes.keySet().iterator();
			String itrKey;
			while (iterator.hasNext()) {
				itrKey = iterator.next().toString();
				float matchPer = Float.parseFloat(template_content_classes.get(
						itrKey).toString());
				System.out.println(itrKey + " \t " + matchPer);
				if(itrKey.equalsIgnoreCase("General")){
					if (matchPer > staticGeneralThreshold) {
						tempSum++;
					}
				}else {
					if (matchPer > staticScoreThreshold) {
						tempSum++;
					}
				}
				
			}

			float TemplateMatchingScore = ((float) (tempSum) / ((float) (template_class_list
					.size()))) * 100;
			
			return TemplateMatchingScore;
		}
		
		
		private static float previousMatchIndex(
				JSONObject template_content_classes, String docClass) {
			// TODO Auto-generated method stub
			int count = 0;
			try {
				count = Integer.parseInt(template_content_classes.get(docClass).toString());
			}catch(Exception e){
				e.printStackTrace();
				count = 0;
			}		
			return count;
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
			
			int foundIndex = 0;
			float[] arr = new float[staticTextSection.size()];
			
			int newSize = staticTextSection.size();
			for (int j = 0; j < newSize; j++) {
				arr[j] = 0;
			}
			
			size = contentResultDocument.size();
			for (int i = 0; i < size; i++) {
				JSONObject eachElement = (JSONObject) contentResultDocument.get(i);
				String secText = eachElement.get("text").toString();
				//newSize = staticTextSection.size();
				for (int j = 0; j < newSize; j++) {
					JSONObject tempObj = (JSONObject) staticTextSection.get(j);
					String tText = tempObj.get("text").toString();
					//float probMatch = FuzzySearch.partialRatio(secText, tText);
					//float probMatch = FuzzySearch.tokenSetPartialRatio(secText, tText);
					float probMatch = FuzzySearch.tokenSetRatio(secText, tText);
					if (probMatch > highestProbability) {
						highestProbability = probMatch;
						foundIndex = j;
						arr[j] = probMatch;
					}
				}
				if (highestProbability >= 90) {
					MatchIndex = MatchIndex + 1;
					JSONObject tempObj = (JSONObject)staticTextSection.get(foundIndex);
					tempObj.put("Found", 1);
				}
				eachElement.put("HProb", (highestProbability / 100));
				highestProbability = 0;
			}
			
			//System.out.println("The static texts are :: \n " );
			//System.out.println(staticTextSection);
			
			//newSize = staticTextSection.size();
			System.out.println("For class "+docClass+"   missing text :: ");
			for (int j = 0; j < newSize; j++) {
				//System.out.println(arr[j]);	
				JSONObject  tempObj = (JSONObject) staticTextSection.get(j);
				if(arr[j] < staticGeneralThreshold){
					tempObj.put("Found", 0);
				//	System.out.println(tempObj);
				//	String tText = tempObj.get("text");
					System.out.println(arr[j] + "\t" +tempObj.get("text"));
				}else {
					tempObj.put("Found", 1);
				}
			}
			
			

		//	System.out.println("TotalStaticSentences = " + TotalStaticSentences);
		//	System.out.println("MatchIndex = " + MatchIndex);

			IndexSentencePair isp = new IndexSentencePair();
			isp.setMatchIndex(MatchIndex);
			isp.setTotalStaticSentences(TotalStaticSentences);
			return isp;
		}
		
		
		
}
