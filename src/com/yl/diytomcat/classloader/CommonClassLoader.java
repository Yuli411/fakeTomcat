package com.yl.diytomcat.classloader;


import cn.hutool.system.SystemUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @Auther: Yhurri
 * @Date: 27/10/2020 22:38
 * @Description:
 */
public class CommonClassLoader extends URLClassLoader {
    public CommonClassLoader() {
        super(new URL[]{});
        try {
            File libFolder  = new File(SystemUtil.get("user.dir"),"lib");
            File[] libs = libFolder.listFiles();
            for (File lib : libs){
                if (lib.getName().endsWith("jar")){
                    this.addURL(new URL("file:"+ lib.getAbsolutePath()));
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }


}
