package com.ibm.contracts.advisor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ClassDefConvert {

	 private static final String FILE_NAME = "C:/Users/IBM_ADMIN/Documents/project 8/cisco/MS_ClassHier_Defn.xlsx";

	 
	public static void main(String[] args) {
		   try {
	        	JSONObject strat=new JSONObject();
	        	putMetadata(strat);
	            FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
	            Workbook workbook = new XSSFWorkbook(excelFile);
	            Sheet datatypeSheet = workbook.getSheetAt(0);
	            Iterator<Row> iterator = datatypeSheet.iterator();
	            iterator.next();
	            
	            
	           // JSONArray catClass=new JSONArray();
	            JSONObject classObj=new JSONObject();
	            while (iterator.hasNext()) {
	            	
	                Row currentRow = iterator.next();
	                Iterator<Cell> cellIterator = currentRow.iterator();
	             
	               
	                int cellNo=0;
	                String className=null,classDef=null;
	                 while (cellIterator.hasNext()) {
	                	
	                    Cell currentCell = cellIterator.next();
	                    
	                   
	                    	
	                    String cellVal=null;
	                    if (currentCell.getCellType() == 1) {//means string cell
	                    
	                       if(cellNo==1)
	                    	   className=	currentCell.getStringCellValue() ;
	                       if(cellNo==2)
	                    	   classDef=currentCell.getStringCellValue() ;
	                    } else  {//numaric cell
	                        System.out.print("got numaric cell which is not possible"+currentCell.getNumericCellValue());
	                        //cellVal=Double.toString( currentCell.getNumericCellValue());
	                    }
	                  
	                    
	                    cellNo++;
	                }
	                JSONObject tmp=new JSONObject();
	                JSONArray tmp2=new JSONArray();
	                tmp.put("text", classDef);
	                tmp.put("ref", "Refer:");
	                tmp.put("href", "/help.html#"+className);
	                tmp2.add(tmp);
	                
	                 classObj.put(className, tmp2);
	               
	            }
	          System.out.println("============================");
	          System.out.println(classObj.toJSONString());
	            FileWriter file1 = new FileWriter("C:/Users/IBM_ADMIN/Documents/project 8/cisco/Class_definition_MS.json");
	    		file1.write(classObj.toJSONString());
	    		file1.close();
	            
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	       

	}


	private static void putMetadata(JSONObject strat) {
		strat.put("layer", "0");
		strat.put("name", "SOW");
		
		
	}

}
