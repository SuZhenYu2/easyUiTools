package com.easyuitools.common.gennerate;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;

import com.alibaba.fastjson.JSONObject;
import com.easyuitools.common.annotation.Search;
import com.easyuitools.common.listener.BtcpContextListener;
import com.easyuitools.util.StringUtils;

public class GenerateSearch {
	private static final Logger LOGGER = Logger.getLogger(GenerateSearch.class);	

	/**
	 * 
	 * @param cls 用来生成search框的注解 Search后的类
	 * @return list<Map> 返回的是
	 */
	public static List Generate(Class<?> cls) {
		return Generate(cls,null);
	}
	public static List Generate(Class<?> cls,Object obj) {
		
	        ArrayList<Field> fields= new ArrayList<>();
	        Field[] fieldArr=null;
	        for(Class<?> clazz =cls; clazz != Object.class;clazz = clazz.getSuperclass()){
	        	fieldArr =clazz.getDeclaredFields();
	        	 Field.setAccessible(fieldArr,   true);
	        	for(Field field:fieldArr){
	        		fields.add(field);
	        	}
	        }
	        List<Map> list =new ArrayList<Map>();
	        Map<String, Comparable> map =null;
	        String labelName="";
	        String tem=null;
	        boolean isDefalt=true;
	        Object value = null;
	        boolean getFlag=false;
	        if(obj!=null){
	        	getFlag=true;
	        }
	       
//	        Field.setAccessible(fields,   true);
	        for (Field field : fields) {
	        	Search search = field.getAnnotation(Search.class);
	            if (search != null) {
	            	map =new HashMap<String, Comparable>();
	            	labelName=search.labelName();
	            	if(search.redSpan()|| search.required()){
	            		labelName=labelName+"<span style=\"color: red;\">*</span>";
	            	}
	            	labelName=labelName+":";
	            	map.put("labelName",labelName );
	            	map.put("id", "search_"+field.getName());
	            	map.put("name", field.getName());
	            	tem =null;
	            	isDefalt=true;
	            	if(search.datebox()){
	            		tem =add(tem, "easyui-datebox");
	            	
	            		isDefalt=false;
	            	}
	            	if(search.required()){
	            		tem =add(tem,"easyui-validatebox");
	            		map.put("missingMessage", labelName+"是必填项");
	            		isDefalt=false;
	            	}
	            	if(search.combobox()){
	            		tem =add(tem,"easyui-combobox");
	            		isDefalt=false;
	            	}
	            	if(isDefalt){
	            		tem=search.Class();
	            	}
	            	map.put("Class", tem);
	            	map.put("type", search.type());
	            	
	            	tem = search.dataOptions();
	            	if(search.required()){
	            		tem=add(tem,"required:true"," ,");
	            	}
	            	if(!"".equals(search.url())){
	            		
	            		tem=add(tem ,"url:'"+BtcpContextListener.getWebroot()+search.url()+"'"," ,");
	            	}
	            	if(search.combobox()){
	            		tem=add(tem ,"valueField:'id', textField:'text'" ," ,");
	            	}
	            	if(!search.initValue().equals("") ||value!=null){
	            		String initValue=search.initValue();
	            		if(value!=null){
	            			initValue=value.toString();
	            		}
	            		if(search.datebox()){
	            			if(StringUtils.isNumeric(search.initValue())){
	            				int day = (int) Double.parseDouble(search.initValue());
	            				Date date = new Date();
	            				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            				initValue = sdf.format(new Date(date.getTime()-(long)86400000*day));
	            			}
	            			map.put("initValue",initValue );
	            		}
	            	}
	            	if(!"".equals(tem)){
	            		map.put("dataOptions", tem);
	            	}
	            	if(!"".equals(search.style())){
	            		map.put("style", search.style());
	            	}
	            	 try {
	            		 if(getFlag){
	            			 value = field.get(obj);
	            		 }
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						LOGGER.error(e.getMessage(), e);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						LOGGER.error(e.getMessage(), e);
					}
	            	if(search.hidden()){
	            		map.put("hidden", search.hidden());
	            	}
	            
	            	list.add(map);
	            }
	        }
			return list;
	}
	/**
	 * 输入参数为：
	 * @param  model ui对象
	 * @param  search 对应的class
	 * @param  head 表头对应的注解后的class
	 * @returns  该方法返回参数为： searchList（list对象）
	 *  				showColums（json） 均放置到model对象中 用来生成页面 search 域和 对应的展示区域
	 *
	 */
	public static void generateUI(Model model,Class<?> search,Class<?> head){
		if(search!=null){
			model.addAttribute("searchList", Generate(search));
		}
		if(head!=null){
			model.addAttribute("showColumns", JSONObject.toJSONString(GenerateColumns.Generate(head)));
		}
	}
	public static void generateUI(Model model,Class<?> search,Object obj,Class<?> head){
		if(search!=null){
			model.addAttribute("searchList", Generate(search,obj));
		}
		if(head!=null){
			model.addAttribute("showColumns", JSONObject.toJSONString(GenerateColumns.Generate(head)));
		}
	}
	private static String add(String tem ,String add,String sp){
		if(tem!=null){
			if(!tem.contains(add)){
				if("".equals(tem.trim())){
		    		return add;	
		    	}
			    if(sp==null){
			    	tem =tem+" ";
			    }else{
			    	
			    	tem =tem+sp;
			    }
				tem=tem+add;
			}
		}else{
			tem =add;
		}
		return tem;
	}
	private static String add(String tem ,String add){
		return add(tem, add, null);
	}


}
