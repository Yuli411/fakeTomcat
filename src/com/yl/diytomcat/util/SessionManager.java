package com.yl.diytomcat.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.yl.diytomcat.core.Request;
import com.yl.diytomcat.core.Response;
import com.yl.diytomcat.http.StandardSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: Yhurri
 * @Date: 30/10/2020 16:20
 * @Description: 创建或获取session通过内置的map结构保存所有的session 并创建另一线程根据过期时间清理session
 */
public class SessionManager {
    //避免出现线程安全问题
    private static Map<String, StandardSession> sessionMap = new ConcurrentHashMap<>();
    //每30分钟做一次session清理 session30分钟后过期
    private static int defaultTimeOut = getTimeOut();

    static {
        startSessionInvalideCheckThread();
    }

    private static int getTimeOut() {
        int defaultTimeOut = 30;
        try {
            Document document = Jsoup.parse(Constant.WEBXML, "utf-8");
            Elements select = document.select("session-config session-timeout");
            if (select.isEmpty()) {
                return defaultTimeOut;
            }
            String timeout = select.text();
            return Integer.parseInt(timeout);
        } catch (IOException e) {
            e.printStackTrace();
            return defaultTimeOut;
        }
    }

    private static void startSessionInvalideCheckThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    deleteSessionExpired();
                    try {
                        Thread.currentThread().sleep(30 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    private static void deleteSessionExpired() {
        Set<String> jSessionIDs = sessionMap.keySet();
        for (String jsessionid : jSessionIDs) {
            StandardSession standardSession = sessionMap.get(jsessionid);
            long lastAccessedTime = standardSession.getLastAccessedTime();
            long currentTime = System.currentTimeMillis();
            long interval = currentTime - lastAccessedTime;
            if (interval > standardSession.getMaxInactiveInterval() * 1000L) {
                sessionMap.remove(jsessionid);
            }
        }
    }

    public static String createJSessionID() {
        byte[] bytes = RandomUtil.randomBytes(16);
        String jSessionId = new String(bytes);
        jSessionId = SecureUtil.md5(jSessionId).toUpperCase();
        return jSessionId;
    }

    public static HttpSession getSession(String jSessionID, Request request, Response response) {
        if (jSessionID==null||jSessionID.length()==0) {
            jSessionID = createJSessionID();
            return configSession(jSessionID, null, request, response);

        } else {
            StandardSession standardSession = sessionMap.get(jSessionID);
            if (standardSession == null) {
                standardSession = configSession(jSessionID, standardSession, request, response);
            }
            standardSession.setLastAccessedTime(System.currentTimeMillis());
            return standardSession;
        }
    }

    private static StandardSession configSession(String jSessionID, StandardSession standardSession, Request request, Response response) {
        jSessionID = createJSessionID();
        standardSession = new StandardSession(request.getServletContext(), jSessionID);
        standardSession.setMaxInactiveInterval(defaultTimeOut);
        standardSession.setLastAccessedTime(System.currentTimeMillis());

        sessionMap.put(jSessionID, standardSession);
        Cookie cookie =  new Cookie("JSESSIONID",jSessionID);
        cookie.setMaxAge(standardSession.getMaxInactiveInterval());
        cookie.setPath(request.getContextPath());
        response.addCookie(cookie);
        return standardSession;
    }

}
