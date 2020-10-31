package com.yl.diytomcat.http;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.*;

/**
 * @Auther: Yhurri
 * @Date: 29/10/2020 01:22
 * @Description:
 */
public class StandardServletConfig implements ServletConfig {
    private ServletContext servletContext;
    private Map<String,String> initParams;
    private String servletName;

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String s) {

        return initParams.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        Set<String> keys = initParams.keySet();
        return Collections.enumeration(keys);
    }

    public StandardServletConfig(ServletContext servletContext, Map<String, String> initParams, String servletName) {
        this.servletContext = servletContext;
        this.initParams = initParams;
        this.servletName = servletName;
        if (initParams == null){
            initParams = new HashMap<>();
        }
    }
}
