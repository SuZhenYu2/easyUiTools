package com.easyuitools.common.pdf.core;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jxl.Cell;
import jxl.Range;
import jxl.Sheet;

import com.lowagie.text.Image;

/**
 * 操作工作簿的类
 * @author newapps
 * 2009-11-8
 */
public class ExcelSheet{
	private static final Logger LOGGER = Logger.getLogger(ExcelSheet.class);	

	/**工作簿列数*/
	private int cols;
	/**工作簿所有列的宽度*/
	private int[] cols_Width;
	/**工作簿行数*/
	private int rows;
	/**工作簿所有行的高度*/
	private int[] rows_Height;
	/**工作簿中图片*/
	private List images=new ArrayList();
	/**所属工作簿*/
	private Sheet sheet;
	/**合并单元格的范围*/
	private Range[] ranges;
	/**工作簿名称*/
	private String sheetName;
	/**左上角的坐标*/
	private Map mergeCell;
	/**所有合并单元格所占的格数*/
	private int numberCells;
	
	public int getNumberCells() {
		return numberCells;
	}
	public void setNumberCells(int numberCells) {
		this.numberCells = numberCells;
	}
	public Map getMergeCell() {
		return mergeCell;
	}
	public void setMergeCell(Map mergeCell) {
		this.mergeCell = mergeCell;
	}
	public String getSheetName() {
		return sheetName;
	}
	public int getCols() {
		return cols;
	}
	private void setCols(int cols) {
		this.cols = cols;
	}
	public int[] getCols_Width() {
		return cols_Width;
	}
	private void setCols_Width(int[] cols_Width) {
		this.cols_Width = cols_Width;
	}
	public List getImages() {
		return images;
	}
	private void setImages(List images) {
		this.images = images;
	}
	public Range[] getRanges() {
		return ranges;
	}
	private void setRanges(Range[] ranges) {
		this.ranges = ranges;
	}
	public int getRows() {
		return rows;
	}
	private void setRows(int rows) {
		this.rows = rows;
	}
	public int[] getRows_Height() {
		return rows_Height;
	}
	private void setRows_Height(int[] rows_Height) {
		this.rows_Height = rows_Height;
	}
	public Sheet getSheet() {
		return sheet;
	}
	private ExcelSheet(){
		
	}
	public ExcelSheet(Sheet sheet){
		this.sheet=sheet;
		readSheet();
	}
	/**
	 * 设置工作簿属性
	 *
	 */
	private void readSheet(){
		this.setCols(sheet.getColumns());
		this.setRows(sheet.getRows());
		this.setCols_Width(colsWidth());
		this.setRows_Height(rowsHeight());
		this.setImages(find_Image());
		this.setRanges(sheet.getMergedCells());
		this.sheetName=sheet.getName();
		this.mergeCell=getMCell();
		this.setNumberCells(getCells());
	}
	/**
	 * 给列宽数组赋值
	 * @return Int的数组
	 */
	private int[] colsWidth(){
		cols_Width=new int[this.getCols()];
		for(int i=0;i<sheet.getColumns();i++){
			cols_Width[i]= sheet.getColumnView(i).getSize()*2;
		}
		return cols_Width;
	}
	/**
	 * 给行高的数组赋值
	 * @return Int的数组
	 */
	private int[] rowsHeight(){
		rows_Height=new int[this.getRows()];
		for(int i=0;i<sheet.getRows();i++){
			rows_Height[i]=sheet.getRowView(i).getSize();
		}
		return rows_Height;
	}
	/**
	 * 读取当前个工作簿中图片
	 * @return List的数组
	 */
	private List find_Image(){
		for(int i=0;i<sheet.getNumberOfImages();i++){
			jxl.Image jxlImage=sheet.getDrawing(i);
			Image image;
			try {
				image = Image.getInstance(jxlImage.getImageData());
				images.add(image);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return images;
	}
	private Map getMCell(){
		Map map=new HashMap();
		if(sheet.getMergedCells()==null){
			return null;
		}
		Range range[]=sheet.getMergedCells();
		for(int i=0;i<range.length;i++){
			int row=range[i].getTopLeft().getRow();
			int col=range[i].getTopLeft().getColumn();
			Cell bcell= range[i].getBottomRight();
			map.put(row+","+col,bcell);
		}
		return map;
	}
	private int getCells(){
		int number=0;
		if(sheet.getMergedCells()==null){
			return 0;
		}
		Range range[]=sheet.getMergedCells();
		for(int i=0;i<range.length;i++){
			int rows=range[i].getBottomRight().getRow()-range[i].getTopLeft().getRow()+1;
			int cols=range[i].getBottomRight().getColumn()-range[i].getTopLeft().getColumn()+1;
			number+=rows*cols;
		}
		return number;
	}
}
