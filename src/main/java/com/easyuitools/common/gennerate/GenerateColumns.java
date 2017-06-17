package com.easyuitools.common.gennerate;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.easyuitools.common.annotation.Display;
import com.easyuitools.common.entity.view.PageResultBean;
import com.easyuitools.util.EhCacheUtils;
import com.easyuitools.util.StringUtils;
import com.github.pagehelper.PageInfo;

public class GenerateColumns {
	private static final Logger LOGGER = Logger.getLogger(GenerateColumns.class);	
  	public static List<List<Map>> Generate(Class<?> cls) {
		Field[] fields= cls.getDeclaredFields();
		List<Map> list =new ArrayList<Map>();
		List<List<Map>> list2 =new ArrayList<List<Map>>();
		Map<String, Comparable> map =null;
		for (Field field : fields) {
			Display display = field.getAnnotation(Display.class);
			if (display != null) {
				map =new HashMap<String, Comparable>();
				map.put("field", field.getName());
				if(display.checkbox()){
					map.put("checkbox", display.checkbox());
				}
				map.put("align", display.align());
				map.put("title", display.title());
				map.put("width", display.width());
				if(display.hidden()){
					map.put("hidden", display.hidden());
				}
				list.add(map);
			}
		}
		list2.add(list);
		return list2;
	}
	
	public static List<Field> getDisplayField(Class<?> cls){
		Field[] fields= cls.getDeclaredFields();
		List<Field> listField =new ArrayList<Field>();
		Field.setAccessible(fields,true);
		for (Field field : fields) {
			Display display = field.getAnnotation(Display.class);
			if (display != null && !display.hidden() && !display.checkbox()) {
				//				listHead.add(display.title());
				listField.add(field);
			}
		}
		return listField;
	}
	public static List<String> getlistHead(List<Field> listField){
		List<String> listHead =new ArrayList<String>();
		for (Field field : listField) {
			Display display = field.getAnnotation(Display.class);
			if (display != null && !display.hidden() && !display.checkbox()) {
				listHead.add(display.title());
			}
		}
		return listHead;
	}
	public static Map<String,SimpleDateFormat> getFormatMap(List<Field> listField){
		Map<String,SimpleDateFormat> map = new HashMap<String,SimpleDateFormat>();
		SimpleDateFormat objFormat;
		for (Field field : listField) {
			Display display = field.getAnnotation(Display.class);
			if (display != null && !display.hidden() && !display.checkbox()) {
				if(!display.format().equals("")){
					//先不做类型区分  默认date类型进行 format
					objFormat =StringUtils.getSimpleDateFormat(display.format());
					map.put(field.getName(), objFormat);
				}
			}
		}
		return map;
	}
	public static Map<String,DecimalFormat> getDecimalFormatMap(List<Field> listField){
		Map<String,DecimalFormat> mapD = new HashMap<String,DecimalFormat>();
		DecimalFormat objFormat;
		for (Field field : listField) {
			Display display = field.getAnnotation(Display.class);
			if (display != null && !display.hidden() && !display.checkbox()) {
				if(!display.DecimalFormat().equals("")){
					//先不做类型区分  默认date类型进行 format
 					objFormat =StringUtils.getDecimalFormat(display.DecimalFormat());
					mapD.put(field.getName(), objFormat);
				}
			}
		}
		return mapD;
	}
	public static List<String> getListRow(List<Field> listField,Map<String,DecimalFormat> map,Map<String,SimpleDateFormat> mapD,Map replaceMap,Object obj){
		if(obj==null){
			return null;
		}
		boolean isMapFlag=false;
		List<String> listRow = new ArrayList<String>();
		if (obj instanceof Map) {
			isMapFlag=true;
		}
		SimpleDateFormat sdf ;
		DecimalFormat dft ;
		Object objTem ;
		Map<?, ?> temMap =null;
		Object value=null;
		for(Field field:listField){
			try{  
				if(isMapFlag){
					temMap =(Map) obj;
					value = temMap.get(field.getName());
				}else{
					value = field.get(obj);
				}
				if(value==null){
					listRow.add(null);
					continue;
				}
				objTem=  map.get(field.getName());
				if(objTem!=null && field.getGenericType().toString().equals("class java.util.Date") ){
					sdf=(SimpleDateFormat) objTem;
					value= sdf.format((Date)value);
				}else{
					objTem=  mapD.get(field.getName());
					if(objTem!=null){
						dft=(DecimalFormat) objTem;
						value= dft.format(new BigDecimal(value+""));
					}
				}
				if(replaceMap!=null){
					temMap= (Map) replaceMap.get(field.getName());
					if(temMap!=null){
						objTem= temMap.get(value+"");
						if(objTem!=null){
							value =objTem;
						}
					}
				}
				listRow.add(value+"");
			}catch(Exception e){  
				listRow.add(null);
				LOGGER.error(e.getMessage(), e);
			}  
		}
		return listRow;
	}
	public static void GenerateExcelBySql(Class<?> cls,List listBean,Map beans,Map replaceMap) {


	}
	/**
	 * 
	 * @param cls 需要生成 列表数据的bean.class  

	 * @param listBean
	 * @param beans
	 */
	public static void GenerateEasyUi(Class<?> cls,List listBean,Map beans) {
		GenerateEasyUi(cls, listBean, beans, null);
	}
	public static void GenerateEasyUi(List listBean,Map beans) {
		GenerateEasyUi(null, listBean, beans, null);
	}

