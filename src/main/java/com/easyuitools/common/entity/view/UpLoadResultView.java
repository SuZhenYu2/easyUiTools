package com.easyuitools.common.entity.view;

import java.util.List;

public class UpLoadResultView {
	private List<UpLoadResult> listUpLoad ;
	private String  success ="fasle" ;
	public List<UpLoadResult> getListUpLoad() {
		return listUpLoad;
	}
	public void setListUpLoad(List<UpLoadResult> listUpLoad) {
		this.listUpLoad = listUpLoad;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	
}
