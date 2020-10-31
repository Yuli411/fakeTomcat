package com.yl.diytomcat.catalina;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: Yhurri
 * @Date: 30/10/2020 22:35
 * @Description:
 */
public class StandardFilterConfig implements FilterConfig {
    private Map<String,String> initParamsMap;
    private String filterName;
    private ServletContext servletContext;

    public StandardFilterConfig(ServletContext servletContext,Map<String,String> initParams,String filterName){
        this.servletContext = servletContext;
        this.initParamsMap = initParams;
        this.filterName = filterName;

    }

    @Override
    public String getFilterName() {
        return filterName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String s) {
        return initParamsMap.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        Set<String> keys = initParamsMap.keySet();
        return Collections.enumeration(keys);
    }


}
