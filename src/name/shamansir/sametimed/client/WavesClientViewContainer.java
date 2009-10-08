package name.shamansir.sametimed.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * @see SametimedView#onModuleLoad()
 * @see name.shamansir.sametimed.wave.WavesClient
 * 
 * Container, holding information, returned with asynchronous call for Waves Client.
 * Contains id of the Waves Client (in order of connected clients), and a JSON string, representing its model.
 *
 */
public class WavesClientViewContainer implements IsSerializable {
	
	/**
	 * Empty constructor required for Serializable classes, generating Invalid ID
	 */
	public WavesClientViewContainer() {
		this(INVALID_ID, "");
	}	
	
	/**
	 * An invalid id, may be used to represent invalid client views
	 */
	public static final int INVALID_ID = -1;
	
	private int clientId;
	private String modelAsJSON; 
	
	/**
	 * @param clientId id of the Waves Client (generated automatically in WavesClient class)
	 * @param modelAsJSON string, representing the Waves Client model in JSON format
	 * 
	 * @see name.shamansir.sametimed.wave.WavesClient#getViewId()
	 * @see name.shamansir.sametimed.wave.WavesClient#getClientModel()
	 */
	public WavesClientViewContainer(int clientId, String modelAsJSON) {
		this.clientId = clientId;
		this.modelAsJSON = modelAsJSON;
	}	
	
	/**
	 * @return id of the Waves Client
	 */
	public int getClientId() {
		return clientId;
	}
	
	/**
	 * @return full model of the Waves Client in JSON format
	 */	
	public String getModelAsJSON() {
		return modelAsJSON;
	}	
	
}
