/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.easyuitools.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来动态生成easy ui 表头
 * @author suzy2
 * @version 2015-10-24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Display
{
	
     String title();//列标题文本
     String align() default "center";//指明如何对齐列数据。可以使用的值有：'left','right','center'
     boolean checkbox() default false ;
     boolean hidden() default false;
     String format() default "";
     String width() default "";
     String DecimalFormat() default "";
     String replaceKey() default "";
}