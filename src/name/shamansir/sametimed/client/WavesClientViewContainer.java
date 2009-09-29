package name.shamansir.sametimed.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WavesClientViewContainer implements IsSerializable {
	
	/* Empty constructor required for Serializable classes */
	public WavesClientViewContainer() {
		this(INVALID_ID, "");
	}	
	
	public static final int INVALID_ID = -1;
	
	private int clientId;
	private String modelAsJSON; 
	
	public WavesClientViewContainer(int clientId, String modelAsJSON) {
		this.clientId = clientId;
		this.modelAsJSON = modelAsJSON;
	}	
	
	public int getClientId() {
		return clientId;
	}
	
	public String getModelAsJSON() {
		return modelAsJSON;
	}	
	
}
