package name.shamansir.sametimed.client.proto;

import java.io.IOException;

import name.shamansir.sametimed.client.WavesClientViewContainer;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("get_client_view")
public interface IWavesClientViewService extends RemoteService {
	WavesClientViewContainer getClientView(String forUser, 
			AWavesClientRedrawEventsHandler eventsHandler, 
			String redrawJSFuncName) throws IOException;
}
