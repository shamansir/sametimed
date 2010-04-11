/**
 * 
 */
package org.sametimed.wave;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;
import org.waveprotocol.wave.examples.fedone.waveclient.common.WaveletOperationListener;
import org.waveprotocol.wave.model.wave.ParticipantId;

/**
 * Project: sametimed
 * Package: org.sametimed.facade.wave
 *
 * WaveClient
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Apr 4, 2010 12:16:46 PM 
 *
 */
public abstract class WaveClient implements WaveletOperationListener {
    
    private static final Logger log = LoggerFactory.getLogger(WaveClient.class);

    private final ParticipantId waveUser;
    
    private ClientBackend backend = null;
    
    public WaveClient(String username, WaveServerProperties waveServerProps) throws WaveServerConnectionException {
        this.waveUser = new ParticipantId(username + "@" + waveServerProps.getDomain()); 
        
        final String wsHostname = waveServerProps.getHostname();
        final int wsPort = waveServerProps.getPort();
        try {
            this.backend = new ClientBackend(waveUser.getAddress(), wsHostname, wsPort);
            this.backend.addWaveletOperationListener(this);
            log.info("Wave Server found at {}:{}", wsHostname, wsPort);
            log.info("Successfully connected to Wave Server as {}", this.waveUser.toString());
        } catch (IOException e) {
            connectionFailed(wsHostname, wsPort);
            log.error("Failed to connect to Wave server at {}:{}", wsHostname, wsPort);
            throw new WaveServerConnectionException();
        }
    }

    protected void connectionFailed(String wsHostname, int wsPort) { }

    public ParticipantId getUser() {
        return waveUser;
    }    
    
}
