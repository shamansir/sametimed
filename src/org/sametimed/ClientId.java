/**
 * 
 */
package org.sametimed;

/**
 * Project: sametimed
 * Package: org.sametimed
 *
 * ClientId
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 11, 2010 2:35:19 PM 
 *
 */
public class ClientId {

    private final String value;

    public ClientId(String value) {
        this.value = value;
    }
    
    public static ClientId valueOf(String strValue) {
        return new ClientId(strValue);
    }
    
    @Override
    public String toString() {
        return value;
    }     
    
}
