package com.yl.diytomcat.filter;


import javax.servlet.*;
import java.io.IOException;

/**
 * @Auther: Yhurri
 * @Date: 30/10/2020 23:31
 * @Description:
 */
public class PerformanceFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println(filterConfig.getFilterName());
        System.out.println(filterConfig.getInitParameter("site"));
        System.out.println("PerformanceFilter初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("调用performance过滤器");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
