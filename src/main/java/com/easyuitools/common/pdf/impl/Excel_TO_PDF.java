package com.easyuitools.common.pdf.impl;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.easyuitools.common.pdf.Excel2Pdf;
import com.easyuitools.common.pdf.core.Convert;
import com.easyuitools.common.pdf.core.HeaderText;
import com.easyuitools.common.pdf.pagesetting.PageSetting;
import com.easyuitools.common.pdf.pdfevent.PageEvent;
import com.easyuitools.common.pdf.pdfpag.PdfPageSize;


public class Excel_TO_PDF implements Excel2Pdf{

	public void conVertFormLocal(String strExcelFilePath, OutputStream outPut, PageSetting pageSetting) 
				throws Exception 
	{
		Convert con=new Convert(strExcelFilePath,outPut);
		if(pageSetting!=null){
		con.setPageSize(pageSetting.getPageSize());
		HeaderText text=con.getHeader();
		PageEvent event=new PageEvent();
		setUp(event,pageSetting,text);
		con.convert(event);
		}else{
			throw new Exception("页面参数未设置");
		}
		
	}

	public void convertFromDB(String strExcelFileName, OutputStream outPut, PageSetting pageSetting) 
				throws Exception 
	{
//		FileIntoDB db=new FileIntoDB();
//		db.fileIntoDB("D:\\CRM1.xls","crm11");
//		InputStream input=db.getInputStream(excelFileName);
//		Convert con=new Convert(input,output);
//		if(pageSetting!=null){
//		con.setPageSize(pageSetting.getPageSize());
//		HeaderText text=con.getHeader();
//		PageEvent event=new PageEvent();
//		event.setHeaderText(text);
//		event.setFooter(pageSetting.getFooter());
//		event.setHeader(pageSetting.getHeader());
//		event.setPageNumberSize(pageSetting.getFooter().getFontSize());
//		event.setPageNumberStyle(pageSetting.getFooter().getPageNumberStyle());
//		con.transform(event);
//		}else{
//			throw new Exception("页面参数未设置");
//		}
		
	}

	public void convertFromLocal(String strExcelFilePath, String strPdfFilePath, PageSetting pageSetting) 
				throws Exception 
	{
		Convert con=new Convert(strExcelFilePath,strPdfFilePath);
		if(pageSetting!=null){
		con.setPageSize(pageSetting.getPageSize());
		HeaderText text=con.getHeader();
		PageEvent event=new PageEvent();
		setUp(event,pageSetting,text);
		con.convert(event);
		}else{
			throw new Exception("页面参数未设置");
		}
		
	}
	
	/**
	 * 设置PdfEvent事件
	 * @param event 
	 * @param pageSetting
	 * @param text
	 */
	private void setUp(PageEvent event,PageSetting pageSetting,HeaderText text){
		event.setHeaderText(text);
		event.setFooter(pageSetting.getFooter());
		event.setHeader(pageSetting.getHeader());
		event.setPageNumberSize(pageSetting.getFooter().getFontSize());
		event.setPageNumberStyle(pageSetting.getFooter().getPageNumberStyle());
	}
	
/*	public static void main(String[] args) {
		PageSetting set=new PageSetting();
		set.setPageSize(PdfPageSize.A2.rotate());
		Excel2Pdf pdf = new Excel_TO_PDF();
		try {
			pdf.conVertFormLocal("c:/123.xls", new FileOutputStream("c:/123.pdf"), set);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	@Override
	public void conVertFormStream(InputStream input, OutputStream outPut, PageSetting pageSetting)
			throws Exception {
		Convert con=new Convert(input,outPut);
		
		if(pageSetting!=null){
		con.setPageSize(pageSetting.getPageSize());
		HeaderText text=con.getHeader();
		PageEvent event=new PageEvent();
		setUp(event,pageSetting,text);
		con.convert(event);
		}else{
			throw new Exception("页面参数未设置");
		}
		// TODO Auto-generated method stub
		
	}

}
