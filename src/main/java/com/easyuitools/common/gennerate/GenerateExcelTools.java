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
import com.easyuitools.common.entity.bean.GenerateBean;
import com.easyuitools.util.EhCacheUtils;
import com.easyuitools.util.StringUtils;

public class GenerateExcelTools {
	private static final Logger LOGGER = Logger.getLogger(GenerateColumns.class);	
	
	
	/**
	 * 
	 * @param cls 需要生成excle的bean  
	 *  title 为列头   checkbox 为 true的不会生成excel列
	 * @param listBean
	 * @param beans
	 */
	public static GenerateBean GenerateExcel(Class<?> cls,List<?> listBean) {
		return GenerateExcel(cls, listBean, null);
	}
	public static GenerateBean GenerateExcel(List<?> listBean,Map<Object, Map<?, ?>> repalceMap) {
		return GenerateExcel(null, listBean, repalceMap);
	}
	public static GenerateBean GenerateExcel(List<?> listBean) {
		return GenerateExcel(null, listBean, null);
	}
	/**
	 * 
	 * @param cls
	 * @param listBean
	 * @param beans
	 * @param replaceMap
	 */
	public static GenerateBean GenerateExcel(Class<?> cls,List<?> listBean,Map replaceMap) {
		GenerateBean beans =new GenerateBean();
		Class<?> temCls = cls;
		if(listBean!=null && listBean.size()>0){
			temCls=listBean.get(0).getClass();
		}else{
			//beans.put("rows", listBody);
			/*beans.put("listHead",new ArrayList<String>() );
			beans.put("listBody", new ArrayList<List<?>>());*/
			return beans;
		}
		List<List<?>> listBody = new ArrayList<List<?>>(listBean.size()); 
		if(cls==null ){
			cls =	temCls;
		}

		if(replaceMap==null){
			replaceMap = new HashMap<Object, Map<?, ?>>();
		}

		Field[] fields= cls.getDeclaredFields();


		List<Field> listField =new ArrayList<Field>();
		Map<String, SimpleDateFormat> map =new HashMap<String, SimpleDateFormat>();
		Map<String, DecimalFormat> mapD =new HashMap<String, DecimalFormat>();
		Field.setAccessible(fields, true);
		Object objFormat;
		Map<?, ?> replaceFieldMap;
		List<String> listHead =new ArrayList<String>((fields.length)) ;

		for (Field field : fields) {
			Display display = field.getAnnotation(Display.class);
			if (display != null && !display.hidden() && !display.checkbox()) {
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
					mapD.put(field.getName(),(DecimalFormat) objFormat);
				}
				if(StringUtils.isNotBlank(display.replaceKey())){
					if(!replaceMap.containsKey(field.getName())){
						replaceFieldMap =(Map<?, ?>) EhCacheUtils.get(display.replaceKey());
						if(replaceFieldMap!=null){
							replaceMap.put(field.getName(), replaceFieldMap);
						}
					}
				}
			}
		}
		List<Object> listRow = null;
		SimpleDateFormat sdf ;
		DecimalFormat dft ;
		Object objTem ;
		Map<?, ?> temMap =null;
		Object value=null;



		if(listField.size()>0 && listBean!=null && listBean.size()>0){
			if(!StringUtils.equals(temCls.getName(),cls.getName())){
				Field[] fieldsTemCls= temCls.getDeclaredFields();
				Map<String, Field> mapField = new HashMap<String, Field>();
				//遍历所有的节点 放置到map中 方便后边替换使用
				Field.setAccessible(fieldsTemCls,   true);

				for(Field field : fieldsTemCls){
					mapField.put(field.getName(), field);
				}
				Field  tempField=null;
				if(mapField.size()>0){

					List<Field> listFieldTemp =new ArrayList<Field>();

					for(Field field :listField){
						tempField =mapField.get(field.getName());
						if(null!=tempField){
							//							field = tempField;
							listFieldTemp.add(tempField);
						}
					}
					listField =listFieldTemp;
				}
			}



			boolean isMapFlag=false;
			if (listBean.get(0) instanceof Map) {
				isMapFlag=true;
			}
			for(Object obj:listBean){

				listRow = new ArrayList<Object>(listBean.size());
				if(obj==null){
					continue;
				}
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
						listRow.add(value);
					}catch(Exception e){  
						listRow.add(null);
						LOGGER.error(e.getMessage(), e);
					}  
				}
				listBody.add(listRow);
			}
		}
		beans.setListBody(listBody);
		beans.setListHead(listHead);


		return beans;

	}
	 
}
