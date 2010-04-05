/**
 * 
 */
package org.sametimed.message;

import org.cometd.Message;
import org.sametimed.facade.SametimedConfig.CommandsDataList;

/**
 * Project: sametimed
 * Package: org.sametimed.message
 *
 * CommandsFactory
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 4, 2010 1:16:43 PM 
 *
 */
public class CommandsFactory {
    
    private final CommandsDataList commandsData;
    
    public CommandsFactory(CommandsDataList commandsDataList) { 
        this.commandsData = commandsDataList;
    }

    public Command fromMessage(Message message) {
        // FIXME: implement
        return null;
    }

}
