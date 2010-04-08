/**
 * 
 */
package org.sametimed.facade;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.cometd.Bayeux;
import org.cometd.Channel;
import org.cometd.Client;
import org.cometd.Message;
import org.cometd.server.BayeuxService;
import org.cometd.server.ext.TimesyncExtension;
import org.sametimed.facade.wave.WaveServerProperties;
import org.sametimed.message.CommandsFactory;
import org.sametimed.message.Update;
import org.sametimed.module.ModulesFactory;
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
    
    // TODO: set Eclipse to Warn about all missing JavaDoc comments/tags and
    //       fix these warnings
    
    private static final Logger log = LoggerFactory
            .getLogger(SametimedService.class);    
    
    private final Channel updatesChannel;
    private final Channel joinChannel;
    private final Map<String, SametimedClient> clients 
          = Collections.synchronizedMap(new HashMap<String, SametimedClient>());
    
    private final WaveServerProperties waveServerProps;
    
    private final CommandsFactory commandsFactory;
    private final ModulesFactory modulesFactory;
    
    /**
     * Initiate service at {@code w/* } channel (or different one, if other
     * specified in configuration file)
     * 
     * @param bayeux a Bayeux server interface
     * @param config Sametimed configuration data
     * @param waveServerProps Wave (FedOne Protocol) Server information 
     */
    public SametimedService(Bayeux bayeux, SametimedConfig config,
                                           WaveServerProperties waveServerProps) {
        super(bayeux, config.getAppName());        
        
        this.waveServerProps = waveServerProps;
                
        bayeux.addExtension(new TimesyncExtension());
        
        subscribe(config.getJoinChannelPath(), "tryJoin");
        subscribe(config.getCmdChannelPath(), "processCmd");
        
        // client <-  updChannel  <-  server
        // client <-> joinChannel <-> server
        // client  -> cmdChannel   -> server
        
        updatesChannel = getBayeux().getChannel(config.getUpdChannelPath(), true);
        joinChannel = getBayeux().getChannel(config.getJoinChannelPath(), true);
        log.info("Sametimed Bayeux service initialized under {}", config.getFullTunnelPath());
        
        // FIXME: check if works with hostname different from localhost        
        
        this.commandsFactory = new CommandsFactory(config.getCommandsData());
        this.modulesFactory = new ModulesFactory(config.getModulesData()); 
        
        log.info("these modules are successfully configured and initialized: {}", 
                                modulesFactory.getEnabledModules().toString());
               
    }
    
    /**
     * Fires when client tries to join
     * 
     * @param remote client that tries to join
     * @param message message with joining information
     */
    public void tryJoin(Client remote, Map<String, Object> data) {
        if (!data.containsKey("status")) { // not receiving message, sent by itself
            log.info("client {} tries to join as {}", remote.getId(),
                    data.get("username"));            
            if (!clients.containsKey(remote.getId())) {
                
                String username = (String)data.get("username") + 
                                  "@" + waveServerProps.getDomain();
                log.info("registering new client with id {}", username);
                    
                clients.put(remote.getId(), 
                    new SametimedClient(remote, username, modulesFactory.getEnabledModules()) {
    
                        @Override
                        public void sendUpdate(Update update) {
                            if (updatesChannel != null) {
                                updatesChannel.publish(getCometdClient(), 
                                                       update.extractData(), 
                                                       update.getHashcode());
                                log.debug("published update to channel");
                            }
                        }                                
                    
                    }); 
                   
                Map<String, String> statusData = new HashMap<String, String>();
                statusData.put("username", data.get("username").toString());
                statusData.put("status", "ok");
                joinChannel.publish(remote, statusData, "serv");
                    
            } else {
                log.info("client already registered, join is not performed");
                Map<String, String> statusData = new HashMap<String, String>();
                statusData.put("username", data.get("username").toString());
                statusData.put("status", "passed");
                joinChannel.publish(remote, statusData, null);            
            }
        }  
    }     
    
    /**
     * Fires when command is received
     * 
     * @param remote client who sent the command
     * @param message the command data
     */
    public void processCmd(Client remote, Message message) {
        if (clients.containsKey(remote.getId())) {
            clients.get(remote.getId()).handleCommand(
                                          commandsFactory.fromMessage(message));
        }
    }

}
