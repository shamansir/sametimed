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

public abstract class AbstractModulatedWavelet extends AbstractUpdatingWavelet {
	
	private static final Logger LOG = Logger.getLogger(AbstractModulatedWavelet.class.getName());
	
	private Map<String, IMutableModule> registeredModules = new HashMap<String, IMutableModule>();	
	
	public AbstractModulatedWavelet(int clientID, IWavesClientRenderer renderer) {
		super(clientID, renderer);
	}
	
	public AbstractModulatedWavelet(int clientID) {
		this(clientID, null);
	}
	
	/* ====== OVERRIDES ====== */
	
	@Override
	protected final List<ModelID> getAdditionalModels() {
		List<ModelID> modelsTypes = new ArrayList<ModelID>();
		return registerModulesModels(modelsTypes);
	}
	
	@Override
	protected void clear() {
		super.clear();
		resetModules();
		registeredModules = new HashMap<String, IMutableModule>();
	}
	
	@Override
	protected void clearWavePart() {
		super.clearWavePart();
        resetModules();	
        registeredModules = new HashMap<String, IMutableModule>();
	}
	
	@Override 
	protected void prepareNewWave(ClientWaveView newWave) {
		super.prepareNewWave(newWave);
		try {
			prepareModules();
		} catch (ParseException e) {
			LOG.severe("Documents preparation failed: " + e.getMessage());
			e.printStackTrace();
		}
		registeredModules = registerModules(new HashMap<String, IMutableModule>());		
	}
	
	@Override
	protected void onViewModeChanged(RenderMode newMode) {
		super.onViewModeChanged(newMode);
		applyMutationToDocuments(new ChangeViewModeMutation(newMode));	
	}
	
	@Override
	protected void onWavePartUpdated() {
		super.onWavePartUpdated();
		updateModulesModels();
	}	
		
	/* ====== MODULES OPERATIONS ====== */
	
	protected abstract Map<String, IMutableModule> registerModules(Map<String, IMutableModule> curModules);
	
	protected abstract List<ModelID> registerModulesModels(List<ModelID> currentTypes);	
	
	protected abstract void prepareModules() throws ParseException;
	
	protected abstract void updateModulesModels();	
	
	protected abstract void resetModules();
	
	protected IMutableModule getRegisteredModule(String moduleID) {
		return registeredModules.get(moduleID);
	}
	
	protected BufferedDocOp getSource(String documentID) {
		return getOpenWavelet() == null ? null : getOpenWavelet()
				.getDocuments().get(documentID);
	}
	
	/* ====== DOCUMENTS-RELATED OPERATIONS ====== */
	
	public boolean applyMutationToDocument(String moduleID, IMutation mutation) {
		if (isWaveOpen()) {
			IMutableModule module = getRegisteredModule(moduleID);
			if (module != null) {
				BufferedDocOp srcDoc = getSource(moduleID);
				try {
					// srcDoc can be null!
					performWaveletOperation(module.compileMutation(srcDoc, mutation));
					return true;
				} catch (MutationCompilationException mce) {
					registerError("Document '" + module.getDocumentID() + "' mutation error: " + mce.getMessage());
					return false;
				}			
			} else {
				registerError("module with id \'" + moduleID + "\' is not found or have no handler");
				return false;
			}			
		} else {
	        registerError(AbstractUpdatingWavelet.NOT_OPENED_WAVE_ERR);
	        return false;
	    }
	}
		
	public void applyMutationToDocuments(IMutation mutation) {
		if (isWaveOpen()) {
			for (IMutableModule module: registeredModules.values()) {
				try {
					performWaveletOperation(
							module.compileMutation(
									getSource(module.getDocumentID()), mutation)
						);
				} catch (MutationCompilationException mce) {
					registerError("Document '" + module.getDocumentID() + "' mutation error: " + mce.getMessage());
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
		IMutableModule opHandler = getDocumentOperationsHandler(documentID);
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
		IMutableModule opHandler = getDocumentOperationsHandler(documentID);
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
		IMutableModule opHandler = getDocumentOperationsHandler(documentID);
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

