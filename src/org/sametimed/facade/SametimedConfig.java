/**
 * 
 */
package org.sametimed.facade;

import org.sametimed.util.XmlConfigurationFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class SametimedConfig extends XmlConfigurationFile implements JSON.Generator {
    
    public static final String CONFIG_FILE_PATH = "/WEB-INF/sametimed.xml";    
    
    private static final Logger log = LoggerFactory
            .getLogger(SametimedConfig.class);
    
    private static InputStream configFile = null;
    private static boolean usedDefaults = false;
    
    private static SametimedConfig instance;
    
    private final ServiceData serviceData = new ServiceData();
    private final CommandsDataList commandsData = new CommandsDataList();
    private final ModulesDataList modulesData = new ModulesDataList();    

    /**
     * Loads data from configuration file, lying in passed servlet context.
     * If file is already loaded - just returns the cached instance, even if
     * the different context passed. (means {@code SametimedConfig} is singleton)
     * 
     * @param fromContext context to load file from
     * @return {@code SametimedConfig} instance
     */
    public final synchronized static SametimedConfig loadConfig(ServletContext fromContext) {
        if ((configFile == null) && !usedDefaults) { // not loaded for the moment
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
        try {
            loadFrom(confFile);          
            configFile = confFile;
            if (configFile != null) log.info("configuration loaded from: {}", CONFIG_FILE_PATH);
        } catch (FileNotFoundException fnfe) {
            loadDefaults();          
            log.debug("no file passed or found at {}, loaded defaults", CONFIG_FILE_PATH);  
        } catch (Exception e) {
            loadDefaults();            
            log.debug("exception appeared while loading XML config: {} from {}", 
                    e.getClass() + " " + e.getMessage(),
                    CONFIG_FILE_PATH);            
            log.debug("ignored exception, loaded defaults");              
        }        
    }    
    
    @Override
    protected void extractValues() throws XPathExpressionException {
        
        // ---------------------------- URL Values -----------------------------
        
        // optional
        final String absoluteURLElmPresentStr = evaluate("/sametimed/service/absolute-url");
        serviceData.useAbsoluteURL = (absoluteURLElmPresentStr.length() > 0);
        log.debug("Using absolute URL set to '{}'", serviceData.useAbsoluteURL);
        if (serviceData.useAbsoluteURL) {
            final String protocolStr = evaluate("/sametimed/service/absolute-url/protocol");
            if (protocolStr.length() > 0) serviceData.protocol = protocolStr;        
            log.debug("Protocol set to '{}'", serviceData.protocol);            
            serviceData.hostname = evaluate("/sametimed/service/absolute-url/hostname"); 
            log.debug("Hostname set to '{}'", serviceData.hostname);
            final String portStr = evaluate("/sametimed/service/absolute-url/port");
            if (portStr.length() > 0) serviceData.port = Integer.valueOf(portStr);        
            log.debug("Port set to '{}'", serviceData.port);
        }
        
        // ---------------------------- Service Tunnel ----------------------        
        
        // required
        serviceData.appName = evaluate("/sametimed/service/app-name");
        log.debug("AppName set to '{}'", serviceData.appName);
        serviceData.tunnelPath = evaluate("/sametimed/service/tunnel");
        log.debug("Tunnel set to '{}'", serviceData.tunnelPath);
        
        // ---------------------------- Cometd ---------------------------------
        
        // optional
        final String cometdPathStr = evaluate("/sametimed/service/cometd-init");
        if (cometdPathStr.length() > 0) serviceData.cometdPath = cometdPathStr;
        log.debug("CometD path set to '{}'", serviceData.cometdPath);
        
        // ---------------------------- Channels -------------------------------
        
        // required
        serviceData.channels.joinChannelPath = evaluate("/sametimed/service/channels/join-channel");
        log.debug("Join channel set to '{}'", serviceData.channels.joinChannelPath);
        serviceData.channels.cfrmChannelPath = evaluate("/sametimed/service/channels/confirm-channel");
        log.debug("Confirm channel set to '{}'", serviceData.channels.cfrmChannelPath);        
        serviceData.channels.cmdChannelPath = evaluate("/sametimed/service/channels/commands-channel");
        log.debug("Commands channel set to '{}'", serviceData.channels.cmdChannelPath);
        serviceData.channels.updChannelPath = evaluate("/sametimed/service/channels/updates-channel");
        log.debug("Updates channel set to '{}'", serviceData.channels.updChannelPath);        
        
        // ---------------------------- Modules Data ---------------------------
        
        List<String> modulesIds = evaluateNodes("/sametimed/enabled-modules/module/@id");
        for (String moduleId: modulesIds) {
            
            final boolean isSystem = 
                "true".equals(evaluate("/sametimed/enabled-modules/module[@id='" + moduleId + "']/@system"));
            
            String path = null;
            final String pathStr =
                evaluate("/sametimed/enabled-modules/module[@id='" + moduleId + "']/@path");
            if (pathStr.length() > 0) path = pathStr;
            
            modulesData.put(moduleId, new ModuleData(moduleId, isSystem, path));
            
        }
        log.info("modules {} data is loaded from 'sametimed' configuration file", 
                                                         modulesIds.toString());
                
        // ---------------------------- Commands Data --------------------------
        
        List<String> commandsAliases = evaluateNodes("/sametimed/registered-commands/command/@alias");
        for (String commandAlias: commandsAliases) {
            
            String commandId = null;
            final String commandIdStr = 
                evaluate("/sametimed/registered-commands/command[@alias='" + commandAlias + "']/@id");
            if (commandIdStr.length() > 0) commandId = commandIdStr;
            
            final boolean isSystem = 
                "true".equals(evaluate("/sametimed/registered-commands/command[@alias='" + commandAlias + "']/@system"));
            
            String definedFor = null;
            final String definedForStr =
                evaluate("/sametimed/registered-commands/command[@alias='" + commandAlias + "']/@defined-for");
            if (definedForStr.length() > 0) definedFor = definedForStr;
            
            commandsData.put(commandAlias, 
                    new CommandData(commandAlias, commandId, isSystem, definedFor));
            
        }
        log.info("commands {} data is loaded from 'sametimed' configuration file", 
                                                    commandsAliases.toString());    
        
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
        // final char q = '\''; TODO: use quot defined here
        
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
                buffer.append("'cfrmChannel':'" 
                              + (serviceData.useAbsoluteURL ? appURL: "")
                              + "/" + serviceData.tunnelPath 
                              + serviceData.channels.cfrmChannelPath + "',");                 
                buffer.append("'cmdChannel':'" 
                              + (serviceData.useAbsoluteURL ? appURL: "")
                              + "/" + serviceData.tunnelPath 
                              + serviceData.channels.cmdChannelPath + "',");
                buffer.append("'updChannel':'" 
                              + (serviceData.useAbsoluteURL ? appURL: "")
                              + "/" + serviceData.tunnelPath 
                              + serviceData.channels.updChannelPath + "'");               
            buffer.append("},");
            
            buffer.append("'modules':[");
                for (Iterator<ModuleData> iter = modulesData.values().iterator(); iter.hasNext(); ) {
                    buffer.append("'" + iter.next().id  + "'");
                    if (iter.hasNext()) buffer.append(",");
                }
            buffer.append("],");
            
            buffer.append("'commands':[");
                for (Iterator<CommandData> iter = commandsData.values().iterator(); iter.hasNext(); ) {
                    buffer.append("'" + iter.next().alias  + "'");
                    if (iter.hasNext()) buffer.append(",");
                }
            buffer.append("]");            
            
        buffer.append("}");
    }
    
    private final class ServiceData {
        
        boolean useAbsoluteURL = false;
        
        String protocol = "http";
        String hostname = "localhost";
        int port = 6067;
        String appName = "app-name";
        String tunnelPath = "t";
        String cometdPath = "cometd";    
        
        ChannelsPaths channels = new ChannelsPaths();
        
        private final class ChannelsPaths {
            
            String joinChannelPath = "/j";
            String cmdChannelPath = "/c";
            String updChannelPath = "/u";
            String cfrmChannelPath = "/r";
            
        }
        
    }
    
    public final class CommandData {
        
        public final String id;
        public final String alias;
        public final boolean system;
        public final String definedFor;
        
        public CommandData(String id, String alias, boolean system, String definedFor) {
            this.id = id;
            this.alias = alias;
            this.system = system;
            this.definedFor = definedFor;
        }
        
        public CommandData(String id, String alias) { this(id, alias, false, null); }
        public CommandData(String id, String alias, String definedFor) { this(id, alias, false, definedFor); }
        public CommandData(String id, String alias, boolean system) { this(id, alias, system, null); }        
        
    }
    
    public final class ModuleData {
        
        public final String id;
        public final boolean system;
        public final String path;
        
        public ModuleData(String id, boolean system, String path) {
            this.id = id;
            this.system = system;
            this.path = path;
        }
        
        public ModuleData(String id) { this(id, false, null); }
        public ModuleData(String id, String path) { this(id, false, path); }
        public ModuleData(String id, boolean system) { this(id, system, null); }           
        
    } 
    
    @SuppressWarnings("serial")
    public final class ModulesDataList extends HashMap<String, ModuleData> { }
    @SuppressWarnings("serial")
    public final class CommandsDataList extends HashMap<String, CommandData> { }    

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
     * Returns Confirm channel path in format {@code /<tunnel-path>/<channel-name>}
     * 
     * @return confirm channel path
     */
    public String getCfrmChannelPath() {
        return  "/" + serviceData.tunnelPath + serviceData.channels.cfrmChannelPath;
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
    
    public ModulesDataList getModulesData() {
        return modulesData;
    }
    
    public CommandsDataList getCommandsData() {
        return commandsData;
    }   

}
