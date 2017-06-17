package com.easyuitools.common.pdf.pagesetting;


import com.easyuitools.common.pdf.pdfpag.PdfFooter;
import com.easyuitools.common.pdf.pdfpag.PdfHeader;
import com.easyuitools.common.pdf.pdfpag.PdfPageSize;
import com.lowagie.text.Rectangle;
/**
 * 页面设置
 * 此类提供很多默认设置
 * @author newapps
 *	2009-11-12
 */
public class PageSetting {
	/**设置页脚*/
	private PdfFooter footer=new PdfFooter();
	/**设置页眉*/
	private PdfHeader header=new PdfHeader();
	/**设置页面大小*/
	private Rectangle pageSize=PdfPageSize.A4;
	/**获得pdf的页脚*/
	public PdfFooter getFooter() {
		return footer;
	}
	/**设置pdf的页脚*/
	public void setFooter(PdfFooter footer) {
		this.footer = footer;
	}
	/**获得pdf的页眉*/
	public PdfHeader getHeader() {
		return header;
	}
	/**设置pdf的页眉*/
	public void setHeader(PdfHeader header) {
		this.header = header;
	}
	/**获得页面纸张大小*/
	public Rectangle getPageSize() {
		return pageSize;
	}
	/**设置页面纸张大小*/
	public void setPageSize(Rectangle pageSize) {
		this.pageSize = pageSize;
	}

	
}
