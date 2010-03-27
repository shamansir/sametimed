/**
 * 
 */
package org.sametimed.cmd;

/**
 * Project: sametimed
 * Package: org.sametimed.cmd
 *
 * CommandsReceiver
 * 
 * The object that receives commands. Gives commands using listeners id's.
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 27, 2010 9:41:07 PM 
 *
 */
public interface CommandsReceiver {

    public void addListener(CommandsListener listener);
    public void removeListener(CommandsListener listener);
    public void dispatchCommand(Command command);
    
}
