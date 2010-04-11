/**
 * 
 */
package org.sametimed.commands;

import org.sametimed.client.ClientId;
import org.sametimed.document.DocumentId;
import org.sametimed.message.Command;
import org.sametimed.message.CommandAlias;
import org.sametimed.message.CommandType;
import org.sametimed.module.ModuleId;

/**
 * Project: sametimed
 * Package: org.sametimed.commands
 *
 * NewCommand
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 4, 2010 4:14:13 PM 
 *
 */
public class NewCommand extends Command {

    private NewCommand(CommandAlias alias, CommandType type,
            ClientId senderId, ModuleId targetModuleId,
            DocumentId targetDocumentId) {
        super(alias, type, senderId, targetModuleId, targetDocumentId);
        // TODO Auto-generated constructor stub
    }


    
}
