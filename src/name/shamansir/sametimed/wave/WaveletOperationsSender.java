package name.shamansir.sametimed.wave;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;
import org.waveprotocol.wave.model.id.WaveletName;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * A stub to send wavelet operations, wrapping ClientBackend for the
 * moment 
 *
 */

public class WaveletOperationsSender {
	
	private final ClientBackend backend;
	
	public WaveletOperationsSender(ClientBackend backend) {
		this.backend = backend;
	}
	
	public void sendWaveletOperation(WaveletName waveletName, WaveletOperation operation) {
		this.backend.sendWaveletOperation(waveletName, operation);
	}
	
}
