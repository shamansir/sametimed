package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.Document;

public class EditorModel extends AModel<List<String>, Document> {

	protected EditorModel(List<String> model) {
		super(ModelID.EDITOR_MODEL, model);
	}
	
	protected EditorModel() {
		super(ModelID.EDITOR_MODEL);
	}	
	
	public List<String> getDocumentContent() {
		return getValue().getDocumentContent();
	}

	@Override
	protected Document extractValue(List<String> fromSource) {
		return new Document(fromSource);
	}

	@Override
	protected Document createEmptyValue() {
		return new Document();
	}		

}
