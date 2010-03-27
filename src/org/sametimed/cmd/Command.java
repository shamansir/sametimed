/**
 * 
 */
package org.sametimed.cmd;

import org.cometd.server.MessageImpl;

/**
 * Project: sametimed
 * Package: org.sametimed.cmd
 *
 * Command
 * 
 * Some command that identifies itself by hash id (inherited from MessageImpl), 
 * knows it's type, the target client (also, from MessageImpl), module and document
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 27, 2010 9:54:15 PM 
 *
 */
public class Command extends MessageImpl {

    // TODO: private final CommandType commandType;
    private final String targetModuleId;
    private final String targetDocumentId;
    
    public Command(String targetModuleId, String targetDocumentId) {
        this.targetModuleId = targetModuleId;
        this.targetDocumentId = targetDocumentId;        
    }
    
}
