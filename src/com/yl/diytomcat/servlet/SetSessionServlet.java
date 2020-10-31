package com.yl.diytomcat.servlet;

import com.yl.diytomcat.core.Response;
import com.yl.diytomcat.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Auther: Yhurri
 * @Date: 30/10/2020 17:26
 * @Description:
 */
public class SetSessionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("keyqqq00","ok§§§§§");
        Response response = (Response) resp;
        response.writeToBrowser(session.getAttribute("keyqqq00").toString(),Constant.CODE200);
    }
}
