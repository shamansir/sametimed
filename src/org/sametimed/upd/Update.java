/**
 * 
 */
package org.sametimed.upd;

import org.cometd.server.MessageImpl;

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
public class Update extends MessageImpl {
    
    // TODO: private final UpdateType updateType;
    private final String changedModuleId;
    private final String changedDocumentId;
    // TODO: private final TagID changedTag;
    
    public Update(String changedModuleId, String changedDocumentId) {
        this.changedModuleId = changedModuleId;
        this.changedDocumentId = changedModuleId;
    }

}
