/**
 * 
 */
package org.sametimed.module;

import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

import org.sametimed.facade.SametimedConfig;
import org.sametimed.facade.SametimedConfig.ModuleData;
import org.sametimed.facade.SametimedConfig.ModulesDataList;

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
    
    public static final String DEFAULT_MODULES_PACKAGE = "org.sametimed.modules";
    public static final String MODULE_CONF_FILE_PREFIX = "sametimed-module-"; // TODO: store in sametimed.xml?
    public static final String MODULE_CLASS_NAME_POSTFIX = "Module";
    
    private final Set<String> disabledModules;
    private final ModulesList modules = new ModulesList();
    
    public ModulesFactory(ModulesDataList modulesToPrepare, Set<String> modulesToDisable) {
        this.disabledModules = modulesToDisable;
        for (ModuleData mdata: modulesToPrepare.values()) {
            createModule(mdata);
        }        
    }
    
    protected static String getModuleClassName(String moduleId) {
        StringBuffer moduleClassName = new StringBuffer();
        String[] mIdParts = moduleId.split("-");            
        for (String mIdPart: mIdParts) {
            moduleClassName.append(mIdPart.substring(0, 1).toUpperCase() + mIdPart.substring(1));
        }  
        moduleClassName.append(MODULE_CLASS_NAME_POSTFIX);
        return moduleClassName.toString();
    }
    
    protected static String getModulePackageName(String moduleId) {
        return DEFAULT_MODULES_PACKAGE + "." + moduleId.replace("-", "_");
    }
    
    protected static String getModuleConfigFileName(String moduleId) {
        return MODULE_CONF_FILE_PREFIX + moduleId + ".xml"; 
    }    
    
    public SametimedModule createModule(ModuleData mdata) {
        return createModule(mdata.id, mdata.packageName);
    }
        
    public SametimedModule createModule(String moduleId) {
        return createModule(moduleId, null);        
    }
    
    public SametimedModule createModule(String moduleId, String modulePackage) {
        if (!moduleDisabled(moduleId)) {
            if (!moduleCreated(moduleId)) {
                
                String moduleClassName = getModuleClassName(moduleId); 
                
                String moduleCanonicalClassName = ((modulePackage != null) 
                                  ? modulePackage
                                  : getModulePackageName(moduleId)) + "." + moduleClassName;
                
                try {
                    
                    Class<?> moduleClass = Class.forName(moduleCanonicalClassName);
                    
                    if (moduleClass != null) {
                                 
                        String configFileName = getModuleConfigFileName(moduleId);
                        InputStream moduleConfigFile = 
                                moduleClass.getResourceAsStream(configFileName);
                                                
                        if (moduleConfigFile != null) {
                            try {
                                         
                                // MAIN SUCCESSFUL RETURN, may be null
                                return createModuleUsingConfig(moduleId, moduleClass,
                                            new ModuleConfig(moduleId, moduleConfigFile)); 
                                
                            } catch (FileNotFoundException e) {
                                log.error("configuration file for module '{}' was " +
                                        "not found at path '{}', so this module is not created.",
                                        moduleId, moduleCanonicalClassName + "/" + configFileName);
                                return null;                                
                            } catch (Exception e) {
                                log.error("configuration file for module '{}' at path '{}' was " +
                                        "failed to be parsed, so this module is not created", 
                                        moduleId, moduleCanonicalClassName + "/" + configFileName);
                                log.error("parsing error was caused by: {} / {}", e.getClass().getName(), e.getMessage());
                                return null;                                
                            }
                        } else {
                            log.error("configuration file for module '{}' was " +
                                    "not found at path '{}', so this module is not created.",
                                    moduleId, moduleCanonicalClassName + "/" + configFileName);
                            return null;
                        }
                        
                    } else {
                        log.error("class {} was not found for module '{}', so this module is not created/loaded",
                                moduleCanonicalClassName, moduleId);
                        return null;
                    }   
                    
                } catch (ClassNotFoundException e) {
                    log.error("class {} was not found for module '{}', so this module is not created/loaded",
                            moduleCanonicalClassName, moduleId);                    
                    return null;
                }
                
            } else return getCreatedModule(moduleId); // if already created
            
        } else return null; //if module disabled        
    }   
    
    public boolean moduleDisabled(String moduleId) {
        return disabledModules.contains(moduleId);
    }
    
    protected boolean moduleCreated(String moduleId) {
        return modules.containsKey(moduleId);
    }    
    
    protected SametimedModule getCreatedModule(String moduleId) {
        return modules.get(moduleId);
    }
    
    protected void onModuleCreated(String moduleId,
            SametimedModule module, ModuleConfig config) { 
        modules.put(moduleId, module);        
    }    
        
    protected SametimedModule createModuleUsingConfig(String moduleId, Class<?> moduleClass, ModuleConfig config) {
        String moduleClassName = moduleClass.getCanonicalName();
        
        try {
            
            Constructor<?> ctor = moduleClass.getDeclaredConstructor(String.class);
            ctor.setAccessible(true);
            Object module = ctor.newInstance(moduleId);
            if (module != null) {
                if (module instanceof SametimedModule) { // TODO: check before instantiating, not after
                    log.debug("module '{}', class {} instance was successfully created", moduleId, moduleClassName);
                    onModuleCreated(moduleId, (SametimedModule)module, config);
                    // MAIN SUCCESSFUL RETURN
                    return (SametimedModule)module;
                } else {
                    log.error("module '{}', class {} instance not extends " +
                              "SametimedModule, module initialization failure", moduleId, moduleClassName);
                    return null;                    
                }
            }
            
            return null;
            
        } catch (SecurityException e) {
            log.error("Constructor(String) in class {} is inaccessible (private?) "
                    + "at module '{}'", moduleClassName, moduleId);            
            return null;
        } catch (NoSuchMethodException e) {
            log.error("No constructor(String) was found in class {} "
                      + "for module '{}'", moduleClassName, moduleId);
            return null;
        } catch (Exception e) {
            log.error("Failed to call constructor(String) of class {} "
                      + "for module '{}'", moduleClassName, moduleId);
            log.error("Error was caused by: {} / {}", e.getClass().getName(), e.getMessage());
            return null;
        } 
    }

    public ModulesList getEnabledModules() {
        return modules;
    }    
    
    public SametimedModule getModule(String moduleId) {
        return modules.get(moduleId);
    }
        
}
