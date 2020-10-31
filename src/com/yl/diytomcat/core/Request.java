package com.yl.diytomcat.core;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import com.yl.diytomcat.catalina.Host;
import com.yl.diytomcat.catalina.Service;
import com.yl.diytomcat.catalina.WebContext;
import com.yl.diytomcat.http.ApplicationRequestDispatcher;
import com.yl.diytomcat.util.WebXMLUtil;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import java.util.*;

/**
 * @Auther: Yhurri
 * @Date: 24/10/2020 22:13
 * @Description:
 */
public class Request extends BaseRequest {
    private final String CRLF = "\r\n";
    private String httpRequest;
    private String uri;
    private String method;
    private String argument;
    private byte[] buffer;
    private int len;
    private Map<String, List<String>> paramMap;//param sent from front end
    private String path; //the name of the context
    private String pathResource; //the path of the resource
    private Service service; //use service to get host and other core component
    private WebContext context;
    private Map<String, String> headerMap;
    private Socket socket;
    private Cookie[] cookies;
    private HttpSession session;
    private Map<String,Object> attributesMap;

    public Request(Socket socket, Service service) throws IOException {
        this.socket = socket;
        this.service = service;
        InputStream inputStream = socket.getInputStream();
        buffer = new byte[1024 * 1024];
        len = inputStream.read(buffer);
        httpRequest = new String(buffer, 0, len);
        headerMap = new HashMap<>();
        attributesMap = new HashMap<>();
        parseHeader();
        parseCookie();
        System.out.println(httpRequest);
        parseHttpRequest(httpRequest);
        parseContext();


    }

    public Request(InputStream inputStream, Service service) throws IOException {
        this.service = service;
        buffer = new byte[1024 * 1024];
        len = inputStream.read(buffer);
        httpRequest = new String(buffer, 0, len);
        headerMap = new HashMap<>();
        parseHeader();
        parseHttpRequest(httpRequest);
        parseContext();

    }

    public String getHttpRequest() {
        return httpRequest;
    }


    public String getUri() {
        return uri;
    }


    public String getArgument() {
        return argument;
    }


    /**
     * 设置好context的path和resource path
     */
    private void parseContext() throws FileNotFoundException {
        Host defaultHost = service.getEngine().getDefaultHost();
        int index = uri.indexOf("/");
        LogFactory.get().info("index :" + index);
        if (index == -1) {
            if (defaultHost.getContextMap().get("/" + uri) != null) {
                path = "/" + uri;
                context = defaultHost.getContextMap().get(path);
                pathResource = WebXMLUtil.getWelcomeFile(context);

            } else {
                path = "/";
                pathResource = uri;
            }
        } else {
            path = "/" + uri.substring(0, index);
            index = uri.lastIndexOf("/");
            pathResource = uri.substring(index + 1);
            //pathResource = uri.substring(index + 1);

        }
        LogFactory.get().info("path :" + path);
        LogFactory.get().info("uri :" + uri);
        LogFactory.get().info("pathResource :" + pathResource);


        context = defaultHost.getContextMap().get(path);
        //防止后面调用空指针异常
        if (context == null) {
            context = defaultHost.getContextMap().get("/");
        }


    }

    public void parseHttpRequest(String httpRequest) {
        LogFactory.get().info("start to parse request");
        // 获取请求方式 从开头到第一个/ 左闭右开
        method = httpRequest.substring(0, httpRequest.indexOf("/") - 1);
        System.out.println(method);
        int idx1 = httpRequest.indexOf("/") + 1;
        int idx2 = httpRequest.indexOf("HTTP/");
        uri = httpRequest.substring(idx1, idx2).trim();
        int queryIndex = uri.indexOf("?");
        if (queryIndex >= 0) {
            String[] urlArray = uri.split("\\?");
            uri = urlArray[0];
            LogFactory.get().info("uri :" + uri);
            argument = urlArray[1];
            LogFactory.get().info("argument :" + argument);
        }

        if (method.equals("POST")) {
            String astr = httpRequest.substring(httpRequest.lastIndexOf(CRLF) + 1).replaceAll("\n", "");
            if (argument == null) {
                argument = astr;
            } else {
                argument += "&" + astr;
            }
        }


        convertToMap(argument);
    }

