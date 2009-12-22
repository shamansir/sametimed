package name.shamansir.sametimed.wave.modules.editor;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.module.mutation.proto.IModuleWithDocument;
import name.shamansir.sametimed.wave.modules.chat.WaveletWithChat;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

public class WaveletWithEditor extends WaveletWithChat {
	
	private EditorModule editorView = null;

	public WaveletWithEditor(int clientID, IWavesClientRenderer renderer) {
		super(clientID, renderer);		
	}
	
	public WaveletWithEditor(int clientID) {
		super(clientID);
	}
	
	@Override	
	protected void prepareModules() throws ParseException {
		super.prepareModules();
		editorView = new EditorModule(this);
	}
	
	@Override
	protected Map<String, IModuleWithDocument<?>> registerModules(
			Map<String, IModuleWithDocument<?>> curModules) {
		curModules.put(editorView.getModuleID(), editorView);
		return super.registerModules(curModules);
	}	
	
	@Override
	protected void updateModulesModels() {
		super.updateModulesModels();
		updateModel(ModelID.EDITOR_MODEL, editorView.extract());		
	}
	
	@Override
	protected List<ModelID> registerModulesModels(List<ModelID> currentTypes) {
		currentTypes.add(ModelID.EDITOR_MODEL);
		return super.registerModulesModels(currentTypes);
	}	
	
	
}
