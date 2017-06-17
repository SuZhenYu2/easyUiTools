package com.easyuitools.common.gennerate;

import com.easyuitools.common.listener.BtcpContextListener;

public class GenerateWidget {

	private GenerateWidget() {

	}

	public static String GenerateA(String url, String title) {
		return "<a href=\"" + BtcpContextListener.getWebroot() + url + "\">" + title + "</a>";
	}
	public static String GenerateUrl(String url) {
		return  BtcpContextListener.getWebroot() + url ;
	}

	public static String GenerateAOnclick(String onclickMethod, String title) {
		return "<a onclick=\"" + onclickMethod + "\"" + " href=\"javascript:void(0)\"" + ">" + title + "</a>";
	}

	public static void main(String[] args) {

		System.out.println(GenerateAOnclick("edit('1')", "12312312312"));

	}

}
