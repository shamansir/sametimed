/**
 * 
 */
package org.sametimed.facade;

import org.sametimed.message.Command;
import org.sametimed.message.Update;
import org.sametimed.module.ModulesList;
import org.sametimed.wave.WavesClient;

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
    
    private final WavesClient wavesClient; // connects to the server and stores 
                                           // waves    
    private final ModulesList modules;
    
    public SametimedClient(String username, ModulesList enabledModules) {
        this.username = username;
        this.modules = enabledModules;
        this.wavesClient = null; // FIXME: implement
    }

    /** 
     * Handles command and applies it internally
     */
    public void handleCommand(Command command) {
        // FIXME: implement        
    }
    
    public abstract void sendUpdate(Update update);
    
    public String getUsername() {
        return username;
    }

}
