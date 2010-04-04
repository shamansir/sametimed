/**
 * 
 */
package org.sametimed.facade.wave;

import org.sametimed.util.XmlConfigurationFile;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public final class WaveServerProperties extends XmlConfigurationFile {
    
    // WARN: be sure to set Project Build Path to WebContent/WEB-INF/classes to get this file
    public static final String PROPS_FILE_PATH = "/WEB-INF/classes/sametimed-waveserver.xml";
    
    private static final Logger log = LoggerFactory
            .getLogger(WaveServerProperties.class);
    
    private static WaveServerProperties instance;    
    private static InputStream wavePropsFile = null;
    
    private boolean wereLoadErrors = false;
    
    private String hostname = null;
    private String domain = null;
    private int port = -1;
    
    private WaveServerProperties(InputStream propsFile) {
        try {
            loadFrom(propsFile);
            wereLoadErrors = false;            
            wavePropsFile = propsFile;
            if (wavePropsFile != null) log.info("wave server configuration loaded from: {}", PROPS_FILE_PATH);
        } catch (FileNotFoundException e) {
            wereLoadErrors = true;            
            log.debug("no file passed or found at {}", PROPS_FILE_PATH); 
        } catch (Exception e) {
            wereLoadErrors = true;            
            log.debug("exception appeared while loading XML config: {} from {}", 
                    e.getClass() + " " + e.getMessage(),
                    PROPS_FILE_PATH);             
        }
    }
    
    @Override
    protected void extractValues() throws XPathExpressionException {
        hostname = evaluate("/waveserver/hostname");
        domain = evaluate("/waveserver/domain-name");
        port = Integer.valueOf(evaluate("/waveserver/port"));        
    }
    
    public static WaveServerProperties load(ServletContext fromContext) {
        if (wavePropsFile == null) { // not loaded or failed to load for the moment
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
    
    public boolean wereLoadErrors() {
        return wereLoadErrors;
    }

}
