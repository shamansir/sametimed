package name.shamansir.sametimed.server;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import name.shamansir.sametimed.client.WavesClientViewContainer;
import name.shamansir.sametimed.client.proto.IWavesClientViewService;
import name.shamansir.sametimed.wave.ADocumentsWavelet;
import name.shamansir.sametimed.wave.WavesClient;
import name.shamansir.sametimed.wave.editor.WaveletWithEditor;
import name.shamansir.sametimed.wave.render.JSUpdatesListener;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * The server side implementation of the RPC service (for GWT version).
 * 
 */

@SuppressWarnings("serial")
public class WavesClientViewServiceImpl extends RemoteServiceServlet implements
IWavesClientViewService {
	
	private static final Logger LOG = Logger.getLogger(WavesClientViewServiceImpl.class.getName());
	
	private static final String CONNECTION_ERR_STR = "Connection to the Wave Server (as user %s) is failed";  

	@Override
	public WavesClientViewContainer getClientView(String user, boolean useEscapedQuotes) throws IOException {
		WavesClient newClient = new WavesClient() {

			@Override
			protected ADocumentsWavelet createWavelet(IWavesClientRenderer renderer) {
				return new WaveletWithEditor(getViewId(), renderer);
			}			
			
		};
		newClient.getWavelet().addUpdatesListener(new JSUpdatesListener());
		
		String quot = useEscapedQuotes ? "\\\"" : "\"";		
		
		try {
			newClient.connect(user);
		} catch (Exception e) {
			LOG.severe(String.format(CONNECTION_ERR_STR + "; Exception thrown: %s, caused by %s", user, e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "nothing"));
			return new WavesClientViewContainer(
					newClient.getViewId(),
					// FIXME: create ErrorModel class
					"{\"error\": " + quot + String.format(CONNECTION_ERR_STR + "; Exception thrown: %s", user, e.getMessage()) + quot + "}"
				);
		}
		
		if (!newClient.isConnected()) {
			LOG.severe(String.format(CONNECTION_ERR_STR, user));
			throw new IOException(String.format(CONNECTION_ERR_STR, user));
		}
		
		return new WavesClientViewContainer(
				newClient.getViewId(),
				newClient.getWavelet().getModel().asJSON(useEscapedQuotes)
			);
	}

}
