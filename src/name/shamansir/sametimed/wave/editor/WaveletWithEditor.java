package name.shamansir.sametimed.wave.editor;

import java.util.List;

import name.shamansir.sametimed.wave.chat.WaveletWithChat;
import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

public class WaveletWithEditor extends WaveletWithChat {

	public WaveletWithEditor(int clientID, IWavesClientRenderer renderer) {
		super(clientID, renderer);		
	}
	
	public WaveletWithEditor(int clientID) {
		super(clientID);
	}
	
	@Override
	protected void updateDocumentsModels() {
		super.updateDocumentsModels();
		updateModel(ModelID.EDITOR_MODEL, null);		
	}	
	
	@Override
	protected List<ModelID> getDocumentsModelsTypes() {
		List<ModelID> modelsTypes = super.getDocumentsModelsTypes();
		modelsTypes.add(ModelID.EDITOR_MODEL);
		return modelsTypes;
	}	
	
	
}
