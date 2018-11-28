package com.autotest.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.skyscreamer.jsonassert.FieldComparisonFailure;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that read data from XSSF sheet
 * 
 */
public class DataWriter {

	protected static final Logger logger = LoggerFactory.getLogger(DataWriter.class);

	public static void writeData(XSSFSheet sheet) {

	}

	public static void writeData(XSSFSheet sheet, String msg, String ID, String test_case) {
		// 检查并创建表头
		if(null == sheet.getRow(0)) {
			String heads = "ID,TestCase,Response";
			String headsWidth = "5,20,100";//字符个数
			
			writeHeads(sheet,heads,headsWidth);
		}
		
		// 在空白行累加数据
		Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
		Cell cell = row.createCell(0);  
		cell.setCellValue(ID);		
		cell = row.createCell(1);  
		cell.setCellValue(test_case); 
		cell = row.createCell(2);  
		cell.setCellValue(msg);		
	}		

	public static void writeData(XSSFSheet sheet, JSONCompareResult result, String ID, String test_case) {
		// 检查并创建表头
		if(null == sheet.getRow(0)) {
			String heads = "ID,TestCase,Message,Failure field Value, , , ";
			String headsWidth = "5,20,40,40,40,40,40";			
			writeHeads(sheet,heads,headsWidth);
		}
		
		// 在空白行累加数据
		Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
		Cell cell = row.createCell(0);  
		cell.setCellValue(ID);		
		cell = row.createCell(1);  
		cell.setCellValue(test_case); 
		
		//输出 JSONCompareResult result
		cell = row.createCell(2);
		cell.setCellValue(result.getMessage());
		
		List<FieldComparisonFailure> _fieldFailures = result.getFieldFailures();
		for(FieldComparisonFailure field : _fieldFailures){
			cell = row.createCell(row.getPhysicalNumberOfCells());
			cell.setCellValue(field.getField());
		}		
	}

	
	public static void writeData(XSSFSheet sheet, String msg, String ID, String test_case,int i) {
		// 检查并创建表头
		if(null == sheet.getRow(0)) {
			String heads = "ID,TestCase,Result";
			String headsWidth = "5,20,20";			
			writeHeads(sheet,heads,headsWidth);
		}
		
		// 在空白行累加数据
		Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
		Cell cell = row.createCell(0);  
		cell.setCellValue(ID);		
		cell = row.createCell(1);  
		cell.setCellValue(test_case); 
		cell = row.createCell(2);  
		cell.setCellValue(msg);	
	}
	
	public static void writeData(XSSFSheet sheet, String msg,String errMsg, 
									String ID, String test_case) {
		// 检查并创建表头
		if(null == sheet.getRow(0)) {
			String heads = "ID,TestCase,Message,Failure field Value, , , ";
			String headsWidth = "5,20,40,40,40,40,40";			
			writeHeads(sheet,heads,headsWidth);
		}
		
		// 在空白行累加数据
		Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
		Cell cell = row.createCell(0);  
		cell.setCellValue(ID);		
		cell = row.createCell(1);  
		cell.setCellValue(test_case); 
		
		cell = row.createCell(2);
		cell.setCellValue(errMsg);
	}
	
	
	public static void writeData(XSSFSheet sheet, double totalcase, double failedcase, 
				String startTime,String endTime) {
		// 检查并创建表头
		if(null == sheet.getRow(0)) {
			String heads = "ID,TestCase,Result";
			String headsWidth = "5,20,20";			
			writeHeads(sheet,heads,headsWidth);
		}
		
		// 在空白行累加数据
		Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
		
		row = sheet.createRow(sheet.getPhysicalNumberOfRows());  
		Cell cell = row.createCell(1);  
		cell.setCellValue("Totalcase:");
		cell = row.createCell(2);
		cell.setCellValue(totalcase);
		

		row = sheet.createRow(sheet.getPhysicalNumberOfRows());  
		cell = row.createCell(1);  
		cell.setCellValue("Failedcase:");
		cell = row.createCell(2);
		cell.setCellValue(failedcase);
		
		row = sheet.createRow(sheet.getPhysicalNumberOfRows());  
		double passPercentage = 0.0;
		passPercentage = Math.round((totalcase - failedcase) / totalcase * 10000) / 100;

		cell = row.createCell(1);  
		cell.setCellValue("PassPercentage:");
		cell = row.createCell(2);
		cell.setCellValue(passPercentage + "%");
		
		row = sheet.createRow(sheet.getPhysicalNumberOfRows());   
		cell = row.createCell(1);  
		cell.setCellValue("StartTime:");
		cell = row.createCell(2);
		cell.setCellValue(startTime);
		
		row = sheet.createRow(sheet.getPhysicalNumberOfRows());  
		cell = row.createCell(1);  
		cell.setCellValue("EndTime:");
		cell = row.createCell(2);
		cell.setCellValue(endTime);
		
	}

	/**
	 * 根据字符串创建表头
	 * @param sheet
	 * @param sHeads
	 * @param headsWidth
	 */
	public static void writeHeads(XSSFSheet sheet, String sHeads,String sHeadsWidth) {
		// 检查并创建表头
		String[] heads = sHeads.split(",");
		String[] headsWidth = sHeadsWidth.split(",");
		
		Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());            
		for(int i = 0;i < heads.length;i++){ 
			Cell cell = row.createCell(i); 
			cell.setCellValue(heads[i]);
			sheet.setColumnWidth(i, 
						Integer.parseInt(String.valueOf((255*Integer.parseInt(headsWidth[i])+185))));
			logger.info(heads[i]);
		} 
	}
}