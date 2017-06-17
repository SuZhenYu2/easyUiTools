package com.easyuitools.common.pdf.pdfpag;

import java.awt.Color;
import java.util.Calendar;

import com.lowagie.text.Element;
/**
 * 设置页脚
 * @author newapps
 * 2009-11-12
 */
public class PdfFooter {
	/**设置页脚的默认大小为12.0f*/
	private static final float DEFAULT_SIZE=12.0f;
	/**设置页脚的对齐方式为中间对齐*/
	public static final int CENTER=Element.ALIGN_CENTER;
	/**设置页脚的对齐方式为左对齐*/
	public static final int LEFT=Element.ALIGN_LEFT;
	/**设置页脚的对齐方式为右对齐*/
	public static final int RIGHT=Element.ALIGN_RIGHT;
	/** 页码样式：Page 1 of 10 */
	public static final String STYLE_PAGE_NUMBER_N_OFTOTAL = "Page #N of #T";
	/** 页码样式：- 1 - */
	public static final String STYLE_PAGE_NUMBER_N = "- #N -";
	/** 页码样式：第 1 页 */
	public static final String STYLE_PAGE_NUMBER_N_CH = "第 #N 页";
	/** 页码样式：◇ 1 ◇ */
	public static final String STYLE_PAGE_NUMBER_N_CH2 = "◇ #N ◇";
    /** 页码样式：第 1 页，共 10 页 */
    public static final String STYLE_PAGE_NUMBER_N_OFTOTAL_CH = "第 #N 页，共 #T 页";
	/** 代表样式里的 当前页码 */
	public static final String SIGN_PAGE_NUMBER = "#N";
	/** 代表样式里的 总页码 */
	public static final String SIGN_TOTAL_NUMBER = "#T";
	Calendar cal=Calendar.getInstance();
	
	/**页脚内容*/
	private String text=" ";//cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DATE);
	/**页脚文本对齐方式*/
	private int textAlign=RIGHT;
	
	private int pageNumberAlign=Element.ALIGN_CENTER;

	/** 是否粗体 */
	private boolean bold=false;
	/** 字体大小 */
	private float fontSize = DEFAULT_SIZE;
	/**字体颜色*/
	private Color color=Color.BLACK;
	/**页眉背景色*/
	private Color backGroundColor=Color.white;
	/**是否显示页码 默认显示页码*/
	private boolean showPageNumber=false;
	/**是否显示总的页码 默认显示总页码*/
	private boolean showTotalNumber=false;
	/**是否设置页脚 默认设置*/
	private boolean hasFooter =false;
	/**页码样式*/
	private String pageNumberStyle=STYLE_PAGE_NUMBER_N_CH;
	/**
	 * 设置页码
	 * @return
	 */
	public String getPageNumberStyle() {
		return pageNumberStyle;
	}

	public void setPageNumberStyle(String pageNumberStyle) {
		this.pageNumberStyle = pageNumberStyle;
	}

	public boolean isHasFooter() {
		return hasFooter;
	}

	public void setHasFooter(boolean hasFooter) {
		this.hasFooter = hasFooter;
	}

	public boolean isShowTotalNumber() {
		return showTotalNumber;
	}

	public void setShowTotalNumber(boolean showTotalNumber) {
		this.showTotalNumber = showTotalNumber;
	}

	public boolean isShowPageNumber() {
		return showPageNumber;
	}

	public void setShowPageNumber(boolean showPageNumber) {
		this.showPageNumber = showPageNumber;
	}

	public Color getBackGroundColor() {
		return backGroundColor;
	}

	public void setBackGroundColor(Color backGroundColor) {
		this.backGroundColor = backGroundColor;
	}

	public PdfFooter(){}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}


	public int getPageNumberAlign() {
		return pageNumberAlign;
	}

	public void setPageNumberAlign(int pageNumberAlign) {
		this.pageNumberAlign = pageNumberAlign;
	}

	public int getTextAlign() {
		return textAlign;
	}

	public void setTextAlign(int textAlign) {
		this.textAlign = textAlign;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float fontSize) {
		if(fontSize>4.0f&&fontSize<40.0f){
			this.fontSize = fontSize;
		}
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
	
	
}
