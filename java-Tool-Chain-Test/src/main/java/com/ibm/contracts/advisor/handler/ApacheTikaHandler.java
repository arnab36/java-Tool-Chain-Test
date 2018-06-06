package com.ibm.contracts.advisor.handler;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.HtmlMapper;
import org.apache.tika.parser.html.IdentityHtmlMapper;
import org.apache.tika.sax.ExpandedTitleContentHandler;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.io.Files;
import com.ibm.contracts.advisor.vo.DocumentConversionObject;



public class ApacheTikaHandler {

	
	/*
	 * Input: File pointer Output: Json conversion Description: converts document
	 * (doc/docx/pdf formated) into json format.
	 *  Retsurns String
	 */
	/*public static String docConversion(File inputFile)
			throws Exception {

			Document doc = null;
			JSONObject result = new JSONObject();
		
			byte[] file = Files.toByteArray(inputFile);
			// Auto parser for pdf, doc, docx, ..
			Parser parser = new AutoDetectParser();
			ParseContext context = new ParseContext();
			context.set(HtmlMapper.class, new IdentityHtmlMapper());

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			TransformerHandler handler = factory.newTransformerHandler();
			handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "html");
			handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "no");
			handler.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			handler.setResult(new StreamResult(output));
			ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);
			parser.parse(new ByteArrayInputStream(file), handler1, new Metadata(), context);

			// html -> String
			String docHtmlStr = new String(output.toByteArray(), "UTF-8");
			output.close();
			// JSoup object
			doc = Jsoup.parse(docHtmlStr, "UTF-8");	

			return doc.toString(); // returns json object
	}*/
	
	
	
	 public static String docConversion(File inputFile)
			    throws Exception
			  {
			    Document doc = null;
			    JSONObject result = new JSONObject();
			    
			    byte[] file = Files.toByteArray(inputFile);
			    
			    Parser parser = new AutoDetectParser();
			    //AutoDetectParser parser = new AutoDetectParser();
			    ParseContext context = new ParseContext();
			    context.set(HtmlMapper.class, new IdentityHtmlMapper());
			    
			    ByteArrayOutputStream output = new ByteArrayOutputStream();
			    SAXTransformerFactory factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
			    TransformerHandler handler = factory.newTransformerHandler();
			    handler.getTransformer().setOutputProperty("method", "html");
			    handler.getTransformer().setOutputProperty("indent", "no");
			    handler.getTransformer().setOutputProperty("encoding", "UTF-8");
			    handler.setResult(new StreamResult(output));
			    ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);
			    parser.parse(new ByteArrayInputStream(file), handler1, new Metadata(), context);
			    

			    String docHtmlStr = new String(output.toByteArray(), "UTF-8");
			    output.close();
			    
			    doc = Jsoup.parse(docHtmlStr, "UTF-8");
			    
