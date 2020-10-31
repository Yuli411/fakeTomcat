package com.yl.diytomcat.classloader;

import cn.hutool.core.io.FileUtil;
import org.apache.jasper.tagplugins.jstl.core.Url;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @Auther: Yhurri
 * @Date: 27/10/2020 23:33
 * @Description:
 */
public class WebappClassLoader extends URLClassLoader {
    //context的绝对路径
    public WebappClassLoader(String docBase, ClassLoader commonClassLoader) {
        super(new URL[]{},commonClassLoader);
        //web文件编译后都在WEB-INF/classes/下
        File webinFolder = new File(docBase,"web/WEB-INF");
        File classesFolder = new File(webinFolder,"classes");
        File libFolder = new File(webinFolder,"lib");
        URL url;
        try {
            //加入 / 使得url识别为一个目录
            url = new URL("file:" + classesFolder.getAbsolutePath() + "/");
            this.addURL(url);
            if (libFolder.exists()) {
                File[] files = libFolder.listFiles();
                for (File lib : files) {
                    url = new URL("file:" + lib.getAbsolutePath());
                    this.addURL(url);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        try {
            close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
