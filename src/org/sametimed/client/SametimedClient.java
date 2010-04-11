/**
 * 
 */
package org.sametimed.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.waveprotocol.wave.examples.fedone.common.HashedVersion;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.data.WaveletData;

import org.sametimed.message.Command;
import org.sametimed.message.Update;
import org.sametimed.module.ModulesList;
import org.sametimed.wave.WaveClient;
import org.sametimed.wave.WaveServerConnectionException;
import org.sametimed.wave.WaveServerProperties;

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
    
    private static final Logger log = LoggerFactory
            .getLogger(SametimedClient.class);
    
    private final String username;
    
    private ParticipantId user = null;    
    private SametimedWaveClient waveClient = null;
    
    private final ModulesList modules;
    
    public SametimedClient(String username, ModulesList enabledModules) {
        this.username = username;
        this.modules = enabledModules;
    }
    
    public void connectToWave(WaveServerProperties waveServerProps) throws WaveServerConnectionException {
        if (!connectedToWave()) {
            this.waveClient = new SametimedWaveClient(getUsername(), waveServerProps);
            this.user = this.waveClient.getUser();
        } else {
            log.info("already connected, no need to connect");
        }
    }
    
    public boolean connectedToWave() {
        return (waveClient != null) && (user != null);
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
    
    private class SametimedWaveClient extends WaveClient {

        public SametimedWaveClient(String username, 
                                   WaveServerProperties waveServerProps) throws WaveServerConnectionException {
            super(username, waveServerProps);
        }

        @Override
        public void noOp(String author, WaveletData wavelet) { }

        @Override
        public void onCommitNotice(WaveletData wavelet, HashedVersion version) { }

        @Override
        public void onDeltaSequenceEnd(WaveletData wavelet) { }

        @Override
        public void onDeltaSequenceStart(WaveletData wavelet) { }

        @Override
        public void participantAdded(String author, WaveletData wavelet,
                ParticipantId participantId) { }

        @Override
        public void participantRemoved(String author, WaveletData wavelet,
                ParticipantId participantId) { }

        @Override
        public void waveletDocumentUpdated(String author, WaveletData wavelet,
                WaveletDocumentOperation docOp) { }
        
        @Override
        public void connectionFailed(String wsHostname, int wsPort) { }
        
    }

}
