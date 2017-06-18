package com.easyuitools.common.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.easyuitools.common.entity.view.Tree;
import com.easyuitools.util.EhCacheUtils;
@Controller
@RequestMapping("/Dictionary")
public class DictionaryController  {

	@ResponseBody
	@RequestMapping("/getTree")
	public Object getTree(String type,String all) {
		//如果type为空则返回null
		if(type==null){
			return null;
		}
		Map<?, ?> map =(Map<?, ?>) EhCacheUtils.get(type);

		Tree tree = new Tree();
		Tree child =null;

		tree.setText("全部");
		tree.setId("");
		tree.setChecked(true);
		List<Tree> list =new ArrayList<Tree>();
		if(map!=null){
			for (Object key :map.keySet()) {
				if(!"".equals(map.get(key))){
					child = new Tree();
					child.setId((String)key);
					child.setText((String)map.get(key));
					list.add(child);
				}

			}
			if(all!=null && !"true".equals(all)){
				// 如果包括所有树则返回最后的结果 否则返回树中的节点
				return list;
			}
			tree.setChildren(list);
		}
		List<Tree> node= new ArrayList<Tree>();
		node.add(tree)	;		//String reslut=JSONObject.toJSONString(map);
		return node;
	}
	
	@ResponseBody
	@RequestMapping("/getCombobox")
	public Object getCombobox(String type,String keyWord,String notIn,String all,String startStr, String endStr) {
		//如果type为空则返回null
		if(type==null){
			return null;
		}
		if(StringUtils.isBlank(keyWord) ){
			keyWord =null;
		}
		if( StringUtils.isBlank(notIn) ){
			notIn =null;
		}
		//			"id":1, 
		//			"text":"text1" 
		Map<?, ?> map =(Map<?, ?>) EhCacheUtils.get(type);

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> resMap = null;
		if(all==null || !"false".equals(all)){
			//tree.setText("全部");
			resMap=new HashMap<String, String>();

			resMap.put("id", "");
			resMap.put("text", "全部");
			list.add(resMap);
		}
		Object value = null;
		if(map!=null){
			for (Object key :map.keySet()) {
				if(keyWord!=null){
					keyWord =","+keyWord+",";
					if(!keyWord.contains(","+key+",")){
						continue;
					}
				}
				if(notIn!=null){
					notIn=","+notIn+",";
					if(notIn.contains(","+key+",")){
						continue;
					}
				}
				if (StringUtils.isNotBlank(startStr)) {
					if (!(key.toString().startsWith(startStr))) {
						continue;
					}
				}
				if (StringUtils.isNotBlank(endStr)) {
					if (!(key.toString().endsWith(endStr))) {
						continue;
					}
				}

				if(!"".equals(map.get(key))){
					resMap=new HashMap<String, String>();
					resMap.put("id", key+"");
					resMap.put("valueField", key+"");
					value = map.get(key);
					if(value instanceof  String){
						resMap.put("text", value+"");
					}else{
						resMap.put("text", JSON.toJSONString(value));
					}
					list.add(resMap);
				}

			}

		}
		//String reslut=JSONObject.toJSONString(map);
		return list;
	}

	@ResponseBody
	@RequestMapping("/clearCache")
	public Object clearCache(String type,String all) {
		//如果type为空则返回null
		EhCacheUtils.clearCache();
		EhCacheUtils.get("config");
		return "缓存刷新成功";
	}



}
