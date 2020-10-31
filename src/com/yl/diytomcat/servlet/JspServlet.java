package com.yl.diytomcat.servlet;

import cn.hutool.core.io.FileUtil;
import com.yl.diytomcat.core.Request;
import com.yl.diytomcat.core.Response;
import com.yl.diytomcat.util.Constant;
import com.yl.diytomcat.util.WebXMLUtil;
import jdk.nashorn.internal.ir.ReturnNode;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.BreakIterator;

/**
 * @Auther: Yhurri
 * @Date: 30/10/2020 19:11
 * @Description:
 */
public class JspServlet extends HttpServlet {
    private static JspServlet instance;
    private JspServlet(){

    }

    public static JspServlet getInstance(){
        if (instance == null){
            synchronized (JspServlet.class){
                if (instance == null){
                    instance = new JspServlet();
                }
            }
        }

        return instance;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        Request request = (Request) req;
        Response response = (Response) res;
        String uri = request.getUri();
        if ("".equals(uri)) {
            String welcomeFile = WebXMLUtil.getWelcomeFile(request.getContext());
            File file = new File(Constant.ROOTFOLDER, welcomeFile);
            FileInputStream in = new FileInputStream(file);
            response.setContentFor200(in);
            response.writeToBrowser(Constant.CODE200);
            in.close();
        } else {

            File file = FileUtil.file(request.getContext().getDocBase(), request.getPathResource());
            if (file.exists()) {
                String extName = FileUtil.extName(file);
                response.setContentType(extName);
                FileInputStream in = new FileInputStream(file);
                response.setContentFor200(in);
                response.writeToBrowser(Constant.CODE200);
            } else {
                response.setContentFor404(uri);
                response.writeToBrowser(Constant.CODE404);
            }

        }


    }
}
