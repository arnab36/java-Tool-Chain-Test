package com.ibm.contracts.advisor.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONObject;

public class CSVUtils {

	private static final char DEFAULT_SEPARATOR = ',';

	public static void writeLine(Writer w, List<String> values)
			throws IOException {
		writeLine(w, values, DEFAULT_SEPARATOR, ' ');
	}

	public static void writeLine(Writer w, List<String> values, char separators)
			throws IOException {
		writeLine(w, values, separators, ' ');
	}

	// https://tools.ietf.org/html/rfc4180
	private static String followCVSformat(String value) {

		String result = value;
		if (result.contains("\"")) {
			result = result.replace("\"", "\"\"");
		}
		return result;

	}

	public static void writeLine(Writer w, List<String> values,
			char separators, char customQuote) throws IOException {

		boolean first = true;

		// default customQuote is empty

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			if (!first) {
				sb.append(separators);
			}
			if (customQuote == ' ') {
				sb.append(followCVSformat(value));
			} else {
				sb.append(customQuote).append(followCVSformat(value))
						.append(customQuote);
			}

			first = false;
		}
		sb.append("\n");
		w.append(sb.toString());

	}
	
	
	public static void writeCSV(String fileName, ArrayList<String> template_class_list, JSONObject template_classes, JSONObject template_content_classes)  {
		// TODO Auto-generated method stub
		String filePath = "C:/Users/IBM_ADMIN/Documents/Phase2/Result/MP-V2/";
		String csvFile = filePath+fileName+".csv";
	    FileWriter writer = null;
		try {
			writer = new FileWriter(csvFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    try {
			CSVUtils.writeLine(writer, Arrays.asList("template_class", "title_match", "content_match"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	    
	    for(int i =0; i < template_class_list.size(); i++){
	    	List<String> list = new ArrayList<>();
	    	String className = template_class_list.get(i);
	    	 list.add(className);
	    	 list.add(template_classes.get(className).toString());
	    	 list.add(template_content_classes.get(className).toString());
	    	 System.out.println(list);
	    	 try {
				CSVUtils.writeLine(writer, list);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	  	    
	    try {
			//writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	}

  
}



