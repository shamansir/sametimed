package name.shamansir.sametimed.wave.modules.editor;

import java.util.List;
import java.util.Map;

import name.shamansir.sametimed.wave.doc.IOperableDocument;
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
	protected void prepareDocuments() {
		super.prepareDocuments();
		editorView = new EditorDocument();
	}
	
	@Override
	protected Map<String, IOperableDocument> registerOperationsHandlers(
			Map<String, IOperableDocument> curHandlers) {
		curHandlers.put(EditorDocument.DOCUMENT_ID, editorView);
		return super.registerOperationsHandlers(curHandlers);
	}	
	
	@Override
	protected void updateDocumentsModels() {
		super.updateDocumentsModels();
		updateModel(ModelID.EDITOR_MODEL, editorView.extract(getDocument(editorView.getDocumentID())));		
	}	
	
	@Override
	protected List<ModelID> registerDocumentsModelsTypes(List<ModelID> currentTypes) {
		currentTypes.add(ModelID.EDITOR_MODEL);
		return super.registerDocumentsModelsTypes(currentTypes);
	}	
	
	
}
