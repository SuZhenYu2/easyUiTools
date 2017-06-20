package com.easyuitools.tools.export;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.DbUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.easyuitools.common.entity.bean.GenerateBean;
import com.easyuitools.common.gennerate.GenerateColumns;
import com.easyuitools.common.gennerate.GenerateExcelTools;
import com.easyuitools.common.pdf.impl.Excel_TO_PDF;
import com.easyuitools.common.pdf.pagesetting.PageSetting;
import com.easyuitools.common.pdf.pdfpag.PdfPageSize;
import com.easyuitools.util.SpringContextUtils;
import com.easyuitools.util.StringUtils;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

public class ExportExcel {


	private static final Logger LOGGER = Logger.getLogger(ExportExcel.class);	
	 /**
	  * 
	  * @param desName 文件名称
	  * @param response
	  * @param beans
	  * @param cls
	  * @param replaceMap
	  */
	public static void export(String desName,HttpServletResponse response,List<?> beans,Class<?> cls,@SuppressWarnings("rawtypes") Map replaceMap){
		GenerateBean generateBean =GenerateExcelTools.GenerateExcel(cls,beans,replaceMap);
		ExportExcel.export(desName, response, generateBean);
	}
	/**
	 * 
	 * @param desName
	 * @param beans
	 * @param cls
	 * @param replaceMap
	 */
	public static void export(String desName,List<?> beans,Class<?> cls,@SuppressWarnings("rawtypes") Map replaceMap){
		GenerateBean generateBean =GenerateExcelTools.GenerateExcel(cls,beans,replaceMap);
		ExportExcel.export(desName, null, generateBean);
	}
	@SuppressWarnings("unchecked")
	public static void exportFile(String desName,OutputStream os,List<?> beans,Class<?> cls,@SuppressWarnings("rawtypes") Map replaceMap) throws IOException{
		GenerateBean generateBean =GenerateExcelTools.GenerateExcel(cls,beans,replaceMap);
		ExcelUtil.writeExcel(os, generateBean.getListHead(), generateBean.getListBody());
	}
	public static void export(String desName,HttpServletResponse response,List<?> beans){
		export(desName, response, beans,null,null);
	}
	public static void export(String desName,List<?> beans){
		export(desName, null, beans,null,null);
	}
	public static void export(String desName,HttpServletResponse response,List<?> beans,@SuppressWarnings("rawtypes") Map replaceMap){
		export(desName, response, beans,null,replaceMap);
	}
	public static void export(String desName,List<?> beans,@SuppressWarnings("rawtypes") Map replaceMap){
		export(desName, null, beans,null,replaceMap);
	}
	/**
	 * @author suzy2
	 * @param desName 导出文件名称
	 * @param response 
	 * @param beans 导出对应用的 Map对象
	 */
	public static void export(String desName,HttpServletResponse response,GenerateBean generateBean){
		export(null, desName, response, generateBean);
	}
	public static void export(String desName,GenerateBean generateBean){
		export(null, desName, null, generateBean);
	}
	/**
	 * @author suzy2
	 * @param temmpTlateName 模板名称
	 * @param desName  导出文件名称
	 * @param response 
	 * @param beans 导出对应用的 Map对象
	 */
	@SuppressWarnings("unchecked")
	public static void export(String temmpTlateName,String desName,HttpServletResponse response,GenerateBean generateBean){
		SimpleDateFormat format = StringUtils.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = format.format(new Date());
		if(response == null){ 
			response= ( (ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
 		}
		response.setContentType("Application/msexcel;charset=UTF-8");

		String fileName = /*"工作日历导出详情"*/ desName+dateStr+".xls";
		if(StringUtils.isBlank(temmpTlateName) || "/template/common/commonTemplate.xls".equals(temmpTlateName) ){
			fileName=fileName+"x";
		}
		try {
			fileName = new String(fileName.getBytes("GBK"),"iso-8859-1");
		} catch (UnsupportedEncodingException e1) {
			LOGGER.error(e1.getMessage(),e1);
		}
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);  
		response.setContentType("application/vnd.ms-excel");  
		if(StringUtils.isBlank(temmpTlateName) || "/template/common/commonTemplate.xls".equals(temmpTlateName) ){
			try {
				ExcelUtil.writeExcel(response.getOutputStream(), generateBean.getListHead(), generateBean.getListBody());
			} catch (IOException e) {
				LOGGER.error(e.getMessage(),e);
			}
		}else{
			export_impl(temmpTlateName, desName, response, generateBean);
		}
	}
	/**
	 * @author suzy2
	 * @param temmpTlateName 模板名称
	 * @param desName  导出文件名称
	 * @param response 
	 * @param mapBean 导出对应用的 Map对象
	 */
	public static void export_impl(String temmpTlateName,String desName,HttpServletResponse response,GenerateBean generateBean){
		String templateFileName= ExportExcel.class.getClassLoader().getResource("").getPath() +temmpTlateName;// "/template/workDayTemplate.xls";  
		Map<Object, Object> mapBean = new HashMap<Object, Object>();
		XLSTransformer transformer = new XLSTransformer();  
		InputStream in=null;  
		OutputStream out=null;  
		//设置响应  
		SimpleDateFormat dateFormat = StringUtils.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//格式化函数  
		mapBean.put("dateFormat", dateFormat);
		mapBean.put("listHead", generateBean.getListHead());
		mapBean.put("listBody", generateBean.getListBody());
		try {  
			in=new BufferedInputStream(new FileInputStream(templateFileName));  
			Workbook workbook=transformer.transformXLS(in, mapBean);  
			out=response.getOutputStream();  
			workbook.write(out);  
			out.flush();  
		} catch (InvalidFormatException e) {  
			LOGGER.error(e.getMessage(),e);  
		} catch (IOException e) {  
			LOGGER.error(e.getMessage(),e);  
		} catch (ParsePropertyException e) {
			LOGGER.error(e.getMessage(),e);
		} finally {  
			if (in!=null){try {in.close();} catch (IOException e) {}}  
			if (out!=null){try {out.close();} catch (IOException e) {}}  
		} 
	}
	/**
	 * @author suzy2
	 * @param temmpTlateName 模板名称
	 * @param desName  导出文件名称
	 * @param response 
	 * @param beans 导出对应用的 Map对象
	 */
	public static void export_impl(String temmpTlateName,String desName,OutputStream out,Map<Object, Object> beans){
		String templateFileName= ExportExcel.class.getClassLoader().getResource("").getPath() +temmpTlateName;// "/template/workDayTemplate.xls";  


		XLSTransformer transformer = new XLSTransformer();  
		InputStream in=null;  
		//	OutputStream out=null;  
		//设置响应  

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//格式化函数  
		beans.put("dateFormat", dateFormat);
		try {  
			in=new BufferedInputStream(new FileInputStream(templateFileName));  
			Workbook workbook=transformer.transformXLS(in, beans);  
			//out=response.getOutputStream();  
			workbook.write(out);  
			out.flush();  
		} catch (InvalidFormatException e) {  
			LOGGER.error(e.getMessage(),e);  
		} catch (IOException e) {  
			LOGGER.error(e.getMessage(),e);  
		} catch (ParsePropertyException e) {
			LOGGER.error(e.getMessage(),e);
		} finally {  
			if (in!=null){try {in.close();} catch (IOException e) {}}  
			if (out!=null){try {out.close();} catch (IOException e) {}}  
		} 
	}
	/**
	 * @author suzy2
	 * @param temmpTlateName 模板名称
	 * @param desName  导出文件名称
	 * @param response 
	 * @param beans 导出对应用的 Map对象
	 * @see    导出pdf 使用 目前该方法比较耗内存  
	 */
	@SuppressWarnings("unchecked")
	public static void exportStream(String temmpTlateName,String desName,HttpServletResponse response,@SuppressWarnings("rawtypes") Map beans){
		String templateFileName= ExportExcel.class.getClassLoader().getResource("").getPath() +temmpTlateName;// "/template/workDayTemplate.xls";  
		PageSetting set=new PageSetting();
		set.setPageSize(PdfPageSize.A2.rotate());
		SimpleDateFormat format = StringUtils.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = format.format(new Date());
		response.setContentType("Application/pdf;charset=UTF-8");
		String fileName = /*"工作日历导出详情"*/ desName+dateStr+".pdf";
		try {
			fileName = new String(fileName.getBytes("GBK"),"iso-8859-1");
		} catch (UnsupportedEncodingException e1) {
			LOGGER.error(e1.getMessage(),e1);
		}
		XLSTransformer transformer = new XLSTransformer();  
		InputStream in=null;  
		OutputStream out=null;  
		//设置响应  
		ByteArrayOutputStream out1 = new ByteArrayOutputStream();  

		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);  
		//    	response.setContentType("application/vnd.ms-excel");  
		SimpleDateFormat dateFormat = StringUtils.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		beans.put("dateFormat", dateFormat);
		try {  
			in=new BufferedInputStream(new FileInputStream(templateFileName));  
			Workbook workbook=transformer.transformXLS(in, beans);  
			Excel_TO_PDF excel_TO_PDF = new Excel_TO_PDF();
			workbook.write(out1);  
			InputStream input =new ByteArrayInputStream(out1.toByteArray());
			out=response.getOutputStream();  
			excel_TO_PDF.conVertFormStream(input, out, set);
			out.flush();  
		} catch (InvalidFormatException e) {  
			LOGGER.error(e.getMessage(),e);  
		} catch (IOException e) {  
			LOGGER.error(e.getMessage(),e);  
		} catch (ParsePropertyException e) {
			LOGGER.error(e.getMessage(),e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		} finally {  
			if (in!=null){try {in.close();} catch (IOException e) {}}  
			if (out!=null){try {out.close();} catch (IOException e) {}}  
		} 
	}
	/**
	 * @author suzy2
	 * @param temmpTlateName 模板名称
	 * @param desName  导出文件名称
	 * @param response 
	 * @param beans 导出对应用的 Map对象
	 * @see    导出pdf 使用 目前该方法比较耗内存  
	 */
	public static void exportPdf(String temmpTlateName,String desName,HttpServletResponse response,@SuppressWarnings("rawtypes") Map beans){
		exportStream(temmpTlateName, desName, response, beans);
	}

	public static void exportExcelBySql(Connection conn,HttpServletResponse response,String desName, String sql, Class<?>  rsh) throws SQLException{
		exportExcelBySql( conn, response,desName, true,  sql, null,  rsh);
	}
	public static void exportExcelBySql(Connection conn,HttpServletResponse response,String desName, String sql,@SuppressWarnings("rawtypes") Map replaceMap, Class<?>  rsh) throws SQLException{
		exportExcelBySql( conn, response,desName, true,  sql, replaceMap,  rsh);
	}
	public static void exportExcelBySql(HttpServletResponse response,String desName, String sql,@SuppressWarnings("rawtypes") Map replaceMap, Class<?>  rsh) throws SQLException{
		SqlSessionFactory   sqlSessionFactory = (SqlSessionFactory) SpringContextUtils.getBean("sqlSessionFactory");
		exportExcelBySql( sqlSessionFactory.openSession().getConnection(), response,desName, true,  sql, replaceMap,  rsh);
	}
	/**
	 * 
	 * @author suzy2
	 * @param conn     数据库库连接
	 * @param response 
	 * @param desName   导出文件名称
	 * @param closeConn 是否关闭连接
	 * @param sql       需要导出的sql
	 * @param replaceMap 替换map对象
	 * @param rsh         需要导出的bean类  必须使用注解才能实现标准导出
	 * @throws SQLException
	 * @see   该方法适合大数据量数据导出  使用多线程生成  提高导出时间 降低内存占用
	 * 
	 */
	public static void exportExcelBySql(Connection conn,HttpServletResponse response,String desName, boolean closeConn, String sql,@SuppressWarnings("rawtypes") Map replaceMap, Class<?>  rsh)
			throws SQLException {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = format.format(new Date());
		response.setContentType("Application/msexcel;charset=UTF-8");
		String fileName = /*"工作日历导出详情"*/ desName+dateStr+".xlsx";
		try {
			fileName = new String(fileName.getBytes("GBK"),"iso-8859-1");
		} catch (UnsupportedEncodingException e1) {
			LOGGER.error(e1.getMessage(),e1);
		}
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);  
		response.setContentType("application/vnd.ms-excel");  



		if(rsh==null){
			return;
		}
		List<Field> displayField = GenerateColumns.getDisplayField(rsh);
		if(displayField==null){
			return;
		}
		List<?> listHead =GenerateColumns.getlistHead(displayField);
		Map<?, ?> map =GenerateColumns.getFormatMap(displayField);
		Map<?, ?> mapD =GenerateColumns.getDecimalFormatMap(displayField);

		Workbook wb = new SXSSFWorkbook(500);
		//Workbook workbook = new SXSSFWorkbook(500);//每次缓存500条到内存，其余写到磁盘。
		CellStyle style = ExcelUtil.getCellStyle(wb);
		Sheet sheet = wb.createSheet();
		/**
		 * 设置Excel表的第一行即表头
		 */

		//			BeanProcessor beanProcessor = new BeanProcessor();
		Row row =sheet.createRow(0);
		for(int i=0;i<listHead.size();i++){
			Cell headCell = row.createCell(i);
			headCell.setCellType(Cell.CELL_TYPE_STRING);
			headCell.setCellStyle(style);//设置表头样式
			headCell.setCellValue(String.valueOf(listHead.get(i)));
		}


		if (conn == null) {
			throw new SQLException("Null connection");
		}
		if (sql == null) {
			if (closeConn) {
				DbUtils.close(conn);
			}
			throw new SQLException("Null SQL statement");
		}
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Object result = null;
		AtomicInteger i=new AtomicInteger(0);
		OutputStream  os =null;
		try {
			os=response.getOutputStream();
			BasicRowProcessor basicRowProcessor = new BasicRowProcessor();
			stmt =conn.prepareStatement(sql);
			//    	            this.fillStatement(stmt, params);
			//    	            stmt.getMetaData().
			//    	            stmt.setMaxRows(max);
			//设置结果集大小 根据结果集进行流式处理
			stmt.setFetchSize(Integer.MIN_VALUE);
			rs = stmt.executeQuery();
			//    	            rs.
			//BeanHandler
			// result = rsh.handle(rs);
			if (!rs.next()) {
				return ;
			}

			//    	            PropertyDescriptor[] props =beanProcessor.propertyDescriptors(rsh);
			//    	            ResultSetMetaData rsmd = rs.getMetaData();
			//    	            int[] columnToProperty = beanProcessor.mapColumnsToProperties(rsmd, props);
			Row rowdata=null;
			//    	            rs.getMetaData().getScale(column)
			ExecutorService threadPool = Executors.newFixedThreadPool(5);  
			//    	            CountDownLatch countdownLatch = new CountDownLatch(5);  
			do {

				//    	            	result =beanProcessor.createBean(rs, rsh, props, columnToProperty);
				rowdata = sheet.createRow(i.incrementAndGet());//创建数据行
				result  = basicRowProcessor.toMap(rs,displayField);
				ExportExcelTask exportExcelTask = new ExportExcelTask(rowdata, map, mapD, basicRowProcessor, displayField, result, replaceMap);
				//    	            	result =basicRowProcessor.toMap(rs,displayField);
				threadPool.execute(exportExcelTask);
				////	    	             rowdata = sheet.createRow(i+1);//创建数据行
				//	    				 mapdata = GenerateTitle.getListRow(displayField, map, mapD, replaceMap, result);
				//	    				int j=0;
				//	    				for(String tt:mapdata){
				//	    					 celldata = rowdata.createCell(j);
				//	    					celldata.setCellType(Cell.CELL_TYPE_STRING);
				//	    					if(tt==null){
				//	    						tt="";
				//	    					}
				//	    					celldata.setCellValue(tt);
				//	    					j++;
				//	    				}	

				//	    				mapdata.clear();

				//	    					wb.write(os);

			} while (rs.next());
			//    	            sleep(200);
			threadPool.shutdown();
			//    	            Thread.currentThread().
			//    	            countdownLatch.await();

		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);

		} finally {
			try {
				DbUtils.close(rs);
			} finally {
				DbUtils.close(stmt);
				if (closeConn) {
					DbUtils.close(conn);
				}
			}
		}
		try {
			if(os!=null){
				wb.write(os);
				os.flush();
			}
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);
		}

	}


}
