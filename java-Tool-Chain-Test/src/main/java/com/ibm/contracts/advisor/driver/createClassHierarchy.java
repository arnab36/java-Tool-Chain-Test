package com.ibm.contracts.advisor.driver;

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

public class createClassHierarchy {

	
	public static void main(String[] args) {
		StringBuffer errorMsg = new StringBuffer();
		ConvertAndSave(
				"C:/Users/IBM_ADMIN/Downloads/DPA_Class_Definitions_V2.xlsx");
	}
	
	
	public static void ConvertAndSave(String file){
		try {
			JSONObject strat = new JSONObject();
			JSONArray childList = new JSONArray();

			putMetadata(strat);
			FileInputStream excelFile = new FileInputStream(new File(file));
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

			System.out.println(childList.toJSONString());
			childList.remove(0);
			strat.put("children", childList);

			FileWriter file1 = new FileWriter(
			  "C:/Users/IBM_ADMIN/Documents/project 8/cisco/MP_ClassHier_Defn.json");
			file1.write(strat.toJSONString()); file1.close();
			 

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

}
