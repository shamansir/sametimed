/**
 * 
 */
package org.sametimed.facade;

import org.cometd.Bayeux;
import org.cometd.Client;
import org.cometd.Message;
import org.cometd.server.BayeuxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project: sametimed
 * Package: org.sametimed.facade
 *
 * SametimedService
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 22, 2010 5:15:25 PM 
 *
 */
public class SametimedService extends BayeuxService {
    
    private static final Logger log = LoggerFactory
            .getLogger(SametimedService.class);    
    
    public static final String SERVICE_NAME = "sametimed";

    // TODO: load channels paths from sametimed.xml
    
    /**
     * Initiate service at {@code sametimed/* } channel
     * 
     * @param bayeux a Bayeux server interface
     */
    public SametimedService(Bayeux bayeux) {
        super(bayeux, SERVICE_NAME);
        subscribe("/" + SERVICE_NAME + "/join", "tryJoin");
        subscribe("/" + SERVICE_NAME + "/cmd", "processCmd");
        log.info("Sametimed Bayeux service initialized under /{}", SERVICE_NAME); 
    }
    
    public void tryJoin(Client remote, Message message) {
        
    }     
    
    public void processCmd(Client remote, Message message) {
        
    }    

}
