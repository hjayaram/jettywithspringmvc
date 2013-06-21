package com.server;
import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.server.nio.*;
import org.eclipse.jetty.util.thread.*;
import org.eclipse.jetty.webapp.*;

public class WebServer
{
	// TODO: You should configure this appropriately for your environment
	private static final String LOG_PATH = "./var/logs/access/yyyy_mm_dd.request.log";
	
	private static final String WEB_XML = "./webapp/WEB-INF/web.xml";
    private static final String CLASS_ONLY_AVAILABLE_IN_IDE = "blah.blah";
    private static final String PROJECT_RELATIVE_PATH_TO_WEBAPP = "./webapp";
      
    private Server server;
    private int port;
    private String bindInterface;
    
    public WebServer(int aPort)
    {
        this(aPort, null);
    }
    
    public WebServer(int aPort, String aBindInterface)
    {        
        port = aPort;
        bindInterface = aBindInterface;
    }
    
    public void start() throws Exception
    {
    	WebAppContext webAppContext = new WebAppContext();
    	
        server = new Server();
        webAppContext.setContextPath("/");
        if(isRunningInShadedJar())
        {          
        	webAppContext.setWar(getShadedWarUrl());
        }
        else
        {            
        	webAppContext.setWar(PROJECT_RELATIVE_PATH_TO_WEBAPP);
        }
        webAppContext.setServer(server);        
        server.setThreadPool(createThreadPool());
        server.addConnector(createConnector());
        server.setHandler(createHandlers(webAppContext));        
        server.setStopAtShutdown(true);
        
        server.start();       
    }
    
    public void join() throws InterruptedException
    {
        server.join();
    }
    
    public void stop() throws Exception
    {        
        server.stop();
    }
    
    private ThreadPool createThreadPool()
    {
    	// TODO: You should configure these appropriately
    	// for your environment - this is an example only
        QueuedThreadPool _threadPool = new QueuedThreadPool();
        _threadPool.setMinThreads(10);
        _threadPool.setMaxThreads(100);
        return _threadPool;
    }
    
    private SelectChannelConnector createConnector()
    {
        SelectChannelConnector _connector = new SelectChannelConnector();
        _connector.setPort(port);
        _connector.setHost(bindInterface);
        return _connector;
    }
    
    private HandlerCollection createHandlers(WebAppContext webAppContext)
    {                
    	ContextHandler _ctx = new ContextHandler();
    	
        _ctx.setContextPath("/");

        
        List<Handler> _handlers = new ArrayList<Handler>();
        
        _handlers.add(_ctx);
        _handlers.add(webAppContext);
        
        HandlerList _contexts = new HandlerList();
        _contexts.setHandlers(_handlers.toArray(new Handler[1]));
        
        RequestLogHandler _log = new RequestLogHandler();
        _log.setRequestLog(createRequestLog());
        
        HandlerCollection _result = new HandlerCollection();
        _result.setHandlers(new Handler[] {_contexts, _log});
        
        return _result;
    }
    
    private RequestLog createRequestLog()
    {
        NCSARequestLog _log = new NCSARequestLog();
    	File _logPath = new File(LOG_PATH);
        _logPath.getParentFile().mkdirs();
                
        _log.setFilename(_logPath.getPath());
        _log.setRetainDays(90);
        _log.setExtended(false);
        _log.setAppend(true);
        _log.setLogTimeZone("GMT");
        _log.setLogLatency(true);
        return _log;
    }
    
//---------------------------
// Discover the war path
//---------------------------   
    
    private boolean isRunningInShadedJar()
    {
    	
        try
        {
            Class.forName(CLASS_ONLY_AVAILABLE_IN_IDE);
            return false;
        }
        catch(ClassNotFoundException anExc)
        {
        	//return false;
            return true;
        }
    }
    
    private URL getResource(String aResource) throws IOException 
    {
    	File file = new File(aResource);
    	URI fileUri = file.toURI();
    	URL fileUrl = fileUri.toURL();
    	return fileUrl;
        //return Thread.currentThread().getContextClassLoader().getResource(aResource); 
    }
    
    private String getShadedWarUrl() throws IOException
    {
        String _urlStr = getResource(WEB_XML).toString();
        // Strip off "WEB-INF/web.xml"
        return _urlStr.substring(0, _urlStr.length() - 15);
    }
}
