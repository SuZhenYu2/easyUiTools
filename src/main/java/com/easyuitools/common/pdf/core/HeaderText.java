package com.easyuitools.common.pdf.core;
import com.lowagie.text.pdf.PdfPCell;


/**
 * 存取页面信息
 * @author newapps
 *	2009-11-12
 */
public class HeaderText { 
	/**第一列内容*/
	private PdfPCell headerText1;
	/**第二列内容*/
	private PdfPCell headerText2;
	/**第三列内容*/
	private PdfPCell headerText3;
	/**获得第一列内容*/
	public PdfPCell getHeaderText1() {
		return headerText1;
	}
	/**获得第二列内容*/
	public PdfPCell getHeaderText2() {
		return headerText2;
	}
	/**获得第三列内容*/
	public void setHeaderText2(PdfPCell headerText2) {
		headerText2.setBorder(0);
		this.headerText2 = headerText2;
	}
	/**设置第一列内容*/
	public PdfPCell getHeaderText3() {
		return headerText3;
	}
	/**设置第三列内容*/
	public void setHeaderText3(PdfPCell headerText3) {
		headerText3.setBorder(0);
		this.headerText3 = headerText3;
	}
	/**设置第四列内容*/
	public void setHeaderText1(PdfPCell headerText1) {
		headerText1.setBorder(0);
		this.headerText1 = headerText1;
	}
	
	
	
}
