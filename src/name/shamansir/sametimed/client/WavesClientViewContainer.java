package name.shamansir.sametimed.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WavesClientViewContainer implements IsSerializable {
	
	/* Empty constructor required for Serializable classes */
	public WavesClientViewContainer() {
		this(INVALID_ID, null, null);
	}	
	
	public static final int INVALID_ID = -1;
	
	private int clientId;
	private String holderElementId; 
	private String clientContent; 	
	
	public WavesClientViewContainer(int clientId, String holderElementId, String clientContent) {
		this.clientId = clientId;
		this.holderElementId = holderElementId;		
		this.clientContent = clientContent;
	}
	
	public WavesClientViewContainer(int clientId, String clientContent) {
		this(clientId, null, clientContent);
	}	
	
	public int getClientId() {
		return clientId;
	}
	
	public String getClientContent() {
		return clientContent;
	}
	
	public String getHolderElementId() {
		return holderElementId;
	}		
	
}
