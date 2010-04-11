/**
 * 
 */
package org.sametimed.message;

/**
 * Project: sametimed
 * Package: org.sametimed.message
 *
 * CommandType
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 11, 2010 2:31:53 PM 
 *
 */
public class CommandType {
    
    private final String value;

    public CommandType(String value) {
        this.value = value;
    }
    
    public static CommandType valueOf(String strValue) {
        return new CommandType(strValue);
    }
    
    @Override
    public String toString() {
        return value;
    }    

}
