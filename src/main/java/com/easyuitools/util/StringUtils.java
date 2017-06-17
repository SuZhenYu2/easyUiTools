/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.easyuitools.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.easyuitools.util.html.Encodes;
import com.google.common.collect.Lists;

/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 * @author ThinkGem
 * @version 2013-05-22
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	private static final Logger LOGGER = Logger.getLogger(StringUtils.class);	
    private static final char SEPARATOR = '_';
    private static final String CHARSET_NAME = "UTF-8";
	private static ThreadLocal<HashMap<String, SimpleDateFormat>> formatMapLocal =   new ThreadLocal<HashMap<String,SimpleDateFormat>>();
	private static  ThreadLocal<HashMap<String, DecimalFormat>> decimalFormatMapLocal= new ThreadLocal<HashMap<String,DecimalFormat>>();

	/**
	 * @param format
	 * @return
	 * @author suzy2
	 * 获取一个安全的数字格式化类
	 */
	public static DecimalFormat getDecimalFormat(String format){
		DecimalFormat objFormat =null;
		HashMap<String, DecimalFormat> map=	decimalFormatMapLocal.get();
		if(map == null){
			map = new HashMap<String, DecimalFormat>();
		}else{
			objFormat =map.get(format);
		}
		if(objFormat==null){
			objFormat  = new DecimalFormat(format);
			map.put(format,(DecimalFormat) objFormat);
		}
		decimalFormatMapLocal.set(map);
		return objFormat;
	}
	/**
	 * @param format
	 * @return
	 * @author suzy2
	 * 获取一个安全的日期格式化类
	 */
    public static SimpleDateFormat getSimpleDateFormat(String format){
		SimpleDateFormat objFormat =null;
		HashMap<String, SimpleDateFormat> map=	formatMapLocal.get();
		if(map == null){
			map = new HashMap<String, SimpleDateFormat>();
		}else{
			objFormat =map.get(format);
		}
		if(objFormat==null){
			objFormat  = new SimpleDateFormat(format);
			map.put(format,(SimpleDateFormat) objFormat);
		}
		formatMapLocal.set(map);
		return objFormat;
	}
    /**
     * 
     * @param str {0} 替换成对应的参数数值
     * @param args 可变参数
     * @return
     */
    public static String fillStringByArgs(String str,String ... args){
        Matcher m=Pattern.compile("\\{ ? ? ?(\\d)\\ ? ? ?}").matcher(str);
        int i =0;
        String strN;
        while(m.find()){
        	i =Integer.parseInt(m.group(1));
        	if(args==null || args.length<i){
        		continue;
        	}
        	strN=args[i];
        	if(strN == null){
        		strN = "";
        	}
            str=str.replace(m.group(),strN);
        }
        
        return str;
    }
    public static String getRequstContent(HttpServletRequest request){
  		@SuppressWarnings("unchecked")
		Enumeration<String> names = request.getParameterNames();  
        //遍历名称的枚举  
  		StringBuilder sb = new StringBuilder(1024);
  		String contents="";
        while(names.hasMoreElements()){  
            String name = names.nextElement();  
            sb.append(name+"=");
            String value = request.getParameter(name);  
            sb.append(value+"&");
        }  
        if(sb.length()>1){
        	contents=sb.substring(0, sb.length()-1);
        }
    	return contents;
    	
    	
    	
    	
	/*	List<?> ioList;
		try {
			ioList = IOUtils.readLines(request.getInputStream());
			for(int  i =0; i < ioList.size() ; i++){
				sb.append(ioList.get(i));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
    	return sb.toString();*/
    }
  
    
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}
	/**
	 * Check whether the given String has actual text.
	 * More specifically, returns {@code true} if the string not {@code null},
	 * its length is greater than 0, and it contains at least one non-whitespace character.
	 * @param str the String to check (may be {@code null})
	 * @return {@code true} if the String is not {@code null}, its length is
	 * greater than 0, and it does not contain whitespace only
	 * @see #hasText(CharSequence)
	 */
	public static boolean hasText(String str) {
		return hasText((CharSequence) str);
	}
    public static boolean hasText(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}
    /**
	 * 将ErrorStack转化为String.
	 */
	public static String getStackTraceAsString(Throwable e) {
		if (e == null){
			return "";
		}
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
    public static String getParams(Map<String, String[]> paramMap){
		if (paramMap == null){
			return null;
		}
		StringBuilder params = new StringBuilder();
		for (Map.Entry<String, String[]> param : ((Map<String, String[]>)paramMap).entrySet()){
			params.append(("".equals(params.toString()) ? "" : "&") + param.getKey() + "=");
			String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
			params.append(StringUtils.abbr(StringUtils.endsWithIgnoreCase(param.getKey(), "password") ? "" : paramValue, 100));
		}
		return params.toString();
	}
	
    /**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static byte[] getBytes(String str){
    	if (str != null){
    		try {
				return str.getBytes(CHARSET_NAME);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
    	}else{
    		return null;
    	}
    }
    
    /**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static String toString(byte[] bytes){
    	try {
			return new String(bytes, CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			return EMPTY;
		}
    }
    
    /**
     * 是否包含字符串
     * @param str 验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inString(String str, String... strs){
    	if (str != null){
        	for (String s : strs){
        		if (str.equals(trim(s))){
        			return true;
        		}
        	}
    	}
    	return false;
    }
    
	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)){
			return "";
		}
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}
	
	/**
	 * 替换为手机识别的HTML，去掉样式及属性，保留回车。
	 * @param html
	 * @return
	 */
	public static String replaceMobileHtml(String html){
		if (html == null){
			return "";
		}
		return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
	}
	
	/**
	 * 替换为手机识别的HTML，去掉样式及属性，保留回车。
	 * @param txt
	 * @return
	 */
	public static String toHtml(String txt){
		if (txt == null){
			return "";
		}
		return replace(replace(Encodes.escapeHtml(txt), "\n", "<br/>"), "\t", "&nbsp; &nbsp; ");
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return "";
	}
	
	public static String abbr2(String param, int length) {
		if (param == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		int n = 0;
		char temp;
		boolean isCode = false; // 是不是HTML代码
		boolean isHTML = false; // 是不是HTML特殊字符,如&nbsp;
		for (int i = 0; i < param.length(); i++) {
			temp = param.charAt(i);
			if (temp == '<') {
				isCode = true;
			} else if (temp == '&') {
				isHTML = true;
			} else if (temp == '>' && isCode) {
				n = n - 1;
				isCode = false;
			} else if (temp == ';' && isHTML) {
				isHTML = false;
			}
			try {
				if (!isCode && !isHTML) {
					n += String.valueOf(temp).getBytes("GBK").length;
				}
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}

			if (n <= length - 3) {
				result.append(temp);
			} else {
				result.append("...");
				break;
			}
		}
		// 取出截取字符串中的HTML标记
		String temp_result = result.toString().replaceAll("(>)[^<>]*(<?)",
				"$1$2");
		// 去掉不需要结素标记的HTML标记
		temp_result = temp_result
				.replaceAll(
						"</?(AREA|BASE|BASEFONT|BODY|BR|COL|COLGROUP|DD|DT|FRAME|HEAD|HR|HTML|IMG|INPUT|ISINDEX|LI|LINK|META|OPTION|P|PARAM|TBODY|TD|TFOOT|TH|THEAD|TR|area|base|basefont|body|br|col|colgroup|dd|dt|frame|head|hr|html|img|input|isindex|li|link|meta|option|p|param|tbody|td|tfoot|th|thead|tr)[^<>]*/?>",
						"");
		// 去掉成对的HTML标记
		temp_result = temp_result.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>",
				"$2");
		// 用正则表达式取出标记
		Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>");
		Matcher m = p.matcher(temp_result);
		List<String> endHTML = Lists.newArrayList();
		while (m.find()) {
			endHTML.add(m.group(1));
		}
		// 补全不成对的HTML标记
		for (int i = endHTML.size() - 1; i >= 0; i--) {
			result.append("</");
			result.append(endHTML.get(i));
			result.append(">");
		}
		return result.toString();
	}
	
	/**
	 * 转换为Double类型
	 */
	public static Double toDouble(Object val){
		if (val == null){
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float toFloat(Object val){
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long toLong(Object val){
		return toDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer toInteger(Object val){
		return toLong(val).intValue();
	}
	
	/**
	 * 获得i18n字符串
	 */
	/*public static String getMessage(String code, Object[] args) {
		LocaleResolver localLocaleResolver = (LocaleResolver) SpringContextUtils.getBean(LocaleResolver.class);
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
		Locale localLocale = localLocaleResolver.resolveLocale(request);
		return SpringContextUtils.getApplicationContext().getMessage(code, args, localLocale);
	}*/
	
	/**
	 * 获得用户远程地址
	 */
	private static enum IP_TYPES {
		X_FORWARDED_FOR {public String getType(){return "X_FORWARDED_FOR";}},
		PROXY_CLIENT_IP {public String getType(){return "Proxy-Client-IP";}},
		WL_PROXY_CLIENT_IP {public String getType(){return "Proxy-Client-IP";}},
		HTTP_CLIENT_IP {public String getType(){return "Proxy-Client-IP";}},
		HTTP_X_FORWARDED_FOR {public String getType(){return "Proxy-Client-IP";}},
		X_REAL_IP {public String getType(){return "X-Real-IP";}};
		public abstract String getType();
	}
	public static String getRemoteAddr(HttpServletRequest request){
		String ip = "";
		for (IP_TYPES type : IP_TYPES.values()){
			ip = request.getHeader(type.getType());
			if (!isInvalidIP(ip)){
				return ip;
			}
		}

		ip = request.getRemoteAddr();
		return ip;
	}
	private static boolean isInvalidIP(String ip){
		if (StringUtils.isBlank(ip) || StringUtils.equals("unknow", ip)){
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    /**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }
    
    /**
     * 如果不为空，则设置值
     * @param target
     * @param source
     */
    public static void setValueIfNotBlank(String target, String source) {
		if (isNotBlank(source)){
			target = source;
		}
	}
 
    /**
     * 转换为JS获取对象值，生成三目运算返回结果
     * @param objectString 对象串
     *   例如：row.user.id
     *   返回：!row?'':!row.user?'':!row.user.id?'':row.user.id
     */
    public static String jsGetVal(String objectString){
    	StringBuilder result = new StringBuilder();
    	StringBuilder val = new StringBuilder();
    	String[] vals = split(objectString, ".");
    	for (int i=0; i<vals.length; i++){
    		val.append("." + vals[i]);
    		result.append("!"+(val.substring(1))+"?'':");
    	}
    	result.append(val.substring(1));
    	return result.toString();
    }
    public static List<String> string2list(String params) {
        if (!StringUtils.hasText(params)) {
            return null;
        }
        List<String> paramList = new ArrayList<>();
        String[] paramArr = params.split(",");
        if (paramArr.length == 1 && paramArr[0].equals("")) {
            return null;
        }
        Collections.addAll(paramList, paramArr);
        return paramList;
    }
    
}
