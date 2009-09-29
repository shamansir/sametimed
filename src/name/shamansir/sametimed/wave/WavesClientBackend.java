package name.shamansir.sametimed.wave;

import java.io.IOException;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;

public class WavesClientBackend extends ClientBackend {
	
	private static WaveServerProperties WAVE_SRV_PROPS = new WaveServerProperties();
	
	private String userAtDomain;
	private String waveServerHostData;
	
	public WavesClientBackend(String username)
			throws IOException {
		super(username + "@" + WAVE_SRV_PROPS.getWaveDomain(), 
				WAVE_SRV_PROPS.getWaveServerHost(),
				WAVE_SRV_PROPS.getWaveServerPort()
			);
		this.userAtDomain = username + "@" + WAVE_SRV_PROPS.getWaveDomain();
		this.waveServerHostData = WAVE_SRV_PROPS.getWaveServerHost() + ":" + WAVE_SRV_PROPS.getWaveServerPort();
	}
	
	public WavesClientBackend(String userAtDomain, String server, int port) throws IOException {
		super(userAtDomain, server, port);
		this.userAtDomain = userAtDomain;
		this.waveServerHostData = server + ":" + port;
	}

	public String getWaveServerHostData() {
		return waveServerHostData;
	}

	public String getUserAtDomain() {
		return userAtDomain;
	}
	


}
