package com.ibm.contracts.advisor.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ibm.contracts.advisor.FieldGlass.STATIC.FieldGlassStatic;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.driver.PrepareStaticDocument;
import com.ibm.contracts.advisor.handler.ApacheTikaHandler;
import com.ibm.contracts.advisor.handler.DocumentConversionHTMLHandler;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;
import com.ibm.contracts.advisor.handler.UploadHandler;
import com.ibm.contracts.advisor.util.JsonRespond;
import com.ibm.contracts.advisor.util.PostDocumentConversionHTMLProcessing;
import com.ibm.contracts.advisor.util.SOP;
import com.ibm.contracts.advisor.util.SessionData;
import com.ibm.contracts.advisor.util.Util;
import com.ibm.contracts.advisor.vo.ConvertReturn;


/*@WebServlet(urlPatterns = { "/UploadTemplateServlet" }, initParams = {
		@WebInitParam(name = "saveDir", value = "uploadFiles"),
		@WebInitParam(name = "allowedTypes", value = "pdf,doc,docx") }

)
*/


@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
maxFileSize = 1024 * 1024 * 10, // 10MB
maxRequestSize = 1024 * 1024 * 50 // 50MB
)

public class UploadTemplateServlet extends HttpServlet implements Constants {
	
	private static final long serialVersionUID = 1L;
	private String templateFileName = null;
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("No do get method");
	}

	
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		StringBuffer errorMsg = new StringBuffer();
		StringBuffer logMsg = new StringBuffer();
		boolean isSuccess = false;
		
		ServletContext sc = this.getServletContext();
		SessionData sd = new SessionData();

		HttpSession session = SessionHelper.sessionHelp(request, sc, response);
		
		System.out.println("============= Inside UploadTemplateServlet ==================== ");
		
		String templateType1 = request.getParameter("templateType1");
		String contractType = request.getParameter("contractType1");
		String documentType = request.getParameter("documentType1");
		String fileName = request.getParameter("input_file_name");
		
		System.out.println("contractType:" + contractType);
		System.out.println("documentType::" + documentType);
		System.out.println("templateType1:" + templateType1);
		System.out.println("fileName::" + fileName);
		
		String outputFileName="";
		String changedContracyType="";
		String changedDocumentType = "";
		
		if(contractType.equalsIgnoreCase("Managed Projects")) {
			changedContracyType = "MP_";
		}else {
			changedContracyType = "MS_";
		}
		System.out.println("changedContracyType :: "+ changedContracyType);
		
		if(documentType.equalsIgnoreCase("FG")) {
			changedDocumentType = "FG_";
		}else {
			changedDocumentType = "";
		}
		System.out.println("changedDocumentType :: "+ changedDocumentType);
		
		
		outputFileName = changedContracyType+changedDocumentType+"Static_Template_V5.json";
		System.out.println("outputFileName :: "+outputFileName);
		
		
		File file = null;		
		try {
			file = UploadHandler.upload(request, response);
		} catch (Exception e1) {
			errorMsg.append("Error in file upload\n" + e1.toString());
			e1.printStackTrace();
		}
		
		if (file != null) {
			if(documentType.equalsIgnoreCase("FG")) {
				if(contractType.equalsIgnoreCase("Managed Projects")) {
					templateFileName = "Template_MP_FG_Classes.json";
				}else {
					templateFileName = "Template_MS_FG_Classes.json";
				}
								
				String templateJson  = SetTemplates(templateFileName);
				createStaticTemplateForFG(outputFileName,file,templateJson);
			}else {
				
				if(contractType.equalsIgnoreCase("Managed Projects")) {
					templateFileName = "Template_MP_Classes.json";
				}else {
					templateFileName = "Template_MS_Classes.json";
				}
				String templateJson  = SetTemplates(templateFileName);
				createStaticTemplateForNonFG(outputFileName,file,templateJson);
			}
		}		
				
		try {
			SOP.printSOPSmall("Now responding ...... :: Successful");
			JsonRespond.uploadServletResopnse(logMsg.toString(),
					errorMsg.toString(), response,
					fileName, 200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void createStaticTemplateForNonFG(String outputFileName, File file, String templateJson) {
		
		JSONObject staticJsonObject;
		Document staticDoc= null;
		String res = "";
		
		try {
			ConvertReturn answers = DocumentConversionHTMLHandler
					.convertFile(file);
			String docHTML = answers.getHtml().toString();			
			
			JSONObject ClasstemplateJson = Util.getJSONObject(templateJson);

			Document doc = Jsoup.parse(docHTML);

			staticDoc = PrepareStaticDocument.extractTableForGeneral(doc,
					ClasstemplateJson);

			staticDoc.select("table").remove();

			staticDoc = PrepareStaticDocument.processOLTag(staticDoc);

			staticDoc = PostDocumentConversionHTMLProcessing
					.ProcessHTMLAndAssignTagID(staticDoc, ClasstemplateJson);

			staticDoc = PostDocumentConversionHTMLProcessing
					.addTitle(staticDoc);
			
			staticJsonObject = PrepareStaticDocument.Html2Jsonobject(staticDoc);
			res = staticJsonObject.toJSONString();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			ObjectStoreHandler.objectStore(new ByteArrayInputStream(res.getBytes()),
					outputFileName, UPLOADCONFIGURATION_CONTAINER);					
			System.out.println("Bug 2");
		} catch (Exception e) {
			System.out.println("Bug 3");
			System.out.println(e);
		}
	}
	
	
	
	public void createStaticTemplateForFG(String outputFileName, File file, String templateJson ) {
		
		try {
			ConvertReturn answers = DocumentConversionHTMLHandler
					.convertFile(file);
			
			if (answers != null) {
				String docHTML = null;
				boolean found = true;
			
				docHTML = answers.getHtml().toString();
				System.out.println();
				Document doc = Jsoup.parse(docHTML);	
				JSONObject classTemplateJSON = Util.getJSONObject(templateJson);
				JSONObject staticTemplateForFG = FieldGlassStatic.createStaticTemplateFG(doc, classTemplateJSON);	
				String res = staticTemplateForFG.toJSONString();
				try {
					ObjectStoreHandler.objectStore(new ByteArrayInputStream(res.getBytes()),
							outputFileName, UPLOADCONFIGURATION_CONTAINER);					
					System.out.println("Bug 2");
				} catch (Exception e) {
					System.out.println("Bug 3");
					System.out.println(e);
				}			
			
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	// This will return the file content as String from object storage
		private String SetTemplates(String fileName) {
			// TODO Auto-generated method stub
			String fileContent = null;
			try {
				fileContent = ObjectStoreHandler.getFileStr(fileName,
						PROPERTIES_CONTAINER);
			} catch (Exception e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
			return fileContent;
		}
	
		
		public static void dummyFunctionFG(File file,String outputFileName, String templateJson) {
			String docHTML = null;
			try {
				docHTML = new ApacheTikaHandler().docConversion(file);
				System.out.println("docHTML:: \n "+docHTML);
				Document doc = Jsoup.parse(docHTML);	
				JSONObject classTemplateJSON = Util.getJSONObject(templateJson);
				JSONObject staticTemplateForFG = FieldGlassStatic.createStaticTemplateFG(doc, classTemplateJSON);	
				String res = staticTemplateForFG.toJSONString();
				try {
					ObjectStoreHandler.objectStore(new ByteArrayInputStream(res.getBytes()),
							outputFileName, UPLOADCONFIGURATION_CONTAINER);					
					System.out.println("Bug 2");
				} catch (Exception e) {
					System.out.println("Bug 3");
					System.out.println(e);
				}		
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		
		public static void dummyFunctionNonFG(File file,String outputFileName, String templateJson) {
			JSONObject staticJsonObject;
			Document staticDoc= null;
			String res = "";
			
			try {
				
				String docHTML = new ApacheTikaHandler().docConversion(file);			
				
				JSONObject ClasstemplateJson = Util.getJSONObject(templateJson);

				Document doc = Jsoup.parse(docHTML);

				staticDoc = PrepareStaticDocument.extractTableForGeneral(doc,
						ClasstemplateJson);

				staticDoc.select("table").remove();

				staticDoc = PrepareStaticDocument.processOLTag(staticDoc);

				staticDoc = PostDocumentConversionHTMLProcessing
						.ProcessHTMLAndAssignTagID(staticDoc, ClasstemplateJson);

				staticDoc = PostDocumentConversionHTMLProcessing
						.addTitle(staticDoc);
				
				staticJsonObject = PrepareStaticDocument.Html2Jsonobject(staticDoc);
				res = staticJsonObject.toJSONString();
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			try {
				ObjectStoreHandler.objectStore(new ByteArrayInputStream(res.getBytes()),
						outputFileName, UPLOADCONFIGURATION_CONTAINER);					
				System.out.println("Bug 2");
			} catch (Exception e) {
				System.out.println("Bug 3");
				System.out.println(e);
			}
		}
		
		public static void main(String[] ab) throws Exception{
			String outputFilePath = "C:/Users/IBM_ADMIN/Documents/testnow/FieldGlass/Defect Document HTML/";
			String fileName = "5550039393.pdf";
			File file = new File("C:\\Users\\IBM_ADMIN\\Documents\\testnow\\cisco-samples\\MP\\"+ fileName);
			
			UploadTemplateServlet obj = new UploadTemplateServlet();
			String templateFileName = "Template_MP_Classes.json";
			String templateJson  = obj.SetTemplates(templateFileName);
			//obj.createStaticTemplateForFG("NewHierarchy.json",file,templateJson);
			//obj.dummyFunctionFG(file,"NewHierarchyNonFG.json",templateJson);
			obj.dummyFunctionNonFG(file,"NewHierarchyNonFG.json",templateJson);
			
		}
		
		
}









