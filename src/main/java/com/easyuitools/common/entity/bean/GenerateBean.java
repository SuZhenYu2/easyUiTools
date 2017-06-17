package com.easyuitools.common.entity.bean;

import java.util.ArrayList;
import java.util.List;

public class GenerateBean  {
	List<String> listHead =new ArrayList<>();
	List listBody =new ArrayList();
	 
	public List<?> getListHead() {
		return listHead;
	}
	public void setListHead(List<String> listHead) {
		this.listHead = listHead;
	}
	public List getListBody() {
		return listBody;
	}
	public void setListBody(List listBody) {
		this.listBody = listBody;
	}
	 
	 
	
}
