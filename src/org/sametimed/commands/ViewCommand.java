/**
 * 
 */
package org.sametimed.commands;

import org.sametimed.message.Command;

/**
 * Project: sametimed
 * Package: org.sametimed.commands
 *
 * ViewCommand
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 4, 2010 4:19:51 PM 
 *
 */
public class ViewCommand extends Command {

    private ViewCommand(String alias, String senderId, String targetModuleId,
            String targetDocumentId) {
        super(alias, senderId, targetModuleId, targetDocumentId);
    }    
    
}
