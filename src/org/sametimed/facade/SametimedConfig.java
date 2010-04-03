/**
 * 
 */
package org.sametimed.facade;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import org.eclipse.jetty.util.ajax.JSON;

/**
 * Project: sametimed
 * Package: org.sametimed.facade
 *
 * SametimedConfig
 *
 * Singleton. Keeps Sametimed configuration data, loaded from file, {@value #CONFIG_FILE_PATH}
 * Main method is {@link #loadConfig(ServletContext)}, it creates the {@code SametimedConfig}
 * instance if file is still not loaded and returns already existing instance if
 * file is already loaded.
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 23, 2010 11:17:11 PM 
 *
 */
public class SametimedConfig implements JSON.Generator {
    
    public static final String CONFIG_FILE_PATH = "/WEB-INF/sametimed.xml";    
    
    private static final Logger log = LoggerFactory
            .getLogger(SametimedConfig.class);
    private static boolean usedDefaults = false;
    
    private static InputStream configFile = null;
    private static SametimedConfig instance;
    
    private final ServiceData serviceData = new ServiceData();

    /**
     * Loads data from configuration file, lying in passed servlet context.
     * If file is already loaded - just returns the cached instance, even if
     * the different context passed. (means {@code SametimedConfig} is singleton)
     * 
     * @param fromContext context to load file from
     * @return {@code SametimedConfig} instance
     */
    public static SametimedConfig loadConfig(ServletContext fromContext) {
        if (configFile == null) { // not loaded for the moment
            instance = new SametimedConfig(fromContext.getResourceAsStream(CONFIG_FILE_PATH));
        }
        return instance;
    }
    
    /**
     * Initiate and Load data from configuration file 
     * 
     * @param confFile configuration file
     */
    private SametimedConfig(InputStream confFile) {
        if (confFile != null) {
            final DocumentBuilderFactory factory = 
                                       DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            try {
                loadFromXML(factory.newDocumentBuilder().parse(confFile));
                log.info("configuration loaded from: {}", CONFIG_FILE_PATH);                
                configFile = confFile; 
            } catch (Exception e) {                
                log.debug("exception appeared while loading XML config: {} from {}", 
                                                e.getClass() + " " + e.getMessage(),
                                                CONFIG_FILE_PATH);
                loadDefaults();             
                log.debug("ignored exception, loaded defaults");                
            }
        } else {
            loadDefaults();
            log.debug("no file passed or found at {}, loaded defaults", CONFIG_FILE_PATH);          
        }        
    }

    /**
     * Loads values from XML document
     * 
     * @param doc source XML document
     * @throws XPathExpressionException when some of properties were not found
     */
    protected void loadFromXML(Document doc) throws XPathExpressionException {
        final XPath xpath = XPathFactory.newInstance().newXPath();
        
        // optional
        final String absoluteURLElmPresentStr = xpath.evaluate("/sametimed/service/absolute-url", doc);
        serviceData.useAbsoluteURL = (absoluteURLElmPresentStr.length() > 0);
        log.debug("Using absolute URL set to '{}'", serviceData.useAbsoluteURL);
        if (serviceData.useAbsoluteURL) {
            final String protocolStr = xpath.evaluate("/sametimed/service/absolute-url/protocol", doc);
            if (protocolStr.length() > 0) serviceData.protocol = protocolStr;        
            log.debug("Protocol set to '{}'", serviceData.protocol);            
            serviceData.hostname = xpath.evaluate("/sametimed/service/absolute-url/hostname", doc); 
            log.debug("Hostname set to '{}'", serviceData.hostname);
            final String portStr = xpath.evaluate("/sametimed/service/absolute-url/port", doc);
            if (portStr.length() > 0) serviceData.port = Integer.valueOf(portStr);        
            log.debug("Port set to '{}'", serviceData.port);
        }
        
        // required
        serviceData.appName = xpath.evaluate("/sametimed/service/app-name", doc);
        log.debug("AppName set to '{}'", serviceData.appName);
        serviceData.tunnelPath = xpath.evaluate("/sametimed/service/tunnel", doc);
        log.debug("Tunnel set to '{}'", serviceData.tunnelPath);
        
        // optional
        final String cometdPathStr = xpath.evaluate("/sametimed/service/cometd-init", doc);
        if (cometdPathStr.length() > 0) serviceData.cometdPath = cometdPathStr;
        log.debug("CometD path set to '{}'", serviceData.cometdPath);
        
        // required
        serviceData.channels.joinChannelPath = xpath.evaluate("/sametimed/service/channels/join-channel", doc);
        log.debug("Join channel set to '{}'", serviceData.channels.joinChannelPath);
        serviceData.channels.cmdChannelPath = xpath.evaluate("/sametimed/service/channels/cmd-channel", doc);
        log.debug("Commands channel set to '{}'", serviceData.channels.cmdChannelPath);
        serviceData.channels.updChannelPath = xpath.evaluate("/sametimed/service/channels/upd-channel", doc);
        log.debug("Updates channel set to '{}'", serviceData.channels.updChannelPath);        
    }

