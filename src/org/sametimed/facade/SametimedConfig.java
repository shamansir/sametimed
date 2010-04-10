/**
 * 
 */
package org.sametimed.facade;

import org.sametimed.util.XmlConfigurationFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    private final CommandsDataList registeredCommands = new CommandsDataList();
    private final ModulesDataList modulesToPrepare = new ModulesDataList();
    private final Set<String> modulesToDisable = new HashSet<String>();    

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
        
        modulesToPrepare.clear();
        modulesToDisable.clear();
        registeredCommands.clear();        
        
        // ---------------------------- URL Values -----------------------------

        // optional
        if (nodeExists("/sametimed/service/location")) {
            serviceData.locationDefined = true;
            log.debug("Using absolute location for service");
            final String protocolStr = evaluate("/sametimed/service/location/protocol");
            if (protocolStr.length() > 0) serviceData.protocol = protocolStr;        
            log.debug("Protocol set to '{}'", serviceData.protocol);            
            serviceData.hostname = evaluate("/sametimed/service/location/hostname"); 
            log.debug("Hostname set to '{}'", serviceData.hostname);
            final String portStr = evaluate("/sametimed/service/location/port");
            if (portStr.length() > 0) serviceData.port = Integer.valueOf(portStr);        
            log.debug("Port set to '{}'", serviceData.port);
        }
        
        // ---------------------------- Service Tunnel ----------------------        
        
        // required
        serviceData.appName = evaluate("/sametimed/service/app-name");
        log.debug("AppName set to '{}'", serviceData.appName);
        serviceData.tunnelPath = evaluate("/sametimed/service/tunnel/path");
        log.debug("Tunnel set to '{}'", serviceData.tunnelPath);
        
        // ---------------------------- Cometd ---------------------------------
        
        // optional        
        final String cometdPathStr = evaluate("/sametimed/service/cometd-init");
        if (cometdPathStr.length() > 0) serviceData.cometdPath = cometdPathStr;
        log.debug("CometD path set to '{}'", serviceData.cometdPath);
        
        // ---------------------------- Channels -------------------------------
        
        // optional
        if (nodeExists("/sametimed/service/tunnel/channels")) {
            serviceData.channels.joinChannelPath = evaluate("/sametimed/service/tunnel/channels/join-channel");
            log.debug("Join channel set to '{}'", serviceData.channels.joinChannelPath);
            serviceData.channels.cfrmChannelPath = evaluate("/sametimed/service/tunnel/channels/confirm-channel");
            log.debug("Confirm channel set to '{}'", serviceData.channels.cfrmChannelPath);        
            serviceData.channels.cmdChannelPath = evaluate("/sametimed/service/tunnel/channels/commands-channel");
            log.debug("Commands channel set to '{}'", serviceData.channels.cmdChannelPath);
            serviceData.channels.updChannelPath = evaluate("/sametimed/service/tunnel/channels/updates-channel");
            log.debug("Updates channel set to '{}'", serviceData.channels.updChannelPath);
            serviceData.channels.mftryChannelPath = evaluate("/sametimed/service/tunnel/channels/mfactory-channel");
            log.debug("Modules Factory channel set to '{}'", serviceData.channels.mftryChannelPath);            
        }
        
        // ---------------------------- Prepared Modules -----------------------
        
        if (nodeExists("/sametimed/prepared-modules")) { 
            List<String> modulesIds = evaluateNodes("/sametimed/prepared-modules/module/@id");
            for (String moduleId: modulesIds) {
                
                String packageName = null;
                final String packageNameStr =
                    evaluate("/sametimed/prepared-modules/module[@id='" + moduleId + "']/@package");
                if (packageNameStr.length() > 0) packageName = packageNameStr;                
                
                modulesToPrepare.put(moduleId, new ModuleData(moduleId, packageName));
                
            }

        }
        
        if (!modulesToPrepare.isEmpty()) {
            log.info("modules {} data is loaded from 'sametimed.xml' configuration file " +
                    "and they are going to be prepared", 
                    modulesToPrepare.keySet().toString());            
        }
        
        // ---------------------------- Disabled Modules -----------------------        
        
        if (nodeExists("/sametimed/disabled-modules")) { 
            modulesToDisable.addAll(
                    evaluateNodes("/sametimed/disabled-modules/module-id"));
        }
        
        if (!modulesToDisable.isEmpty()) {
            log.info("modules {} are going to be disabled conforming with 'sametimed.xml'", 
                    modulesToDisable.toString());
        }        
                
        // ---------------------------- Commands Data --------------------------
        
        List<String> commandsAliases = evaluateNodes("/sametimed/registered-commands/command/@alias");
        for (String commandAlias: commandsAliases) {
            
            String commandId = null;
            final String commandIdStr = 
                evaluate("/sametimed/registered-commands/command[@alias='" + commandAlias + "']/@id");
            if (commandIdStr.length() > 0) commandId = commandIdStr;
            
            String className = null;
            final String classNameStr = 
                evaluate("/sametimed/registered-commands/command[@alias='" + commandAlias + "']/@class");
            if (classNameStr.length() > 0) className = classNameStr;            
            
            registeredCommands.put(commandAlias, new CommandData(commandId, commandAlias, className));            
        }
        
        if (!registeredCommands.isEmpty()) {
            log.info("commands {} data is loaded from 'sametimed.xml' configuration file", 
                                                registeredCommands.keySet().toString());
        }
        
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
        final String shortAppURL = "/" + serviceData.appName;        
        final String fullAppURL = (serviceData.locationDefined 
                        ? (serviceData.protocol + "://" +
                           serviceData.hostname + ":" + serviceData.port + "/" +
                           serviceData.appName) 
                        : shortAppURL);
        final String cometdPath = serviceData.cometdPath.startsWith("/")
                                  ? serviceData.cometdPath.substring(1)
                                  : serviceData.cometdPath;
        
            buffer.append("'appURL':'" +  fullAppURL + "',");
            buffer.append("'cometdURL':'" + (serviceData.locationDefined 
                                            ? (fullAppURL + cometdPath) 
                                            : cometdPath) + "',");
            
            buffer.append("'channels':{");
                buffer.append("'joinChannel':'" 
                              + serviceData.tunnelPath 
                              + serviceData.channels.joinChannelPath + "',");
                buffer.append("'cfrmChannel':'" 
                              + serviceData.tunnelPath 
                              + serviceData.channels.cfrmChannelPath + "',");                 
                buffer.append("'cmdChannel':'" 
                              + serviceData.tunnelPath 
                              + serviceData.channels.cmdChannelPath + "',");
                buffer.append("'updChannel':'" 
                              + serviceData.tunnelPath 
                              + serviceData.channels.updChannelPath + "',");
                buffer.append("'mftryChannel':'" 
                              + serviceData.tunnelPath 
                              + serviceData.channels.mftryChannelPath + "'");                
            buffer.append("},");
            
            buffer.append("'modules':[");
                for (Iterator<String> iter = modulesToPrepare.keySet().iterator(); iter.hasNext(); ) {
                    String moduleId = iter.next();
                    if (!modulesToDisable.contains(moduleId)) {
                        buffer.append("'" + moduleId + "'");
                        if (iter.hasNext()) buffer.append(",");
                    }
                }
            buffer.append("],");
                        
            buffer.append("'commands':[");
                for (Iterator<CommandData> iter = registeredCommands.values().iterator(); iter.hasNext(); ) {
                    buffer.append("'" + iter.next().alias  + "'");
                    if (iter.hasNext()) buffer.append(",");
                }
            buffer.append("]");            
            
        buffer.append("}");
    }
    
    private final class ServiceData {
        
        boolean locationDefined = false;
        
        String protocol = "http";
        String hostname = "localhost";
        int port = 6067;
        String appName = "fooapp";
        String tunnelPath = "/t";
        String cometdPath = "/cometd";    
        
        ChannelsPaths channels = new ChannelsPaths();
        
        private final class ChannelsPaths {
            
            String joinChannelPath = "/j";
            String cmdChannelPath = "/c";
            String updChannelPath = "/u";
            String cfrmChannelPath = "/r";
            String mftryChannelPath = "/m";
            
        }
        
    }
    
    public final class CommandData {
        
        public final String id;
        public final String alias;
        public final String className;
        
        public CommandData(String id, String alias, String className) {
            this.id = id;
            this.alias = alias;
            this.className = className;
        }
        
        public CommandData(String id, String alias) { this(id, alias, null); }        
        
    }
    
    public final class ModuleData {
        
        public final String id;
        public final String packageName;    
        
        public ModuleData(String id, String packageName) {
            this.id = id;
            this.packageName = packageName;
        }
        
        public ModuleData(String id) { this(id, null); }           
        
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
        return serviceData.tunnelPath + serviceData.channels.joinChannelPath;
    }
    
    /**
     * Returns Confirm channel path in format {@code /<tunnel-path>/<channel-name>}
     * 
     * @return confirm channel path
     */
    public String getCfrmChannelPath() {
        return  serviceData.tunnelPath + serviceData.channels.cfrmChannelPath;
    }    

    /**
     * Returns Commands channel path in format {@code /<tunnel-path>/<channel-name>}
     * 
     * @return commands channel path
     */
    public String getCmdChannelPath() {
        return serviceData.tunnelPath + serviceData.channels.cmdChannelPath;
    }

    /**
     * Returns Updates channel path in format {@code /<tunnel-path>/<channel-name>}
     * 
     * @return updates channel path
     */
    public String getUpdChannelPath() {
        return serviceData.tunnelPath + serviceData.channels.updChannelPath;
    }  
    
    /**
     * Returns Modules factory channel path in format {@code /<tunnel-path>/<channel-name>}
     * 
     * @return modules factory channel path
     */    
    public String getMFtryChannelPath() {
        return serviceData.tunnelPath + serviceData.channels.mftryChannelPath;        
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
        return (serviceData.locationDefined 
                ? (serviceData.protocol + "://" + serviceData.hostname + ":" + serviceData.port) 
                : "") + "/" + serviceData.appName + "/" + serviceData.tunnelPath;
    }
    
    public ModulesDataList getModulesToPrepare() {
        return modulesToPrepare;
    }
    
    public Set<String> getModulesToDisable() {
        return modulesToDisable;
    }    
    
    public CommandsDataList getRegisteredCommands() {
        return registeredCommands;
    }   

}
