/**
 * 
 */
package org.sametimed.facade;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cometd.Client;
import org.sametimed.facade.wave.WavesClient;
import org.sametimed.message.Command;
import org.sametimed.message.Update;
import org.sametimed.module.SametimedModule;
import org.sametimed.view.SametimedClientView;

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
    
    private final String username;    
    
    private final Client cometdClient;
    private final WavesClient wavesClient; // connects to the server and stores 
                                           // waves
    private final SametimedClientView view;
    
    private final Map<String, SametimedModule> modules = 
                                         new HashMap<String, SametimedModule>();
    
    public SametimedClient(Client cometdClient, String username, Set<SametimedModule> modules) {
        this.cometdClient = cometdClient;
        this.wavesClient = null; // FIXME: implement
        this.view = null; // FIXME: implement
        this.username = username;
        for (SametimedModule module: modules) {
            this.modules.put(module.getId(), module);
        }
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
    
    public String getUsername() {
        return username;
    }

}
