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
import org.sametimed.message.CommandsFactory;
import org.sametimed.message.Update;
import org.sametimed.module.ModulesFactory;
import org.sametimed.wave.WaveServerProperties;
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
    
    private final Channel confirmChannel;    
    private final Channel updatesChannel;
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
        
        // client  -> joinChannel  -> server
        // client <-  cfrmChannel <-  server 
        // client  -> cmdChannel   -> server
        // client <-  updChannel  <-  server      
        
        // TODO: some channel that will monitor admin console changes
        
        confirmChannel = getBayeux().getChannel(config.getCfrmChannelPath(), true);        
        updatesChannel = getBayeux().getChannel(config.getUpdChannelPath(), true);
        log.info("Sametimed Bayeux service initialized under {}", config.getFullTunnelPath());
        
        this.commandsFactory = new CommandsFactory(config.getRegisteredCommands());
        this.modulesFactory = new ModulesFactory(config.getModulesToPrepare(),
                                                 config.getModulesToDisable()); 
        
        log.info("these modules are successfully configured and initialized: {}", 
                                modulesFactory.getEnabledModules().toString());
               
    }
    
    /**
     * Fires when client tries to join
     * 
     * @param remote client that tries to join
     * @param data joining process information
     */
    public void tryJoin(final Client remote, Map<String, Object> data) {
        log.info("client {} tries to join as {}", remote.getId(),
                data.get("username"));
        
        if (!clients.containsKey(remote.getId())) {
            
            String username = (String)data.get("username") + 
                              "@" + waveServerProps.getDomain();
            log.info("registering new client as {}", username);
                
            clients.put(remote.getId(), 
                new SametimedClient(username, modulesFactory.getEnabledModules()) {

                    @Override
                    public void sendUpdate(Update update) {
                        if (updatesChannel != null) {
                            updatesChannel.publish(remote, 
                                                   update.extractData(), 
                                                   update.getHashcode());
                            log.debug("published update to channel");
                        }
                    }                                
                
                }); 
               
            Map<String, String> statusData = new HashMap<String, String>();
            statusData.put("username", data.get("username").toString());
            statusData.put("status", "ok");
            confirmChannel.publish(remote, statusData, "serv");
                
        } else {
            
            log.info("client already registered, join is not performed");            
            Map<String, String> statusData = new HashMap<String, String>();
            statusData.put("username", data.get("username").toString());
            statusData.put("status", "passed");
            confirmChannel.publish(remote, statusData, null);  
            
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
