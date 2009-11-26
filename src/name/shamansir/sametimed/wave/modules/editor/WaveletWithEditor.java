package name.shamansir.sametimed.wave.modules.editor;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import name.shamansir.sametimed.wave.doc.IMutableDocument;
import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.modules.chat.WaveletWithChat;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

public class WaveletWithEditor extends WaveletWithChat {
	
	private EditorDocument editorView = null;

	public WaveletWithEditor(int clientID, IWavesClientRenderer renderer) {
		super(clientID, renderer);		
	}
	
	public WaveletWithEditor(int clientID) {
		super(clientID);
	}
	
	@Override	
	protected void prepareDocuments() throws ParseException {
		super.prepareDocuments();
		editorView = new EditorDocument();
	}
	
	@Override
	protected Map<String, IMutableDocument> registerDocuments(
			Map<String, IMutableDocument> curHandlers) {
		curHandlers.put(EditorDocument.DOCUMENT_ID, editorView);
		return super.registerDocuments(curHandlers);
	}	
	
	@Override
	protected void updateDocumentsModels() {
		super.updateDocumentsModels();
		updateModel(ModelID.EDITOR_MODEL, editorView.extract(getSource(editorView.getDocumentID())));		
	}	
	
	@Override
	protected List<ModelID> registerDocumentsModelsTypes(List<ModelID> currentTypes) {
		currentTypes.add(ModelID.EDITOR_MODEL);
		return super.registerDocumentsModelsTypes(currentTypes);
	}	
	
	
}
