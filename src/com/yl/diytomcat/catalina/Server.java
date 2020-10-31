package com.yl.diytomcat.catalina;

/**
 * @Auther: Yhurri
 * @Date: 25/10/2020 23:06
 * @Description:
 */
public class Server {
    private Service service;

    public Server(Boolean isRunning) {
        service = new Service(isRunning);
    }


}
