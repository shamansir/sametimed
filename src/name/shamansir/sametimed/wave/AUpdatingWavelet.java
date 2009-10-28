package name.shamansir.sametimed.wave;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import name.shamansir.sametimed.wave.messaging.IUpdatesListener;
import name.shamansir.sametimed.wave.messaging.ModelUpdateMessage;
import name.shamansir.sametimed.wave.messaging.UpdateMessage;
import name.shamansir.sametimed.wave.model.AModel;
import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.model.WaveletModel;
import name.shamansir.sametimed.wave.render.NullRenderer;
import name.shamansir.sametimed.wave.render.WavesClientInfoProvider;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.common.IndexEntry;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.operation.wave.AddParticipant;
import org.waveprotocol.wave.model.operation.wave.RemoveParticipant;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.data.WaveletData;

// FIXME: move here all code, not related to documents
// FIXME: remove methods related to documents

public abstract class AUpdatingWavelet {
	
	// FIXME: extract in some errors class
	public static final String NOT_CONNECTED_ERR = "not connected, run \"/connect user@domain server port\"";
	public static final String NOT_OPENED_WAVE_ERR = "Error: no wave is open, run \"/open index\"";
	
	private static final Logger LOG = Logger.getLogger(AUpdatingWavelet.class.getName());	
	
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
	
	private Set<IUpdatesListener> updatesListeners = new HashSet<IUpdatesListener>();
	
	public AUpdatingWavelet(int clientID, IWavesClientRenderer renderer) {
		this.clientID = clientID;
		this.waveletModel = new WaveletModel(this.clientID, getAdditionalModels());
		if (renderer == null) {
			this.renderUpdates = false;
			this.renderer = getDefaultRenderer();
		} else {
			this.renderUpdates = true;
			this.renderer = renderer;
		}		
	}
	
	/* ====== BASIC METHODS ====== */
	
	// FIXME: store errors texts as constants	
	public void registerError(String errorText) {
		LOG.warning("Client Error: " + errorText);
		errors.add(errorText);
		updateModel(ModelID.ERRORBOX_MODEL, errors);
	}	
	
	protected void initWith(WavesProvider wavesProvider, WaveletOperationsSender opsSender, String userInfo, String hostInfo) {
		if (renderUpdates) renderer.initialize();
		
		updateModel(ModelID.INFOLINE_MODEL, 
				WavesClientInfoProvider.getInfoLineCaption(userInfo, hostInfo, getClientID()));
		
		inbox = new InboxWaves(wavesProvider);	
		operationsSender = opsSender;
	}
	
	protected void clear() {
	    openedWave = null;
	    inbox = null;		
	}
	
	protected void clearWavePart() {
	    openedWave = null;
	}	
	
	protected void prepareNewWave(ClientWaveView newWave) { }
	
	protected void onViewModeChanged(RenderMode newMode) { }
	
	protected void onWavePartUpdated() { }
	
	protected abstract List<ModelID> getAdditionalModels();
		
	/* ====== UPDATING MODELS ====== */

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
	
	/* ====== UPDATES & UPDATES LISTENERS OPERATIONS ====== */
	
	private void updateAll(boolean waveOpened) {
		if (waveOpened && (inbox != null)) { // FIXME: inbox must not be null if wave is opened
						// NPE fires (if not checking inbox) when application is just started and
						// some waves are already in the user's inbox at the server
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
			onWavePartUpdated();
		}
	}	
		
	public void addUpdatesListener(IUpdatesListener listener) {
		synchronized (updatesListeners) {
			updatesListeners.add(listener);
		}
	}
	
	protected void dispatchUpdate(UpdateMessage updateMessage) {
		synchronized (updatesListeners) {
			for(IUpdatesListener listener: updatesListeners) {
				listener.onUpdate(updateMessage);
			}
		}
		
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
	
	protected boolean isWaveOpen() {
		return /*isConnected() &&*/ openedWave != null;
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
	
	/* ====== SIGNALS FROM CLIENT ====== */
	
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
	
	protected boolean onParticipantRemoved(WaveId waveId, ParticipantId removedUser, boolean isCurrentUser) {
	    if (isWaveOpen() && isCurrentUser) {
	        // We might have been removed from our open wave (an impressively verbose check...)
	        if (waveId.equals(openedWave.getWaveId())) {
	          clearWavePart();
	        }
	    }	
	    return true;
	}
	
	protected boolean onWavesRead() {
		inbox.updateHashedVersions();
		updateAll(isWaveOpen());
		return true;
	}
	
	protected boolean onOpenWave(ClientWaveView openedWave) {
    	if (ClientUtils.getConversationRoot(openedWave) == null) {
    		openedWave.createWavelet(ClientUtils.getConversationRootId(openedWave));
    	}

    	this.openedWave = openedWave;
    	prepareNewWave(this.openedWave);
    	updateAll(true);
    	return true;
	}
	
	protected boolean onDeltaSequenceEnd(WaveletData wavelet, boolean waveOpened) {
		updateAll(waveOpened);
		return true;
	}
	
	protected WaveId getCorrectedEntry(int entryPos, ClientWaveView indexWave) {
		List<IndexEntry> existentWaves = ClientUtils.getIndexEntries(indexWave);
		if (entryPos >= existentWaves.size()) {
    		registerError("Error: entry is out of range, ");
	        if (existentWaves.isEmpty()) {
	        	registerError("there are no available waves (try \"/new\")");	        	
	        } else {
	        	registerError("expecting [0.." + (existentWaves.size() - 1) + "] (for example, \"/open 0\")");
	        }
	        return null;
    	} else return existentWaves.get(entryPos).getWaveId();
	}	
	
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
	
	protected boolean setViewMode(RenderMode mode) {
		if (isWaveOpen()) {
			onViewModeChanged(mode);						
			renderer.setRenderingMode(mode);
			updateWavePart();
			return true;
		} else {
			registerError(AUpdatingWavelet.NOT_OPENED_WAVE_ERR);
			return false;
		}
	}
	
	
}
