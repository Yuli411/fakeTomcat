package com.yl.diytomcat.servlet;

import com.yl.diytomcat.catalina.WebContext;
import com.yl.diytomcat.classloader.WebappClassLoader;
import com.yl.diytomcat.core.Request;
import com.yl.diytomcat.core.Response;
import com.yl.diytomcat.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Auther: Yhurri
 * @Date: 26/10/2020 23:44
 * @Description:
 */
public class InvokeServlet extends HttpServlet {
    public static InvokeServlet instance = new InvokeServlet();

    private InvokeServlet() {
    }

    public static InvokeServlet getInstance() {
        return instance;
    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Request request = (Request) req;
        Response response = (Response) resp;

        String pathResource = request.getPathResource();
        WebContext context = request.getContext();
        String servletClassName = context.getClassName("/"+pathResource);

        try {
            WebappClassLoader webappClassLoader = context.getWebappClassLoader();
            Class<?> servletClass = webappClassLoader.loadClass(servletClassName);
            System.out.println("servletClass:" + servletClass);
            System.out.println("servletClass'classLoader:" + servletClass.getClassLoader());
            HttpServlet httpServlet = context.getHttpServlet(servletClass);
            //ReflectUtil.invoke(o,"service",request,response);
            Method service = servletClass.getMethod("service", ServletRequest.class, ServletResponse.class);
            service.setAccessible(true);
            service.invoke(httpServlet,request,response);
            if (response.getRedirectPath() != null){
                response.writeToBrowser(302);
            }


        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }
}
