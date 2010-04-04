/**
 * 
 */
package org.sametimed.commands;

import org.sametimed.message.Command;

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

    private UndoCommand(String senderId, String targetModuleId,
            String targetDocumentId) {
        super(senderId, targetModuleId, targetDocumentId);
    }    
    
}
