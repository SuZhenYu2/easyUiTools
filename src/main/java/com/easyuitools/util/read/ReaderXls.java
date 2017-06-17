package com.easyuitools.util.read;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DateUtil;
import org.xml.sax.SAXException;

import net.sf.jxls.reader.ReaderBuilder;
import net.sf.jxls.reader.XLSReadStatus;
import net.sf.jxls.reader.XLSReader;


public class ReaderXls {
	private static final Logger LOGGER = LogManager.getLogger(ReaderXls.class); 

	/**
	 * @param xmlConfig
	 * @param inputXLS
	 * @return map
	 * @author suzy2
	 * @throws Exception
	 */
	public static Map<String, List<?>> read(String xmlConfig,InputStream inputXLS ) throws Exception{
//		InputStream inputXML = new BufferedInputStream(ReaderXls.class.getResourceAsStream(xmlConfig));
//        XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
        List<Map> list = new ArrayList<Map>();
        Map<String, List<?>> beans = new HashMap<String, List<?>> ();
        beans.put("list", list);
        read(xmlConfig, inputXLS, beans);
//        XLSReadStatus readStatus = mainReader.read(inputXLS, beans);
//        inputXLS.close();
//        System.err.println(list.size());
        if(list.size()>0 && LOGGER.getLevel().DEBUG==LOGGER.getLevel()){
	        for (Map map :list){
	        		 Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
	        		  while (it.hasNext()) {
		        		   Map.Entry<String, String> entry = it.next();
		        		   LOGGER.debug( entry.getValue()+"  ");
	        		  }
	        		  LOGGER.debug("");
        	}
        }
        return beans;
		
	}
	/**
	 * 
	 * @param xmlConfig
	 * @param inputXLS
	 * @param beans
	 * @throws Exception
	 * @author suzy2
	 */
	public static void read(String xmlConfig,InputStream inputXLS,Map beans) throws Exception{
		InputStream inputXML = new BufferedInputStream(ReaderXls.class.getResourceAsStream(xmlConfig));
		XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
		XLSReadStatus readStatus = mainReader.read(inputXLS, beans);
		inputXLS.close();
	}
	/**
	 * @author suzy2
	 * @param 
	 * 该方法主要功能如下：
	 * 1、把listMap中的 自段自动转义
	 * 传入参数为：
	 *  需要转义的listmap
	 *  对应的转义规则 replaceMap  map组成为：  key Map 的形式  一个key对应一个map  map为对应的转义规则 
	 *   如： 1=是 0=否  或者 是=0 否=1 等等 
 	 */
	public static void replaceBean(List list,Map replaceMap ) throws Exception{
		   
		  if(list !=null && list.size()>0 && replaceMap!=null && replaceMap.size()>0 ){
			  String key =null;
	        	Map mapTem=null;
	        	Object tem;
	        	Object value;
	        	Class cls =list.get(0).getClass();
	        	Map fieldMap = new HashMap();
	        	Field[] Fields=cls.getDeclaredFields();
	        	Field.setAccessible(Fields, true);
	        	for(Field item : Fields){
	        		fieldMap.put(item.getName(), item);
	        	}
//	        	org.apache.poi.ss.usermodel.DateUtil dateUtil=null;
	        	Field temField;
		        for (Object obj :list){
		        	if(obj!=null){
		        		Iterator<Map.Entry<String, Object>> it = replaceMap.entrySet().iterator();
		        		while (it.hasNext()) {
		        			Map.Entry<String, Object> entry = it.next();
		        			key =entry.getKey();
		        			temField =(Field) fieldMap.get(key);
		        			if(temField!=null){
		        				tem =replaceMap.get(key);
		        				if(tem!=null){
//		        					if(tem instanceof String){
		        					mapTem =(Map) tem;
		        					
		        					tem =mapTem.get(temField.get(obj));
		        					if(tem!=null){
		        						temField.set(obj, tem);
//		        						System.out.println(temField.get(obj));
		        					}
//		        					}
		        					
		        				}
		        			}
		        		}
		        	}
		        	
	        	}
		  }
	}
	public static void replaceObect(List list,Map replaceMap ) throws Exception{
		if(list!=null && list.size()>0){
			if(list.get(0) instanceof Map ){
				ReaderXls.replace((List<Map> )list, replaceMap);
			}else{
				replaceBean(list, replaceMap);
			}
		}
	}
	public static void replace(List<Map> list,Map replaceMap ) throws Exception{
		
        if(list !=null && list.size()>0 && replaceMap!=null && replaceMap.size()>0 ){
        	String key =null;
        	Map mapTem=null;
        	Object tem;
        	Object value;
//        	org.apache.poi.ss.usermodel.DateUtil dateUtil=null;
	        for (Map map :list){
	        	if(map!=null){
	        		Iterator<Map.Entry<String, Object>> it = replaceMap.entrySet().iterator();
	        		while (it.hasNext()) {
	        			Map.Entry<String, Object> entry = it.next();
	        			key =entry.getKey();
	        			value=map.get(key);
	        			if(value!=null){
	        				tem=replaceMap.get(key);
	        				if(tem==null){
	        					continue;
	        				}
	        				if(tem instanceof Map){
	        					mapTem =(Map) replaceMap.get(key);
	        					if(mapTem!=null){
	        						tem =mapTem.get(value+"");
	        						if(tem!=null){
	        							map.put(key, tem);
	        						}
	        					}
	        				}else if(tem instanceof String){
	        					//读取excel格式时有 时间格式数据 如果改成string格式的则不用此处理
	        					if("org.apache.poi.ss.usermodel.DateUtil".equals(tem)){
	        						map.put(key,DateUtil.getJavaDate( Double.parseDouble( value+"")) );
	        					}else if("replaceAll(,)".equals(tem)){
	        						map.put(key,(value+"").replaceAll(",", ""));
	        					}
	        				}
	        			}
	        		}
	        	}
        	}
        }
		
	}
	
}
