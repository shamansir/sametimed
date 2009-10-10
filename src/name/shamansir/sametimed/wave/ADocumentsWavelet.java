package name.shamansir.sametimed.wave;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils; // FIXME: get rid of it or move all usage here 
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.operation.wave.AddParticipant;
import org.waveprotocol.wave.model.operation.wave.RemoveParticipant;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.data.WaveletData;

import name.shamansir.sametimed.wave.chat.WaveletWithChat;
import name.shamansir.sametimed.wave.messaging.ModelUpdateMessage;
import name.shamansir.sametimed.wave.messaging.UpdateMessage;
import name.shamansir.sametimed.wave.model.AModel;
import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.model.WaveletModel;
import name.shamansir.sametimed.wave.render.NullRenderer;
import name.shamansir.sametimed.wave.render.WavesClientInfoProvider;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

/**
 * @see #addUpdatesListener(IUpdatesListener)
 * 
 * @see IWavesClientRenderer
 * @see IUpdatesListener
 * @see WavesClientInfoProvider
 * 
 * @see WaveletModel
 * @see ClientWaveView 
 * @see InboxWaves
 * @see Chat 
 * 
 */

public abstract class ADocumentsWavelet extends AUpdatingWavelet {
	
	private static final Logger LOG = Logger.getLogger(WaveletWithChat.class.getName());	
	
	private final int clientID;
	
	/* models */
	private InboxWaves inbox = null;
	private ClientWaveView openedWave = null;
	private List<String> errors = new ArrayList<String>();
	
	private WaveletModel waveletModel = null;	
	
	/* flags */
	private boolean renderUpdates = false;
	private boolean notifyUpdates = true;
	
	/* updates */
	private final IWavesClientRenderer renderer; 
	// private final WavesClientInfoProvider infoProvider = new WavesClientInfoProvider();
	
	private WaveletOperationsSender operationsSender = null;
	
	public ADocumentsWavelet(int clientID, IWavesClientRenderer renderer) {
		this.clientID = clientID;
		this.waveletModel = new WaveletModel(this.clientID, getDocumentsModelsTypes());
		if (renderer == null) {
			this.renderUpdates = false;
			this.renderer = getDefaultRenderer();
		} else {
			this.renderUpdates = true;
			this.renderer = renderer;
		}		
	}
	
	public ADocumentsWavelet(int clientID) {
		this(clientID, null);
	}
		
	/* ====== DOCUMENTS OPERATIONS ====== */
	
	protected abstract void prepareDocuments(ClientWaveView currentWave);
	
	protected abstract void updateDocumentsModels();	
	
	protected abstract void resetDocuments();
	
	protected abstract List<ModelID> getDocumentsModelsTypes();
	
	protected abstract void changeDocumentsOutputMode(RenderMode mode);
	
	protected abstract boolean documentPerformUndo(String userId);
	
	protected abstract void documentPerformAppend(String text, ParticipantId author);
	
	protected BufferedDocOp getDocument(String documentID) {
		return getOpenWavelet() == null ? null : getOpenWavelet()
				.getDocuments().get(documentID);
	}	
	
	/* ====== BASIC METHODS ====== */
	
	// FIXME: store errors texts as constants	
	@Override
	protected void registerError(String errorText) {
		LOG.warning("Client Error: " + errorText);
		errors.add(errorText);
		updateModel(ModelID.ERRORBOX_MODEL, errors);
	}	
	
	@Override
	protected void initWith(WavesProvider wavesProvider, WaveletOperationsSender opsSender, String userInfo, String hostInfo) {
		if (renderUpdates) renderer.initialize();
		
		updateModel(ModelID.INFOLINE_MODEL, 
				WavesClientInfoProvider.getInfoLineCaption(userInfo, hostInfo, getClientID()));
		
		inbox = new InboxWaves(wavesProvider);	
		operationsSender = opsSender;
	}
	
	@Override
	protected void clear() {
	    openedWave = null;
	    inbox = null;		
	}	
	
	/* ====== SIGNALS FROM CLIENT ====== */
	
	@Override
	protected boolean onParticipantsUpdated() {
		if (isWaveOpen()) {
			// FIXME: add participant to list, not recreate model 
			List<ParticipantId> participants = getOpenWavelet().getParticipants();
			if (participants != null) {
				// TODO: pass adding user message
				// FIXME: may be rendered twice if user adding himself
				updateModel(ModelID.USERSLIST_MODEL, participants);
			}
		} /* else {
			registerError("No waves opened now");
		} */
		return true;
	}

	@Override	
	protected boolean onParticipantRemoved(WaveId waveId, ParticipantId removedUser, boolean isCurrentUser) {
	    if (isWaveOpen() && isCurrentUser) {
	        // We might have been removed from our open wave (an impressively verbose check...)
	        if (waveId.equals(openedWave.getWaveId())) {
	          openedWave = null;
	          resetDocuments();
	        }
	    }	
	    return true;
	}
	
	@Override	
	protected boolean onWavesRead() {
		inbox.updateHashedVersions();
		updateAll(isWaveOpen());
		return true;
	}
	
	@Override
	protected boolean onOpenWave(ClientWaveView openedWave) {
		if (ClientUtils.getConversationRoot(openedWave) == null) {
			openedWave.createWavelet(ClientUtils.getConversationRootId(openedWave));
		}

		this.openedWave = openedWave;
		prepareDocuments(this.openedWave);
		updateAll(true);
		return true;
	}
	
