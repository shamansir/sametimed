package name.shamansir.sametimed.wave;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.IndexEntry;
import org.waveprotocol.wave.examples.fedone.waveclient.common.WaveletOperationListener;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode; // FIXME: get rid
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.data.WaveletData;

import name.shamansir.sametimed.wave.messaging.Command;
import name.shamansir.sametimed.wave.messaging.ICommandsPerformer;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * The main class that controls/encapsulates all operations between wave server 
 * and waves client. Represents the single Waves Client (one connection as some username) 
 * and holds the full model of it. Can use Renderer (IWavesClientRenderer) to make signals about
 * requirement to update model of some panel (inbox, users list, chat & s.o.) and 
 * also sends messages to the client using Listeners (IUpdatesListener)
 * 
 * FIXME: split in view and commands performer
 * 
 * @see WaveletOperationListener
 * @see #connect(String)
 * @see #shutdown()
 * @see #doCommand(Command)
 * @see #getViewId()
 * 
 * @see WavesClientBackend
 * 
 */

public abstract class WavesClient implements WaveletOperationListener, ICommandsPerformer {	
	
	private static final Logger LOG = Logger.getLogger(WavesClient.class.getName());
	
	private final boolean LOG_OPS = true;
	
	private static int LAST_VIEW_ID = -1;
	private final int VIEW_ID;
	
	private static Map<Integer, WavesClient> registeredClients = new HashMap<Integer, WavesClient>();
	
	private WavesClientBackend backend = null;
	private AUpdatingWavelet curWavelet = null;
		
	public WavesClient() {
		this(null);
	}
	
	public WavesClient(IWavesClientRenderer renderer) {
		this.VIEW_ID = generateViewId();
		this.curWavelet = createWavelet(renderer);
		
		registerClient(this.VIEW_ID, this);
	}
	
	protected abstract AUpdatingWavelet createWavelet(IWavesClientRenderer renderer);
	
	/* GETTERS */
	
	protected int generateViewId() {
		int newId = LAST_VIEW_ID + 1;
		LAST_VIEW_ID = newId;
		return newId;
	}
	
	public AUpdatingWavelet getWavelet() {
		return this.curWavelet;
	}
	
	/* GETTERS */
			
	@Override
	public void noOp(String author, WaveletData wavelet) {
		// TODO Auto-generated method stub
		if (LOG_OPS) LOG.info("NoOp fired");
		
	}

	@Override
	public void onDeltaSequenceEnd(WaveletData wavelet) {
		if (LOG_OPS) LOG.info("Delta Sequence End fired");
		// FIXME: must to make decision what to update		
		curWavelet.onDeltaSequenceEnd(wavelet, isConnected() && backend.getIndexWave() != null);
	}

	@Override
	public void onDeltaSequenceStart(WaveletData wavelet) {
		// TODO Auto-generated method stub
		if (LOG_OPS) LOG.info("Delta Sequence Start fired");
		
	}

	@Override
	public void participantAdded(String name, WaveletData wavelet,
			ParticipantId participant) {
		if (LOG_OPS) LOG.info("Participant added fired " + participant.getAddress());
		if (isConnected()) {
			curWavelet.onParticipantsUpdated();
		}
	}

	@Override
	public void participantRemoved(String author, WaveletData wavelet,
			ParticipantId participant) {
		if (LOG_OPS) LOG.info("Participant added fired " + participant.getAddress());
		if (isConnected()) {
			curWavelet.onParticipantRemoved(wavelet.getWaveletName().waveId, participant, participant.equals(backend.getUserId()));
		}
	}

	@Override
	public void waveletDocumentUpdated(String author, WaveletData wavelet,
			WaveletDocumentOperation performed) {
		if (LOG_OPS) LOG.info("Document updated fired");		
	}
	
	public int getViewId() {
		return VIEW_ID;
	}
	
	private static void registerClient(int clientId, WavesClient client) {
		registeredClients.put(Integer.valueOf(clientId), client);
	}
	
	public static WavesClient get(int clientId) {
		return registeredClients.get(Integer.valueOf(clientId));
	}

