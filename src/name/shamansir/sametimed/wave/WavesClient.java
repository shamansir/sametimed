package name.shamansir.sametimed.wave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

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

import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.model.WaveModel;
import name.shamansir.sametimed.wave.model.base.InboxWaveView;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;
import name.shamansir.sametimed.wave.render.proto.NullRenderer;

// FIXME: Javadoc

public class WavesClient implements WaveletOperationListener {
	
	private static final Logger LOG = Logger.getLogger(WavesClient.class.getName());
	
	private static int LAST_VIEW_ID = -1;
	private final int VIEW_ID;
	
	private static Map<Integer, WavesClient> registeredClients = new HashMap<Integer, WavesClient>();
	
	private static final String MAIN_DOCUMENT_ID = "main"; // FIXME: means all command done for the single document	
	
	private WavesClientBackend backend = null;
	private WaveModel waveModel = null;
	
	private InboxWaveView inbox = null;
	private ClientWaveView openedWave = null;	
	private List<String> participants = null;	 
	
	private List<String> errors = new ArrayList<String>();
	
	private final IWavesClientRenderer renderer; 
	
	public WavesClient() {
		this(null);
	}
	
	public WavesClient(IWavesClientRenderer renderer) {
		this.VIEW_ID = generateViewId();
		this.waveModel = new WaveModel(this.VIEW_ID);
		this.renderer = (renderer != null) ? renderer : getDefaultRenderer(this.VIEW_ID);
		
		registerClient(this.VIEW_ID, this);
	}	
	
	protected int generateViewId() {
		int newId = LAST_VIEW_ID + 1;
		LAST_VIEW_ID = newId;
		return newId;
	}
	
	public WaveModel getWaveModel() {
		return this.waveModel;
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
			// FIXME: add participant to list, not recreate model 
			participants = extractParticipantsData(getOpenWavelet().getParticipants());
			if (participants != null) { 
				waveModel.setModel(ModelID.USERSLIST_MODEL, participants);
				renderer.renderByModel(waveModel.getModel(ModelID.USERSLIST_MODEL));
			}
		} else {
			sendErrorToUser("No waves opened now");
		}
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
					return sendAppendMutation(command.getArgument("text"));
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
					return setView(command.getArgument("mode"));
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
		// FIXME: implement
		errors.add(errorText);
		waveModel.setModel(ModelID.ERRORBOX_MODEL, errors);
		renderer.renderByModel(waveModel.getModel(ModelID.ERRORBOX_MODEL));
		// renderer.updatePanel(PanelID.ERROR_BOX_PANEL);
	}
	
	protected void updateRendererFromOpenedWave() {
		waveModel.setModel(ModelID.ERRORBOX_MODEL, errors);
		renderer.renderByModel(waveModel.getModel(ModelID.ERRORBOX_MODEL));
		// FIXME: implement				
		
		/*
		renderer.updatePanel(PanelID.INBOX_PANEL);
		renderer.updatePanel(PanelID.CHAT_PANEL);
		renderer.updatePanel(PanelID.EDITOR_PANEL);
		renderer.updatePanel(PanelID.INFOLINE_PANEL);
		renderer.updatePanel(PanelID.USERS_LIST_PANEL);
		*/		
	}
	
	protected void updateRendererFromUpdatedInbox() {
		waveModel.setModel(ModelID.ERRORBOX_MODEL, errors);
		renderer.renderByModel(waveModel.getModel(ModelID.ERRORBOX_MODEL));
		// FIXME: implement			
		
		/*
		renderer.updatePanel(PanelID.INBOX_PANEL);
		renderer.updatePanel(PanelID.CHAT_PANEL);
		renderer.updatePanel(PanelID.EDITOR_PANEL); */		
	}
	
	public IWavesClientRenderer getDefaultRenderer(int clientID) {
		return new NullRenderer(clientID);
	}
	
	/* =========================================================================================== */
	/* the code below is the copy of ConsoleClient functionality with changes related to rendering */
	
	protected void cleanConnection() {
		backend.shutdown();
	    backend = null;
	    openedWave = null;
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
		
		backend.addWaveletOperationListener(this);
		
		LOG.info("Connected ok");
				
		renderer.initialize();
		
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
		
		backend.addWaveletOperationListener(this);
		
		LOG.info("Connected ok");
				
		renderer.initialize();
		
		return true;		
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
		updateRendererFromOpenedWave();
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
			updateRendererFromUpdatedInbox();
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

	private boolean setView(String mode) {
		if (isWaveOpen()) {
			if (mode.equals("normal")) {
				renderer.setRenderingMode(RenderMode.NORMAL);
				updateRendererFromOpenedWave();
				return true;
			} else if (mode.equals("xml")) {
				renderer.setRenderingMode(RenderMode.NORMAL);
				updateRendererFromOpenedWave();
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
			docOp.elementEnd();
			docOp.characters(text);

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
