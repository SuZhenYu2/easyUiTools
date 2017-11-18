package com.easyuitools.wx.sendmsg;

import java.util.ArrayList;
import java.util.List;

 
public class SendWxMsgBean {
	private String userId;
	private String openId;
	private String templateId;
	private List<WxMpMsgData> datas = new ArrayList<WxMpMsgData>();
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public List<WxMpMsgData> getDatas() {
		return datas;
	}
	public void setDatas(List<WxMpMsgData> datas) {
		this.datas = datas;
	}
 
	
	
}
