package com.yl.diytomcat.core;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import com.yl.diytomcat.util.Constant;
import com.yl.diytomcat.util.WebXMLUtil;
import org.jsoup.helper.DataUtil;


import javax.servlet.http.Cookie;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * @Auther: Yhurri
 * @Date: 24/10/2020 22:13
 * @Description:
 */
public class Response extends BaseResponse {
    private final String BLANK = " ";
    private final String CRLF = "\r\n";
    private byte[] headInfo;
    private byte[] content;
    private OutputStream outputStream;
    private int len;
    private String contentType;
    private int status;
    private List<Cookie> cookies;
    private String redirectPath;


    public Response(Socket socket) throws IOException {
        //headInfo = new StringBuffer();
        //content = new StringBuffer();
        //字节流变成字符流
        this.cookies = new ArrayList<>();
        outputStream = socket.getOutputStream();

    }


    private void createResonseHead(int status) {

        switch (status) {
            case 200:
                String head200 = StrUtil.format(Constant.RESPONSEHEAD200, contentType,this.getCookiesHeader());
                LogFactory.get().info("head200 :{}", head200);
                headInfo = head200.getBytes();
                break;
            case 404:
                String head404 = StrUtil.format(Constant.RESPONSEHEAD404, contentType);
                headInfo = head404.getBytes();
                break;
            case 500:
                String head500 = StrUtil.format(Constant.RESPONSEHEAD500, contentType);
                headInfo = head500.getBytes();
                break;
            case 302:
                String head302 = StrUtil.format(Constant.RESPONSEHEAD302, redirectPath);
                headInfo = head302.getBytes();

        }
//        headInfo.append("Date:").append(new Date()).append(CRLF);
//        headInfo.append("Service:").append("local Service/0.0.1;charset=GBK").append(CRLF);
//        headInfo.append("Content-type:text/html").append(CRLF);
//        headInfo.append("Content-length:").append(len).append(CRLF);
//        headInfo.append(CRLF);

    }

    //处理servlet
    public void writeToBrowser(int status) throws IOException {
        setStatus(status);
        createResonseHead(status);

        BufferedOutputStream bos = new BufferedOutputStream(outputStream);

        bos.write(headInfo);
        if (content != null) {
            bos.write(content);
        }
        bos.flush();

//        bufferedWriter.append(headInfo);
//        bufferedWriter.append(content);
//        bufferedWriter.flush();
//        bufferedWriter.close();
    }

    //把字符写出去
    public void writeToBrowser(String content,int status) throws IOException {
        setStatus(status);
        setContentType("text/html");
        createResonseHead(status);
        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
        bos.write(headInfo);
        bos.write(content.getBytes());
        bos.flush();
    }

    public void setContentType(String exName) {
        contentType = WebXMLUtil.getMimeType(exName);
    }


    //设置返回的页面信息 输入流为某一资源
    public void setContentFor200(InputStream in) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) != -1) {
            //把buffer写到缓存区
            baos.write(buffer, 0, len);
        }
        len = baos.size();
        content = baos.toByteArray();
        baos.close();

    }

    public void setContentFor404(String uri) {
        String text = StrUtil.format(Constant.CONTENT404, uri, uri);
        content = text.getBytes();

    }

    public void setContentFor500(Exception e) {
        String msg = e.getMessage();
        if (null != msg && msg.length() > 20)
            msg = msg.substring(0, 19);

        StringBuffer buffer = new StringBuffer();
        StackTraceElement[] stackTraces = e.getStackTrace();
        for (StackTraceElement stackTrace : stackTraces) {
            buffer.append("\t");
            buffer.append(stackTrace.toString());
            buffer.append("\r\n");
        }
        String text = StrUtil.format(Constant.CONTENT500, msg, e.toString(), buffer.toString());
        content = text.getBytes();


    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }



    @Override
    public void addCookie(Cookie cookie) {
        if (cookie != null) {
            cookies.add(cookie);
        }
    }

    public List<Cookie> getCookies() {
        return cookies;
    }


    public String getCookiesHeader() {
        if (cookies.size() == 0) {
            return "";
        }
        StringBuffer stringbuffer = new StringBuffer();
        String pattern = "EEE, d MMM yyyy HH:mm:ss 'GMT'";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        for (Cookie cookie : cookies) {
            stringbuffer.append(CRLF);
            stringbuffer.append("Set-Cookie: ");
            stringbuffer.append(cookie.getName() + "=" + cookie.getValue() + "; ");
            Date now = new Date();
            Date expire = DateUtil.offset(now, DateField.MINUTE, cookie.getMaxAge());
            String format = sdf.format(expire);
            stringbuffer.append("expires=" + format + "; ");
            if (cookie.getPath() != null) {
                stringbuffer.append("path=" + cookie.getPath());
            }
            stringbuffer.append(CRLF);

        }

        return stringbuffer.toString();


    }

    public String getRedirectPath(){
        return redirectPath;
    }

    public void sendRedirect(String redirectPath){
        this.redirectPath = redirectPath;
    }

}
