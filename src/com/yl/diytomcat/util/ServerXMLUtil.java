package com.yl.diytomcat.util;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import com.yl.diytomcat.catalina.Connector;
import com.yl.diytomcat.catalina.Host;
import com.yl.diytomcat.catalina.WebContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Yhurri
 * @Date: 25/10/2020 21:10
 * @Description: 将xml的内容映射到对象上 并对需要的对象进行初始化
 */
public class ServerXMLUtil {
    public static List<WebContext> getContexts(Host host){
        List<WebContext> list = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constant.SERVERXML);
        Document document = Jsoup.parse(xml);
        Elements contexts = document.select("Context");

        for (Element context : contexts){
            String path = context.attr("path");
            String docBase = context.attr("docBase");
            Boolean reloadable = Convert.toBool(context.attr("reloadable"), true);
            WebContext webContext = new WebContext(path,docBase,host,reloadable);
            list.add(webContext);
        }
        return list;

    }

    public static String getHostName(){
        String serverXML = FileUtil.readUtf8String(Constant.SERVERXML);
        Document document = Jsoup.parse(serverXML);
        Element host = document.select("Host").first();
        return host.attr("name");
    }

    /**
     *
     * @return the name of default host
     */
    public static String getEngineDefaultHost(){
        String serverXML = FileUtil.readUtf8String(Constant.SERVERXML);
        Document document = Jsoup.parse(serverXML);
        Element engine = document.select("Engine").first();
        return engine.attr("defaultHost");
    }

    public static List<Host> getHosts(){
        String serverXML = FileUtil.readUtf8String(Constant.SERVERXML);
        Document document = Jsoup.parse(serverXML);
        Elements hosts = document.select("Host");
        List<Host> list = new ArrayList<>();
        for(Element hostE : hosts){
            Host host = new Host();
            String name = hostE.attr("name");
            host.setName(name);
            list.add(host);
        }

        return list;
    }
    public static String getServiceName() {
        String xml = FileUtil.readUtf8String(Constant.SERVERXML);
        Document d = Jsoup.parse(xml);

        Element host = d.select("Service").first();
        return host.attr("name");
    }


    public static List<Connector> getConnectors(){
        List<Connector> list = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constant.SERVERXML);
        Document document = Jsoup.parse(xml);
        Elements connectors = document.select("Connector");
        for (Element connectorE : connectors){
            Connector connector = new Connector();
            String port = connectorE.attr("port");
            connector.setPort(port);
            list.add(connector);
        }

        return list;
    }
 }
