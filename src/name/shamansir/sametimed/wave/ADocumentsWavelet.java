package name.shamansir.sametimed.wave;

import java.util.List;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.model.ModelID;
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
	
	
	public ADocumentsWavelet(int clientID, IWavesClientRenderer renderer) {
		super(clientID, renderer);
	}
	
	public ADocumentsWavelet(int clientID) {
		this(clientID, null);
	}
	
	/* ====== OVERRIDES ====== */
	
	@Override
	protected final List<ModelID> getAdditionalModels() {
		return getDocumentsModelsTypes();
	}
	
	@Override
	protected void clearWavePart() {
		super.clearWavePart();
        resetDocuments();		
	}
	
	@Override 
	protected void prepareNewWave(ClientWaveView newWave) {
		super.prepareNewWave(newWave);
		prepareDocuments(newWave);		
	}
	
	@Override
	protected void onViewModeChanged(RenderMode newMode) {
		super.onViewModeChanged(newMode);
		changeDocumentsOutputMode(newMode);		
	}
	
	@Override
	protected void onWavePartUpdated() {
		super.onWavePartUpdated();
		updateDocumentsModels();
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
	
	/* ====== DOCUMENTS-RELATED OPERATIONS ====== */
		
	public boolean onUndoCall(ParticipantId userId) {
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
			
	public boolean onDocumentAppendMutation(String text, ParticipantId author) {
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
					
}

