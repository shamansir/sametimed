/**
 * 
 */
package org.sametimed.message;

/**
 * Project: sametimed
 * Package: org.sametimed.message
 *
 * CommandAlias
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 11, 2010 2:30:18 PM 
 *
 */
public class CommandAlias {
    
    private final String value;

    public CommandAlias(String value) {
        this.value = value;
    }
    
    public static CommandAlias valueOf(String strValue) {
        return new CommandAlias(strValue);
    }
    
    @Override
    public String toString() {
        return value;
    }
      
}

