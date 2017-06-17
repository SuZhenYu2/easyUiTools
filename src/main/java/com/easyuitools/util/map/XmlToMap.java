package com.easyuitools.util.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

public class XmlToMap {
	public  static Map<String, Object> xml2map(String xmlString) throws DocumentException {  
		Document doc = DocumentHelper.parseText(xmlString);  
		Element rootElement = doc.getRootElement();  
		Map<String, Object> map = new HashMap<String, Object>();  
		ele2map(map, rootElement);  
		//		  System.out.println(map);  
		// 到此xml2map完成，下面的代码是将map转成了json用来观察我们的xml2map转换的是否ok  
		/*String string = JSON.toJSONString(map);*/
		//		  System.out.println(string);  
		return map;  
	} 
	/*** 
	 * 核心方法，里面有递归调用 
	 *  
	 * @param map 
	 * @param ele 
	 */  
	@SuppressWarnings("unchecked")
	public  static String ele2map(Map<String, Object> map, Element ele) {  
		// 获得当前节点的子节点  
		List<Element> elements = ele.elements();  
		if (elements.size() == 0) {  
			// 没有子节点说明当前节点是叶子节点，直接取值即可  
			return ele.getStringValue();  
		} else if (elements.size() == 1) {  
			// 只有一个子节点说明不用考虑list的情况，直接继续递归即可  
			Map<String, Object> tempMap = new HashMap<String, Object>();  
			String aa =ele2map(tempMap, elements.get(0));  
			map.put(ele.getName(), aa);  
		} else {  
			// 多个子节点的话就得考虑list的情况了，比如多个子节点有节点名称相同的  
			// 构造一个map用来去重  
			Map<String, Object> tempMap = new HashMap<String, Object>();  
			for (Element element : elements) {  
				tempMap.put(element.getName(), null);  
			}  
			Set<String> keySet = tempMap.keySet();  
			for (String string : keySet) {  
				Namespace namespace = elements.get(0).getNamespace();  
				List<Element> elements2 = ele.elements(new QName(string,namespace));  
				// 如果同名的数目大于1则表示要构建list  
				if (elements2.size() > 1) {  
					List<Map> list = new ArrayList<Map>();  
					for (Element element : elements2) {  
						Map<String, Object> tempMap1 = new HashMap<String, Object>();  
						ele2map(tempMap1, element);  
						list.add(tempMap1);  
					}  
					map.put(string, list);  
				} else {  
					// 同名的数量不大于1则直接递归去  
					Map<String, Object> tempMap1 = new HashMap<String, Object>();  
					String aa = ele2map(tempMap1, elements2.get(0));  
					if(aa==null){
						map.put(string, tempMap1); 
					}else{
						map.put(string, aa); 
					}
					
				}  
			}  
		} 
		return null;
	}  

}
