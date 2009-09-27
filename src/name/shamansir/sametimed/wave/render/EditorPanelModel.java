package name.shamansir.sametimed.wave.render;

import java.util.List;

import name.shamansir.sametimed.wave.model.Document;
import name.shamansir.sametimed.wave.render.proto.APanelModel;

public class EditorPanelModel extends APanelModel<List<String>, Document> {

	protected EditorPanelModel(List<String> model) {
		super(model);
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
