package name.shamansir.sametimed.client.proto;

import name.shamansir.sametimed.client.WavesClientViewContainer;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>SametimedClientViewService</code>.
 */
public interface IWavesClientViewServiceAsync {

	void getClientView(String forUser, 
			AsyncCallback<WavesClientViewContainer> callback);

}
