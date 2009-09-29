package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.Document;

public class DocumentModel extends AModel<List<String>, Document> {
	
	protected DocumentModel() {
		super(ModelID.EDITOR_MODEL);
	}
	
	protected DocumentModel(List<String> source) {
		super(ModelID.EDITOR_MODEL, source);
	}	

	@Override
	public Document createEmptyValue() {
		return new Document();
	}

	@Override
	public Document extractValue(List<String> source) {
		return new Document(source);
	}	

}
