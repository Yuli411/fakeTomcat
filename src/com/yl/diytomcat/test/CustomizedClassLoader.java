package com.yl.diytomcat.test;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @Auther: Yhurri
 * @Date: 27/10/2020 17:52
 * @Description: 如果直接继承urlclassloader就不用写加载文件 拆分字符串的那步骤
 */
public class CustomizedClassLoader extends ClassLoader {
    private File classesFolder = new File(System.getProperty("user.dir"),"classes4test");

    @Override
    protected Class<?> findClass(String fullQualifiedName) throws ClassNotFoundException {
        byte[] data = loadClassData(fullQualifiedName);
        return defineClass(fullQualifiedName,data,0,data.length);
    }

    private byte[] loadClassData(String fullQualifiedName) throws ClassNotFoundException {
        String fileName = StrUtil.replace(fullQualifiedName, ".", "/") + ".class";
        File classFile = new File(classesFolder, fileName);
        if(!classFile.exists())
            throw new ClassNotFoundException(fullQualifiedName);

        return FileUtil.readBytes(classFile);
    }

    public static void main(String[] args) throws Exception {

        CustomizedClassLoader loader = new CustomizedClassLoader();
        System.out.println(loader.getParent());

        Class<?> how2jClass = loader.loadClass("cn.how2j.diytomcat.test.HOW2J");

        Object o = how2jClass.newInstance();

        Method m = how2jClass.getMethod("hello");

        m.invoke(o);

        System.out.println(how2jClass.getClassLoader());

    }


}
