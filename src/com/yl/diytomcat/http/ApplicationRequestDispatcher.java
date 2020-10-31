package com.yl.diytomcat.http;

import cn.hutool.log.LogFactory;
import com.yl.diytomcat.core.DefaultServlet;
import com.yl.diytomcat.core.Request;
import com.yl.diytomcat.core.Response;
import com.yl.diytomcat.servlet.InvokeServlet;
import com.yl.diytomcat.servlet.JspServlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @Auther: Yhurri
 * @Date: 30/10/2020 21:27
 * @Description:
 */
public class ApplicationRequestDispatcher implements RequestDispatcher {
    private String pathResource;
    @Override
    public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        //if uri = /;
        Request request = (Request) servletRequest;
        Response response = (Response) servletResponse;
        //get servlet class name
        String servletClassName = request.getContext().getClassName("/" + pathResource);
        LogFactory.get().info("servletClassName:{}", servletClassName);
        if (servletClassName != null) {
            InvokeServlet.getInstance().service(request, response);
        } else if (pathResource.endsWith(".jsp")) {
            JspServlet.getInstance().service(request,response);
        } else {
            //handle request of html css...
            DefaultServlet.getInstance().service(request, response);
        }

    }

    @Override
    public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {

    }

    public ApplicationRequestDispatcher(String pathResource){
        this.pathResource = pathResource;
    }
}
