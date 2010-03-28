/**
 * 
 */
package org.sametimed.facade;

import org.cometd.Client;
import org.sametimed.message.Command;
import org.sametimed.message.Update;

/**
 * Project: sametimed
 * Package: org.sametimed.facade
 *
 * SametimedClient
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 27, 2010 8:57:41 PM 
 *
 */
public abstract class SametimedClient {
    
    private final Client cometdClient;
    
    public SametimedClient(Client cometdClient) {
        this.cometdClient = cometdClient;
    }

    /** 
     * Handles command and applies it internally
     */
    public void handleCommand(Command command) {
        // FIXME: implement        
    }
    
    public abstract void sendUpdate(Update update);
    
    protected Client getCometdClient() {
        return this.cometdClient;
    }

}
