package name.shamansir.sametimed.wave;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.model.id.WaveId;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * A stub to provide waves by ID (if it will be allowed in 
 * future versions of wave-protocol server), wrapping ClientBackend for the
 * moment 
 *
 */

public class WavesProvider {
	
	private final ClientBackend source;
	
	public WavesProvider(ClientBackend source) {
		this.source = source;
	}
	
	public ClientWaveView getWave(WaveId waveID) {
		return source.getWave(waveID);
	}
	
	public ClientWaveView getIndexWave() {
		return source.getIndexWave();
	}

}
