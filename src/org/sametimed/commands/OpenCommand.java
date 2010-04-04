/**
 * 
 */
package org.sametimed.commands;

import org.sametimed.message.Command;

/**
 * Project: sametimed
 * Package: org.sametimed.commands
 *
 * OpenCommand
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 4, 2010 4:18:43 PM 
 *
 */
public class OpenCommand extends Command {

    private OpenCommand(String senderId, String targetModuleId,
            String targetDocumentId) {
        super(senderId, targetModuleId, targetDocumentId);
    }    
    
}
