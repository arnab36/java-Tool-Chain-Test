package com.ibm.contracts.advisor.FieldGlass.DYNAMIC;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


	/**
	 * 
	 * it is still under development.
	 * As payment method is itself a big class with four separate conditions so I have written this in a separate 
	 * class.
	 * 
	 * */
	
	public class FGPaymentMethod {
	
		
		public static void main(String[] ab) {

			long startTime = System.nanoTime();

			String filePath = "C:/Users/IBM_ADMIN/Documents/testnow/FieldGlass/FieldGlassHTML/";
			String fileName = "CSCOTQ00104609.pdf_Converted.html"; 
			
			String documentClass = "";
			File file = new File(filePath + fileName);
			
			Document doc = null;
			try {
				doc = Jsoup.parse(file, "UTF-8");
				// System.out.println(doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String content = doc.toString();
			
			// Calling the actual function
			try {
						
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("documentClass :: "+ documentClass);
			
		}
		
	}
