package com.easyuitools.common.pdf.pdfpag;

import java.awt.Color;

import com.lowagie.text.Element;
/**
 * 设置页脚
 * @author newapps
 * 2009-11-12
 */
public class PdfHeader {
	/**设置页眉的默认大小为12.0f*/
	private static final float DEFAULT_SIZE=12.0f;
	/**设置页眉的对齐方式为中间对齐*/
	public static final int CENTER=Element.ALIGN_CENTER;
	/**设置页眉的对齐方式为左对齐*/
	public static final int LEFT=Element.ALIGN_LEFT;
	/**设置页眉的对齐方式为右对齐*/
	public static final int RIGHT=Element.ALIGN_RIGHT;
	/**页眉内容*/
	private String text=" ";
	/**对齐方式*/
	private int align=RIGHT;
	/** 是否粗体 */
	private boolean bold=false;
	/** 字体大小 */
	private float fontSize = DEFAULT_SIZE;
	/**字体颜色*/
	private Color color=Color.BLACK;
	/**页眉背景色*/
	private Color backGroundColor=Color.white;
	
	public int getAlign() {
		return align;
	}
	public void setAlign(int align) {
		this.align = align;
	}
	public boolean isBold() {
		return bold;
	}
	public void setBold(boolean bold) {
		this.bold = bold;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
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
	public Color getBackGroundColor() {
		return backGroundColor;
	}
	public void setBackGroundColor(Color backGroundColor) {
		this.backGroundColor = backGroundColor;
	}
	
}
