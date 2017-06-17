package com.easyuitools.util;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.web.context.ContextLoader;

import com.easyuitools.util.factory.DynamicBean;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Cache工具类
 * @author suzy2
 */
public class EhCacheUtils {
	private static final Logger LOGGER = Logger.getLogger(EhCacheUtils.class);

	private static CacheManager cacheManager = (CacheManager) ContextLoader.getCurrentWebApplicationContext().getBean("cacheManager");
//	private static CacheManager cacheManager = (CacheManager) SpringContextUtils.getApplicationContext().getBean(CacheManager.class);

	private static final String CACHE_TABLE = "cacheTable";
	
	private static volatile boolean initFlag = false;
 
 
	private  static  Map<String,Class<?>> MAP_DYNAMIC_BEANS = new ConcurrentHashMap<>();
	private  String cacheSql = null;
	private  static String cacheSqlStr = null;

	/**
	 * 获取CACHE_TABLE缓存
	 * @param key 实体bean map 集合
	 * @return
	 */
	public static <T> Map<String, T> getCacheTypeByKey(String key) throws ClassCastException{

		Map<String, T> tem = (Map<String, T> ) get(key);

		return tem;
	}
	/**
	 * 获取CACHE_TABLE缓存
	 * @param key1 cacheTable中的key  subKey cacheTable装载的key 联合可以取出一个 bean
	 * @return
	 */
	public static <T>  T  getCacheTypeByKK(String key,String subKey) throws ClassCastException{

		Map<String, T> tem = (Map<String, T> ) get(key);
		if(null != tem){
			return tem.get(subKey);
		}
		return null;
	}
	/**
	 * 获取CACHE_TABLE缓存
	 * @param key1 cacheTable中的key  subKey cacheTable装载的key 联合可以取出一个String
	 * @return
	 */
	public static String getByKK(String key1,String subKey) {
	        Map<?, ?> tem = (Map<?, ?>) get(key1);
	        if (tem != null) {
	        	Object obj =tem.get(subKey);
	        	if(obj instanceof List ){
	        		return ((List<?>)obj).toString();
	        	}
	            return (String)obj ;
	        }
	        else {
	            return null;
	        }
	}
	/**
	 * 获取CACHE_TABLE缓存
	 * @param key
	 * @return
	 */
	public static Object get(String key) {
		return get(CACHE_TABLE, key);
	}
	
	/**
	 * 写入CACHE_TABLE缓存
	 * @param key
	 * @return
	 */
	public static void put(String key, Object value) {
		put(CACHE_TABLE, key, value);
	}
	
	/**
	 * 从CACHE_TABLE缓存中移除
	 * @param key
	 * @return
	 */
	public static void remove(String key) {
		remove(CACHE_TABLE, key);
	}
	
	void afterPropertiesSet() throws Exception {
		get("");
	}
	
