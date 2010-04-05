/**
 * 
 */
package org.sametimed.module;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.sametimed.util.XmlConfigurationFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Project: sametimed
 * Package: org.sametimed.module
 *
 * ModuleConfig
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 5, 2010 5:55:49 PM 
 *
 */
public class ModuleConfig extends XmlConfigurationFile {
    
    private static final Logger log = LoggerFactory
            .getLogger(ModuleConfig.class);

    public ModuleConfig(String moduleId, InputStream source) throws XPathExpressionException, SAXException, ParserConfigurationException, IOException {
        loadFrom(source);
        log.info("module '{}' configuration is loaded", moduleId);
    }
    
    @Override
    protected void extractValues() throws XPathExpressionException {
        // TODO Auto-generated method stub
    }

}
