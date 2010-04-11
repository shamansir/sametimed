/**
 * 
 */
package org.sametimed.commands;

import org.sametimed.ClientId;
import org.sametimed.document.DocumentId;
import org.sametimed.message.Command;
import org.sametimed.message.CommandAlias;
import org.sametimed.message.CommandType;
import org.sametimed.module.ModuleId;

/**
 * Project: sametimed
 * Package: org.sametimed.commands
 *
 * RemoveCommand
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 9, 2010 8:41:33 AM 
 *
 */
public class RemoveCommand extends Command {

    private RemoveCommand(CommandAlias alias, CommandType type,
            ClientId senderId, ModuleId targetModuleId,
            DocumentId targetDocumentId) {
        super(alias, type, senderId, targetModuleId, targetDocumentId);
        // TODO Auto-generated constructor stub
    }


    
}
