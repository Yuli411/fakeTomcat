package com.yl.diytomcat.core;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import com.yl.diytomcat.catalina.Service;
import com.yl.diytomcat.classloader.CommonClassLoader;
import com.yl.diytomcat.util.Constant;
import com.yl.diytomcat.util.ThreadUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * @Auther: Yhurri
 * @Date: 24/10/2020 22:10
 * @Description:
 */
public class Server {
    private ServerSocket serverSocket;
    private boolean isRunning;
    private Service service;

    private static void logJVM() {
        Map<String, String> infos = new LinkedHashMap<>();
        infos.put("Service version", "DiyTomcat/1.0.1");
        infos.put("Service built", "2020-07-08 10:20:22");
        infos.put("Service number", "1.0.1");
        infos.put("OS Name\t", SystemUtil.get("os.name"));
        infos.put("OS Version", SystemUtil.get("os.version"));
        infos.put("Architecture", SystemUtil.get("os.arch"));
        infos.put("Java Home", SystemUtil.get("java.home"));
        infos.put("JVM Version", SystemUtil.get("java.runtime.version"));
        infos.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));
        Set<String> keys = infos.keySet();
        for (String key : keys) {
            LogFactory.get().info(key + ":\t\t" + infos.get(key));
        }
    }

//    public void receiveRequest() throws IOException {
//        HttpProcessor httpProcessor = new HttpProcessor();
//        httpProcessor.execute(serverSocket,isRunning,service);
//
//
//    }

    public void start() throws IOException {
        TimeInterval timeInterval = DateUtil.timer();
        logJVM();
        //int port = 18188;
        isRunning = true;
        //if (NetUtil.isUsableLocalPort(port)){
        service = new Service(isRunning);


        // LogFactory.get().info("Starting ProtocolHandler [http-bio-{}]",port);

        //serverSocket = new ServerSocket(port);

        LogFactory.get().info("Initialization processed in {} ms", timeInterval.intervalMs());


    }


}
