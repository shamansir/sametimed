/**
 * 
 */
package org.sametimed.commands;

import org.sametimed.message.Command;

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

    private RemoveCommand(String alias, String senderId, String targetModuleId,
            String targetDocumentId) {
        super(alias, senderId, targetModuleId, targetDocumentId);
    } 
    
}
