package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.Document;
import name.shamansir.sametimed.wave.model.base.atom.TextChunk;

public class DocumentModel extends AModel<List<TextChunk>, Document> {
	
	protected DocumentModel() {
		super(ModelID.EDITOR_MODEL);
	}
	
	protected DocumentModel(List<TextChunk> source) {
		super(ModelID.EDITOR_MODEL, source);
	}	

	@Override
	public Document createEmptyValue() {
		return new Document();
	}

	@Override
	public Document extractValue(List<TextChunk> source) {
		return new Document(source);
	}	

}
