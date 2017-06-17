package com.easyuitools.common.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class BtcpContextListener implements ServletContextListener {
	private static String webroot;
	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		setWebroot(event.getServletContext().getServletContextName());
		event.getServletContext().setAttribute("basepath", webroot);
		System.out.println("file.encoding:"+System.getProperty("file.encoding"));
	}

	public static String getWebroot() {
		return webroot;
	}

	public static void setWebroot(String webroot) {
		BtcpContextListener.webroot = webroot;
	}
	

}