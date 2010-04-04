/**
 * 
 */
package org.sametimed.message;

import org.cometd.Message;

/**
 * Project: sametimed
 * Package: org.sametimed.cmd
 *
 * Command
 * 
 * Some command that identifies itself by hash and id (inherited from MessageImpl), 
 * knows it's type, the target client (also, from MessageImpl), module and document
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 27, 2010 9:54:15 PM 
 *
 */
public class Command {
    
    private final String senderId;
    //private final String hash;
    // TODO: private final CommandType commandType;
    private final String targetModuleId;
    private final String targetDocumentId;
    // private final String[] arguments;
    
    protected Command(String senderId, 
                      String targetModuleId, String targetDocumentId) {
        this.senderId = senderId;
        this.targetModuleId = targetModuleId;
        this.targetDocumentId = targetDocumentId;
        //this.hash = ""; // TODO: generate hash
        //this.arguments = new String[3]; // FIXME: load number of args from constructor
    }
    
    public static Command fromMessage(Message message) {
        // FIXME: implement
        return null;
    }
    
}
