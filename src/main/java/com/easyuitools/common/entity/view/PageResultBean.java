package com.easyuitools.common.entity.view;

import java.util.ArrayList;
import java.util.List;
/**
 * 该bean主要用来生成返回的页面list 及total信息
 * @author suzy2
 *
 */
public class PageResultBean<T> {
	
   private String total="0";
   private List<T> rows = new ArrayList<T>();
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public void setTotal(long total) {
		this.total = total+"";
	}
	public void setTotal(int total) {
		this.total = total+"";
	}
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
   
}
