package com.yl.diytomcat.servlet;

import com.yl.diytomcat.core.Response;
import com.yl.diytomcat.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther: Yhurri
 * @Date: 29/10/2020 23:48
 * @Description:
 */
public class SetCookieServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie cookie = new Cookie("yylllll","beautiful");
        cookie.setMaxAge(60*60);
        Response response = (Response) resp;
        response.addCookie(cookie);
        response.writeToBrowser("sdsdsdsd",200);
    }
}
