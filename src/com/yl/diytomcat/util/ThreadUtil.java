package com.yl.diytomcat.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: Yhurri
 * @Date: 25/10/2020 18:52
 * @Description:
 */
public class ThreadUtil {
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            20,100,60, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(10)
    );
    public static void run(Runnable runnable){
        threadPoolExecutor.execute(runnable);
    }
}
