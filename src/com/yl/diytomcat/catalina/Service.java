package com.yl.diytomcat.catalina;

import com.yl.diytomcat.util.ServerXMLUtil;

import java.net.ServerSocket;
import java.util.List;

/**
 * @Auther: Yhurri
 * @Date: 25/10/2020 22:58
 * @Description:
 */
public class Service {
    private String name;
    private Engine engine;
    private List<Connector> connectors;

    public Service(Boolean isRunning) {
        name = ServerXMLUtil.getServiceName();
        engine = new Engine();
        connectors = ServerXMLUtil.getConnectors();
        for (Connector connector : connectors){
            connector.init(isRunning, this);
            connector.start();
        }


    }

    public Engine getEngine() {
        return engine;
    }
}
