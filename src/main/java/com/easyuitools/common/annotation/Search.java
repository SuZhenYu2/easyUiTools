/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.easyuitools.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来动态生成easy ui 搜索条件
 * @author suzy2
 * @version 2015-10-24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Search
{
	
     String labelName();//搜索框文本显示
     boolean redSpan() default false;//红色span*
     String Class() default "easyui-textbox";//指明如何对齐列数据。可以使用的值有：'left','right','center'
     String type() default "text" ;
     String dataOptions() default "" ;
     boolean hidden() default false;
     String initValue() default "";
     String style() default "";
     String url() default "";
     boolean combobox() default false;
     boolean required() default false;
     boolean datebox() default false;
}