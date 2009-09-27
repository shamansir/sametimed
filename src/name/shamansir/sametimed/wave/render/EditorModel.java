package name.shamansir.sametimed.wave.render;

import java.util.ArrayList;
import java.util.List;

public class EditorModel extends APanelModel {
	
	protected class EditorModelValue implements IModelValue {
		
		private List<String> documentContent;
		
		private EditorModelValue(List<String> documentContent) {
			this.documentContent = documentContent; 
		}
		
		private EditorModelValue() {
			this.documentContent = new ArrayList<String>(); 
		}		
		
		public List<String> getDocumentContent() {
			return documentContent;
		}
		
	}	

	protected EditorModel(List<String> model) {
		super(model);
	}
	
	public List<String> getDocumentContent() {
		return ((EditorModelValue)getValue()).getDocumentContent();
	}

	@Override
	protected IModelValue extractModel(List<String> fromList) {
		return new EditorModelValue(fromList);
	}

	@Override
	protected IModelValue createEmptyValue() {
		return new EditorModelValue();
	}		

}
