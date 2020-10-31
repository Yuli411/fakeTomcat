package com.yl.diytomcat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.log.LogFactory;
import com.yl.diytomcat.classloader.WebappClassLoader;
import com.yl.diytomcat.http.ApplicationContext;
import com.yl.diytomcat.http.StandardServletConfig;
import com.yl.diytomcat.util.ContextUtil;
import com.yl.diytomcat.watcher.ContextFileChangeWatcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @Auther: Yhurri
 * @Date: 25/10/2020 19:42
 * @Description: one app == one context
 */
public class WebContext {
    private String path;
    //the location of the webcontext
    private String docBase;
    private File contextXMLFile;

    private Map<String, String> urlClassName;
    private Map<String, String> urlServletName;
    private Map<String, String> servletNameClassName;
    private Map<String, String> classNameServletName;
    private WebappClassLoader webappClassLoader;

    private Host host;
    private boolean reloadable;
    private ContextFileChangeWatcher contextFileChangeWatcher;

    private ServletContext servletContext;
    private Map<Class<?>, HttpServlet> servletMap;
    private Map<String,Map<String,String>> servletClassNameInitParams;
    private List<String> loadOnStartUpList;
    private Map<String, Filter> filterMap;
    private Map<String,String> filterNameFilterClassName;
    private Map<String,String> urlFilterName;
    private Map<String,String> urlFilterClassName;
    private Map<String,String> filterClassNameFilterName;
    private Map<String,Map<String,String>> filterClassNameInitParams;
    private List<ServletContextListener> servletContextListeners;
    private ServletContextEvent event;

