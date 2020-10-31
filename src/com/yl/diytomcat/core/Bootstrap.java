package com.yl.diytomcat.core;

import cn.hutool.log.LogFactory;
import com.yl.diytomcat.classloader.CommonClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Auther: Yhurri
 * @Date: 28/10/2020 00:05
 * @Description:
 */
public class Bootstrap {
    public static void main(String[] args) {
        CommonClassLoader commonClassLoader = new CommonClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(commonClassLoader);
            Class<?> serverClass = commonClassLoader.loadClass("com.yl.diytomcat.core.Server");
            LogFactory.get().info("Classloader:{}",serverClass.getClassLoader());
            Object o = serverClass.newInstance();
            Method start = serverClass.getDeclaredMethod("start");
            start.invoke(o);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
