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
 * UndoCommand
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 4, 2010 4:21:19 PM 
 *
 */
public class UndoCommand extends Command {

    protected UndoCommand(CommandAlias alias, CommandType type,
            ClientId senderId, ModuleId targetModuleId,
            DocumentId targetDocumentId) {
        super(alias, type, senderId, targetModuleId, targetDocumentId);
    }

  
    
}
