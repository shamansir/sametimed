/**
 * 
 */
package org.sametimed.cmd;

/**
 * Project: sametimed
 * Package: org.sametimed.cmd
 *
 * CommandsListener
 * 
 * Someone who listens for received commands
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 27, 2010 9:34:50 PM 
 *
 */
public interface CommandsListener {
    
    public String getClientId(); // returns id that determines it as 
                                 // concrete commands listener
    public void commandReceived(Command command);

}
