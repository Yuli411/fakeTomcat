package com.yl.diytomcat.watcher;

import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.log.LogFactory;
import com.yl.diytomcat.catalina.WebContext;

import java.nio.file.Path;
import java.nio.file.WatchEvent;


/**
 * @Auther: Yhurri
 * @Date: 28/10/2020 23:56
 * @Description:
 */
public class ContextFileChangeWatcher {
    private WatchMonitor watchMonitor;
    private Boolean stop;

    public ContextFileChangeWatcher(WebContext context) {
        stop = false;
        //监听的文件夹 监听子目录
        watchMonitor = WatchUtil.createAll(context.getDocBase(), Integer.MAX_VALUE, new Watcher() {
            @Override
            public void onCreate(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent, context);
            }

            @Override
            public void onModify(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent, context);
            }

            @Override
            public void onDelete(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent, context);
            }

            @Override
            public void onOverflow(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent, context);
            }
        });
    }

    private void dealWith(WatchEvent<?> event, WebContext context) {
        //取得当前发生变化的文件或文件夹的名称
        String fileName = event.context().toString();
        //
        if (stop)
            return;
        synchronized (ContextFileChangeWatcher.class) {
            if (stop)
                return;
            if (fileName.endsWith(".jar") || fileName.endsWith(".class") || fileName.endsWith(".xml")) {
                stop = true;
                LogFactory.get().info(ContextFileChangeWatcher.this + " Detect change in files under web {} ", fileName);
                context.reload();
            }
        }

    }

    public void start(){
        watchMonitor.start();
    }

    public void stop(){
        watchMonitor.close();
    }


}
