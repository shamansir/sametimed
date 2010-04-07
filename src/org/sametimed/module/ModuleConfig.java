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
    
    //private final String moduleId;
    private boolean treeStructured = false;
    private String moduleClassName = null;    
    private String documentId = null;
    private String modelClassName = null;
    private String modelAtomClassName = null;    
    private String viewClassName = null;
    private String viewTagClassName = null;

    public ModuleConfig(final String moduleId, InputStream source) throws XPathExpressionException, SAXException, ParserConfigurationException, IOException {
        loadFrom(source);
        //this.moduleId = moduleId;
        log.debug("module '{}' configuration is loaded from its configuration file", moduleId);
    }
    
    @Override
    protected void extractValues() throws XPathExpressionException {
        treeStructured = "true".equalsIgnoreCase(evaluate("/module/@tree-structured"));
        
        // TODO: evaluate to nodes to check for null?
        String moduleClassNameStr = evaluate("/module/class");
        if (moduleClassNameStr.length() > 0) moduleClassName = moduleClassNameStr;
        String documentIdStr = evaluate("/module/documentId");
        if (documentIdStr.length() > 0) documentId = documentIdStr;

        String modelClassNameStr = evaluate("/module/model/class");
        if (modelClassNameStr.length() > 0) modelClassName = modelClassNameStr;        
        String modelAtomClassNameStr = evaluate("/module/model/atom");
        if (modelAtomClassNameStr.length() > 0) modelAtomClassName = modelAtomClassNameStr;
        
        String viewClassNameStr = evaluate("/module/view/class");
        if (viewClassNameStr.length() > 0) viewClassName = viewClassNameStr;        
        String viewTagClassNameStr = evaluate("/module/view/tag");
        if (viewTagClassNameStr.length() > 0) viewTagClassName = viewTagClassNameStr;        
    }

    protected boolean isTreeStructured() {
        return treeStructured;
    }

    protected String getModuleClassName() {
        return moduleClassName;
    }

    protected String getDocumentId() {
        return documentId;
    }

    protected String getModelClassName() {
        return modelClassName;
    }

    protected String getModelAtomClassName() {
        return modelAtomClassName;
    }

    protected String getViewClassName() {
        return viewClassName;
    }

    protected String getViewTagClassName() {
        return viewTagClassName;
    }

}
