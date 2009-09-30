package name.shamansir.sametimed.wave.render;

public class WaveInfoProvider {

	public String getInfoLineCaption(String userAtDomain,
			String waveServerHostData, int viewID) {
		return "connected as " + userAtDomain +
			 " on the " + waveServerHostData + " server" + 
			 // FIXME: remove
			 " (" + Integer.toString(viewID) + ")";
	}

}
