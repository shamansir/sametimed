package name.shamansir.sametimed.wave.doc;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.AbstractUpdatingWavelet;
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

public abstract class AbstractDocumentsWavelet extends AbstractUpdatingWavelet {
	
	private static final Logger LOG = Logger.getLogger(AbstractDocumentsWavelet.class.getName());
	
	private Map<String, IOperableDocument> docOpsHandlers = new HashMap<String, IOperableDocument>();	
	
	public AbstractDocumentsWavelet(int clientID, IWavesClientRenderer renderer) {
		super(clientID, renderer);
	}
	
	public AbstractDocumentsWavelet(int clientID) {
		this(clientID, null);
	}
	
	/* ====== OVERRIDES ====== */
	
	@Override
	protected final List<ModelID> getAdditionalModels() {
		List<ModelID> modelsTypes = new ArrayList<ModelID>();
		return registerDocumentsModelsTypes(modelsTypes);
	}
	
	@Override
	protected void clear() {
		super.clear();
		resetDocuments();
		docOpsHandlers = new HashMap<String, IOperableDocument>();
	}
	
	@Override
	protected void clearWavePart() {
		super.clearWavePart();
        resetDocuments();	
        docOpsHandlers = new HashMap<String, IOperableDocument>();
	}
	
	@Override 
	protected void prepareNewWave(ClientWaveView newWave) {
		super.prepareNewWave(newWave);
		try {
			prepareDocuments();
		} catch (ParseException e) {
			LOG.severe("Documents preparation failed: " + e.getMessage());
			e.printStackTrace();
		}
		docOpsHandlers = registerOperationsHandlers(new HashMap<String, IOperableDocument>());		
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
	
	protected abstract Map<String, IOperableDocument> registerOperationsHandlers(Map<String, IOperableDocument> curHandlers);
	
	protected abstract List<ModelID> registerDocumentsModelsTypes(List<ModelID> currentTypes);	
	
	protected abstract void prepareDocuments() throws ParseException;
	
	protected abstract void updateDocumentsModels();	
	
	protected abstract void resetDocuments();
	
	protected IOperableDocument getDocumentOperationsHandler(String documentID) {
		return docOpsHandlers.get(documentID);
	}
	
	protected BufferedDocOp getDocument(String documentID) {
		return getOpenWavelet() == null ? null : getOpenWavelet()
				.getDocuments().get(documentID);
	}
	
	/* ====== DOCUMENTS-RELATED OPERATIONS ====== */
	
	private void changeDocumentsOutputMode(RenderMode mode) {
		for (String documentID: docOpsHandlers.keySet()) {
			documentChangeRenderMode(documentID, mode);
		}
	}	
		
	public boolean onUndoCall(String documentID, ParticipantId userId) {
		if (isWaveOpen()) {
	    	if (getOpenWavelet().getParticipants().contains(userId)) {
	    		return documentPerformUndo(documentID, userId);
	    	} else {
	    		registerError("Error: " + userId + " is not a participant of this wave");
	    	}
	    } else {
	        registerError(AbstractUpdatingWavelet.NOT_OPENED_WAVE_ERR);
	    }
		return false;
	}
			
	public boolean onDocumentAppendMutation(String documentID, String text, ParticipantId author) {
		if ((text == null) || (text.length() == 0)) {
			throw new IllegalArgumentException("Cannot append an empty String");
		} else if (isWaveOpen()) {
			documentPerformAppend(documentID, text, author);
			return true;
		} else {
			registerError("Error: no open wave, run \"/open\"");
			return false;
		}
	}	
	
	private void documentChangeRenderMode(String documentID, RenderMode mode) {
		IOperableDocument opHandler = getDocumentOperationsHandler(documentID);
		BufferedDocOp operableDoc = getDocument(documentID); 
		if (opHandler != null) {
			if (operableDoc != null) { // if document already created
				opHandler.handleRenderModeChange(operableDoc, mode);
			}
		} else {
			registerError("document with id \'" + documentID + "\' is not found or have no handler");
		}
	}
	
	private boolean documentPerformUndo(String documentID, ParticipantId userId) {
		IOperableDocument opHandler = getDocumentOperationsHandler(documentID);
		BufferedDocOp operableDoc = getDocument(documentID); 
		if (opHandler != null) {
			if (operableDoc == null) {
				registerError("Document is empty, nothing to undo");
				return false;
			}
			WaveletDocumentOperation undoOp = opHandler.getUndoOp(operableDoc, userId); 
			if (undoOp == null) {
				registerError("Error: " + userId + " hasn't written anything yet");
				return false;
			}
			performDocumentOperation(undoOp);			
			return true;			
		} else {
			registerError("document with id \'" + documentID + "\' is not found or have no handler");
		}
		return false;
	}
	
	private void documentPerformAppend(String documentID, String text, ParticipantId author) {
		IOperableDocument opHandler = getDocumentOperationsHandler(documentID);
		BufferedDocOp operableDoc = getDocument(documentID); 
		if (opHandler != null) {
			WaveletDocumentOperation appendOp = opHandler.getAppendOp(operableDoc, author, text);
			if (appendOp != null) performDocumentOperation(appendOp);
		} else {
			registerError("document with id \'" + documentID + "\' is not found or have no handler");
		}	
	}
	
	private void performDocumentOperation(WaveletDocumentOperation operation) {
		getOperationsSender().sendWaveletOperation(getOpenWavelet().getWaveletName(),
				operation);
	}
					
}

