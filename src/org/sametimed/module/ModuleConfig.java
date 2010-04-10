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
    
    private final String moduleId;
    private boolean treeStructured = false;
    private String documentId = null;

    public ModuleConfig(final String moduleId, InputStream source) throws XPathExpressionException, SAXException, ParserConfigurationException, IOException {
        this.moduleId = moduleId;        
        loadFrom(source);
        log.debug("module '{}' configuration is loaded from its configuration file", moduleId);
    }
    
    @Override
    protected void extractValues() throws XPathExpressionException {
        treeStructured = "true".equalsIgnoreCase(evaluate("/module/@tree-structured"));
        
        String documentIdStr = evaluate("/module/documet-id");
        if (documentIdStr.length() > 0) documentId = documentIdStr;
        
        // TODO: 'declared-commands' and 'accepts-commands'
        
    }

    protected boolean treeStructured() {
        return treeStructured;
    }
    
    public String getDocumentId() {
        return (documentId != null) ? documentId : moduleId;
    }

}
