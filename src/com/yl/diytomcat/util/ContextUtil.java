package com.yl.diytomcat.util;

import cn.hutool.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


/**
 * @Auther: Yhurri
 * @Date: 26/10/2020 22:42
 * @Description:
 */
public class ContextUtil {
    public static String getWatchedResource(){
        String contextXML = FileUtil.readUtf8String(Constant.CONTEXTXML);
        Document document = Jsoup.parse(contextXML);
        Element watchedResource = document.select("WatchedResource").first();
        return watchedResource.text();
    }
}
