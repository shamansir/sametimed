package name.shamansir.sametimed.client.proto;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 */

/* FIXME: this class must to be abstract, but GWT doesn't recognizes it as Serializable int his way */ 
public class AWavesClientRedrawEventsHandler implements IsSerializable {
	
	private static final long serialVersionUID = -5597838847532592353L;
	
	/* Empty constructor required for Serializable classes */
	public AWavesClientRedrawEventsHandler(){}	

	public void redrawClientPanel(String holderId, String panelContent) { };	

	// public abstract void redrawClientPanel(String holderId, String panelContent); 	
	
}