    /**
     * Load defaults values for configuration
     */
    private void loadDefaults() {
        // defaults are already set through the inner classes attributes' default values
        usedDefaults = true;
    }

    /**
     * Are default values are loaded instead of values from file (file
     * was failed to be read). Changes value only when constructor called. 
     * 
     * @return are default values are loaded instead of values from file
     */
    public boolean usedDefaults() {
        return usedDefaults;
    }

    /**
     * Generates JSON object from configuration data and add it to the passed
     * {@code StringBuffer}
     * 
     * @param buffer buffer to append JSON representation to
     */
    @Override
    public void addJSON(StringBuffer buffer) {
        buffer.append("{");
        String appURL = (serviceData.useAbsoluteURL 
                        ? (serviceData.protocol + "://" +
                           serviceData.hostname + ":" + serviceData.port + "/" +
                           serviceData.appName) 
                        : "/" + serviceData.appName);
        buffer.append("'appURL':'" +  appURL + "',");
        buffer.append("'cometdURL':'" + (serviceData.useAbsoluteURL 
                                        ? (appURL + serviceData.cometdPath) 
                                        : serviceData.cometdPath) + "',");
            buffer.append("'channels':{");
                buffer.append("'joinChannel':'" 
                              + (serviceData.useAbsoluteURL ? appURL: "")
                              + "/" + serviceData.tunnelPath 
                              + serviceData.channels.joinChannelPath + "',");
                buffer.append("'cmdChannel':'" 
                              + (serviceData.useAbsoluteURL ? appURL: "")
                              + "/" + serviceData.tunnelPath 
                              + serviceData.channels.cmdChannelPath + "',");
                buffer.append("'updChannel':'" 
                              + (serviceData.useAbsoluteURL ? appURL: "")
                              + "/" + serviceData.tunnelPath 
                              + serviceData.channels.updChannelPath + "'");                
            buffer.append("}");
        buffer.append("}");
    }
    
    private final class ServiceData {
        
        boolean useAbsoluteURL = false;
        
        String protocol = "http";
        String hostname = "localhost";
        int port = 6067;
        String appName = null;
        String tunnelPath = null;
        String cometdPath = "cometd";    
        
        ChannelsPaths channels = new ChannelsPaths();
        
        private final class ChannelsPaths {
            
            String joinChannelPath;
            String cmdChannelPath;
            String updChannelPath;            
            
        }
        
    }

    /**
     * Returns application name
     * 
     * @return application name
     */
    public String getAppName() {
        return serviceData.appName;
    }

    /**
     * Returns Join channel path in format {@code /<tunnel-path>/<channel-name>}
     * 
     * @return join channel path
     */
    public String getJoinChannelPath() {
        return "/" + serviceData.tunnelPath + serviceData.channels.joinChannelPath;
    }

    /**
     * Returns Commands channel path in format {@code /<tunnel-path>/<channel-name>}
     * 
     * @return commands channel path
     */
    public String getCmdChannelPath() {
        return "/" + serviceData.tunnelPath + serviceData.channels.cmdChannelPath;
    }

    /**
     * Returns Updates channel path in format {@code /<tunnel-path>/<channel-name>}
     * 
     * @return updates channel path
     */
    public String getUpdChannelPath() {
        return "/" + serviceData.tunnelPath + serviceData.channels.updChannelPath;
    }

    /**
     * Returns full path to tunnel. If absolute path is set in sametimed configuration
     * file ({@value #CONFIG_FILE_PATH}) then URL is returned in format {@code 
     * <protocol>://<hostname>:<port>/<app-name>/<tunnel-name>}, the last
     * {@code /<app-name>/<tunnel-name>} part is returned either.
     * 
     * @return
     */
    public String getFullTunnelPath() {
        return (serviceData.useAbsoluteURL 
                ? (serviceData.protocol + "://" + serviceData.hostname + ":" + serviceData.port) 
                : "") + "/" + serviceData.appName + "/" + serviceData.tunnelPath;
    }

}
