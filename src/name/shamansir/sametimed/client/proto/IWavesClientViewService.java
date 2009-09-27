package name.shamansir.sametimed.client.proto;

import java.io.IOException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import name.shamansir.sametimed.client.WavesClientViewContainer;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("get_client_view")
public interface IWavesClientViewService extends RemoteService {
	WavesClientViewContainer getClientView(String forUser, 
			AWavesClientRedrawEventsHandler eventsHandler) throws IOException;
}
