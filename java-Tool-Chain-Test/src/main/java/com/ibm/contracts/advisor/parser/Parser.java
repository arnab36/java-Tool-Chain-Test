/**
 * 
 */
package com.ibm.contracts.advisor.parser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Atrijit
 * 
 */
public class Parser {

	/**
	 * 
	 */
	public Parser() {
		// TODO Auto-generated constructor stub
	}

	public List parseString(String jsonstr) throws Exception {
		List<String> textList = null;
		try {
			textList = this.parseFileString(jsonstr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return textList;
	}

	private static List<String> parseFileString(String fileReader) {
		// TODO Auto-generated method stub

		List<String> textList = new ArrayList();

		int count = 0;

		JSONParser parser = new JSONParser();

		try {
			JSONObject jsonRoot = (JSONObject) parser.parse(fileReader);

			JSONArray answerUnits = (JSONArray) jsonRoot.get("answer_units");

			for (Object o : answerUnits) {

				JSONArray contentArray = (JSONArray) ((JSONObject) o)
						.get("content");

				for (Object c : contentArray) {

					count++;
					// System.out.println(((JSONObject) c).get("text"));
					String temp = (String) (((JSONObject) c).get("text"));
					temp = temp.trim();
					// if(temp!=null && !temp.equalsIgnoreCase("")){
					textList.add(temp);
					// }

				}

			}

			// System.out.println("Count:" + count);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return textList;
	}

	private static List<String> parseFile(FileReader fileReader) {
		// TODO Auto-generated method stub

		List<String> textList = new ArrayList();

		int count = 0;

		JSONParser parser = new JSONParser();

		try {
			JSONObject jsonRoot = (JSONObject) parser.parse(fileReader);

			JSONArray answerUnits = (JSONArray) jsonRoot.get("answer_units");

			for (Object o : answerUnits) {

				JSONArray contentArray = (JSONArray) ((JSONObject) o)
						.get("content");

				for (Object c : contentArray) {

					count++;
					// System.out.println(((JSONObject) c).get("text"));
					String temp = (String) (((JSONObject) c).get("text"));
					temp = temp.trim();
					if (temp != null && !temp.equalsIgnoreCase("")) {
						textList.add(temp);
					}

				}

			}

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return textList;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String abc = "fsdfsk dgfdg df glgfhgh gj.fghgf gfhf,fh fghgfh";
		abc.replaceAll("", "Hi");
		System.out.println(abc);
	}

}
