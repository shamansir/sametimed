/**
 * 
 */
package org.sametimed.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Project: sametimed
 * Package: org.sametimed.util
 *
 * XmlConfigurationFile
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 3, 2010 11:37:03 PM 
 *
 */
public abstract class XmlConfigurationFile {
    
    private Document sourceDoc = null;
    private XPath xpath = null;
        
    // FIXME: can't be called in constructor because of thrown exceptions
    //        (the super constructor can't be surrounded with try/catch),
    //        but user _must_ to use it in his implementation constructor to
    //        load configuration properly. fix this... 
    protected void loadFrom(InputStream source) throws XPathExpressionException, SAXException, ParserConfigurationException, IOException {
        if (source != null) {        
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);        
            this.sourceDoc = factory.newDocumentBuilder().parse(source);
            this.xpath = XPathFactory.newInstance().newXPath();
            extractValues();             
        } else throw new FileNotFoundException("Passed source is null");       
    }
    
    protected abstract void extractValues() throws XPathExpressionException;
    
    public String evaluate(String xpathStr) throws XPathExpressionException {
        return xpath.evaluate(xpathStr, sourceDoc); // will throw NPE if 
                                 // loadFrom was not called, but it's ok     
    }

}
