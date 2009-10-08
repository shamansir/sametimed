package name.shamansir.sametimed.wave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.common.IndexEntry;
import org.waveprotocol.wave.examples.fedone.waveclient.common.WaveletOperationListener;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ConsoleUtils; // FIXME: get rid
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode; // FIXME: get rid
import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.AddParticipant;
import org.waveprotocol.wave.model.operation.wave.RemoveParticipant;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.data.WaveletData;

import com.google.common.collect.ImmutableMap;

import name.shamansir.sametimed.wave.InboxWaveView;
import name.shamansir.sametimed.wave.messaging.Command;
import name.shamansir.sametimed.wave.messaging.IUpdatesListener;
import name.shamansir.sametimed.wave.messaging.ModelUpdateMessage;
import name.shamansir.sametimed.wave.messaging.UpdateMessage;
import name.shamansir.sametimed.wave.model.AModel;
import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.model.WavesClientModel;
import name.shamansir.sametimed.wave.render.JSUpdatesListener;
import name.shamansir.sametimed.wave.render.NullRenderer;
import name.shamansir.sametimed.wave.render.WavesClientInfoProvider;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * The main class that controls/encapsulates all operations between wave server 
 * and waves client. Represents the single Waves Client (one connection as some username) 
 * and holds the full model of it. Can use Renderer (IWavesClientRenderer) to make signals about
 * requirement to update model of some panel (inbox, users list, chat & s.o.) and 
 * also sends messages to the client using Listeners (IUpdatesListener) (now,
 * JSUpdatesListener is default, I plan to get rid of it and add it after construction)
 * 
 * @see WaveletOperationListener
 * @see IWavesClientRenderer
 * @see IUpdatesListener
 * 
 */

public class WavesClient implements WaveletOperationListener {	
	
	private static final Logger LOG = Logger.getLogger(WavesClient.class.getName());
	
	private final boolean LOG_OPS = true;
	
	private static int LAST_VIEW_ID = -1;
	private final int VIEW_ID;
	
	private static Map<Integer, WavesClient> registeredClients = new HashMap<Integer, WavesClient>();
	
	private static final String MAIN_DOCUMENT_ID = "main"; // FIXME: means all command done for the single document	
	
	private WavesClientBackend backend = null;
	private WavesClientModel clientModel = null;
	
	private InboxWaveView inbox = null;
	private ClientWaveView openedWave = null;
	private ChatView chatView = null;
	
	private List<String> errors = new ArrayList<String>();
	
	private final IWavesClientRenderer renderer; 
	private final WavesClientInfoProvider infoProvider = new WavesClientInfoProvider();
	
	private Set<IUpdatesListener> updatesListeners = new HashSet<IUpdatesListener>();	 
	
	public WavesClient() {
		this(null);
	}
	
	public WavesClient(IWavesClientRenderer renderer) {
		this.VIEW_ID = generateViewId();
		this.clientModel = new WavesClientModel(this.VIEW_ID);
		this.renderer = (renderer != null) ? renderer : getDefaultRenderer(this.VIEW_ID);
		
		this.updatesListeners.add(getDefaultUpdatesListener());
		
		registerClient(this.VIEW_ID, this);
	}	
	
	protected int generateViewId() {
		int newId = LAST_VIEW_ID + 1;
		LAST_VIEW_ID = newId;
		return newId;
	}
	
	public WavesClientModel getClientModel() {
		return this.clientModel;
	}
			
	/* private void connectToWaveServer(String waveServer) {
		this.connectToWaveServer(waveServer, 9876);
	} */

	@Override
	public void noOp(String author, WaveletData wavelet) {
		// TODO Auto-generated method stub
		if (LOG_OPS) LOG.info("NoOp fired");
		
	}

	@Override
	public void onDeltaSequenceEnd(WaveletData wavelet) {
		if (LOG_OPS) LOG.info("Delta Sequence End fired");
		// FIXME: must to make decision what to update		
		updateFullView();
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
		if (isWaveOpen()) {
			// FIXME: add participant to list, not recreate model 
			List<ParticipantId> participants = getOpenWavelet().getParticipants();
			if (participants != null) {
				// TODO: pass adding user message
				// FIXME: may be rendered twice if user adding himself
				updateView(ModelID.USERSLIST_MODEL, participants);
			}
		} /* else {
			sendErrorToUser("No waves opened now");
		} */
	}

