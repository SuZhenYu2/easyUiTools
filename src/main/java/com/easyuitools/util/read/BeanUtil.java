package com.easyuitools.util.read;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

 
/**
 * 当把Person类作为BeanUtilTest的内部类时，程序出错<br>
 * java.lang.NoSuchMethodException: Property '**' has no setter method<br>
 * 本质：内部类 和 单独文件中的类的区别 <br>
 * BeanUtils.populate方法的限制：<br>
 * The class must be public, and provide a public constructor that accepts no arguments. <br>
 * This allows tools and applications to dynamically create new instances of your bean, <br>
 * without necessarily knowing what Java class name will be used ahead of time
 * 
 * @author suzy2
 */
public class BeanUtil {
    private static final Log LOG = (Log) LogFactory.getLog(BeanUtil.class);
    public static void main(String[] args) {

        Map<String, Object> mp = new HashMap<String, Object>();
        mp.put("name", "Mike");
        mp.put("age", 25);
        mp.put("mN", "male");

        // 将map转换为bean
   //     transMap2Bean2(mp, person);


        // 将javaBean 转换为map
      //  Map<String, Object> map = transBean2Map(person);


    }

    // Map --> Bean 2: 利用org.apache.commons.beanutils 工具类实现 Map --> Bean
    public static void transMap2Bean2(Map<String, Object> map, Object obj) {
        if (map == null || obj == null) {
            return;
        }
        try {
            BeanUtils.populate(obj, map);
        } catch (Exception e) {
            System.out.println("transMap2Bean2 Error " + e);
        }
    }
    public static Object transMap2Bean3(Map<String, Object> map, Class cls) {
    	if (map == null || cls == null) {
    		return null;
    	}
    	try {
			Object objtem = cls.newInstance();
	        Field[] fields= cls.getDeclaredFields();

			Object obj;
			 Field.setAccessible(fields,   true);
    		for(Field field:fields){
    			obj =map.get(field.getName());
    			if(obj!=null){
    				field.set(obj.getClass(), obj);
    			}
    		}
    		return objtem;
    	} catch (Exception e) {
    		LOG.error("transMap2Bean2 Error ",  e);
    	}
    	return null;
    }

    // Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
    public static void transMap2Bean(Map<String, Object> map, Object obj) {

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }

            }

        } catch (Exception e) {
        	LOG.error("transMap2Bean Error " , e);
        }

        return;

    }
    /**
     * 
     * @param map
     * @param Class
     * @author suzy2
     * @return List
     * Bean 1: 利用Introspector,PropertyDescriptor实现 ListMap --> ListBean
     */
    // Map --> 
    public static List transMapList2Bean(List<Map> mapList, Class cla) {
		List<Object> list= new ArrayList();
		Object objtem=null;
    	if(mapList!=null){
    		for(Map map : mapList){
    			//    				objtem = cla.newInstance();
				//	    			BeanUtil.transMap2Bean2(map, objtem);
				    				objtem =transMap2Bean3(map, cla);
									list.add(objtem);
    		}
    	}
    	return list;
    	
    }

    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
    public static Map<String, Object> transBean2Map(Object obj) {

        if(obj == null){
            return null;
        }        
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }

            }
        } catch (Exception e) {
        	LOG.error("transBean2Map Error " , e);
        }

        return map;

    }
    public static List<Map> transBeanList2Map(List list) {
    	List<Map> newlist= new ArrayList<Map>();
    	if(list==null){
    		return null;
    	}
    	if(list.size()==0){
    		return null;
    	}
    	Map maptem=null;
    	for(Object obj :list){
    		maptem=	transBean2Map(obj);
    		newlist.add(maptem);
    	}
    	return newlist;
    	
    }


}