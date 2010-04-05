/**
 * 
 */
package org.sametimed.module;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.sametimed.facade.SametimedConfig.ModuleData;
import org.sametimed.facade.SametimedConfig.ModulesDataList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Project: sametimed
 * Package: org.sametimed.module
 *
 * ModulesFactory
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 4, 2010 1:15:46 PM 
 *
 */
public class ModulesFactory {
    
    private static final Logger log = LoggerFactory
            .getLogger(ModulesFactory.class);
    
    public static final String DEFAULT_MODULES_PATH = "org.sametimed.modules"; // TODO: store in sametimed.xml?
    public static final String MODULE_CONF_FILE_PERFIX = "sametimed-module-"; // TODO: store in sametimed.xml?
    
    private final ModulesList modules = new ModulesList();
    
    public ModulesFactory(ModulesDataList modulesDataList, ServletContext servletContext) {      
        for (ModuleData mdata: modulesDataList.values()) {
            String configPath =
                "/WEB-INF/classes/" +
                ((mdata.path != null) 
                         ? mdata.path 
                         : DEFAULT_MODULES_PATH)
                .replace(".", "/") + "/" + mdata.id + "/" + MODULE_CONF_FILE_PERFIX + mdata.id + ".xml";
            try {
                createModule(mdata, new ModuleConfig(mdata.id, servletContext.getResourceAsStream(configPath)));
            } catch (FileNotFoundException fnfe) {
                log.error("configuration file for module '" + mdata.id + "' was " +
                          "not found at path '" + configPath + "', so this module is not created."); 
            } catch (Exception e) {
                log.error("configuration file for module '" + mdata.id + "' " +
                        "at path '" + configPath + "' was failed to be parsed, " +
                        "so this module is not created. Caused by: {} / {}", 
                               e.getClass(), e.getMessage());
            }
        }        
    }
    
    public SametimedModule createModule(ModuleData mdata, ModuleConfig config) {
        if (!modules.containsKey(mdata.id)) {
            // FIXME: implement
            /* SametimedModule module;
            modules.put(mdata.id, module);
            return module; */
        } else return modules.get(mdata.id);
        return null;
    }
    
    public ModulesList getEnabledModules() {
        return modules;
    }    
    
}
