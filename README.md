欢迎使用 btcp-redis


项目介绍
-------------------
> **btcp-sdk 介绍**

>-1、 集成了easyui页面表格生成功能
>-2、 缓存装载功能
>-3、 高性能excel导出功能 
>-4、 pdf导出功能
>-5、 replaceMap相关替换功能

> **集成方式   **
> -
>  1、pom依赖<br>`	<!-- redis end -->
		<dependency>
			<groupId>com.lenovo.btcp</groupId>
			<artifactId>btcp-sdk</artifactId>
			<version>1.0.6</version>
			<exclusions>
				<exclusion>
					<artifactId>spring-expression</artifactId>
					<groupId>org.springframework</groupId>
				</exclusion>
				<exclusion>
					<artifactId>spring-beans</artifactId>
					<groupId>org.springframework</groupId>
				</exclusion>
				<exclusion>
					<artifactId>spring-aop</artifactId>
					<groupId>org.springframework</groupId>
				</exclusion>
				<exclusion>
					<artifactId>spring-context</artifactId>
					<groupId>org.springframework</groupId>
				</exclusion>
			</exclusions>
		</dependency>
		 `
><br>
>  2、spring配置 <br>
    `	<import resource="classpath:config/btcp-sdk.xml" />`
    <br>
    btcp-sdk.xml<br>
    `<bean id="contextHolder" class="com.lenovo.btcp.util.SpringContextUtils"/>
	<bean id="ehCacheUtils" class="com.lenovo.btcp.util.EhCacheUtils">
	  <property name="cacheSql" value="select cacheName,sqlText from cacheTable where flag ='1' and system ='btcp'"/> 
	</bean>
		<!-- ################################## 缓存配置 ################################## -->
	<bean id="cacheManager" class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean">
		<property name="configLocation" value="classpath:${ehcache.configFile}" />
	</bean>`
    <br>
> 3、ehcache-local.xml<br>
`<?xml version="1.0" encoding="UTF-8"?>
<ehcache updateCheck="false" name="defaultCache">
<!-- 	<diskStore path="../temp/btcp/ehcache" />
 -->	<diskStore path="java.io.tmpdir"/>		
	<!-- 默认缓存配置. -->
	<defaultCache maxEntriesLocalHeap="100" eternal="false" timeToIdleSeconds="300" timeToLiveSeconds="600"
		overflowToDisk="true" maxEntriesLocalDisk="100000" />
	<!-- cacheTable缓存 -->
	<cache name="cacheTable" maxEntriesLocalHeap="100" eternal="true" overflowToDisk="true"/>
</ehcache>`

>-4、使用方式简单介绍

一、下拉列表的使用<br>
`<td>审核状态：</td>
<td>
<input id="auditState" name="auditState" class="easyui-combobox"
data-options="valueField:'id',textField:'text',url:'<%=ctx%>/Dictionary/getCombobox.do?type=invoiceAuditState'"/>
</td>`
><br>
>getCombobox 方法参数说明
><br>
>String type 值CacheTable表中的cacheName 必填<br>
>String keyWord 非必填<br>
>>在结果集中包含的值in的意思<br>
>String notIn<br>
>在结果集中不包含的值notin的意思<br>
>String all<br>
>是否在下拉列表中包含“全部”,对应的值为空。<br>

二、列表数据的使用<br>

>1.自动生成注释，可能跟mybatis自动生成工具的代码一样，因此需要微调下。<br>
>http://10.120.26.201:8080/sys/home.jsp<br>
>admin/admin<br>

>2.修改代码的地方<br>
>参考<br>
>RefundOrder<br>
>RefundOrderIndex.jsp 列表<br>
>RefundOrderServiceImpl.queryReturnRefundOrders<br>

>3.数据库参数配置<br>

 
三、清空或刷新缓存调用的Action<br>
/Dictionary/clearCache.do








