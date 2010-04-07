/**
 * 
 */
package org.sametimed.module;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

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
    public static final String MODULE_CONF_FILE_PREFIX = "sametimed-module-"; // TODO: store in sametimed.xml?
    public static final String MODULE_CLASS_NAME_POSTFIX = "Module";    
    
    private final ModulesList modules = new ModulesList();
    
    public ModulesFactory(ModulesDataList modulesDataList) {      
        for (ModuleData mdata: modulesDataList.values()) {
            createModule(mdata);
        }        
    }
        
    public SametimedModule createModule(ModuleData mdata) {
        if (!modules.containsKey(mdata.id)) {
            
            StringBuffer moduleClassName = new StringBuffer();
            String[] mIdParts = mdata.id.split("-");            
            for (String mIdPart: mIdParts) {
                moduleClassName.append(mIdPart.substring(0, 1).toUpperCase() + mIdPart.substring(1));
            }  
            moduleClassName.append(MODULE_CLASS_NAME_POSTFIX);
            
            String moduleCanonicalClassName = 
                ((mdata.path != null) ? mdata.path : DEFAULT_MODULES_PATH) + "."
                + mdata.id.replace("-", "_") + "." + moduleClassName;
            
            try {
                
                Class<?> moduleClass = 
                          (Class<?>)Class.forName(moduleCanonicalClassName);
                
                if (moduleClass != null) {
                    
                    String configFileName = MODULE_CONF_FILE_PREFIX + mdata.id + ".xml";                
                    InputStream moduleConfigFile = moduleClass.getResourceAsStream(configFileName);
                    
                    if (moduleConfigFile != null) {
                        try {
                            
                            SametimedModule module = createModuleUsingConfig(mdata.id, moduleClass,
                                     new ModuleConfig(mdata.id, moduleConfigFile));
                            modules.put(mdata.id, module);
                            return module; // MAIN SUCCESSFUL RETURN
                            
                        } catch (FileNotFoundException e) {
                            log.error("configuration file for module '{}' was " +
                                    "not found at path '{}', so this module is not created.",
                                    mdata.id, moduleCanonicalClassName + "/" + configFileName);
                        } catch (Exception e) {
                            log.error("configuration file for module '{}' at path '{}' was " +
                                    "failed to be parsed, so this module is not created", 
                                    mdata.id, moduleCanonicalClassName + "/" + configFileName);
                            log.error("parsing error was caused by: {} / {}", e.getClass().getName(), e.getMessage());
                        }
                    } else {
                        log.error("configuration file for module '{}' was " +
                                "not found at path '{}', so this module is not created.",
                                mdata.id, moduleCanonicalClassName + "/" + configFileName);                         
                    }
                    
                } else {
                    log.error("class {} was not found for module '{}', so this module is not created/loaded",
                            moduleCanonicalClassName, mdata.id);
                }   
                
            } catch (ClassNotFoundException e) {
                log.error("class {} was not found for module '{}', so this module is not created/loaded",
                        moduleCanonicalClassName, mdata.id);
            }
            
        } else return modules.get(mdata.id); // if already created
        
        return null; // if were errors
    }
    
    private static SametimedModule createModuleUsingConfig(String moduleId, Class<?> moduleClass, ModuleConfig config) {
        String configClassName = config.getModuleClassName();
        String moduleClassName = moduleClass.getCanonicalName();
        if ((configClassName != null) &&
            (!moduleClassName.equals(configClassName))) {
                log.warn("Module class {} defined in configuration file is " +
                         "differ from the expected class {}", configClassName,
                         moduleClassName);
        }
        
        try {
            
            Constructor<?> ctor = moduleClass.getDeclaredConstructor(String.class);
            ctor.setAccessible(true);
            Object module = ctor.newInstance(moduleId);
            if (!(module instanceof SametimedModule)) {
                log.error("module '{}', class {} instance not extends " +
                          "SametimedModule, module initialization failure", moduleId, moduleClassName);
            } else {
                log.debug("module '{}', class {} instance was successfully created", moduleId, moduleClassName);
                return (SametimedModule)module; // MAIN SUCCESSFUL RETURN
            }
            
        } catch (SecurityException e) {
            log.error("Constructor(String) in class {} is inaccessible (private?) "
                    + "at module '{}'", moduleClassName, moduleId);
        } catch (NoSuchMethodException e) {
            log.error("No constructor(String) was found in class {} "
                      + "for module '{}'", moduleClassName, moduleId);
        } catch (Exception e) {
            log.error("Failed to call constructor(String) of class {} "
                      + "for module '{}'", moduleClassName, moduleId);
            log.error("Error was caused by: {} / {}", e.getClass().getName(), e.getMessage());
        }
        
        return null;    
    }
    
    public ModulesList getEnabledModules() {
        return modules;
    }    
    
    public SametimedModule getModule(String moduleId) {
        return modules.get(moduleId);
    }
    
}
