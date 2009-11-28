package name.shamansir.sametimed.wave.module;

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
import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.module.mutation.ChangeViewModeMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.IModuleMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;
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
		applyModulesMutation(new ChangeViewModeMutation(newMode));	
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
	
	// FIXME: Test if things work when module ID is different than inner document ID
	
	public boolean applyModuleMutation(String moduleID, IModuleMutation mutation) {
		if (isWaveOpen()) {
			IMutableModule module = getRegisteredModule(moduleID);
			if (module != null) {
				BufferedDocOp srcDoc = getSource(module.getDocumentID());
				try {
					// srcDoc can be null!
					performWaveletOperation(module.apply(srcDoc, mutation));
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
		
	public void applyModulesMutation(IModuleMutation mutation) {
		if (isWaveOpen()) {
			for (IMutableModule module: registeredModules.values()) {
				try {
					performWaveletOperation(
							module.apply(
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
	
	private void performWaveletOperation(WaveletDocumentOperation operation) {
		if (operation != null) {
			getOperationsSender().sendWaveletOperation(getOpenWavelet().getWaveletName(),
					operation);
		}
	}
					
}

