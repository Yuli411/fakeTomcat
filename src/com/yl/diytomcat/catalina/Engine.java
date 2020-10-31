package com.yl.diytomcat.catalina;

import com.yl.diytomcat.util.ServerXMLUtil;

import java.util.List;

/**
 * @Auther: Yhurri
 * @Date: 25/10/2020 22:31
 * @Description:
 */
public class Engine {
    private String defaultHost;
    private List<Host> hosts;

    public Engine(){
        defaultHost = ServerXMLUtil.getEngineDefaultHost();
        hosts = ServerXMLUtil.getHosts();
        //查看是否是默认host 如果不是报错
        checkDefault();
    }

    private void checkDefault() {
        if(null==getDefaultHost())
            throw new RuntimeException("the defaultHost" + defaultHost + " does not exist!");
    }


    public Host getDefaultHost(){

        //通过名称找到的defaultHost
        for (Host host : hosts) {
            if(host.getName().equals(defaultHost))
                return host;
        }
        return null;
    }

    public List<Host> getHosts() {
        return hosts;
    }
}
