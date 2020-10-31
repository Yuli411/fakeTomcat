package com.yl.diytomcat.http;

import com.yl.diytomcat.catalina.WebContext;

import java.io.File;
import java.util.*;

/**
 * @Auther: Yhurri
 * @Date: 29/10/2020 01:11
 * @Description:context域
 */
public class ApplicationContext extends BaseServletContext {
    private Map<String,Object> attributesMap;
    private WebContext context;

    public ApplicationContext(WebContext webContext){
        context = webContext;
        this.attributesMap = new HashMap<>();
    }

    @Override
    public void removeAttribute(String s) {
        attributesMap.remove(s);
    }
    public void setAttribute(String name, Object value) {
        attributesMap.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributesMap.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        Set<String> keys = attributesMap.keySet();
        return Collections.enumeration(keys);
    }

    public String getRealPath(String path) {
        return new File(context.getDocBase(), path).getAbsolutePath();
    }
}
