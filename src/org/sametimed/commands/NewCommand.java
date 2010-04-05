/**
 * 
 */
package org.sametimed.commands;

import org.sametimed.message.Command;

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

    private NewCommand(String alias, String senderId, String targetModuleId,
            String targetDocumentId) {
        super(alias, senderId, targetModuleId, targetDocumentId);
    }
    
}
