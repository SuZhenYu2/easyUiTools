package com.easyuitools.tools.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	
	public static final String HEADERINFO="headInfo";
	public static final String DATAINFON="dataInfo";
	
	/**
	 * 
	 * @Title: getWeebWork
	 * @Description: TODO(根据传入的文件名获取工作簿对象(Workbook))
	 * @param filename
	 * @return
	 * @throws IOException 
	 */
	public static Workbook getWeebWork(String filename) throws IOException{
		Workbook workbook=null;
		if(null!=filename){
			String fileType=filename.substring(filename.lastIndexOf("."),filename.length());
			FileInputStream fileStream = new FileInputStream(new File(filename));
			if(".xls".equals(fileType.trim().toLowerCase())){
				workbook = new HSSFWorkbook(fileStream);// 创建 Excel 2003 工作簿对象
			}else if(".xlsx".equals(fileType.trim().toLowerCase())){
				workbook = new XSSFWorkbook(fileStream);//创建 Excel 2007 工作簿对象
			}
		}
		return workbook;
	}
	/**
	 * 
	 * @Title: writeExcel
	 * @Description: TODO(导出Excel表)
	 * @param pathname:导出Excel表的文件路径
	 * @param map：封装需要导出的数据(HEADERINFO封装表头信息，DATAINFON：封装要导出的数据信息,此处需要使用TreeMap)
	 * 例如： map.put(ExcelUtil.HEADERINFO,List<String> headList);
	 * 		 map.put(ExcelUtil.DATAINFON,List<TreeMap<String,Object>>  dataList);
	 * @param wb
	 * @throws IOException
	 */
	public static void writeExcel(String pathname,Map<String,Object> map,Workbook wb) throws IOException{
		if(null!=map && null!=pathname){
			List<Object> headList = (List<Object>) map.get(ExcelUtil.HEADERINFO);
			List<TreeMap<String,Object>> dataList =  (List<TreeMap<String, Object>>) map.get(ExcelUtil.DATAINFON);
			CellStyle style = getCellStyle(wb);
			Sheet sheet = wb.createSheet();
			/**
			 * 设置Excel表的第一行即表头
			 */
			Row row =sheet.createRow(0);
			for(int i=0;i<headList.size();i++){
				Cell headCell = row.createCell(i);
				headCell.setCellType(Cell.CELL_TYPE_STRING);
				headCell.setCellStyle(style);//设置表头样式
				headCell.setCellValue(String.valueOf(headList.get(i)));
			}
			
			for (int i = 0; i < dataList.size(); i++) {
				Row rowdata = sheet.createRow(i+1);//创建数据行
				TreeMap<String, Object> mapdata =dataList.get(i);
				Iterator<String> it = mapdata.keySet().iterator();
				int j=0;
				while(it.hasNext()){
					String strdata = String.valueOf(mapdata.get(it.next()));
					Cell celldata = rowdata.createCell(j);
					celldata.setCellType(Cell.CELL_TYPE_STRING);
					celldata.setCellValue(strdata);
					j++;
				}
			}
			File file = new File(pathname);
			OutputStream os = new FileOutputStream(file);
			os.flush();
			wb.write(os);
			os.close();
		}
	}
	public static void writeExcel(OutputStream os,List headList,List<List> dataList) throws IOException{
		Workbook wb = new SXSSFWorkbook(1000);
		//Workbook workbook = new SXSSFWorkbook(500);//每次缓存500条到内存，其余写到磁盘。
			CellStyle style = getCellStyle(wb);
			Sheet sheet = wb.createSheet();
			/**
			 * 设置Excel表的第一行即表头
			 */
			Row row =sheet.createRow(0);
			for(int i=0;i<headList.size();i++){
				Cell headCell = row.createCell(i);
				headCell.setCellType(Cell.CELL_TYPE_STRING);
				headCell.setCellStyle(style);//设置表头样式
				headCell.setCellValue(String.valueOf(headList.get(i)));
			}
			
			for (int i = 0; i < dataList.size(); i++) {
				Row rowdata = sheet.createRow(i+1);//创建数据行
				List<Object> mapdata = dataList.get(i);
				int j=0;
				for(Object tt:mapdata){
					
					Cell celldata = rowdata.createCell(j);
					if(tt==null){
						tt="";
					}
					if(tt!=null && tt instanceof BigDecimal){
						celldata.setCellType(Cell.CELL_TYPE_NUMERIC);
					}else{
						celldata.setCellType(Cell.CELL_TYPE_STRING);
					}
					celldata.setCellValue( String.valueOf(tt));
					j++;
				}
			}
			
			os.flush();
			wb.write(os);
			//os.close();
	}
	/**
	 * 
	 * @Title: getCellStyle
	 * @Description: TODO（设置表头样式）
	 * @param wb
	 * @return
	 */
	public static CellStyle getCellStyle(Workbook wb){
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short)12);//设置字体大小
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//加粗
		style.setFillForegroundColor(HSSFColor.LIME.index);// 设置背景色
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.SOLID_FOREGROUND);//让单元格居中
		//style.setWrapText(true);//设置自动换行
		style.setFont(font);
		return style;
	}
	
}
