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
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 22, 2010 5:15:25 PM 
 *
 */
public class SametimedService extends BayeuxService {
    
    private static final Logger log = LoggerFactory
            .getLogger(SametimedService.class);    
    
    public static final String SERVICE_NAME = "w";

    // TODO: load channels paths from sametimed.xml
    
    /**
     * Initiate service at {@code sametimed/* } channel
     * 
     * @param bayeux a Bayeux server interface
     */
    public SametimedService(Bayeux bayeux) {
        super(bayeux, SERVICE_NAME);
        bayeux.addExtension(new TimesyncExtension());
        subscribe("/" + SERVICE_NAME + "/join", "tryJoin");
        subscribe("/" + SERVICE_NAME + "/cmd", "processCmd");
        log.info("Sametimed Bayeux service initialized under /{}", SERVICE_NAME);
        Channel channel = getBayeux().getChannel("/" + SERVICE_NAME + "/upd", true);
        if (channel != null)
        {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("test", "aaafaa");
            channel.publish(null, data, null);
            log.info("published to channel");
        }        
    }
    
    public void tryJoin(Client remote, Message message) {
        log.info("join received");
    }     
    
    public void processCmd(Client remote, Message message) {
        
    }    

}
