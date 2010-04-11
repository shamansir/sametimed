/**
 * 
 */
package org.sametimed.module;

/**
 * Project: sametimed
 * Package: org.sametimed.module
 *
 * SametimedModule
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 4, 2010 12:24:33 PM 
 *
 */
public class SametimedModule {
        
    private final ModuleId moduleId;
    
    protected SametimedModule(final ModuleId id) {
        moduleId = id;
    }

    /**
     * @return
     */
    public ModuleId getId() {
        return moduleId;
    }

}
