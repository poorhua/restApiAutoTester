package com.autotest.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Class that read data from XSSF sheet
 * 
 */
public class SheetUtils {

	protected static final Logger logger = LoggerFactory.getLogger(SheetUtils.class);

	private HashMap<String, RecordHandler> map = new HashMap<String, RecordHandler>();

	private Boolean byColumnName = false;
	private Boolean byRowKey = false;
	private List<String> headers = new ArrayList<String>();

	private Integer size = 0;

	public SheetUtils() {
	}

	public static void removeSheetByName(XSSFWorkbook workBook,String sheetName) {
		if(workBook.getSheetIndex(sheetName)>=0) {
			workBook.removeSheetAt(workBook.getSheetIndex(sheetName));
			logger.info("workBook.removeSheetAt("+sheetName+")");
		}
	}
    
	public static XSSFSheet createSheet(XSSFWorkbook workBook,String sheetName) {
		XSSFSheet sheet = null;
		if(workBook.getSheetIndex(sheetName)<0)
			sheet = workBook.createSheet(sheetName);
		return sheet;
	}
    
	/**
   * Utility method used for getting an excel cell value. Cell's type is switched to String before accessing.
   * 
   * @param cell Given excel cell.
   */
  public static String getSheetCellValue(XSSFCell cell) {

    String value = "";

    try {
      cell.setCellType(Cell.CELL_TYPE_STRING);
      value = cell.getStringCellValue();
    } catch(NullPointerException npe) {
      return "";
    }

    return value;
  }

	
}