package com.yl.diytomcat.util;

import cn.hutool.core.io.FileUtil;
import com.yl.diytomcat.catalina.WebContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Yhurri
 * @Date: 26/10/2020 00:11
 * @Description:
 */
public class WebXMLUtil {
    private static Map<String, String> mimeTypeMap;

    //get resource path
    public static String getWelcomeFile(WebContext context) {
        String webXML = FileUtil.readUtf8String(Constant.WEBXML);
        Document document = Jsoup.parse(webXML);

        Elements elements = document.select("welcome-file");
        for (Element element : elements) {
            String fileName = element.text();
            File file = new File(context.getDocBase(), fileName);
            if (!file.exists()) {
                continue;
            }
            return file.getName();

        }

        return "index.html";
    }

    public static void initMimeType() {
        mimeTypeMap = new HashMap<>();
        String webXML = FileUtil.readUtf8String(Constant.WEBXML);
        Document document = Jsoup.parse(webXML);
        Elements elements = document.select("mime-mapping");
        for (Element element : elements) {
            String extension = element.selectFirst("extension").text();
            String mimeType = element.selectFirst("mime-type").text();
            mimeTypeMap.put(extension, mimeType);
        }
    }

    //为避免map初始化多次 所以此处map的初始化放到静态方法里 且在此处加入同步代码块
    public static String getMimeType(String extension) {
        if (mimeTypeMap == null) {
            synchronized (WebXMLUtil.class) {
                if (mimeTypeMap == null) {
                    initMimeType();
                }
            }
        }
        String mimeType = mimeTypeMap.get(extension);
        if (mimeType == null) {
            return "text/html";
        }

        return mimeType;
    }


}