	@Override
	protected boolean onDeltaSequenceEnd(WaveletData wavelet, boolean waveOpened) {
		updateAll(waveOpened);
		return true;
	}
	
	@Override
	protected boolean onUndoCall(ParticipantId userId) {
		if (isWaveOpen()) {
	    	if (getOpenWavelet().getParticipants().contains(userId)) {
	    		return documentPerformUndo(userId.getAddress());
	    	} else {
	    		registerError("Error: " + userId + " is not a participant of this wave");
	    	}
	    } else {
	        registerError(AUpdatingWavelet.NOT_OPENED_WAVE_ERR);
	    }
		return false;
	}
		
	@Override
	protected boolean addParticipant(ParticipantId userId) {
		if (isWaveOpen()) {

			// Don't send an invalid op, although the server should be robust
			// enough to deal with it
			if (!getOpenWavelet().getParticipants().contains(userId)) {
				operationsSender.sendWaveletOperation(getOpenWavelet().getWaveletName(),
						new AddParticipant(userId));
				return true;
			} else {
				registerError("Error: " + userId.getAddress()
						+ " is already a participant on this wave");
				return false;
			}
		} else {
			registerError(AUpdatingWavelet.NOT_OPENED_WAVE_ERR);
			return false;
		}
	}	
	
	@Override
	protected boolean removeParticipant(ParticipantId userId) {
		if (isWaveOpen()) {
			if (getOpenWavelet().getParticipants().contains(userId)) {
				operationsSender.sendWaveletOperation(getOpenWavelet().getWaveletName(),
						new RemoveParticipant(userId));
				return true;
			} else {
				registerError("Error: " + userId.getAddress()
						+ " is not a participant on this wave");
				return false;
			}
		} else {
			registerError(AUpdatingWavelet.NOT_OPENED_WAVE_ERR);
			return false;
		}
	}	
	
	@Override
	protected boolean setViewMode(RenderMode mode) {
		if (isWaveOpen()) {
			changeDocumentsOutputMode(mode);						
			renderer.setRenderingMode(mode);
			updateWavePart();
			return true;
		} else {
			registerError(AUpdatingWavelet.NOT_OPENED_WAVE_ERR);
			return false;
		}
	}
	
	@Override
	protected boolean onDocumentAppendMutation(String text, ParticipantId author) {
		if (text.length() == 0) {
			throw new IllegalArgumentException("Cannot append a empty String");
		} else if (isWaveOpen()) {
			documentPerformAppend(text, author);
			return true;
		} else {
			registerError("Error: no open wave, run \"/open\"");
			return false;
		}
	}
	
	
	/* === UPDATING MODELS ===*/

	protected <SourceType> void updateModel(ModelID modelType, SourceType model) {		
		updateModel(modelType, model, null);
	}
	
	protected <SourceType> void updateModel(ModelID modelType, SourceType model, UpdateMessage message) {
		waveletModel.useModel(modelType, model);
		AModel<?, ?> newModel = waveletModel.getModel(modelType);
		if (renderUpdates) renderer.renderByModel(newModel);		
		if (notifyUpdates) {
			UpdateMessage updateMessage = (message != null) ? message : new ModelUpdateMessage(clientID, modelType, newModel);   
			dispatchUpdate(updateMessage);
		}
	}
	
	/* ====== UPDATES LISTENERS OPERATIONS ====== */
	
	private void updateAll(boolean waveOpened) {
		if (waveOpened) {
			inbox.setOpenWave(openedWave);			
		}
		
		updateModel(ModelID.INBOX_MODEL, inbox.getOpenedWaves());
		// updateModel(ModelID.INFOLINE_MODEL, null);
		updateModel(ModelID.ERRORBOX_MODEL, errors);
		
		updateWavePart();
	}
	
	private void updateWavePart() {
		if (getOpenWavelet() != null) {
			updateModel(ModelID.USERSLIST_MODEL, getOpenWavelet().getParticipants());
			updateDocumentsModels();
		}
	}
	
	/* ====== GETTERS AND CHECKS ====== */
		
	public IWavesClientRenderer getRenderer() {
		return this.renderer;
	}
	
	public IWavesClientRenderer getDefaultRenderer() {
		return new NullRenderer(getClientID());
	}	
		
	public int getClientID() {
		return this.clientID;
	}
	
	public WaveletModel getModel() {
		return this.waveletModel;	
	}
	
	public void setRenderUpdates(boolean doRenderUpdates) {
		this.renderUpdates = doRenderUpdates;
	}
	
	public void setNotifyUpdates(boolean doNotifyUpdates) {
		this.notifyUpdates = doNotifyUpdates;
	}
		
	/* ====== INNER GETTERS ====== */
	
	protected WaveletData getOpenWavelet() {
	    return (openedWave == null) ? null : ClientUtils.getConversationRoot(openedWave);
	}	
	
	protected ClientWaveView getOpenedWave() {
		return openedWave;
	}
	
	protected WaveletOperationsSender getOperationsSender() {
		return operationsSender;
	}
	
	private boolean isWaveOpen() {
		return /*isConnected() &&*/ openedWave != null;
	}
		
}

