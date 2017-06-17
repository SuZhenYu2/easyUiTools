package com.easyuitools.common.entity.view;

import java.util.List;

public class Tree {
	    private String id;  //人员编号  
	    private String text; //人员名称  
	    private Boolean checked = false; //是否选中  
	    private List<Tree> children; //子节点  
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public Boolean getChecked() {
			return checked;
		}
		public void setChecked(Boolean checked) {
			this.checked = checked;
		}
		public List<Tree> getChildren() {
			return children;
		}
		public void setChildren(List<Tree> children) {
			this.children = children;
		}
	    
}
