package name.shamansir.sametimed.wave.modules.editor;

import java.util.List;

import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.modules.chat.WaveletWithChat;
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
	protected List<ModelID> registerDocumentsModelsTypes(List<ModelID> currentTypes) {
		currentTypes.add(ModelID.EDITOR_MODEL);
		return super.registerDocumentsModelsTypes(currentTypes);
	}	
	
	
}
