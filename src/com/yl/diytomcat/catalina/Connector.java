package com.yl.diytomcat.catalina;

import cn.hutool.log.LogFactory;
import com.yl.diytomcat.core.DefaultServlet;
import com.yl.diytomcat.core.Request;
import com.yl.diytomcat.core.Response;
import com.yl.diytomcat.servlet.InvokeServlet;
import com.yl.diytomcat.servlet.JspServlet;
import com.yl.diytomcat.util.Constant;
import com.yl.diytomcat.util.SessionManager;
import com.yl.diytomcat.util.ThreadUtil;


import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * @Auther: Yhurri
 * @Date: 26/10/2020 21:28
 * @Description:
 */
public class Connector implements Runnable {
    private int port;
    private Service service;
    private Boolean isRunning;


    public int getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = Integer.parseInt(port);
    }

    //通过serverUtil得到所有带有端口的
    public void init(Boolean isRunning, Service service) {
        this.isRunning = isRunning;
        this.service = service;
        LogFactory.get().info("Initializing ProtocolHandler [http-bio-{}]", port);
        //List<Connector> connectors = ServerXMLUtil.getConnectors();

    }


    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        HttpServlet workingServlet;
                        Request request = null;
                        Response response = null;
                        try {
                            request = new Request(clientSocket, service);
                            response = new Response(clientSocket);
                            configSession(request, response);
                            //if uri = /;
                            String pathResource = request.getPathResource();
                            //get servlet class name
                            String servletClassName = request.getContext().getClassName("/" + pathResource);
                            LogFactory.get().info("servletClassName:{}", servletClassName);
                            if (servletClassName != null) {
                                workingServlet = InvokeServlet.getInstance();

                            } else if (pathResource.endsWith(".jsp")) {
                                workingServlet = JspServlet.getInstance();
                            } else {
                                //handle request of html css...
                                workingServlet = DefaultServlet.getInstance();
                            }

                            List<Filter> matchedFilters = request.getContext().getMatchedFilters(request.getUri());
                            ApplicationFIlterChain filterChain = new ApplicationFIlterChain(matchedFilters,workingServlet);
                            filterChain.doFilter(request,response);

                        } catch (IOException | ServletException e) {
                            try {
                                if (response != null) {
                                    response.setContentFor500(e);
                                    response.writeToBrowser(Constant.CODE500);
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                        } finally {
                            try {
                                if (!clientSocket.isClosed()) {
                                    clientSocket.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                };

                ThreadUtil.run(runnable);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        LogFactory.get().info("Starting ProtocolHandler [http-bio-{}]", port);
        new Thread(this).start();
    }

    public void configSession(Request request, Response response) {
        String sessionID = request.getSessionID();//via cookie parse
        HttpSession session = SessionManager.getSession(sessionID, request, response);
        request.setSession(session);

    }
}








