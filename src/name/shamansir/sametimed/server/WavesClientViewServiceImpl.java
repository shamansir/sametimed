package name.shamansir.sametimed.server;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import name.shamansir.sametimed.client.WavesClientViewContainer;
import name.shamansir.sametimed.client.proto.IWavesClientViewService;
import name.shamansir.sametimed.wave.WavesClient;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class WavesClientViewServiceImpl extends RemoteServiceServlet implements
IWavesClientViewService {
	
	private static final Logger LOG = Logger.getLogger(WavesClientViewServiceImpl.class.getName());
	
	private static final String CONNECTION_ERR_STR = "Connection to the Wave Server (as user %s) is failed";  

	@Override
	public WavesClientViewContainer getClientView(String user) throws IOException {
		WavesClient newClient = new WavesClient();
		
		try {
			newClient.connect(user);
		} catch (Exception e) {
			LOG.severe(String.format(CONNECTION_ERR_STR + "; Exception thrown: %s, caused by %s", user, e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "nothing"));
			return new WavesClientViewContainer(
					newClient.getViewId(),
					// FIXME: create ErrorModel class
					"{error: '" + String.format(CONNECTION_ERR_STR + "; Exception thrown: %s", user, e.getMessage()) + "'}"
				);
		}
		
		if (!newClient.isConnected()) {
			LOG.severe(String.format(CONNECTION_ERR_STR, user));
			throw new IOException(String.format(CONNECTION_ERR_STR, user));
		}
		
		return new WavesClientViewContainer(
				newClient.getViewId(),
				newClient.getWaveModel().asJSON()
			);
	}

}
