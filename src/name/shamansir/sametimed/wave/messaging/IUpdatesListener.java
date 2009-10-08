package name.shamansir.sametimed.wave.messaging;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * A listener that can receive messages about client updates (passed from wave
 * server or the server-side (WavesClient))
 * 
 * @see UpdateMessage 
 *
 */

public interface IUpdatesListener {
	
	/**
	 * Called on any update (currently, updates of the WavesClient model)
	 * 
	 * @param updateMessage message, describing the update
	 */
	public void onUpdate(UpdateMessage updateMessage);

}