	public static void GenerateEasyUi(List listBean,Map beans,Map repalceMap) {
		GenerateEasyUi(null, listBean, beans, repalceMap);
	}
	/**
	 * 
	 * @param cls 需要生成excle的bean  
	 *  title 为列头   checkbox 为 true的不会生成excel列
	 * @param listBean
	 * @param beans
	 */
	public static void GenerateEasyUi(Class<?> cls,List listBean,Map beans,Map replaceMap) {
		beans.put("rows", GeneratePageResult(cls,listBean,replaceMap).getRows());
	}
	/**
	 * 
	 * @param cls 需要生成excle的bean  
	 *  title 为列头   checkbox 为 true的不会生成excel列
	 * @param listBean
	 * @param beans
	 */
	@SuppressWarnings("unchecked")
	public static PageResultBean GeneratePageResult(Class<?> cls,List listBean,Map replaceMap) {
		PageResultBean<Map>  pageResultBean = new PageResultBean();
		if(cls==null ){
			if(listBean!=null && listBean.size()>0){
				cls=listBean.get(0).getClass();
			}else{
 				return pageResultBean;
			}
		}
		List<Map> listBody = new ArrayList<Map>(listBean.size()); 
		if(replaceMap==null){
			replaceMap = new HashMap<Object, Map<?, ?>>();
		}
		Field[] fields= cls.getDeclaredFields();
		List<String> listHead =new ArrayList<String>();
		List<Field> listField =new ArrayList<Field>();
		Map<String, SimpleDateFormat> map =new HashMap<String, SimpleDateFormat>();
		Map<String, DecimalFormat> mapD =new HashMap<String, DecimalFormat>();
		Field.setAccessible(fields,   true);
		Object objFormat;
		Map<String, String> replaceFieldMap;
		for (Field field : fields) {
			Display display = field.getAnnotation(Display.class);
			if (display != null ) {
				listHead.add(display.title());
				listField.add(field);
				if(!display.format().equals("")){
					//先不做类型区分  默认date类型进行 format
					objFormat =StringUtils.getSimpleDateFormat(display.format());
					map.put(field.getName(), (SimpleDateFormat)objFormat);
				}
				if(!display.DecimalFormat().equals("")){
					//先不做类型区分  默认date类型进行 format
 					objFormat =StringUtils.getDecimalFormat(display.DecimalFormat());
					mapD.put(field.getName(), (DecimalFormat)objFormat);
				}
				if(StringUtils.isNotBlank(display.replaceKey())){
					if(!replaceMap.containsKey(field.getName())){
						replaceFieldMap =(Map<String, String>) EhCacheUtils.get(display.replaceKey());
						if(replaceFieldMap!=null){
							replaceMap.put(field.getName(), replaceFieldMap);
						}
					}
				}
			}
		}
		Map<String, Object> mapRow = null;
		SimpleDateFormat sdf ;
		DecimalFormat dft ;

		Object objTem ;
		Map<String, ?> temMap =null;
		Object value=null;
		if(listField.size()>0 && listBean!=null && listBean.size()>0){
			for(Object obj:listBean){
				/*
				 * 与 16 32 64 128 这三个数比较 再大了 需要取log2 n
				 */
				mapRow = null;

				if(listField.size()<16){
					mapRow =new HashMap<String, Object>();
				}else if(listField.size()<32){
					mapRow =new HashMap<String, Object>(32);
				}else if(listField.size()<64){
					mapRow =new HashMap<String, Object>(64);
				}else if(listField.size()<128){
					mapRow =new HashMap<String, Object>(128);
				}else{
					/*取出二的指数 然后加一*/
					mapRow =new HashMap<String, Object>(2^((int)(Math.log(listField.size())/Math.log(2))+1));
				}

				for(Field field:listField){
					try{  
						value = field.get(obj);
						if(value==null){
							continue;
						}
						objTem=  map.get(field.getName());
						if(objTem!=null && field.getGenericType().toString().equals("class java.util.Date") ){
							sdf=(SimpleDateFormat) objTem;
							value= sdf.format((Date)value);
						}else{
							objTem=  mapD.get(field.getName());
							if(objTem!=null){
								dft=(DecimalFormat) objTem;
								value= dft.format(new BigDecimal(value+""));
							}
						}
						if(replaceMap!=null){
							temMap= (Map<String, ?>) replaceMap.get(field.getName());
							if(temMap!=null){
								objTem= temMap.get(value+"");
								if(objTem!=null){
									value =objTem;
								}
							}
						}
						//map.add(value);
						mapRow.put(field.getName(), value);
					}catch(Exception e){  
						LOGGER.error(e.getMessage(), e);
					}  
				}
				listBody.add(mapRow);
			}
		}
		//		beans.put("rows", listBody);
		pageResultBean.setRows(listBody);
		return pageResultBean;
	}
 
	public static String toFirstLetterUpperCase(String str) {  
		if(str == null || str.length() < 2){  
			return str;  
		}  
		String firstLetter = str.substring(0, 1).toUpperCase();  
		return firstLetter + str.substring(1, str.length());  
	}
	public static PageResultBean  GenerateEasyUi(PageInfo<?> pageInfo) {
		// TODO Auto-generated method stub
		PageResultBean  pageResultBean = GeneratePageResult(null, pageInfo.getList(), null);
		pageResultBean.setTotal(pageInfo.getTotal());
		return pageResultBean;

	} 
	public static PageResultBean GenerateEasyUi(PageInfo pageInfo,Map replaceMap) {
		// TODO Auto-generated method stub
		PageResultBean  pageResultBean = GeneratePageResult(null, pageInfo.getList(), replaceMap);
		pageResultBean.setTotal(pageInfo.getTotal());
		return pageResultBean;

	} 
	 
}
