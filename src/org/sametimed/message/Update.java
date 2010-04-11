/**
 * 
 */
package org.sametimed.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sametimed.ClientId;
import org.sametimed.document.DocumentId;
import org.sametimed.module.ModuleId;

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
    
    private final ClientId callerId;
    // TODO: private final UpdateType updateType;
    private final ModuleId changedModuleId;
    private final DocumentId changedDocumentId;
    private final String hashcode;
    // TODO: private final TagID changedTag;
    private final List<String> arguments = new ArrayList<String>(); 
    
    private Update(ClientId callerId, 
                   ModuleId changedModuleId, 
                   DocumentId changedDocumentId) {
        this.callerId = callerId;
        this.changedModuleId = changedModuleId;
        this.changedDocumentId = changedDocumentId;
        this.hashcode = null; // FIXME: implement
    }

    /**
     * @return
     */
    public Map<String, String> extractData() {
        // FIXME: implement
        return null;
    }
    
    public ClientId getCallerId() {
        return callerId;
    }
    
    public String getHashcode() {
        return hashcode;
    }

}
