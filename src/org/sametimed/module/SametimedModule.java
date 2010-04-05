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
    
    // FIXME: private final ModuleId moduleId;    
    private final String moduleId;
    
    protected SametimedModule(final String id) {
        moduleId = id;
    }

    /**
     * @return
     */
    public String getId() {
        return moduleId;
    }

}
