package com.easyuitools.common.pdf.core;

import java.awt.Color;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.easyuitools.common.pdf.pdfevent.PageEvent;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;

import jxl.Cell;
import jxl.CellType;
import jxl.Range;
import jxl.Sheet;
import jxl.format.Alignment;
import jxl.format.BoldStyle;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.RGB;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;


public class Convert extends WriterPdf{
	private static final Logger LOGGER = Logger.getLogger(Convert.class);	

	/**pdf表格*/
	private Table table=null;
	/** 当 Excel 的 border 是 NONE 是，pdf 的 border 是否是 0 */
	private boolean noEmptyBorder = false;
	/**工作簿数组*/
	private Sheet[] sheets=null;
	/**工作簿*/
	private Excel excel=null;
	/**页眉*/
	private HeaderText header;
	/**最后一个工作簿图片*/
	private SheetImage sheetImage;
	/**获得页面对象*/
	public HeaderText getHeader() {
		return header;
	}
	/**
	 * 读入一个excel文件获得一个pdf文件输出流
	 * @param filePath excel文件路径
	 * @param output pdf输出流
	 */
	public Convert(String filePath,OutputStream output) {
		super(output);
		readExcel(filePath);
	}
	/**
	 * 将指定路径一个excel文件转化为指定路径的pdf文件
	 * @param filePath excel文件路径
	 * @param destFilePath pdf文件路径
	 */
	public Convert(String filePath,String destFilePath){
		super(destFilePath);
		readExcel(filePath);
	}
	/**
	 * 从数据库中读入一个excel文件的输入转化为一个输出流
	 * @param input excel文件的输入流
	 * @param output excel文件输出流
	 */
	public Convert(InputStream input,OutputStream output){
		super(output);
		readExcel(input);
	}
	/**
	 * 读取excel的方法
	 * @param obj Object对象
	 */
	private void readExcel(Object obj){
		excel=new Excel();
		if(obj instanceof String){
			String filePath=(String)obj;
			excel.readExcel(filePath);//从输入流中读入excel文件
		}else if(obj instanceof InputStream ){
			InputStream input=(InputStream)obj;
			excel.readExcelFromDB(input);
		}
		sheets=excel.getSheets();//获得excel文件工作簿数
		LOGGER.info("->sheet的个数为:"+sheets.length);
		int length=sheets.length-1;
		ExcelSheet image_Sheet=new ExcelSheet(sheets[length]);
		List<?> imageList=image_Sheet.getImages();
		sheetImage=new SheetImage();
		sheetImage.setImage(imageList);
		try {
			header=readHeader(sheets[length]);
		} catch (Exception e) {
			LOGGER.error("->读取页眉出错",e);
		}
	}
	/**
	 * 转化的核心方法
	 * @param pageEvent 页面事件
	 * @throws Exception
	 */
	public void convert(PageEvent pageEvent)throws Exception{
		noEmptyBorder = true;
		writer.setPageEvent(pageEvent);//设置页面事件
		document.setPageSize(pageSize);//设置pdf页面的大小
		document.open();//打开document对象
		LOGGER.info("->document已经打开");
		if(sheets==null||sheets.length<0){
			LOGGER.info("->excel文件中没有工作簿");
			return;
		}
		for(int i=0;i<sheets.length-1;i++){
			int colswidth[]=colsWidth(sheets[i]);
			int rows=sheets[i].getRows();
			int cols=sheets[i].getColumns();
			Map<Integer, String> merge=new HashMap<Integer, String>();//存放合并单元格的左上角的坐标和在list中的下标位置
			List<Integer> subs=new ArrayList<Integer>();//存放不包括左上角下标位置的已合并单元格的位置
			Range[] range=sheets[i].getMergedCells();//得到工作簿的合并单元格的数组
			for(int k=0;k<range.length;k++){
				int row=range[k].getTopLeft().getRow();
				int col=range[k].getTopLeft().getColumn();
				int index=row*cols+col+1;
				int r=range[k].getBottomRight().getRow()-range[k].getTopLeft().getRow()+1;
				int c=range[k].getBottomRight().getColumn()-range[k].getTopLeft().getColumn()+1;
				for(int m=row;m<=range[k].getBottomRight().getRow();m++){
					for(int n=col;n<=range[k].getBottomRight().getColumn();n++){
						if(m!=row||n!=col){
							int xy=m*cols+n+1;
							subs.add(Integer.valueOf(xy));
						}
					}
				}

				String key=r+","+c;
				merge.put(Integer.valueOf(index),key);
			}
			if(cols>0){
				List<Cell> cells=new ArrayList<Cell>();
				table=new Table(cols);//创建含有cols列的表格
				table.setWidths(colswidth);//设置每列的大小
				table.setPadding(2.0f);//设置填充间隔
				table.setSpacing(0.0f);//设置单元格之间距离
				table.setWidth(100.0f);//设置表格的宽度百分比
				table.setBorder(0);//设置表格的边框
				table.setOffset(30.0f);//设置表与表之间的偏移量
				cells=getSheetCell(sheets[i],rows,cols);
				for(int p=0;p<cells.size();p++){
					if(!findIndex(p,subs)){
						if(merge.containsKey(Integer.valueOf(p+1))){
							com.lowagie.text.Cell pcell=null;
							Phrase phrase = null;
							Cell jxlcell=(Cell) cells.get(p);
							CellFormat format = jxlcell.getCellFormat();//取得单元格的格式
							Font font = null;
							if(format != null && format.getFont() != null) {
								font = convertFont(format.getFont());// 调用convertFont()的方法转变字体
							}else{
								font = new Font(Font.COURIER, 10.0f, Font.NORMAL, Color.BLACK);
							}
							String content=jxlcell.getContents();
							String key=(String) merge.get(Integer.valueOf(p+10));
							String []s=key.split(",");
							int r=Integer.parseInt(s[0]);//合并单元格左上角的行数
							int c=Integer.parseInt(s[1]);//合并单元格左上角的列数
							phrase=new Phrase(content,font);
							pcell=new com.lowagie.text.Cell();
							pcell.addElement(phrase);
							if(r>1){
								pcell.setRowspan(r);//对行进行合并
							}
							if(c>1){
								pcell.setColspan(c);//对列进行合并
							}
							transferFormat(pcell, jxlcell,p,cols,cells);//将jxl中的cell转化为pdf的cell
							pcell.setBorderWidthRight(2.0f);
							table.addCell(pcell);
						}else{
							com.lowagie.text.Cell pcell=null;
							Phrase phrase = null;
							Cell jxlcell=(Cell) cells.get(p);
							CellFormat format = jxlcell.getCellFormat();//取得单元格的格式
							Font font = null;
							if(format != null && format.getFont() != null) {
								font = convertFont(format.getFont());// 调用convertFont()的方法转变字体
							}else{
								font = new Font(Font.COURIER, 10.0f, Font.NORMAL, Color.BLACK);
							}
							String content=jxlcell.getContents();
							phrase=new Phrase(content,font);
							pcell=new com.lowagie.text.Cell();
							pcell.addElement(phrase);
							transferFormat(pcell, jxlcell,p,cols,cells);//将jxl中的cell转化为pdf的cell
							table.addCell(pcell);
							//			            } 
						}
					}
				}
				document.add(table);//将table对象添加到文档对象中去
				cells.clear();//清空cell单元格
			}

		}
		CloseDocument();
		excel.closeWorkbook();
	}
	/**
	 * 获得工作簿中所有列宽的数组
	 * @param sheet 工作簿
	 * @return 列宽数组
	 */
	private int[] colsWidth(Sheet sheet){
		int width[]=new int[sheet.getColumns()];
		for(int i=0;i<width.length;i++){
			width[i]=sheet.getColumnView(i).getSize();
		}
		return width;
	}
	/**
	 * 将当前工作簿中的所有单元格添加到List集合中
	 * @param sheet 工作簿
	 * @param rows  工作簿行数
	 * @param cols  工作簿列数
	 */
	private List<Cell> getSheetCell(Sheet sheet,int rows,int cols){
		List<Cell> cells=new ArrayList<Cell>();
		for(int i=0;i<rows;i++){
			for(int j=0;j<cols;j++){
				cells.add(sheet.getCell(j,i));
			}
		}
		return cells;
	}
	/**
	 * 查找已合并后单元格下标
	 * @param i 下标位置
	 * @param subs 合并单元格的下标集合
	 * @return 是否找到
	 */
	private boolean findIndex(int i,List<Integer> subs){
		for(int n = 0; n < subs.size(); n++){
			if(n==i+1){
				return true;
			}
		}
		return false;
	}
	/**
	 * 转换字体
	 * @param f - 字体
	 * @return
	 */
	private com.lowagie.text.Font convertFont(jxl.format.Font f) {
		if (f == null || f.getName() == null)
			return FontFactory.getFont(FontFactory.COURIER, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

		int fontStyle = convertFontStyle(f);
		com.lowagie.text.Font font = null;
		Color fontColor = convertColour(f.getColour(), Color.BLACK);
		if (ChineseFont.BASE_CHINESE_FONT != null && ChineseFont.containsChinese(f.getName())) {
			font = new Font(ChineseFont.BASE_CHINESE_FONT, f.getPointSize(), fontStyle, fontColor);
		} else {
			String s = f.getName().toLowerCase();
			int fontFamily;
			if (s.indexOf("courier") >= 0) //"courier new".equals(s) || "courier".equals(s))
				fontFamily = Font.COURIER;
			else if (s.indexOf("times") >= 0)
				fontFamily = Font.TIMES_ROMAN;
			else
				fontFamily = Font.HELVETICA;

			font = new Font(fontFamily, f.getPointSize(), fontStyle, fontColor);

		}

		return font;
	}
	/**
	 * 颜色转换
	 * @param c
	 * @param defaultColor
	 * @return
	 */
	private Color convertColour(Colour c, Color defaultColor) {
		if (defaultColor == null)
			defaultColor = Color.WHITE;

		if (c == null)
			return defaultColor;

		if (c == Colour.AUTOMATIC) // Excel中的自动(前景色)
			return Color.BLACK;
		// return new Color(Colour.AUTOMATIC.getDefaultRGB().getRed(),Colour.AUTOMATIC.getDefaultRGB().getGreen(),Colour.AUTOMATIC.getDefaultRGB().getBlue());
		else if (c == Colour.DEFAULT_BACKGROUND) // Excel中的自动(底色)
			return Color.white;
		//  return new Color(Colour.DEFAULT_BACKGROUND.getDefaultRGB().getRed(),Colour.DEFAULT_BACKGROUND.getDefaultRGB().getGreen(),Colour.DEFAULT_BACKGROUND.getDefaultRGB().getBlue());

		RGB rgb = c.getDefaultRGB();
		return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
	}
	/**
	 * 转换字体样式
	 * @param font - 字体
	 * @return
	 */
	private int convertFontStyle(jxl.format.Font font) {

		int result = com.lowagie.text.Font.NORMAL;
		if (font.isItalic())
			result |= com.lowagie.text.Font.ITALIC;

		if (font.isStruckout())
			result |= com.lowagie.text.Font.STRIKETHRU;

		if (font.getBoldWeight() == BoldStyle.BOLD.getValue())
			result |= com.lowagie.text.Font.BOLD;

		if (font.getUnderlineStyle() != null) {
			// 下划线
			UnderlineStyle style = font.getUnderlineStyle();
			if (style.getValue() != UnderlineStyle.NO_UNDERLINE.getValue())
				result |= com.lowagie.text.Font.UNDERLINE;
		}
		return result;
	}
	/**
	 * 转换单元格的格式 PdfPCell
	 * @param pdfCell
	 * @param cell
	 * @param mergeRow
	 */
	private void transferFormat2(PdfPCell pcell,Cell cell){
		jxl.format.CellFormat format = cell.getCellFormat();
		if (format != null) {
			// 水平对齐
			pcell.setHorizontalAlignment(convertAlignment(format.getAlignment(), cell.getType()));
			// 垂直对齐
			pcell.setVerticalAlignment(convertVerticalAlignment(format.getVerticalAlignment()));
			// 背景
			// pcell.setBorderWidthBottom(1.0f);
			//pcell.setBorderWidthRight(1.0f);

			// 处理 border
			BorderLineStyle lineStyle = null;

			lineStyle = format.getBorderLine(jxl.format.Border.BOTTOM);
			if (lineStyle.getValue() == BorderLineStyle.NONE.getValue()){
				pcell.setBorderColorBottom(Color.WHITE);
				pcell.setBorderWidthBottom(0.0f);
			}
			else{
				pcell.setBorderColorBottom(convertColour(format.getBorderColour(jxl.format.Border.BOTTOM), Color.BLACK));
				pcell.setBorderWidthBottom(convertBorderStyle(lineStyle));
			}
			//            lineStyle = format.getBorderLine(jxl.format.Border.TOP);
			//           
			//            if (lineStyle.getValue() == BorderLineStyle.NONE.getValue()){
			//                pcell.setBorderColorTop(Color.WHITE);
			//                pcell.setBorderWidthTop(0.0f);
			//            }
			//            else{
			//                pcell.setBorderColorTop(convertColour(format.getBorderColour(jxl.format.Border.TOP), Color.BLACK));
			//                pcell.setBorderWidthTop(convertBorderStyle(lineStyle));//
			//            }
			//            lineStyle = format.getBorderLine(jxl.format.Border.LEFT);
			//            //convertBorderStyle(lineStyle)
			//            if (lineStyle.getValue() == BorderLineStyle.NONE.getValue()){
			//                pcell.setBorderColorLeft(Color.WHITE);
			//                pcell.setBorderWidthLeft(0.0f);
			//            }
			//            else{
			//                pcell.setBorderColorLeft(convertColour(format.getBorderColour(jxl.format.Border.LEFT), Color.BLACK));
			//                pcell.setBorderWidthLeft(convertBorderStyle(lineStyle));
			//            }
			lineStyle = format.getBorderLine(jxl.format.Border.RIGHT);

			if (lineStyle.getValue() == BorderLineStyle.NONE.getValue()){
				pcell.setBorderColorRight(Color.WHITE);
				pcell.setBorderWidthRight(0.0f);
			}
			else{
				pcell.setBorderColorRight(convertColour(format.getBorderColour(jxl.format.Border.RIGHT), Color.BLACK));
				pcell.setBorderWidthRight(convertBorderStyle(lineStyle));
			}
			if (format.getBackgroundColour().getValue() != Colour.DEFAULT_BACKGROUND.getValue()) {
				pcell.setBackgroundColor(convertColour(format.getBackgroundColour(), Color.WHITE));
			}
		}else{
			pcell.setBorder(0);
		}

	}
	/**
	 * 转换单元格的格式 com.lowagie.text.Cell
	 * @param pdfCell
	 * @param cell
	 * @param mergeRow
	 */
	private void transferFormat(com.lowagie.text.Cell pcell, Cell cell,int index,int cols,List<Cell> cells) {
		jxl.format.CellFormat format = cell.getCellFormat();
		if (format != null) {
			// 水平对齐
			pcell.setHorizontalAlignment(convertAlignment(format.getAlignment(), cell.getType()));
			// 垂直对齐
			pcell.setVerticalAlignment(convertVerticalAlignment(format.getVerticalAlignment()));
			// 背景
			// 处理 border
			boolean left=false,top=false,right=false,bottom=false;
			if(index==0){
				top=true;
				left=true;
				right=true;
				bottom=true;
			}else if(index<cols){
				//            	if(cellLeftBorderLineStyle(cells.get(0))&&cellRightBorderLineStyle(cells.get(1))){
				//            		top=true;
				//	            	left=true;
				//	            	right=true;
				//	            	bottom=true;
				//            	}else{
				top=true;
				left=false;
				right=true;
				bottom=true;
				//            	}
			}else if(index%cols==0){
				top=false;
				left=true;
				right=true;
				bottom=true;
			}else if(cellBottomBorderLineStyle((Cell)cells.get(index-cols))&&cellLeftBorderLineStyle((Cell)cells.get(index-1))){
				top=true;
				left=true;
				right=true;
				bottom=true;
			}else if(cellBottomBorderLineStyle((Cell)cells.get(index-cols))){
				top=true;
				left=false;
				right=true;
				bottom=true;
			}else if(cellLeftBorderLineStyle((Cell)cells.get(index-1))){
				top=false;
				left=true;
				right=true;
				bottom=true;
			}else{
				top=false;
				left=false;
				right=true;
				bottom=true;
			}
			BorderLineStyle lineStyle = null;
			lineStyle = format.getBorderLine(jxl.format.Border.BOTTOM);
			if (lineStyle.getValue() == BorderLineStyle.NONE.getValue()){
				pcell.setBorderColorBottom(Color.WHITE);
				pcell.setBorderWidthBottom(0.0f);
			}
			else if(bottom){
				pcell.setBorderColorBottom(convertColour(format.getBorderColour(jxl.format.Border.BOTTOM), Color.BLACK));
				pcell.setBorderWidthBottom(convertBorderStyle(lineStyle));
			}
			lineStyle = format.getBorderLine(jxl.format.Border.TOP);

			if (lineStyle.getValue() == BorderLineStyle.NONE.getValue()){
				pcell.setBorderColorTop(Color.WHITE);
				pcell.setBorderWidthTop(0.0f);
			}
			else if(top){
				pcell.setBorderColorTop(convertColour(format.getBorderColour(jxl.format.Border.TOP), Color.BLACK));
				pcell.setBorderWidthTop(convertBorderStyle(lineStyle));//
			}
			lineStyle = format.getBorderLine(jxl.format.Border.LEFT);
			//convertBorderStyle(lineStyle)
			if (lineStyle.getValue() == BorderLineStyle.NONE.getValue()){
				pcell.setBorderColorLeft(Color.WHITE);
				pcell.setBorderWidthLeft(0.0f);
			}
			else if(left){
				pcell.setBorderColorLeft(convertColour(format.getBorderColour(jxl.format.Border.LEFT), Color.BLACK));
				pcell.setBorderWidthLeft(convertBorderStyle(lineStyle));
			}
			lineStyle = format.getBorderLine(jxl.format.Border.RIGHT);

			if (lineStyle.getValue() == BorderLineStyle.NONE.getValue()){
				pcell.setBorderColorRight(Color.WHITE);
				pcell.setBorderWidthRight(0.0f);
			}
			else if(right){
				pcell.setBorderColorRight(convertColour(format.getBorderColour(jxl.format.Border.RIGHT), Color.BLACK));
				pcell.setBorderWidthRight(convertBorderStyle(lineStyle));
			}
			if (format.getBackgroundColour().getValue() != Colour.DEFAULT_BACKGROUND.getValue()) {
				pcell.setBackgroundColor(convertColour(format.getBackgroundColour(), Color.WHITE));
			}
		}else{
			pcell.setBorder(0);
		}
	}


	/**
	 * 转换边框样式
	 * @param style
	 * @return
	 */
	private float convertBorderStyle(BorderLineStyle style) {
		if (style == null) return 0.0f;

		float w = 0.0f;
		if(BorderLineStyle.HAIR.getValue()==style.getValue()){
			w=0.0f;
		}else if(BorderLineStyle.NONE.getValue() == style.getValue()) {
			// 默认全部使用边框，边框大小 0.5f
			if (noEmptyBorder){
				w = 0.0f;
			}
		}else if(BorderLineStyle.THIN.getValue() == style.getValue()){
			w = 1.0f;
		}else if (BorderLineStyle.THICK.getValue() == style.getValue()) {
			w = 1.5f;
		}else if (BorderLineStyle.MEDIUM.getValue() == style.getValue()) {
			w = 1.0f;
		}else if(BorderLineStyle.DOUBLE.getValue()==style.getValue()){
			w = 1.5f;
		}else {
			w = 0.0f;
		}
		return w;
	}
	/**
	 * 转换对齐方式
	 * @param align
	 * @param cellType
	 * @return int 
	 */
	private int convertAlignment(Alignment align, CellType cellType) {
		if (align == null)
			return Element.ALIGN_UNDEFINED;

		if (Alignment.CENTRE.getValue() == align.getValue())
			return  Element.ALIGN_CENTER;

		if (Alignment.LEFT.getValue() == align.getValue())
			return Element.ALIGN_LEFT;

		if (Alignment.RIGHT.getValue() == align.getValue())
			return Element.ALIGN_RIGHT;

		if (Alignment.JUSTIFY.getValue() == align.getValue())
			return Element.ALIGN_JUSTIFIED;

		if (Alignment.GENERAL.getValue() == align.getValue()) {
			// 所有未明确设置对齐方式的元素，都属于 Alignment.GENERAL 类型
			if (cellType == CellType.NUMBER || cellType == CellType.NUMBER_FORMULA)
				return Element.ALIGN_RIGHT;   // 数字右对齐
			if (cellType == CellType.DATE || cellType == CellType.DATE_FORMULA)
				return Element.ALIGN_RIGHT;   // 日期右对齐
		}
		return Element.ALIGN_UNDEFINED;
	}
	/**
	 * 转换垂直对齐方式
	 * @param align - jxl 的对齐方式
	 * @return int
	 */
	private int convertVerticalAlignment(VerticalAlignment align) {
		if (align == null)
			return Element.ALIGN_UNDEFINED;

		if (VerticalAlignment.BOTTOM.getValue() == align.getValue())
			return Element.ALIGN_BOTTOM;

		if (VerticalAlignment.CENTRE.getValue() == align.getValue())
			return Element.ALIGN_MIDDLE;

		if (VerticalAlignment.TOP.getValue() == align.getValue())
			return Element.ALIGN_TOP;

		if (VerticalAlignment.JUSTIFY.getValue() == align.getValue())
			return Element.ALIGN_JUSTIFIED;

		return Element.ALIGN_UNDEFINED;
	}
	/**
	 * 读取页眉信息
	 * @param sheet
	 * @return HeaderText
	 */
	private HeaderText readHeader(Sheet sheet){
		int addNum=0,imageNums=0;
		if(sheetImage.getImage()!=null){
			imageNums=sheetImage.getImage().size();
			LOGGER.info("最后一个sheet 含有图片的数量："+imageNums);
		}
		HeaderText text=new HeaderText();
		PdfPCell pdfCell = null;
		Phrase content = null;
		if(sheet!=null&&sheet.getColumns()>0){
			Cell cell1=sheet.getCell(0,1);
			if(cell1!=null&& cell1.getContents().equalsIgnoreCase("t")){
				Cell cell = sheet.getCell(0,0);//取得每一行的单元格
				jxl.format.CellFormat format = cell.getCellFormat();//取得单元格的格式
				Font font = null;
				if (format != null && format.getFont() != null) {
					font = convertFont(format.getFont());// 调用convertFont()的方法转变字体
				} else {
					font = new Font(Font.COURIER, 10.0f, Font.NORMAL, Color.BLACK);
					//font = ChineseFont.createChineseFont(10,Font.NORMAL,Color.BLACK);
				}
				if(cell.getContents()==null){
					text.setHeaderText1(new PdfPCell(new Phrase(" ")));
				}else{
					content = new Phrase(cell.getContents(), font);
					pdfCell = new com.lowagie.text.pdf.PdfPCell(content);
					transferFormat2(pdfCell, cell);
					text.setHeaderText1(pdfCell);
				}
			}else if(cell1!=null&&cell1.getContents().equalsIgnoreCase("i")){
				if(sheetImage.getImage()!=null&&sheetImage.getImage().size()>0){
					pdfCell=new PdfPCell((Image)sheetImage.getImage().get(0));
					pdfCell.setBorder(0);
					text.setHeaderText1(pdfCell);
					addNum++;
				}else{
					text.setHeaderText1(new PdfPCell(new Phrase(" ")));
				}
			}
			Cell cell2=sheet.getCell(1,1);
			if(cell2!=null&& cell2.getContents().equalsIgnoreCase("t")){
				Cell cell = sheet.getCell(1,0);//取得每一行的单元格
				jxl.format.CellFormat format = cell.getCellFormat();//取得单元格的格式
				Font font = null;
				if (format != null && format.getFont() != null) {
					font = convertFont(format.getFont());// 调用convertFont()的方法转变字体
				} else {
					font = new Font(Font.COURIER, 10.0f, Font.NORMAL, Color.BLACK);
					//font = ChineseFont.createChineseFont(10,Font.NORMAL,Color.BLACK);
				}if(cell.getContents()==null){
					text.setHeaderText2(new PdfPCell(new Phrase(" ")));
				}else{
					content = new Phrase(cell.getContents(), font);
					pdfCell = new com.lowagie.text.pdf.PdfPCell(content);
					transferFormat2(pdfCell, cell);
					text.setHeaderText2(pdfCell);
				}
			}else if(cell2!=null&&cell2.getContents().equalsIgnoreCase("i")){
				if(sheetImage.getImage()!=null&&sheetImage.getImage().size()>0){
					if(addNum==0){
						Image image=(Image) sheetImage.getImage().get(0);
						pdfCell=new PdfPCell(image);
						pdfCell.setBorder(0);
						text.setHeaderText2(pdfCell);
					}else if(addNum==1&&addNum<imageNums){
						pdfCell=new PdfPCell((Image)sheetImage.getImage().get(1));
						pdfCell.setBorder(0);
						text.setHeaderText2(pdfCell);
					}
					addNum++;
				}else{
					text.setHeaderText2(new PdfPCell(new Phrase(" "))); 
				}
			}
			Cell cell3=sheet.getCell(2,1);
			if(cell3!=null&& cell3.getContents().equalsIgnoreCase("t")){
				//text.setHeaderText3(sheet.getCell(2,0).getContents());
				Cell cell = sheet.getCell(2,0);//取得每一行的单元格
				jxl.format.CellFormat format = cell.getCellFormat();//取得单元格的格式
				Font font = null;
				if (format != null && format.getFont() != null) {
					font = convertFont(format.getFont());// 调用convertFont()的方法转变字体
				} else {
					font = new Font(Font.COURIER, 10.0f, Font.NORMAL, Color.BLACK);
					//font = ChineseFont.createChineseFont(10,Font.NORMAL,Color.BLACK);
				}if(cell.getContents()==null){
					text.setHeaderText3(new PdfPCell(new Phrase(" ")));
				}else{
					content = new Phrase(cell.getContents(), font);
					pdfCell = new com.lowagie.text.pdf.PdfPCell(content);
					transferFormat2(pdfCell, cell);
					text.setHeaderText3(pdfCell);
				}
			}else if(cell3!=null&&cell3.getContents().equalsIgnoreCase("i")){
				if(sheetImage.getImage()!=null&&sheetImage.getImage().size()>0){
					if(addNum==0){
						pdfCell=new PdfPCell((Image)sheetImage.getImage().get(0));
						pdfCell.setBorder(0);
						text.setHeaderText3(pdfCell);
						//text.setHeaderText3(sheetImage.getImage().get(0));
					}else if(addNum==1&&addNum<imageNums){
						pdfCell=new PdfPCell((Image)sheetImage.getImage().get(1));
						pdfCell.setBorder(0);
						text.setHeaderText3(pdfCell);
						//text.setHeaderText3(sheetImage.getImage().get(0+1));
					}else if(addNum==2&&addNum<imageNums){
						pdfCell=new PdfPCell((Image)sheetImage.getImage().get(2));
						pdfCell.setBorder(0);
						text.setHeaderText3(pdfCell);
						// text.setHeaderText3(sheetImage.getImage().get(0+2));
					}
				}else{
					text.setHeaderText3(new PdfPCell(new Phrase(" ")));
				}
			}
		}
		return text;
	}
	/***
	 * 判断下边框是否是默认边框
	 * @param cell
	 * @return
	 */
	private boolean cellBottomBorderLineStyle(Cell cell){
		boolean b=false;
		jxl.format.CellFormat format = cell.getCellFormat();
		if(format!=null){
			BorderLineStyle lineStyle = null;
			lineStyle = format.getBorderLine(jxl.format.Border.BOTTOM);
			if (lineStyle.getValue() == BorderLineStyle.NONE.getValue()){
				b=true;
			}
		}
		return b;
	}
	/**
	 * 判断左边框是否是默认边框
	 * @param cell
	 * @return
	 */
	private boolean cellLeftBorderLineStyle(Cell cell){
		boolean b=false;
		jxl.format.CellFormat format = cell.getCellFormat();
		if(format!=null){
			BorderLineStyle lineStyle = null;
			lineStyle = format.getBorderLine(jxl.format.Border.RIGHT);
			if (lineStyle.getValue() == BorderLineStyle.NONE.getValue()){
				b=true;
			}
		}
		return b;
	}
	//	private boolean cellRightBorderLineStyle(Cell cell){
	//		boolean b=false;
	//		jxl.format.CellFormat format = cell.getCellFormat();
	//		if(format!=null){
	//			BorderLineStyle lineStyle = null;
	//            lineStyle = format.getBorderLine(jxl.format.Border.LEFT);
	//            if (lineStyle.getValue()!= BorderLineStyle.NONE.getValue()){
	//              b=true;
	//            }
	//		}
	//		return b;
	//	}
}

