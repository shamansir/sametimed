/**
 * 
 */
package org.sametimed.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
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
    
    public List<String> evaluateNodes(String xpathStr) throws XPathExpressionException {
        List<String> result = new ArrayList<String>(); 
        XPathExpression expr = xpath.compile(xpathStr);
        NodeList nodes = (NodeList)expr.evaluate(sourceDoc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            result.add(nodes.item(i).getTextContent()); 
        }
        return result;
    }
    
    public boolean nodeExists(String xpathStr) throws XPathExpressionException {
        XPathExpression expr = xpath.compile(xpathStr);        
        NodeList nodes = (NodeList)expr.evaluate(sourceDoc, XPathConstants.NODESET);
        return nodes.getLength() > 0;
    }

}
