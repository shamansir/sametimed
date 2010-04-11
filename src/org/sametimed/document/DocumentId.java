/**
 * 
 */
package org.sametimed.document;

/**
 * Project: sametimed
 * Package: org.sametimed.module
 *
 * DocumentId
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 11, 2010 2:32:30 PM 
 *
 */
public class DocumentId {
    
    private final String value;

    public DocumentId(String value) {
        this.value = value;
    }
    
    public static DocumentId valueOf(String strValue) {
        return new DocumentId(strValue);
    }
    
    @Override
    public String toString() {
        return value;
    }       

}