			    return doc.toString();
			  }
	 
	 
	 
			  
	
	
	/*
	 * Input: File pointer Output: Json conversion Description: converts document
	 * (doc/docx/pdf formated) into json format.
	 *  Returns document
	 */
	/*public static Document docConversionDocument(File inputFile)
			throws Exception {

			Document doc = null;
			JSONObject result = new JSONObject();
		
			byte[] file = Files.toByteArray(inputFile);
			// Auto parser for pdf, doc, docx, ..
			Parser parser = new AutoDetectParser();
			ParseContext context = new ParseContext();
			context.set(HtmlMapper.class, new IdentityHtmlMapper());

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			TransformerHandler handler = factory.newTransformerHandler();
			handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "html");
			handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "no");
			handler.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			handler.setResult(new StreamResult(output));
			ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);
			parser.parse(new ByteArrayInputStream(file), handler1, new Metadata(), context);

			// html -> String
			String docHtmlStr = new String(output.toByteArray(), "UTF-8");
			output.close();
			// JSoup object
			doc = Jsoup.parse(docHtmlStr, "UTF-8");	
			return doc;
	}
	*/
	
	public static Document docConversionDocument(File inputFile)
		    throws Exception
		  {
		    Document doc = null;
		    JSONObject result = new JSONObject();
		    
		    byte[] file = Files.toByteArray(inputFile);
		    
		    Parser parser = new AutoDetectParser();
		    //AutoDetectParser parser = new AutoDetectParser();
		    ParseContext context = new ParseContext();
		    context.set(HtmlMapper.class, new IdentityHtmlMapper());
		    
		    ByteArrayOutputStream output = new ByteArrayOutputStream();
		    SAXTransformerFactory factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
		    TransformerHandler handler = factory.newTransformerHandler();
		    handler.getTransformer().setOutputProperty("method", "html");
		    handler.getTransformer().setOutputProperty("indent", "no");
		    handler.getTransformer().setOutputProperty("encoding", "UTF-8");
		    handler.setResult(new StreamResult(output));
		    ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);
		    parser.parse(new ByteArrayInputStream(file), handler1, new Metadata(), context);
		    

		    String docHtmlStr = new String(output.toByteArray(), "UTF-8");
		    output.close();
		    
		    doc = Jsoup.parse(docHtmlStr, "UTF-8");
		    return doc;
		  }
	
	
		public static Document newApacheTikaDocumentConversion(File inputFile) throws Exception {
			Document doc = null;
		    JSONObject result = new JSONObject();		   
		    
		    ParseContext context = new ParseContext();
		    context.set(HtmlMapper.class, new IdentityHtmlMapper());
		    Parser parser = new AutoDetectParser();
		  //  AutoDetectParser parser = new AutoDetectParser();
		    Metadata metadata = new Metadata();		    
		    ByteArrayOutputStream output = new ByteArrayOutputStream();
		    SAXTransformerFactory factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
		    TransformerHandler handler = factory.newTransformerHandler();
		    handler.getTransformer().setOutputProperty("method", "html");
		    handler.getTransformer().setOutputProperty("indent", "no");
		    handler.getTransformer().setOutputProperty("encoding", "UTF-8");
		    handler.setResult(new StreamResult(output));
		    //ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);
		       
		    
		    InputStream is = Files.asByteSource(inputFile).openStream();
		   // InputStream is = FileUtils.openInputStream(inputFile);
		    parser.parse(is, handler, metadata,context);		    
	
		    String docHtmlStr = new String(output.toByteArray(), "UTF-8");
		    output.close();
		    
		    doc = Jsoup.parse(docHtmlStr, "UTF-8");
		    
		    return doc;
		}
		
		
		public static String newApacheTikaDocumentConversionString(File inputFile) throws Exception {
			Document doc = null;
		    JSONObject result = new JSONObject();
		    		    
		    ParseContext context = new ParseContext();
		    context.set(HtmlMapper.class, new IdentityHtmlMapper());
		    Parser parser = new AutoDetectParser();
		   // AutoDetectParser parser = new AutoDetectParser();
		    Metadata metadata = new Metadata();		    
		    ByteArrayOutputStream output = new ByteArrayOutputStream();
		    SAXTransformerFactory factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
		    TransformerHandler handler = factory.newTransformerHandler();
		    handler.getTransformer().setOutputProperty("method", "html");
		    handler.getTransformer().setOutputProperty("indent", "no");
		    handler.getTransformer().setOutputProperty("encoding", "UTF-8");
		    handler.setResult(new StreamResult(output));
		    //ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);
		       
		    
		    InputStream is = Files.asByteSource(inputFile).openStream();
		   // InputStream is = FileUtils.openInputStream(inputFile);
		    parser.parse(is, handler, metadata,context);	
		    String docHtmlStr = new String(output.toByteArray(), "UTF-8");
		    output.close();
		    
		    doc = Jsoup.parse(docHtmlStr, "UTF-8");	    
		    return doc.toString();
		}
		
		
		/*
		 *  The following is the new document conversion method
		 */
		public  static DocumentConversionObject newDcouemntConversionObj(File file ) throws Exception{
			
			Detector detector = new DefaultDetector();
	        Parser parser = new AutoDetectParser(detector);
	        ParseContext parseContext = new ParseContext();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			SAXTransformerFactory factory = (SAXTransformerFactory)
			 SAXTransformerFactory.newInstance();
			TransformerHandler handler = factory.newTransformerHandler();
			handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "html");
			handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
			handler.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			handler.setResult(new StreamResult(out));
			ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);
			
			byte[] b = new byte[(int) file.length()];
	         try {
	               FileInputStream fileInputStream = new FileInputStream(file);
	               fileInputStream.read(b);
	               fileInputStream.close();
	          } catch (FileNotFoundException e) {
	                      System.out.println("File Not Found.");
	                      e.printStackTrace();
	          }
			
	        InputStream is = Files.asByteSource(file).openStream();
	        parser.parse(is, handler1, new Metadata(),parseContext);
	        
			//parser.parse(new ByteArrayInputStream(b), handler1, new Metadata());
			return new DocumentConversionObject( new String(out.toByteArray(), "UTF-8"));
		}
	
	
	// The following method will write the doc into a html file
		public static void WriteToFile(Document doc, String fileName, String filePath) {
			// TODO Auto-generated method stub
			
			String absPath = filePath +fileName+ "_Converted.html";
			File file = new File(absPath);
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			    writer.write(doc.toString());		     
			    writer.close();
			}catch(Exception e){
				e.printStackTrace();
			}		
		}
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String fileName = "5550039393.pdf";
		String filePath = "C:\\Users\\IBM_ADMIN\\Documents\\testnow\\cisco-samples\\MP\\";
		File file  = new File(filePath+fileName);
		//String result =  ApacheTikaHandler.docConversion(file);
		//Document doc = ApacheTikaHandler.docConversionDocument(file);
		//Document doc = ApacheTikaHandler.newApacheTikaDocumentConversion(file);
		//System.out.println(result);
		DocumentConversionObject newObj = newDcouemntConversionObj(file);
		WriteToFile(newObj.getDocumentDocument(),fileName+"_converted.html",filePath);
	}

	
}
