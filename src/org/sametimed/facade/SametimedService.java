/**
 * 
 */
package org.sametimed.facade;

import java.util.HashMap;
import java.util.Map;

import org.cometd.Bayeux;
import org.cometd.Channel;
import org.cometd.Client;
import org.cometd.Message;
import org.cometd.server.BayeuxService;
import org.cometd.server.ext.TimesyncExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project: sametimed
 * Package: org.sametimed.facade
 *
 * SametimedService
 * 
 * The main entry point for the clients, manages them, receives and dispatches 
 * commands, creates updates channel
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 22, 2010 5:15:25 PM 
 *
 */
public class SametimedService extends BayeuxService {
    
    private static final Logger log = LoggerFactory
            .getLogger(SametimedService.class);    
    
    private final Channel updatesChannel;

    /**
     * Initiate service at {@code w/* } channel
     * 
     * @param bayeux a Bayeux server interface
     */
    public SametimedService(Bayeux bayeux) {
        this(bayeux, null);
    }
    
    /**
     * Initiate service at {@code w/* } channel
     * 
     * @param bayeux a Bayeux server interface
     * @param config Sametimed configuration data
     */
    public SametimedService(Bayeux bayeux, SametimedConfig config) {
        super(bayeux, config.getAppName());        
        
        bayeux.addExtension(new TimesyncExtension());
        
        subscribe(config.getJoinChannelPath(), "tryJoin");
        subscribe(config.getCmdChannelPath(), "processCmd");
        
        updatesChannel = getBayeux().getChannel(config.getUpdChannelPath(), true);
        log.info("Sametimed Bayeux service initialized under {}", config.getFullTunnelPath());
        
        // FIXME: check if works on hostname different from localhost
    }
    
    /**
     * Fires when client tries to join
     * 
     * @param remote client that tries to join
     * @param message message with joining information
     */
    public void tryJoin(Client remote, Message message) {
        log.info("join received");
        if (updatesChannel != null)
        {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("test", "aaafaa");
            updatesChannel.publish(null, data, null);
            log.info("published to channel");
        }         
    }     
    
    /**
     * Fires when command is received
     * 
     * @param remote client who sent the command
     * @param message the command data
     */
    public void processCmd(Client remote, Message message) {
        
    }    

}
