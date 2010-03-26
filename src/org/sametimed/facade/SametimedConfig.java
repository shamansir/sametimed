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
                log.debug("exception appeared while loading XML config: {}", 
                                        e.getClass() + " " + e.getMessage());
                loadDefaults();             
                log.debug("ignored exception, loaded defaults");                
            }
        } else {
            loadDefaults();
            log.debug("no file passed, loaded defaults");             
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
        log.info("name: {}", xpath.evaluate("/sametimed/channel/name", doc));
        log.info("spath: {}", xpath.evaluate("/sametimed/channel/service-path", doc));
        log.info("jchan: {}", xpath.evaluate("/sametimed/channel/join-channel", doc));
    }

    /**
     * Load defaults values for configuration
     */
    protected void loadDefaults() {
        // TODO Auto-generated method stub
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
     * Returns the main service path
     * 
     * @return path to the service
     */
    public String getServicePath() {
        
        // FIXME: implement
        return null;
    }

    /**
     * Generates JSON object from configuration data and add it to the passed
     * {@code StringBuffer}
     * 
     * @param buffer buffer to append JSON representation to
     */
    @Override
    public void addJSON(StringBuffer buffer) {
        // FIXME: implement        
        buffer.append("{ 'success': 'true' }");
    }
    
    

}
