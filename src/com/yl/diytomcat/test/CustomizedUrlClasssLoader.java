package com.yl.diytomcat.test;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @Auther: Yhurri
 * @Date: 27/10/2020 20:55
 * @Description:
 */
public class CustomizedUrlClasssLoader extends URLClassLoader {
    public CustomizedUrlClasssLoader(URL[] urls) {
        super(urls);
    }
}
