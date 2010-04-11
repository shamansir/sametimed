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
import org.sametimed.ClientId;
import org.sametimed.message.CommandsFactory;
import org.sametimed.message.Update;
import org.sametimed.module.ModuleConfig;
import org.sametimed.module.ModuleId;
import org.sametimed.module.ModulesFactory;
import org.sametimed.module.SametimedModule;
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
    
    // TODO: ability to store user session between page reloads
    
    private static final Logger log = LoggerFactory
            .getLogger(SametimedService.class);    
    
    private final Channel confirmChannel;    
    private final Channel updatesChannel;
    private final Channel mfactoryChannel;
    
    private final Map<ClientId, SametimedClient> clients 
          = Collections.synchronizedMap(new HashMap<ClientId, SametimedClient>());
    
    private final Map<ModuleId, ModuleConfig> modulesConfigs
          = Collections.synchronizedMap(new HashMap<ModuleId, ModuleConfig>());
    
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
        mfactoryChannel = getBayeux().getChannel(config.getMFtryChannelPath(), true);
        log.info("Sametimed Bayeux service initialized under {}", config.getFullTunnelPath());
        
        this.commandsFactory = new CommandsFactory(config.getRegisteredCommands());
        this.modulesFactory = new ModulesFactory(config.getModulesToPrepare(),
                                                 config.getModulesToDisable()) {
            
            @Override
            protected void onModuleReady(ModuleId moduleId, 
                                         SametimedModule module, 
                                         ModuleConfig config) {
                super.onModuleReady(moduleId, module, config);
                // for modules-to-prepare, this method is called right from
                // the factory constructor so when user joins, these modules
                // are ready (created) for sure
                modulesConfigs.put(moduleId, config);
            }
            
        }; 
        
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
        
        // FIXME: use service client? 
        if (!clients.containsKey(remote.getId())) {
            
            // TODO: recheck username
            String username = (String)data.get("username") + 
                              "@" + waveServerProps.getDomain();
            log.info("registering new client as {}", username);
                
            clients.put(ClientId.valueOf(remote.getId()),
                new SametimedClient(username, modulesFactory.getEnabledModules()) {

                    @Override
                    public void sendUpdate(Update update) {
                        publishUpdate(update);
                    }                                
                
                }); 
               
            remote.startBatch();
            publishConfirmation(remote, data.get("username").toString(), true);
            publishModulesConfiguration(remote);
            remote.endBatch();
            
        } else {
            
            publishConfirmation(remote, data.get("username").toString(), false);
            
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
                                          // TODO: pass only map and msgId/clientId
                                          commandsFactory.fromMessage(message));
        }
    }
    
    protected void publishUpdate(Update update) {
        updatesChannel.publish(getClient(), 
                               update.extractData(), 
                               update.getHashcode());
        log.debug("published update to channel");        
    }
    
    protected void publishConfirmation(final Client remote, String username, boolean registered) {
        Map<String, String> statusData = new HashMap<String, String>();
        statusData.put("username", username);
        statusData.put("status", registered ? "ok" : "passed");
        remote.deliver(getClient(), confirmChannel.getId(), statusData, null);
    }
    
    protected void publishModuleData(final Client remote, ModuleId moduleId, ModuleConfig moduleConfig) {
        Map<String, String> moduleData = new HashMap<String, String>();
        moduleData.put("moduleId", moduleId.toString());
        moduleData.put("treeStructured", String.valueOf(moduleConfig.treeStructured()));
        moduleData.put("prerendersUpdates", String.valueOf(moduleConfig.prerendersUpdates()));
        remote.deliver(getClient(), mfactoryChannel.getId(), moduleData, null);
    }
    
    protected void publishModulesConfiguration(Client remote) {
        for (Map.Entry<ModuleId, ModuleConfig> entry: modulesConfigs.entrySet()) {
            publishModuleData(remote, entry.getKey(), entry.getValue());
        }        
    }    

}
