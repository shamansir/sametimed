/**
 * 
 */
package org.sametimed.module;

/**
 * Project: sametimed
 * Package: org.sametimed.module
 *
 * ModuleId
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 11, 2010 2:27:12 PM 
 *
 */
public class ModuleId {
    
    private final String value;

    public ModuleId(String value) {
        this.value = value;
    }
    
    public static ModuleId valueOf(String strValue) {
        return new ModuleId(strValue);
    }
    
    @Override
    public String toString() {
        return value;
    }
    
}
