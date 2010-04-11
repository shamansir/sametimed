/**
 * 
 */
package org.sametimed.message;

import org.sametimed.ClientId;
import org.sametimed.document.DocumentId;
import org.sametimed.module.ModuleId;

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
    
    private final CommandAlias alias;
    private final CommandType type; 
    private final ClientId senderId;
    //private final String hash;
    // FIXME: private final CommandType commandType;
    private final ModuleId targetModuleId;
    private final DocumentId targetDocumentId;
    // private final String[] arguments;
    
    protected Command(CommandAlias alias, CommandType type, ClientId senderId, 
                      ModuleId targetModuleId, DocumentId targetDocumentId) {
        this.alias = alias;
        this.type = type;
        this.senderId = senderId;
        this.targetModuleId = targetModuleId;
        this.targetDocumentId = targetDocumentId;
        //this.hash = ""; // TODO: generate hash
        //this.arguments = new String[3]; // FIXME: load number of args from constructor
    }
    
    // FIXME: implement getType()
    
    public CommandAlias getAlias() {
        return alias;
    }
    
}
