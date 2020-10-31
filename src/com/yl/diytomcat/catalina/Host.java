package com.yl.diytomcat.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.log.LogFactory;
import com.yl.diytomcat.util.Constant;
import com.yl.diytomcat.util.ServerXMLUtil;
import com.yl.diytomcat.watcher.WarFileWatcher;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: Yhurri
 * @Date: 25/10/2020 21:50
 * @Description:
 */
public class Host {
    private String name;
    private Map<String,WebContext> contextMap;

    public Host() {

        name = ServerXMLUtil.getHostName();
        contextMap = new HashMap<>();
        scanContextInServerXML();
        scanContextsUnderWebapp();
        WarFileWatcher warFileWatcher = new WarFileWatcher(this);
        warFileWatcher.start();

    }

    public WebContext getContext(String path){
        return contextMap.get(path);
    }

    public void scanContextsUnderWebapp(){
        //拿到webapp下面所有文件夹即context 作为file
        File[] files = Constant.WEBAPPFOLDER.listFiles();
        for (File file : files) {
            if (file.getName().toLowerCase().endsWith(".war")){
                loadWarFileInMap(file);
            }else{
                if (!file.isDirectory()){
                    continue;
                }
                loadFolderInMap(file);
            }

        }
    }

    private void loadFolderInMap(File context){
        String path = context.getName();
        if ("root".equals(path)){
            path = "/";
        }else{
            path ="/" + path;
        }
        //文件的绝对路径
        String docBase = context.getAbsolutePath();
        WebContext webContext = new WebContext(path,docBase,this,true);
        //通过解析浏览器的url就可以得到context 包括它的绝对路径和相对路径
        contextMap.put(path,webContext);

    }

    private void scanContextInServerXML(){
        List<WebContext> contexts = ServerXMLUtil.getContexts(this);
        for (WebContext context : contexts){
            contextMap.put(context.getPath(),context);
        }

    }

    public void loadWarFileInMap(File warFile){
        String fileName = warFile.getName();//including its ext
        int index = fileName.indexOf(".");
        String folderName = fileName.substring(0, index);
        //看war文件名是否已经存在在webapp下
        if (contextMap.get("/"+folderName)!=null){
            return;
        }

        File file  = new File(Constant.WEBAPPFOLDER,folderName);
        if (file.exists()){
            return;
        }
        //新建一个文件 在folderName下 名字为fileName
        File tempFile = FileUtil.file(Constant.WEBAPPFOLDER, folderName, fileName);
        File folderContext = tempFile.getParentFile();
        folderContext.mkdir();
        FileUtil.copyFile(warFile,tempFile);
        //解压tempFile
        String command = "jar xvf " + fileName;
        Process exec = RuntimeUtil.exec(null,folderContext,command);
        try {
            exec.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tempFile.delete();
        loadFolderInMap(folderContext);


    }


    public String getName() {
        return name;
    }

    public Map<String, WebContext> getContextMap() {
        return contextMap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void reload(WebContext context){
        LogFactory.get().info("Reloading Context with name [{}] has started", context.getPath());
        boolean reloadable = context.isReloadable();
        String docBase = context.getDocBase();
        String path = context.getPath();
        if (reloadable){
            context.stop();
            contextMap.remove(path);
            context = new WebContext(path,docBase,this,true);
            contextMap.put(path,context);
            LogFactory.get().info("Reloading Context with name [{}] has completed", context.getPath());
        }
    }
}