	@Override
	public void participantRemoved(String author, WaveletData wavelet,
			ParticipantId participant) {
		if (LOG_OPS) LOG.info("Participant added fired " + participant.getAddress());
	    if (isWaveOpen() && participant.equals(backend.getUserId())) {
	        // We might have been removed from our open wave (an impressively verbose check...)
	        if (wavelet.getWaveletName().waveId.equals(openedWave.getWaveId())) {
	          openedWave = null;
	          chatView = null;
	        }
	    }		
	}

	@Override
	public void waveletDocumentUpdated(String author, WaveletData wavelet,
			WaveletDocumentOperation performed) {
		if (LOG_OPS) LOG.info("Document updated fired");		
	}
	
	public IWavesClientRenderer getRenderer() {
		return renderer;
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
						sendErrorToUser("Port number " + command.getArgument("port") + "for command 'connect' can not be parsed, not performing. " + e.getMessage());
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
			        	sendErrorToUser("Error: " + command.getArgument("entry") + " is not a number");
			        	return false;
			        }			
				}
			case CMD_NEW_WAVE: {
					return newWave();
				}
			case CMD_ADD_USER: {
					return addParticipant(command.getArgument("user"));
				}
			case CMD_REMOVE_USER: {
					return removeParticipant(command.getArgument("user"));
				}
			case CMD_SAY: {
					return sendAppendMutation(StringEscapeUtils.unescapeXml(command.getArgument("text")));
				}
			case CMD_UNDO_OP: {
					if (command.getArgument("user") != null) {
						return undo(command.getArgument("user"));
					} else if (backend != null) {
						return undo(backend.getUserId().getAddress());
					} else {
						errorNotConnected();
						return false;
					}
				}
			case CMD_MARK_READ: {
					return readAllWaves();
				}
			case CMD_CHANGE_VIEW: {
					return setViewMode(command.getArgument("mode"));
				}
			case CMD_QUIT: {
					// FIXME: implement clearing and closing the client
					sendErrorToUser("quitting is not implemented still");
					return false;
				}
			default: return false;
		}
	}
	
	private void sendErrorToUser(String errorText) {
		LOG.warning("Client Error: " + errorText);
		errors.add(errorText);
		updateView(ModelID.ERRORBOX_MODEL, errors);
	}
		
	protected IWavesClientRenderer getDefaultRenderer(int clientID) {
		return new NullRenderer(clientID);
	}
	
	protected IUpdatesListener getDefaultUpdatesListener() {
		return new JSUpdatesListener();
	}	
	
	public void addUpdatesListener(IUpdatesListener listener) {
	    synchronized(updatesListeners) {
	    	updatesListeners.add(listener);
	    }		
	}
	
	private void dispatchUpdate(UpdateMessage updateMessage) {
	    synchronized(updatesListeners) {
	        for (IUpdatesListener listener: updatesListeners) {
	          listener.onUpdate(updateMessage);
	        }
	    }		
	}
	
	public <SourceType> void updateView(ModelID modelType, SourceType model) {
		clientModel.setModel(modelType, model);
		AModel<?, ?> newModel = clientModel.getModel(modelType); 
		renderer.renderByModel(newModel);
		dispatchUpdate(new ModelUpdateMessage(VIEW_ID, modelType, newModel));
	}
	
	public <SourceType> void updateView(ModelID modelType, SourceType model, UpdateMessage message) {
		updateView(modelType, model);
		dispatchUpdate(message);
	}	
	
	protected void updateFullView() {
		if (isConnected() && backend.getIndexWave() != null) {
			inbox.setOpenWave(openedWave);			
		}
		
		updateView(ModelID.INBOX_MODEL, inbox.getOpenedWaves());
		// updateView(ModelID.INFOLINE_MODEL, null);
		updateView(ModelID.ERRORBOX_MODEL, errors);
		
		updateWavePart();
	}
	
	protected void updateWavePart() {
		if (getOpenWavelet() != null) {
			updateView(ModelID.USERSLIST_MODEL, getOpenWavelet().getParticipants());
			if (isChatReady()) {
				updateView(ModelID.CHAT_MODEL, chatView.getChatLines());
			}
		}
		updateView(ModelID.EDITOR_MODEL, null);
	}	
	
	/* =========================================================================================== */
	/* the code below is the copy of ConsoleClient functionality with changes related to rendering */
	
	protected void cleanConnection() {
		backend.shutdown();
	    backend = null;
	    openedWave = null;
	    chatView = null;
	    inbox = null;		
	}	
	
	public boolean connect(String asUser) {
		if (isConnected()) {
			sendErrorToUser("Already connected to Wave Server");
			cleanConnection();
		}		
		
		
	    try {
	        backend = new WavesClientBackend(asUser);
	    } catch (IOException e) {
	    	sendErrorToUser("Error: failed to connect to Wave Server, " + e.getMessage());
	        return false;
	    }		
		
	    afterSuccessfullConnection();
	    
	    return true;
	}
	
	// identical to connect(asUser) but passes server and port to backend
	protected boolean connect(String userAtDomain, String server, int port) {
		if (isConnected()) {
			sendErrorToUser("Already connected to Wave Server");
			cleanConnection();
		}		
		
		
	    try {
	        backend = new WavesClientBackend(userAtDomain, server, port);
	    } catch (IOException e) {
	    	sendErrorToUser("Error: failed to connect to Wave Server, " + e.getMessage());
	        return false;
	    }		
		
	    afterSuccessfullConnection();
		
		return true;		
	}
	
	private void afterSuccessfullConnection() {
		backend.addWaveletOperationListener(this);
		
		LOG.info("Connected ok");
				
		renderer.initialize();
		
		clientModel.setModel(ModelID.INFOLINE_MODEL, 
				infoProvider.getInfoLineCaption(backend.getUserAtDomain(),
												backend.getWaveServerHostData(),
												getViewId()));
		renderer.renderByModel(clientModel.getModel(ModelID.INFOLINE_MODEL));
		inbox = new InboxWaveView(backend, backend.getIndexWave());	
	}

	public boolean shutdown() {
		if (isConnected()) {
			backend.shutdown();
			backend = null;
		}
		return isConnected();
	}	
	
	private boolean isWaveOpen() {
		return isConnected() && openedWave != null;
	}	
	
	private boolean isChatReady() {
		return chatView != null;
	}
	
	private WaveletData getOpenWavelet() {
	    return (openedWave == null) ? null : ClientUtils.getConversationRoot(openedWave);
	}
	
	private BufferedDocOp getOpenDocument() {
		return getOpenWavelet() == null ? null : getOpenWavelet()
				.getDocuments().get(MAIN_DOCUMENT_ID);
	}	
	
	public boolean isConnected() {
		return backend != null;
	}	
	
	private void errorNotConnected() {
		sendErrorToUser("not connected, run \"/connect user@domain server port\"");
	}
	
	private void errorNoWaveOpen() {
		sendErrorToUser("Error: no wave is open, run \"/open index\"");
	}	
	
	private boolean doOpenWave(int entry) {
		if (isConnected()) {
			List<IndexEntry> index = ClientUtils.getIndexEntries(backend.getIndexWave());

	    	if (entry >= index.size()) {
	    		sendErrorToUser("Error: entry is out of range, ");
		        if (index.isEmpty()) {
		        	sendErrorToUser("there are no available waves (try \"/new\")");
		        } else {
		        	sendErrorToUser("expecting [0.." + (index.size() - 1) + "] (for example, \"/open 0\")");
		        }
	    	} else {
	    		setOpenWave(backend.getWave(index.get(entry).getWaveId()));
	    		return true;
	    	}
		} else {
			errorNotConnected();
	    }
		return false;
	}	
	
	private void setOpenWave(ClientWaveView wave) {
		if (ClientUtils.getConversationRoot(wave) == null) {
		    wave.createWavelet(ClientUtils.getConversationRootId(wave));
		}

		openedWave = wave;
		chatView = new ChatView(openedWave);
		updateFullView();
	}
	
	private boolean undo(String userId) {
		if (isWaveOpen()) {
	    	if (getOpenWavelet().getParticipants().contains(new ParticipantId(userId))) {
	    		return undoLastLineBy(userId);
	    	} else {
	    		sendErrorToUser("Error: " + userId + " is not a participant of this wave");
	    	}
	    } else {
	      errorNoWaveOpen();
	    }
		return false;
	}
	
	private boolean undoLastLineBy(final String userId) {
		if (getOpenDocument() == null) {
			sendErrorToUser("Error: document is empty");
			return false;
		}

		// Find the last line written by the participant given by userId (by
		// counting the number of
		// <line></line> elements, and comparing to their authors).
		final AtomicInteger totalLines = new AtomicInteger(0);
		final AtomicInteger lastLine = new AtomicInteger(-1);

		// FIXME: it works for console, implement it for HTMLView (using models)
		getOpenDocument().apply(
				new InitializationCursorAdapter(new DocInitializationCursor() {
					@Override
					public void elementStart(String type, Attributes attrs) {
						if (type.equals(ConsoleUtils.LINE)) {
							totalLines.incrementAndGet();

							if (userId.equals(attrs
									.get(ConsoleUtils.LINE_AUTHOR))) {
								lastLine.set(totalLines.get() - 1);
							}
						}
					}

					@Override
					public void characters(String s) {
					}

					@Override
					public void annotationBoundary(AnnotationBoundaryMap map) {
					}

					@Override
					public void elementEnd() {
					}
				}));

		// Delete the line
		if (lastLine.get() >= 0) {
			WaveletDocumentOperation undoOp = new WaveletDocumentOperation(
					MAIN_DOCUMENT_ID, ConsoleUtils.createLineDeletion(
							getOpenDocument(), lastLine.get()));
			backend.sendWaveletOperation(getOpenWavelet().getWaveletName(),
					undoOp);
			return true;
		} else {
			sendErrorToUser("Error: " + userId + " hasn't written anything yet");
			return false;
		}
	}	
	
	private boolean readAllWaves() {
		if (isConnected()) {
			inbox.updateHashedVersions();
			updateFullView();
			return true;
		} else {
			errorNotConnected();
			return false;
		}
	}	
	
	private boolean newWave() {
		if (isConnected()) {
			backend.createConversationWave();
			return true;
		} else {
			errorNotConnected();
			return false;
		}
	}	
	
	private boolean addParticipant(String name) {
		if (isWaveOpen()) {
			ParticipantId addId = new ParticipantId(name);

			// Don't send an invalid op, although the server should be robust
			// enough to deal with it
			if (!getOpenWavelet().getParticipants().contains(addId)) {
				backend.sendWaveletOperation(getOpenWavelet().getWaveletName(),
						new AddParticipant(addId));
				return true;
			} else {
				sendErrorToUser("Error: " + name
						+ " is already a participant on this wave");
				return false;
			}
		} else {
			errorNoWaveOpen();
			return false;
		}
	}

	private boolean removeParticipant(String name) {
		if (isWaveOpen()) {
			ParticipantId removeId = new ParticipantId(name);

			if (getOpenWavelet().getParticipants().contains(removeId)) {
				backend.sendWaveletOperation(getOpenWavelet().getWaveletName(),
						new RemoveParticipant(removeId));
				return true;
			} else {
				sendErrorToUser("Error: " + name
						+ " is not a participant on this wave");
				return false;
			}
		} else {
			errorNoWaveOpen();
			return false;
		}
	}

	private boolean setViewMode(String mode) {
		if (isWaveOpen()) {
			if (mode.equals("normal")) {
				chatView.setOutputMode(RenderMode.NORMAL);				
				renderer.setRenderingMode(RenderMode.NORMAL);
				updateWavePart();
				return true;
			} else if (mode.equals("xml")) {
				chatView.setOutputMode(RenderMode.XML);
				renderer.setRenderingMode(RenderMode.XML);
				updateWavePart();
				return true;
			} else {
				sendErrorToUser("Error: unsupported rendering, run \"?\"");
				return false;
			}
		} else {
			errorNoWaveOpen();
			return false;
		}
	}
	
	private boolean sendAppendMutation(String text) {
		if (text.length() == 0) {
			throw new IllegalArgumentException("Cannot append a empty String");
		} else if (isWaveOpen()) {
			// FIXME: this code is for console, make it work on HTML View
			BufferedDocOp openDoc = getOpenDocument();
			int docSize = (openDoc == null) ? 0 : ClientUtils
					.findDocumentSize(openDoc);
			DocOpBuilder docOp = new DocOpBuilder();

			if (docSize > 0) {
				docOp.retain(docSize);
			}

			docOp.elementStart(ConsoleUtils.LINE, new AttributesImpl(
					ImmutableMap.of(ConsoleUtils.LINE_AUTHOR, backend
							.getUserId().getAddress())));
			docOp.characters(text);
			docOp.elementEnd();
			
			// ATTENTION: this differs from console, first characters, then - elementEnd
			//            console does end before characters 

			backend.sendWaveletOperation(getOpenWavelet().getWaveletName(),
					new WaveletDocumentOperation(MAIN_DOCUMENT_ID, docOp
							.finish()));
			return true;
		} else {
			sendErrorToUser("Error: no open wave, run \"/open\"");
			return false;
		}
	}	

}
