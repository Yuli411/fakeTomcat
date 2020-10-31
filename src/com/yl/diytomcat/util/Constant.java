package com.yl.diytomcat.util;

import cn.hutool.system.SystemUtil;

import java.io.File;

/**
 * @Auther: Yhurri
 * @Date: 25/10/2020 19:11
 * @Description:
 */
public class Constant {

    public static final int CODE200 = 200;
    public static final int CODE302 = 302;
    public static final int CODE404 = 404;
    public static final int CODE500 = 500;
    public final static File WEBAPPFOLDER = new File(SystemUtil.get("user.dir"), "webapp");
    public final static File ROOTFOLDER = new File(WEBAPPFOLDER, "ROOT");

    public final static File CONFFOLDER = new File(SystemUtil.get("user.dir"), "conf");
    public final static File SERVERXML = new File(CONFFOLDER, "server.xml");
    public final static File WEBXML = new File(CONFFOLDER,"web.xml");
    public static final File CONTEXTXML = new File(CONFFOLDER, "context.xml");
    public final static String WORKFOLDER = SystemUtil.get("user.dir") + File.separator + "work";

    public final static String RESPONSEHEAD200 =
            "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: {}{}\r\n\r\n";

    public static final String RESPONSEHEAD404 =
            "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: {}\r\n\r\n";

    public static final String RESPONSEHEAD500 = "HTTP/1.1 500 Internal Server Error\r\n"
            + "Content-Type: {}\r\n\r\n";

    public static final String RESPONSEHEAD302 =
            "HTTP/1.1 302 Found\r\nLocation: {}\r\n\r\n";

    public static final String CONTENT404 =
            "<html><head><title>DIY Tomcat/1.0.1 - Error report</title><style>" +
                    "<!--H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} " +
                    "H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} " +
                    "H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} " +
                    "BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} " +
                    "B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} " +
                    "P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}" +
                    "A {color : black;}A.name {color : black;}HR {color : #525D76;}--></style> " +
                    "</head><body><h1>HTTP Status 404 - {}</h1>" +
                    "<HR size='1' noshade='noshade'><p><b>type</b> Status report</p><p><b>message</b> <u>{}</u></p><p><b>description</b> " +
                    "<u>The requested resource is not available.</u></p><HR size='1' noshade='noshade'><h3>DiyTocmat 1.0.1</h3>" +
                    "</body></html>";

    public static final String CONTENT500 = "<html><head><title>DIY Tomcat/1.0.1 - Error report</title><style>"
            + "<!--H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} "
            + "H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} "
            + "H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} "
            + "BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} "
            + "B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} "
            + "P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}"
            + "A {color : black;}A.name {color : black;}HR {color : #525D76;}--></style> "
            + "</head><body><h1>HTTP Status 500 - An exception occurred processing {}</h1>"
            + "<HR size='1' noshade='noshade'><p><b>type</b> Exception report</p><p><b>message</b> <u>An exception occurred processing {}</u></p><p><b>description</b> "
            + "<u>The server encountered an internal error that prevented it from fulfilling this request.</u></p>"
            + "<p>Stacktrace:</p>" + "<pre>{}</pre>" + "<HR size='1' noshade='noshade'><h3>DiyTocmat 1.0.1</h3>"
            + "</body></html>";

}
