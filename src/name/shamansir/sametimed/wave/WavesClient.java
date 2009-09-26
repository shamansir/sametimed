package name.shamansir.sametimed.wave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import name.shamansir.sametimed.client.proto.AWavesClientRedrawEventsHandler;
import name.shamansir.sametimed.wave.render.IWavesClientRenderer;
import name.shamansir.sametimed.wave.render.PanelModel;
import name.shamansir.sametimed.wave.render.PanelID;
import name.shamansir.sametimed.wave.render.html.WavesClientHTMLRenderer;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.common.WaveletOperationListener;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.data.WaveletData;

public class WavesClient implements WaveletOperationListener {
	
	private static final Logger LOG = Logger.getLogger(WavesClient.class.getName());
	
	private static int LAST_VIEW_ID = -1;
	private final int VIEW_ID;
	
	private WavesClientBackend backend = null;
	private IWavesClientRenderer renderer = null;
	
	private InboxWaveView inbox = null;
	private ClientWaveView openedWave = null;	
	
	List<String> errors = new ArrayList<String>();	

	private static final String AJAX_REQUEST_JS_FUNC = "makeRequest";
	private static final String CMD_EXECUTOR_URL = "/same_timed/cmd_exec";
	
	private final String redrawJSFuncName;
	
	// FIXME: is there a way to move SametimedClientEventsHandler interface into this package?
	public WavesClient(AWavesClientRedrawEventsHandler redrawEventsHandler, String redrawJSFuncName) {
		this.VIEW_ID = generateViewId();
		this.renderer = new WavesClientHTMLRenderer(this.VIEW_ID, redrawEventsHandler);
		this.redrawJSFuncName = redrawJSFuncName;
	}	
	
	protected int generateViewId() {
		int newId = LAST_VIEW_ID + 1;
		LAST_VIEW_ID = newId;
		return newId;
	}
	
	public boolean isConnected() {
		return backend != null;
	}
	
	public boolean connect(String asUser) {
		if (isConnected()) {
			LOG.warning("Already connected to Wave Server");
			backend.shutdown();
		    backend = null;
		    openedWave = null;
		    inbox = null;			
		}		
		
		
	    try {
	        backend = new WavesClientBackend(asUser);
	    } catch (IOException e) {
	    	LOG.severe("Error: failed to connect to Wave Server, " + e.getMessage());
	        return false;
	    }		
		
		backend.addWaveletOperationListener(this);
		
		LOG.info("Connected ok");
				
		renderer.initialize(backend.getUserAtDomain(), backend.getWaveServerHostData());
		
		return true;
	}
	
	public boolean shutdown() {
		if (isConnected()) {
			backend.shutdown();
			backend = null;
		}
		return isConnected();
	}
		
	/* private void connectToWaveServer(String waveServer) {
		this.connectToWaveServer(waveServer, 9876);
	} */

	@Override
	public void noOp(String arg0, WaveletData arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeltaSequenceEnd(WaveletData arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeltaSequenceStart(WaveletData arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void participantAdded(String name, WaveletData arg1,
			ParticipantId arg2) {
		if (isWaveOpen()) {
			PanelModel participants = new PanelModel();
			participants.put("participants", extractParticipantsData(getOpenWavelet().getParticipants()));
			renderer.updatePanel(PanelID.USERS_LIST_PANEL, participants);
		} else {
			errors.add("Participant is already in the list");
			PanelModel errorsModel = new PanelModel();
			errorsModel.put("errors", errors);
			renderer.updatePanel(PanelID.ERROR_BOX_PANEL, errorsModel);
		}
	}

	private WaveletData getOpenWavelet() {
	    return (openedWave == null) ? null : ClientUtils.getConversationRoot(openedWave);
	}

	@Override
	public void participantRemoved(String arg0, WaveletData arg1,
			ParticipantId arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void waveletDocumentUpdated(String arg0, WaveletData arg1,
			WaveletDocumentOperation arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public IWavesClientRenderer getRenderer() {
		return renderer;
	}

	public int getViewId() {
		return VIEW_ID;
	}
	
	// TODO: list must to be immutable
	protected List<String> extractParticipantsData(List<ParticipantId> participants) {
		List<String> participantsStrings = new ArrayList<String>();
		for (ParticipantId participantId: participants) {
			participantsStrings.add(participantId.toString());
		}
		return participantsStrings;
	}
	
	private boolean isWaveOpen() {
		return isConnected() && openedWave != null;
	}	
	
	public static final String generateCmdExecutionJavascript(int clientId, WaveletOperation command) {
		return AJAX_REQUEST_JS_FUNC + "('" +
			CMD_EXECUTOR_URL + "','" + 
				"clientId=" + String.valueOf(clientId) + "&" +
				"cmd=test&" + 
				"args={}" +
			"',null,true);";
			// FIXME: refresh client function must to be called from here
	}

}
