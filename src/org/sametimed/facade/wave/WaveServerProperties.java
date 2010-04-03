/**
 * 
 */
package org.sametimed.facade.wave;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Project: sametimed
 * Package: org.sametimed.facade.wave
 *
 * WaveServerProperties
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 3, 2010 6:12:45 PM 
 *
 */
public class WaveServerProperties {
    
    // WARN: be sure to set Project Build Path to WebContent/WEB-INF/classes to get this file
    public static final String PROPS_FILE_PATH = "/WEB-INF/classes/sametimed-waveserver.xml";
    
    private static final Logger log = LoggerFactory
            .getLogger(WaveServerProperties.class);
    
    private static WaveServerProperties instance;    
    private static InputStream wavePropsFile = null;
    
    private String hostname = null;
    private String domain = null;
    private int port = -1;    
    
    private WaveServerProperties(InputStream propsFile) {
        if (propsFile != null) {
            final DocumentBuilderFactory factory = 
                                       DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            try {
                loadFromXML(factory.newDocumentBuilder().parse(propsFile));
                log.info("wave server configuration loaded from: {}", PROPS_FILE_PATH);                
                wavePropsFile = propsFile; 
            } catch (Exception e) {                
                log.debug("exception appeared while loading XML config: {} from {}", 
                                                e.getClass() + " " + e.getMessage(),
                                                PROPS_FILE_PATH);                
            }
        } else {
            log.debug("no file passed or found at {}, nothing is loaded", PROPS_FILE_PATH);             
        }        
    }
    
    private void loadFromXML(Document doc) throws XPathExpressionException {
        final XPath xpath = XPathFactory.newInstance().newXPath();
        
        hostname = xpath.evaluate("/waveserver/hostname", doc);
        domain = xpath.evaluate("/waveserver/domain-name", doc);
        port = Integer.valueOf(xpath.evaluate("/waveserver/port", doc));        
    }
    
    public static WaveServerProperties load(ServletContext fromContext) {
        if (wavePropsFile == null) { // not loaded for the moment
            instance = new WaveServerProperties(fromContext.getResourceAsStream(PROPS_FILE_PATH));
        }
        return instance;        
    }
    
    public String getHostname() {
        return hostname;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public int getPort() {
        return port;
    }    

}
