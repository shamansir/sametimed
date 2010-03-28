/**
 * 
 */
package org.sametimed.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Project: sametimed
 * Package: org.sametimed.upd
 *
 * Update
 * 
 * Some update that identifies itself by hash id (inherited from MessageImpl), 
 * knows it's type, the called client (also from MessageImpl), changed module 
 * and changed document
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 27, 2010 9:57:50 PM 
 *
 */
public class Update {
    
    private final String callerId;
    // TODO: private final UpdateType updateType;
    private final String changedModuleId;
    private final String changedDocumentId;
    private final String hash;
    // TODO: private final TagID changedTag;
    private final List<String> arguments = new ArrayList<String>();    
    
    private Update(String callerId, 
                   String changedModuleId, 
                   String changedDocumentId) {
        this.callerId = callerId;
        this.changedModuleId = changedModuleId;
        this.changedDocumentId = changedModuleId;
        this.hash = ""; // FIXME: implement
    }

    /**
     * @return
     */
    public Map<String, String> extractData() {
        // FIXME: implement
        return null;
    }
    
    public String getCallerId() {
        return callerId;
    }

}
