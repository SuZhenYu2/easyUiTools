package com.easyuitools.common.entity.bean;

import java.util.ArrayList;
import java.util.List;

public class GenerateBean  {
	List<String> listHead =new ArrayList<>();
	@SuppressWarnings("rawtypes")
	List listBody =new ArrayList();
	 
	public List<?> getListHead() {
		return listHead;
	}
	public void setListHead(List<String> listHead) {
		this.listHead = listHead;
	}
	@SuppressWarnings("rawtypes")
	public List getListBody() {
		return listBody;
	}
	public void setListBody(@SuppressWarnings("rawtypes") List listBody) {
		this.listBody = listBody;
	}
	 
	 
	
}