    public WebContext(String path, String docBase, Host host, Boolean reloadable) {
        TimeInterval timeInterval = DateUtil.timer();
        this.path = path;
        this.docBase = docBase;
        contextXMLFile = new File(docBase, ContextUtil.getWatchedResource());
        this.reloadable = reloadable;
        this.host = host;
        this.servletContext = new ApplicationContext(this);
        this.servletMap = new HashMap<>();
        this.servletClassNameInitParams = new HashMap<>();
        this.loadOnStartUpList = new ArrayList<>();
        this.filterMap = new HashMap<>();
        urlClassName = new HashMap<>();
        urlServletName = new HashMap<>();
        servletNameClassName = new HashMap<>();
        classNameServletName = new HashMap<>();
        filterClassNameFilterName = new HashMap<>();
        filterNameFilterClassName = new HashMap<>();
        urlFilterClassName = new HashMap<>();
        urlFilterName = new HashMap<>();
        filterClassNameInitParams = new HashMap<>();
        //new webappClassLoader 并设定commonClassLoader是它的parent
        servletContextListeners = new ArrayList<>();
        webappClassLoader = new WebappClassLoader(docBase, Thread.currentThread().getContextClassLoader());
        try {
            deploy();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        LogFactory.get().info("Deployment of web application directory {} has finished in {} ms",
                this.docBase, timeInterval.intervalMs());


    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDocBase() {
        return docBase;
    }

    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    private void init() throws FileNotFoundException {
        if (contextXMLFile.exists()) {
            String contextXML = FileUtil.readUtf8String(contextXMLFile);
            Document document = Jsoup.parse(contextXML);
            parseServletMapping(document);
            parseInitParam(document);
            parseStartOnLoadUp(document);
            startServletOnLoadUp();
            parseFilter(document);
            parseFilterInitParams(document);
            parseListener(document);
            initFilter();


        }
    }

    private void parseServletMapping(Document document) {
        Elements elements = document.select("servlet-mapping url-pattern");
        for (Element element : elements) {
            String url = element.text();
            String servletName = element.parent().select("servlet-name").first().text();
            urlServletName.put(url, servletName);
        }
        elements = document.select("servlet servlet-class");
        for (Element element : elements) {
            String servletClass = element.text();
            String servletName = element.parent().select("servlet-name").first().text();
            servletNameClassName.put(servletName, servletClass);
            classNameServletName.put(servletClass, servletName);
        }

        Set<String> urls = urlServletName.keySet();
        for (String url : urls) {
            String servletName = urlServletName.get(url);
            String className = servletNameClassName.get(servletName);
            urlClassName.put(url, className);
        }
    }

    private void parseInitParam(Document document){
        Elements elements = document.select("servlet servlet-class");
        for (Element element : elements){
            String servletClassName = element.text();
            Elements initParams = element.parent().select("init-param");
            if (!initParams.isEmpty()) {
                Map<String, String> params = new HashMap<>();
                for (Element initParam : initParams) {
                    String paramName = initParam.select("param-name").first().text();
                    String paramValue = initParam.select("param-value").first().text();
                    params.put(paramName,paramValue);
                }

                servletClassNameInitParams.put(servletClassName,params);
            }

        }


    }

    private void parseFilterInitParams(Document document){
        Elements filterInits = document.select("filter filter-class");
        for (Element filterInit : filterInits){
            String filterClassName = filterInit.text();
            Elements select = filterInit.parent().select("init-param");
            Map<String,String> params = new HashMap<>();
            for (Element init : select){
                String paramName = init.select("param-name").first().text();
                String paramValue = init.select("param-value").first().text();
                params.put(paramName,paramValue);
            }

            filterClassNameInitParams.put(filterClassName,params);
        }

    }

    private void parseStartOnLoadUp(Document document){
        Elements elements = document.select("load-on-startup");
        for (Element element : elements){
            String loadOnStartupServletClassName = element.parent().select("servlet-class").text();
            loadOnStartUpList.add(loadOnStartupServletClassName);
        }
    }

    private void startServletOnLoadUp(){
        for (String servletClassName : loadOnStartUpList){

            Class<?> serveletClass = null;
            try {
                serveletClass = webappClassLoader.loadClass(servletClassName);
                getHttpServlet(serveletClass);

            } catch (ClassNotFoundException | ServletException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }


        }
    }

    private void parseFilter(Document document){
        Elements filterNames = document.select("filter filter-name");
        for (Element filterName: filterNames){
            String filterClassName = filterName.parent().select("filter-class").first().text();
            String name = filterName.text();
            filterNameFilterClassName.put(name,filterClassName);
            filterClassNameFilterName.put(filterClassName,name);

        }
        Elements filterMappings = document.select("filter-mapping filter-name");
        for (Element filterMapping:filterMappings){
            String url = filterMapping.parent().select("url-pattern").first().text();
            String filterName = filterMapping.text();
            urlFilterName.put(url,filterName);
        }

        Set<String> urls = urlFilterName.keySet();
        for (String url : urls){
            String filterName = urlFilterName.get(url);
            String filterClassName = filterNameFilterClassName.get(filterName);
            urlFilterClassName.put(url,filterClassName);
        }

    }

    private void parseListener(Document document){
        Elements elements = document.select("listener listener-class");
        for (Element element : elements){
            try {
                Class<?> listenerClass = webappClassLoader.loadClass(element.text());
                System.out.println(listenerClass.getName());
                event = new ServletContextEvent(servletContext);
                ServletContextListener  servletContextListener = (ServletContextListener) listenerClass.newInstance();
                servletContextListener.contextInitialized(event);
                servletContextListeners.add(servletContextListener);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, String> getUrlClassName() {
        return urlClassName;
    }

    public Map<String, String> getUrlServletName() {
        return urlServletName;
    }

    public Map<String, String> getServletNameClassName() {
        return servletNameClassName;
    }

    public Map<String, String> getClassNameServletName() {
        return classNameServletName;
    }

    public String getClassName(String uri) {
        return urlClassName.get(uri);
    }

    public WebappClassLoader getWebappClassLoader() {
        return webappClassLoader;
    }

    public boolean isReloadable() {
        return reloadable;
    }

    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }

    public void deploy() throws FileNotFoundException {
        //解析xml文件的servlet path
        init();
        //开启监听功能
        if (reloadable) {
            contextFileChangeWatcher = new ContextFileChangeWatcher(this);
            contextFileChangeWatcher.start();
        }
    }


    public void stop() {
        if (webappClassLoader != null) {
            webappClassLoader.stop();
        }
        if (contextFileChangeWatcher != null) {

            contextFileChangeWatcher.stop();
        }

        destroyServlet();
        for (ServletContextListener servletContextListener:servletContextListeners){
            servletContextListener.contextDestroyed(event);
        }
    }

    private void destroyServlet(){
        Collection<HttpServlet> servlets = servletMap.values();
        for (HttpServlet servlet : servlets){
            servlet.destroy();//调用销毁方法执行一些操作
        }

    }

    public void reload() {
        //为了对host中的map进行操作 必须调用父类中的方法
        host.reload(this);
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    //当前端请求一个servlet时创建 并用类对象作为其key
    public synchronized HttpServlet getHttpServlet(Class<?> servletClass) throws IllegalAccessException, InstantiationException, ServletException {
        HttpServlet httpServlet = servletMap.get(servletClass);
        if (httpServlet == null) {
            httpServlet = (HttpServlet) servletClass.newInstance();
            Map<String, String> initParams = servletClassNameInitParams.get(servletClass.getName());
            String servletName = classNameServletName.get(servletClass.getName());
            StandardServletConfig servletConfig = new StandardServletConfig(servletContext,initParams,servletName);
            httpServlet.init(servletConfig);
            servletMap.put(servletClass,httpServlet);
        }
        return httpServlet;
    }

    private void initFilter(){
        Set<String> filterClassNames = filterClassNameInitParams.keySet();
        for (String filterClassName: filterClassNames){
            try {
                Filter filter = (Filter)webappClassLoader.loadClass(filterClassName).newInstance();
                Map<String, String> initParams = filterClassNameInitParams.get(filterClassName);
                String filterName = filterClassNameFilterName.get(filterClassName);
                StandardFilterConfig standardFilterConfig = new StandardFilterConfig(servletContext,initParams,filterName);
                filter.init(standardFilterConfig);
                filterMap.put(filterClassName,filter);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (ServletException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean isMatched(String filterUrl,String uri){
        if (filterUrl.equals(uri)){
            return true;
        }

        if (filterUrl.equals("/*")){
            return true;
        }

        if (uri != null && uri.length() !=0){
            if (uri.endsWith(".jsp")){
                return true;
            }
        }

        return false;

    }

    public List<Filter> getMatchedFilters(String uri){
        List<Filter> filters = new ArrayList<>();
        Set<String> urls = urlFilterClassName.keySet();
        for (String url : urls){
            if (isMatched(url,uri)){
                String filterClassName = urlFilterClassName.get(url);
                Filter filter = filterMap.get(filterClassName);
                filters.add(filter);
            }
        }

        return filters;
    }


}
