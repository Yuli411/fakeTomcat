package com.yl.diytomcat.servlet;

import com.yl.diytomcat.core.Response;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther: Yhurri
 * @Date: 30/10/2020 21:06
 * @Description:
 */
public class JumpServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Response response = (Response) resp;
        req.setAttribute("a","bb");
        response.sendRedirect("hello");
    }
}