	public boolean doCommand(Command command) {
		switch (command.getType()) {
		
			case CMD_CONNECT: {
					int port = -1;
					try {
						port = Integer.valueOf(command.getArgument("port"));
					} catch (NumberFormatException e) {
						curWavelet.registerError("Port number " + command.getArgument("port") + "for command 'connect' can not be parsed, not performing. " + e.getMessage());
						return false;
					}
					return connect(command.getArgument("user"), 
								   command.getArgument("server"),
							       port);
				}
			
			case CMD_OPEN_WAVE: {
			        try {
			            return doOpenWave(Integer.parseInt(command.getArgument("entry")));
			        } catch (NumberFormatException e) {
			        	curWavelet.registerError("Error: " + command.getArgument("entry") + " is not a number");
			        	return false;
			        }			
				}

			case CMD_NEW_WAVE: {
					if (isConnected()) {
						backend.createConversationWave();
						return true;
					} else {
						curWavelet.registerError(AUpdatingWavelet.NOT_CONNECTED_ERR);
						return false;
					}
				}

			case CMD_ADD_USER: {
					return curWavelet.addParticipant(
							new ParticipantId(command.getArgument("user")));
				}
			
			case CMD_REMOVE_USER: {
					return curWavelet.removeParticipant(
						new ParticipantId(command.getArgument("user")));
				}
			
			// FIXME: possibly, must be described in WaveletWithChat
			case CMD_SAY: {
					return curWavelet.onDocumentAppendMutation(
							StringEscapeUtils.unescapeXml(command.getArgument("text")),
							backend.getUserId());
				}
			
			case CMD_UNDO_OP: {
					if (command.getArgument("user") != null) {
						return curWavelet.onUndoCall(
								new ParticipantId(command.getArgument("user")));
					} else if (backend != null) {
						return curWavelet.onUndoCall(
								new ParticipantId(backend.getUserId().getAddress()));
					} else {
						curWavelet.registerError(AUpdatingWavelet.NOT_CONNECTED_ERR);
						return false;
					}
				}
			case CMD_MARK_READ: {
					if (isConnected()) {
						return curWavelet.onWavesRead();
					} else {
						curWavelet.registerError(AUpdatingWavelet.NOT_CONNECTED_ERR);
						return false;
					}
				}
			case CMD_CHANGE_VIEW: {
					String modeStr = command.getArgument("mode");
					RenderMode mode = RenderMode.NORMAL; 
					if (modeStr.equals("normal")) {}
					else if (modeStr.equals("xml")) { mode = RenderMode.XML; }
					else {
						curWavelet.registerError("Error: unsupported rendering, run \"?\"");
					}
					return curWavelet.setViewMode(mode);
				}
			case CMD_QUIT: {
					// FIXME: implement clearing and closing the client
					curWavelet.registerError("quitting is not implemented still");
					return false;
				}
			default: return false;
		}
	}
		
	/* =========================================================================================== */
	/* the code below is the copy of ConsoleClient functionality with changes related to rendering */
	
	protected void cleanConnection() {
		backend.shutdown();
	    backend = null;
	    curWavelet.clear();
	}	
	
	public boolean connect(String asUser) {
		if (isConnected()) {
			curWavelet.registerError("Already connected to Wave Server");
			cleanConnection();
		}		
		
		
	    try {
	        backend = new WavesClientBackend(asUser);
	    } catch (IOException e) {
	    	curWavelet.registerError("Error: failed to connect to Wave Server, " + e.getMessage());
	        return false;
	    }		
		
	    afterSuccessfullConnection();
	    
	    return true;
	}
	
	// identical to connect(asUser) but passes server and port to backend
	protected boolean connect(String userAtDomain, String server, int port) {
		if (isConnected()) {
			curWavelet.registerError("Already connected to Wave Server");
			cleanConnection();
		}		
		
		
	    try {
	        backend = new WavesClientBackend(userAtDomain, server, port);
	    } catch (IOException e) {
	    	curWavelet.registerError("Error: failed to connect to Wave Server, " + e.getMessage());
	        return false;
	    }		
		
	    afterSuccessfullConnection();
		
		return true;		
	}
	
	private void afterSuccessfullConnection() {
		backend.addWaveletOperationListener(this);
		
		LOG.info("Connected ok");
		
		curWavelet.initWith(new WavesProvider(backend),
				new WaveletOperationsSender(backend),
				backend.getUserAtDomain(),
				backend.getWaveServerHostData());				
	}

	public boolean shutdown() {
		if (isConnected()) {
			backend.shutdown();
			backend = null;
		}
		return isConnected();
	}	
		
	public boolean isConnected() {
		return backend != null;
	}		
	
	// FIXME: none of these methods must exist here
	
	private boolean doOpenWave(int entry) {
		if (isConnected()) {
			List<IndexEntry> index = ClientUtils.getIndexEntries(backend.getIndexWave());

	    	if (entry >= index.size()) {
	    		curWavelet.registerError("Error: entry is out of range, ");
		        if (index.isEmpty()) {
		        	curWavelet.registerError("there are no available waves (try \"/new\")");
		        } else {
		        	curWavelet.registerError("expecting [0.." + (index.size() - 1) + "] (for example, \"/open 0\")");
		        }
	    	} else {
	    		return curWavelet.onOpenWave(backend.getWave(index.get(entry).getWaveId()));
	    	}
		} else {
			curWavelet.registerError(AUpdatingWavelet.NOT_CONNECTED_ERR);
	    }
		return false;
	}	
	
}
