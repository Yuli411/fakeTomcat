package com.yl.diytomcat.core;

import cn.hutool.core.io.FileUtil;
import com.yl.diytomcat.servlet.JspServlet;
import com.yl.diytomcat.util.Constant;
import com.yl.diytomcat.util.WebXMLUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Auther: Yhurri
 * @Date: 27/10/2020 17:01
 * @Description:
 */
public class DefaultServlet extends HttpServlet {
    private static DefaultServlet defaultServlet = null;

    private DefaultServlet() {
    }

    public static DefaultServlet getInstance() {
        if (defaultServlet == null) {
            synchronized (DefaultServlet.class) {
                if (defaultServlet == null) {
                    defaultServlet = new DefaultServlet();
                }
            }
        }

        return defaultServlet;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Request request = (Request) req;
        Response response = (Response) resp;
        String uri = request.getUri();
        InputStream in = null;


        if ("".equals(uri)) {
            String welcomeFile = WebXMLUtil.getWelcomeFile(request.getContext());
            if (welcomeFile.endsWith(".jsp")) {
                JspServlet.getInstance().service(request, response);
            } else {

                File file = new File(Constant.ROOTFOLDER, "index.html");
                //通过fileinputstream把文件读进来
                in = new FileInputStream(file);
                response.setContentFor200(in);
                response.writeToBrowser(Constant.CODE200);
                in.close();
            }

        } else {

            File file = FileUtil.file(request.getContext().getDocBase(), request.getPathResource());
            if (file.exists()) {
                //InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("/"+uri);
                in = new FileInputStream(file);
                String extName = FileUtil.extName(file);
                response.setContentType(extName);
                response.setContentFor200(in);
                response.writeToBrowser(Constant.CODE200);
                in.close();


            } else {
                response.setContentFor404(uri);
                response.writeToBrowser(Constant.CODE404);
            }
        }
    }


}
