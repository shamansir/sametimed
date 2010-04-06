/**
 * 
 */
package org.sametimed.module;

import java.io.FileNotFoundException;

import javax.servlet.ServletContext;

import org.sametimed.facade.SametimedConfig.ModuleData;
import org.sametimed.facade.SametimedConfig.ModulesDataList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            // FIXME: load configuration file using module class
            String configPath =
                "/WEB-INF/classes/" +
                ((mdata.path != null) 
                         ? mdata.path 
                         : DEFAULT_MODULES_PATH)
                .replace(".", "/") + "/" + mdata.id + "/" + MODULE_CONF_FILE_PERFIX + mdata.id + ".xml";
            try {
                createModule(mdata, new ModuleConfig(mdata.id, servletContext.getResourceAsStream(configPath)));
            } catch (FileNotFoundException fnfe) {
                log.error("configuration file for module '{}' was " +
                          "not found at path '{}', so this module is not created.",
                          mdata.id, configPath); 
            } catch (Exception e) {
                log.error("configuration file for module '{}' at path '{}' was " +
                        "failed to be parsed, so this module is not created", mdata.id, configPath);
                log.error("parsing error was caused by: {} / {}", e.getClass().getName(), e.getMessage());
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
