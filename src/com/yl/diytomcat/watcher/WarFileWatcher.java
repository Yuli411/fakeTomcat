package com.yl.diytomcat.watcher;

import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.io.watch.Watcher;
import com.yl.diytomcat.catalina.Host;
import com.yl.diytomcat.util.Constant;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * @Auther: Yhurri
 * @Date: 31/10/2020 19:40
 * @Description:
 */
public class WarFileWatcher {
    private WatchMonitor monitor;
    private boolean stop;
    private Host host;

    public WarFileWatcher(Host host) {
        this.host = host;
        monitor = WatchUtil.createAll(Constant.WEBAPPFOLDER, Integer.MAX_VALUE, new Watcher() {
            @Override
            public void onCreate(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent, host);
            }

            @Override
            public void onModify(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent, host);
            }

            @Override
            public void onDelete(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent, host);
            }

            @Override
            public void onOverflow(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent, host);
            }
        });
    }

    private void dealWith(WatchEvent<?> watchEvent, Host host) {
        String fileName = watchEvent.context().toString();
        if (fileName.toLowerCase().endsWith(".war")) {
            File warFile = new File(Constant.WEBAPPFOLDER, fileName);
            host.loadWarFileInMap(warFile);
        }
    }

    public void start(){
        monitor.start();
    }

    public void stop(){
        monitor.close();
    }
}
