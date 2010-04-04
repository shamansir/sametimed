/**
 * 
 */
package org.sametimed.commands;

import org.sametimed.message.Command;

/**
 * Project: sametimed
 * Package: org.sametimed.commands
 *
 * AddCommand
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 4, 2010 4:19:19 PM 
 *
 */
public class AddCommand extends Command {

    private AddCommand(String senderId, String targetModuleId,
            String targetDocumentId) {
        super(senderId, targetModuleId, targetDocumentId);
    }     
    
}