	/**
	 * 获取缓存
	 * @param cacheName
	 * @param key
	 * @return
	 * @author suzy2
	 */
	public static Object get(String cacheName, String key) {
		Cache  cache =getCache(cacheName);
		if(!initFlag){
			if (cacheManager == null){
				
			}
			synchronized (cacheManager) {
				LOGGER.info("开始初始化 EhCacheUtils ");
				LOGGER.debug("加载cache："+cacheName);
				if(!initFlag){
					String keyTem =null;
					String mapSql;
					String value1;
					String key1;
					//Map<Object, Object> map =null;
					Map<String, Object> map1 =null;
					DefaultSqlSessionFactory ssf = (DefaultSqlSessionFactory) SpringContextUtils.getBean("sqlSessionFactory");
					SqlSession ss = ssf.openSession();
					Connection connection =ss.getConnection();
					String sql =null;
					EhCacheUtils ehCacheUtils =null;
					try {
						ehCacheUtils = SpringContextUtils.getApplicationContext().getBean(EhCacheUtils.class);
					} catch (BeansException e1) {
						LOGGER.error(e1.getMessage(),e1);
					}
					if(null != ehCacheUtils && StringUtils.isNotBlank(ehCacheUtils.getCacheSql())){
						sql=ehCacheUtils.getCacheSql();
					}else{
						sql = "select cacheName,sqlText from cacheTable where flag ='1'";
					}
					
					LOGGER.debug("加载  sql："+sql);
					try {
						PreparedStatement pre =connection.prepareStatement(sql);
						PreparedStatement pre1 =null;
						ResultSet rs =pre.executeQuery();
						ResultSet rs1=null;
						Object temp = null;
						List<Object> tempList = null;
						//map = new HashMap<Object, Object>();

						QueryRunner queryRunner = new QueryRunner();

						String  className =null;
						// sql是否配置 classname
						Boolean isConfigClassName = false;
						HashMap<String, Class<String>> propertyMap = new HashMap<String, Class<String>>();
						if(StringUtils.contains(sql,"className")){
							// sql 中配置了 class name
							isConfigClassName = true;
							propertyMap.put("key1",String.class);
						}

						while(rs.next()) {
							keyTem = rs.getString("cacheName");
							mapSql = rs.getString("sqlText");
							// 如果sql配置了
							if(StringUtils.isBlank(mapSql)){
								continue;
							}
							if (isConfigClassName) {
								className = rs.getString("className");
								if (StringUtils.isNoneBlank(className)) {
									Class<? extends Object> dynamicCls = MAP_DYNAMIC_BEANS.get(className);
									if (null == dynamicCls) {
										try {
											Class<?> cla = Class.forName(className);
											DynamicBean dynamicBean = new DynamicBean(propertyMap, cla);

											dynamicCls = dynamicBean.getObject().getClass();
											MAP_DYNAMIC_BEANS.put(className, dynamicCls);
										} catch (Exception e) {
											LOGGER.error(e.getMessage(), e);
											continue;
										}
									}
									Method method = null;
									try {
										method = dynamicCls.getMethod("getKey1");

										map1 = new HashMap<String, Object>();

										BeanListHandler<? extends Object> bh = new BeanListHandler(dynamicCls);
										List<?> list = queryRunner.query(connection, mapSql, bh);

										for (Object obj : list) {
											String keyTemp = (String) method.invoke(obj);
											map1.put(keyTemp,obj);
										}
										Element element = new Element(keyTem, map1);
										cache.put(element);
										continue;
									} catch (Exception e) {
										LOGGER.error(e.getMessage(), e);
									}
								}

							}

							try {
								pre1 = connection.prepareStatement(mapSql);
								LOGGER.debug("加载  mapSql：" + mapSql);
								rs1 = pre1.executeQuery();
								map1 = new HashMap<String, Object>();
								while (rs1.next()) {
											value1 = rs1.getString("value");
											key1 = rs1.getString("key1");
											temp = map1.get(key1);
											if (temp != null) {
												if (temp instanceof List) {
													tempList = (List<Object>) temp;
												} else {
													tempList = new ArrayList<>();
													tempList.add(temp);
												}
												tempList.add(value1);
												map1.put(key1, tempList);
											} else {
												map1.put(key1, value1);
											}
										}
									} catch (Exception e) {
										LOGGER.error(e.getMessage(), e);
								}
								//cache.put(keyTem, map1);
								Element element = new Element(keyTem, map1);
								cache.put(element);
							}
							LOGGER.info("缓存加载完毕");

						} catch (SQLException e) {
							LOGGER.error(e.getMessage(),e);
						}finally {
							try {
								DbUtils.close(connection);
							} catch (SQLException e) {
								LOGGER.error(e.getMessage(),e);
							}
						}
				}
				initFlag=true;
			}
		}
		Element element = cache.get(key);
		return element==null?null:element.getObjectValue();
	}

	/**
	 * 写入缓存
	 * @param cacheName
	 * @param key
	 * @param value
	 */
	public static void put(String cacheName, String key, Object value) {
		Element element = new Element(key, value);
		getCache(cacheName).put(element);
	}

	/**
	 * 从缓存中移除
	 * @param cacheName
	 * @param key
	 */
	public static void remove(String cacheName, String key) {
		getCache(cacheName).remove(key);
	}
	
	/**
	 * 获得一个Cache，没有则创建一个。
	 * @param cacheName
	 * @return
	 */
	private static Cache getCache(String cacheName){
		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null){
			cacheManager.addCache(cacheName);
			cache = cacheManager.getCache(cacheName);
			cache.getCacheConfiguration().setEternal(true);
		}
		return cache;
	}

	public static CacheManager getCacheManager() {
		return cacheManager;
	}
	public static void clearCache(){
		cacheManager.clearAll();
		initFlag=false;
	}
	public String getCacheSql() {
		if(cacheSql ==null){
			return EhCacheUtils.cacheSqlStr;
		}
		return cacheSql;
	}
	public  void setCacheSql(String cacheSql) {
		this.cacheSql = cacheSql;
		EhCacheUtils.cacheSqlStr = cacheSql;
	}
	
}
