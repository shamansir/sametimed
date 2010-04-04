/**
 * 
 */
package name.shamansir.sametimed.modules.chat.commands;

import org.sametimed.message.Command;

/**
 * Project: sametimed
 * Package: name.shamansir.sametimed.modules.chat.commands
 *
 * SayCommand
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 4, 2010 4:22:19 PM 
 *
 */
public class SayCommand extends Command {

    private SayCommand(String senderId, String targetModuleId,
            String targetDocumentId) {
        super(senderId, targetModuleId, targetDocumentId);
    }     
    
}
