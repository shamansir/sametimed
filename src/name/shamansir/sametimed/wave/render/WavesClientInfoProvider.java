package name.shamansir.sametimed.wave.render;

/**
 * @author shamansir <shaman.sir@gmail.com>
 *
 * An object that holds rendering-related information about the 
 * current client 
 *
 */
public class WavesClientInfoProvider {

	/**
	 * Get information line about the current client state
	 * 
	 * @param userAtDomain user in user@domain format
	 * @param waveServerHostData host data in host:port format
	 * @param viewID id of the client
	 * @return string, containing the info 
	 */
	public static String getInfoLineCaption(String userAtDomain,
			String waveServerHostData, int viewID) {
		return "connected as " + userAtDomain +
			 " on the " + waveServerHostData + " server" + 
			 // FIXME: remove
			 " (" + Integer.toString(viewID) + ")";
	}

}
