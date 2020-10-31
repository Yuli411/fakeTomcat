package com.yl.diytomcat.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @Auther: Yhurri
 * @Date: 31/10/2020 20:21
 * @Description:
 */
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("listening a initialization event from "+servletContextEvent.getSource());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("listening a destroy event from " +servletContextEvent.getSource());
    }
}