    private void convertToMap(String argument) {
        if (argument != null && !"".equals(argument)) {
            paramMap = new HashMap<String, List<String>>();
            String[] params = argument.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                String key = keyValue[0] != null ? decode(keyValue[0], "utf-8") : null;
                String value = keyValue[1] != null ? decode(keyValue[1], "utf-8") : null;
                LogFactory.get().info("key :" + key);
                LogFactory.get().info("value :" + value);
                if (!paramMap.containsKey(key)) {
                    paramMap.put(key, new ArrayList<String>());
                }

                paramMap.get(key).add(value);
            }
        }
    }

    private String decode(String value, String enc) {
        try {
            return java.net.URLDecoder.decode(value, enc);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public WebContext getContext() {
        return context;
    }


    public String getPath() {
        return path;
    }

    public String getPathResource() {
        return pathResource;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public ServletContext getServletContext() {
        return context.getServletContext();
    }

//
//    @Override
//    public String getParameter(String s) {
//        List<String> params = paramMap.get(s);
//        if (params.size() != 0){
//            return null;
//        }
//    }

    public void parseHeader() {
        StringReader stringReader = new StringReader(httpRequest);
        List<String> lines = new ArrayList<>();
        IoUtil.readLines(stringReader, lines);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() == 0) {
                continue;
            }
            String[] split = line.split(":");
            headerMap.put(split[0].trim(), split[1].trim());
        }

    }

    @Override
    public String getHeader(String s) {

        return headerMap.get(s);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> headerNames = headerMap.keySet();
        return Collections.enumeration(headerNames);
    }

    @Override
    public int getIntHeader(String s) {
        String headerContent = headerMap.get(s);
        return Integer.parseInt(headerContent);
    }

    @Override
    public String getLocalAddr() {
        return socket.getLocalAddress().getHostAddress();
    }

    @Override
    public String getLocalName() {
        return socket.getLocalAddress().getHostName();
    }

    @Override
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    @Override
    public String getProtocol() {
        return "HTTP:/1.1";
    }

    @Override
    public String getRemoteAddr() {
        InetSocketAddress remoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        String temp = remoteSocketAddress.getAddress().toString();
        return StrUtil.subAfter(temp, "/", false);


    }

    @Override
    public String getRemoteHost() {
        InetSocketAddress remoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        return remoteSocketAddress.getHostName();
    }

    @Override
    public int getRemotePort() {
        return socket.getPort();
    }

    @Override
    public String getScheme() {
        return "http";
    }

    @Override
    public String getServerName() {
        return getHeader("Host").trim();
    }

    @Override
    public int getServerPort() {

        return getLocalPort();
    }

    @Override
    public String getContextPath() {

        return context.getPath();
    }

    @Override
    public String getRequestURI() {
        return getUri();
    }

    @Override
    public String getServletPath() {
        return uri;
    }

    @Override
    public Cookie[] getCookies() {
        return cookies;
    }

    public void setCookies(Cookie[] cookies) {
        this.cookies = cookies;
    }

    @Override
    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    private void parseCookie() {
        List<Cookie> cookies = new ArrayList<>();
        String cookieString = headerMap.get("Cookie");
        if (cookieString != null) {
            String[] split = cookieString.split(";");

            for (String s : split) {
                String[] keyValue = s.split("=");
                String cookieName = keyValue[0].replaceAll(" ", "");
                Cookie cookie = new Cookie(cookieName, keyValue[1].trim());
                cookies.add(cookie);
            }

            this.cookies = ArrayUtil.toArray(cookies, Cookie.class);

        }

    }

    public String getSessionID() {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;

    }

    public RequestDispatcher getRequestDispatcher(String pathResource) {
        setPathResource(pathResource);
        return new ApplicationRequestDispatcher(pathResource);
    }

    public void setPathResource(String pathResource) {
        this.pathResource = pathResource;

    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public Object getAttribute(String s) {
        return attributesMap.get(s);
    }

    @Override
    public void setAttribute(String s, Object o) {
        attributesMap.put(s,o);
    }

    @Override
    public void removeAttribute(String s) {
        attributesMap.remove(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> keys = attributesMap.keySet();
        return Collections.enumeration(keys);
    }


}
