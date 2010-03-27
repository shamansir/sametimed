/**
 * 
 */
package org.sametimed.facade;

import org.cometd.Client;
import org.sametimed.cmd.Command;
import org.sametimed.cmd.CommandsListener;

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
public class SametimedClient implements CommandsListener {
    
    private final Client cometdClient;
    
    public SametimedClient(Client cometdClient) {
        this.cometdClient = cometdClient;
    }

    /* (non-Javadoc)
     * @see org.sametimed.cmd.CommandsListener#commandReceived(org.sametimed.cmd.Command)
     */
    @Override
    public void commandReceived(Command command) {
        // FIXME: implement        
    }

    /**
     * Returns client id
     * 
     * @return client id
     */
    @Override
    public String getClientId() {
        return cometdClient.getId();
    }

}
