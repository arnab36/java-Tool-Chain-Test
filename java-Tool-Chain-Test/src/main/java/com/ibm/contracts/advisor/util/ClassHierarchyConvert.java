package com.ibm.contracts.advisor.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;

public class ClassHierarchyConvert implements Constants {

	 private static final String FILE_PATH = "C:/Users/IBM_ADMIN/Documents/Phase2/CISCO-Phase 3/";
	 private static final String FILE_NAME = "DynamicClassHierarchy.xlsx";

	
	/*
	 *  The following function will create a multiple level hierarchy
	 * */
	public static boolean createMultilevelClasshierarchy(String FILE_NAME, StringBuffer errorMsg, String outputFileName) {
		
		try {
			JSONObject strat = new JSONObject();
			JSONArray childList = new JSONArray();

			putMetadata(strat);
			FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			iterator.next();

			String currentCat = "0.0", prevCat = "", putCat = "";

			JSONArray catClass = new JSONArray();
			JSONObject catObj = new JSONObject();
			boolean done = false, isFirst = true;
			while (iterator.hasNext()) {

				Row currentRow = iterator.next();
				Iterator<Cell> cellIterator = currentRow.iterator();
				int colNum = 0;
				JSONObject classObj = new JSONObject();
				classObj.put("layer", "2");
				System.out.println("===================");
				while (cellIterator.hasNext()) {
					if (colNum > 1)
						break;
					Cell currentCell = cellIterator.next();

					String cellVal = getCellData(currentCell);
					if (cellVal.trim().equals("")) {
						done = true;
						break;
					}
					System.out.println(cellVal);
					if (colNum == 1)
						classObj.put("name", cellVal);

					if (colNum == 0) {
						if (!currentCat.equals("0.0"))
							prevCat = currentCat;
						currentCat = cellVal;
					}

					colNum++;
				}
				if (!currentCat.equals("0.0")) {
					System.out.println("cat:::::::::" + prevCat);
					catObj.put("name", prevCat);
					catObj.put("layer", "1");
					catObj.put("children", catClass);
					System.out.println("coming here ::"
							+ catClass.toJSONString());
					childList.add(catObj);
					catObj = new JSONObject();
					catClass = new JSONArray();

				}
				catClass.add(classObj);
			}

			System.out.println(childList);
			childList.remove(0);
			strat.put("children", childList);

			try {
				ObjectStoreHandler.objectStore(new ByteArrayInputStream(strat
						.toJSONString().getBytes()), outputFileName,
						UPLOADCONFIGURATION_CONTAINER);
			} catch (Exception e) {
				errorMsg.append(e.toString());
				System.out.println(e);
				return false;
			}			 

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;

	}	
	
	
	/*
	 *  The following function will create a single level hierarchy.
	 *  It is basically for dynamic classes
	 * */
	public static boolean createSingleLevelClassHierarchy(String FILE_CONETNT,
			StringBuffer errorMsg, String outputFileName) {
		// TODO Auto-generated method stub
		try {
			JSONObject strat = new JSONObject();
			JSONArray childList = new JSONArray();

			putMetadata(strat);
			
			FileInputStream excelFile = new FileInputStream(new File(FILE_CONETNT));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			iterator.next();

			String currentCat = "0.0", prevCat = "", putCat = "";
			JSONArray catClass = new JSONArray();
			JSONObject catObj = new JSONObject();
			boolean done = false, isFirst = true;
			
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				Iterator<Cell> cellIterator = currentRow.iterator();
				int colNum = 0;
				JSONObject classObj = new JSONObject();
				while (cellIterator.hasNext()) {
					
					if (colNum > 0){
						break;
					}
					
					Cell currentCell = cellIterator.next();
					String cellVal = getCellData(currentCell);
					//System.out.println(colNum);
					if (cellVal.trim().equals("")) {
						done = true;
						break;
					}
					classObj.put("name", cellVal);	
					classObj.put("layer", "1");					
										
					childList.add(classObj);
					colNum++;
					
				}
			}
			childList.remove(0);
			strat.put("children", childList);
			System.out.println(strat);
			
			try {
				ObjectStoreHandler.objectStore(new ByteArrayInputStream(strat
						.toJSONString().getBytes()), outputFileName,
						UPLOADCONFIGURATION_CONTAINER);
			} catch (Exception e) {
				errorMsg.append(e.toString());
				System.out.println(e);
				return false;
			}	

		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	
	private static String getCellData(Cell currentCell) {
		String cellVal = null;
		if (currentCell.getCellType() == 1) {// means string cell
			// System.out.print(currentCell.getStringCellValue() + "--");
			cellVal = currentCell.getStringCellValue();
			// classObj.put("name", cellVal);
			return cellVal;
		} else {// numaric cell
			// System.out.print(currentCell.getNumericCellValue() + "::");
			cellVal = Double.toString(currentCell.getNumericCellValue());
			return cellVal;
		}

	}

	private static void putMetadata(JSONObject strat) {
		strat.put("layer", "0");
		strat.put("name", "SOW");
	}
	
	
	public static void main(String[] args) {
		StringBuffer errorMsg = new StringBuffer();
		/*convertAndSaveMultilevel(
				"C:/Users/IBM_ADMIN/Downloads/DPA_Class_Definitions_V2.xlsx",
				errorMsg,"temp.json");*/
		
		createSingleLevelClassHierarchy(FILE_PATH+FILE_NAME,errorMsg,"temp.json");
	}

	

}
