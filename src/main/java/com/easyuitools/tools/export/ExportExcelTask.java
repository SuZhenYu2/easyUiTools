package com.easyuitools.tools.export;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.easyuitools.common.gennerate.GenerateColumns;

public class ExportExcelTask implements Runnable {

	
	  private Row rowdata=null;
	  private List<String> mapdata ;
	  private Cell celldata;
	  private  Map map ;
	  private Map mapD;
	  private Map replaceMap;
	  private List<Field> displayField;
	  private BasicRowProcessor basicRowProcessor;
	  private Object  result;
	  public ExportExcelTask(Row rowdata,
			  Map map,
			  Map mapD ,
			  BasicRowProcessor basicRowProcessor,
			  List<Field> displayField,
			  Object result,Map replaceMap){
		  this.rowdata=rowdata;
		  this.map=map;
		  this.mapD=mapD;
		  this.basicRowProcessor=basicRowProcessor;
		  this.displayField=displayField;
		  this.replaceMap=replaceMap;
		  this.result=result;
	  }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		 mapdata = GenerateColumns.getListRow(displayField, map, mapD, replaceMap, result);
		int j=0;
		for(String tt:mapdata){
			 celldata = rowdata.createCell(j);
			celldata.setCellType(Cell.CELL_TYPE_STRING);
			if(tt==null){
				tt="";
			}
			celldata.setCellValue(tt);
			j++;
		}	
	}

}
