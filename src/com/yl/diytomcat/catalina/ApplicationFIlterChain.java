package com.yl.diytomcat.catalina;

import cn.hutool.core.util.ArrayUtil;

import javax.servlet.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Auther: Yhurri
 * @Date: 31/10/2020 00:01
 * @Description:
 */
public class ApplicationFIlterChain implements FilterChain {
    private Filter[] filters;
    private Servlet servlet;
    private int count;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        if (filters.length > count){
            filters[count++].doFilter(servletRequest,servletResponse,this);
        }else{
            servlet.service(servletRequest,servletResponse);
        }
    }

    public ApplicationFIlterChain(List<Filter> filters,Servlet servlet){
        this.filters = ArrayUtil.toArray(filters,Filter.class);
        this.servlet = servlet;
    }


}
