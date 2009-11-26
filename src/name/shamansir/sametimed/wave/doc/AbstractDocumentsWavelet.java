package name.shamansir.sametimed.wave.doc;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

import name.shamansir.sametimed.wave.AbstractUpdatingWavelet;
import name.shamansir.sametimed.wave.doc.mutation.ChangeViewModeMutation;
import name.shamansir.sametimed.wave.doc.mutation.IMutation;
import name.shamansir.sametimed.wave.doc.mutation.MutationCompilationException;
import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.render.RenderMode;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

// NOTE: There are naming problems between wave-protocol Document concept
//       (an extension of BufferedDocOp for the moment),
//       and mine document concept, as a model holder, tags-projection
//       and mutations performer (itself). I even can extend wave-protocol
//       type, but it is too 'soft' for my version. So I need to keep
//		 my type of Document as an external interface and keep wave-protocol
//       documents inside, not allowing for sametimed-api-users to touch it.
//       Again, in two words: my MutableDocuments are 'very strict 
//       but easy extendible' wrappers for wave-protocol DocOps-based documents.
//       Also, wave-protocol are _Sources_ for me and my documents are _Keepers_.
//       And Also, Mutations are in fact wrappers for DocOps, but applied
//       not for Wavelets, but for MutableDocuments

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
	
	private Map<String, IMutableDocument> registeredDocuments = new HashMap<String, IMutableDocument>();	
	
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
		registeredDocuments = new HashMap<String, IMutableDocument>();
	}
	
	@Override
	protected void clearWavePart() {
		super.clearWavePart();
        resetDocuments();	
        registeredDocuments = new HashMap<String, IMutableDocument>();
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
		registeredDocuments = registerDocuments(new HashMap<String, IMutableDocument>());		
	}
	
	@Override
	protected void onViewModeChanged(RenderMode newMode) {
		super.onViewModeChanged(newMode);
		applyMutationToDocuments(new ChangeViewModeMutation(newMode));	
	}
	
	@Override
	protected void onWavePartUpdated() {
		super.onWavePartUpdated();
		updateDocumentsModels();
	}	
		
	/* ====== DOCUMENTS OPERATIONS ====== */
	
	protected abstract Map<String, IMutableDocument> registerDocuments(Map<String, IMutableDocument> curHandlers);
	
	protected abstract List<ModelID> registerDocumentsModelsTypes(List<ModelID> currentTypes);	
	
	protected abstract void prepareDocuments() throws ParseException;
	
	protected abstract void updateDocumentsModels();	
	
	protected abstract void resetDocuments();
	
	protected IMutableDocument getRegisteredDocument(String documentID) {
		return registeredDocuments.get(documentID);
	}
	
	protected BufferedDocOp getSource(String documentID) {
		return getOpenWavelet() == null ? null : getOpenWavelet()
				.getDocuments().get(documentID);
	}
	
	/* ====== DOCUMENTS-RELATED OPERATIONS ====== */
	
	public boolean applyMutationToDocument(String documentID, IMutation mutation) {
		if (isWaveOpen()) {
			IMutableDocument mutableDoc = getRegisteredDocument(documentID);
			if (mutableDoc != null) {
				BufferedDocOp srcDoc = getSource(documentID);
				try {
					// srcDoc can be null!
					performWaveletOperation(mutableDoc.compileMutation(srcDoc, mutation));
					return true;
				} catch (MutationCompilationException mce) {
					registerError("Document '" + mutableDoc.getDocumentID() + "' mutation error: " + mce.getMessage());
					return false;
				}			
			} else {
				registerError("document with id \'" + documentID + "\' is not found or have no handler");
				return false;
			}			
		} else {
	        registerError(AbstractUpdatingWavelet.NOT_OPENED_WAVE_ERR);
	        return false;
	    }
	}
		
	public void applyMutationToDocuments(IMutation mutation) {
		if (isWaveOpen()) {
			for (IMutableDocument mutableDoc: registeredDocuments.values()) {
				try {
					performWaveletOperation(
							mutableDoc.compileMutation(
									getSource(mutableDoc.getDocumentID()), mutation)
						);
				} catch (MutationCompilationException mce) {
					registerError("Document '" + mutableDoc.getDocumentID() + "' mutation error: " + mce.getMessage());
				}
			}		
		} else {
	        registerError(AbstractUpdatingWavelet.NOT_OPENED_WAVE_ERR);
	    }
	}
	
	/*
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
		IMutableDocument opHandler = getDocumentOperationsHandler(documentID);
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
		IMutableDocument opHandler = getDocumentOperationsHandler(documentID);
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
		IMutableDocument opHandler = getDocumentOperationsHandler(documentID);
		BufferedDocOp operableDoc = getDocument(documentID); 
		if (opHandler != null) {
			WaveletDocumentOperation appendOp = opHandler.getAppendOp(operableDoc, author, text);
			if (appendOp != null) performDocumentOperation(appendOp);
		} else {
			registerError("document with id \'" + documentID + "\' is not found or have no handler");
		}	
	} */
	
	private void performWaveletOperation(WaveletDocumentOperation operation) {
		if (operation != null) {
			getOperationsSender().sendWaveletOperation(getOpenWavelet().getWaveletName(),
					operation);
		}
	}
					
}

