package com.easyuitools.common.pdf.core;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.easyuitools.common.gennerate.GenerateSearch;

import jxl.Sheet;
import jxl.Workbook;

/**
 * 操作excel文件的类
 * @author newapps
 * 2009-11-8
 */

public class Excel {
	private static final Logger LOGGER = Logger.getLogger(Excel.class);	

	/**工作簿数目*/
	private Sheet[] sheets=null;
	/**excel文件是否被保护*/
	private boolean isProtected=false;
	/**工作簿Workbook对象*/
	private Workbook wb=null;
	/**默认构造函数*/
	public Excel(){
	}
	/**获得该excel文件中的工作簿数组*/
	public Sheet[] getSheets() {
		return sheets;
	}
	/**查看excel文件是否被保护*/
	public boolean isProtected() {
		return isProtected;
	}
	/**
	 * 从本地读取一个excel文件
	 * @param sourceFilePath excel文件路径
	 */
	public void readExcel(String sourceFilePath){
		InputStream is=null;
		try {
			is=new FileInputStream(sourceFilePath);
			wb=Workbook.getWorkbook(is);
			sheets=wb.getSheets();
			isProtected=wb.isProtected();
			LOGGER.info("-->从本地读取excel文件成功!");
		} catch (Exception e) {
			LOGGER.error("-->从本地读取excel文件失败!",e);
		
		}
	}
	/**
	 * 从数据库读取一个excel文件输入流
	 * @param is excel文件输入流
	 */
	public void readExcelFromDB(InputStream is){
		Workbook wb=null;
		try {
			wb=Workbook.getWorkbook(is);
			sheets=wb.getSheets();
			isProtected=wb.isProtected();
			LOGGER.info("-->从数据库读取excel文件成功!");
		} catch (Exception e) {
			LOGGER.error("-->从数据库读取excel文件失败!",e);
		}
	}
	
	/**
	 * 关闭工作簿对象
	 *
	 */
	public void closeWorkbook(){
		if(wb!=null){
		wb.close();
		LOGGER.info("-->关闭Workbook对象成功!");
		}
	}
}
