package name.shamansir.sametimed.wave.model;

import java.util.ArrayList;
import java.util.List;


public class Document implements IModelValue {
	
	private List<String> documentContent;
	
	public Document(List<String> documentContent) {
		this.documentContent = documentContent; 
	}
	
	public Document() {
		this.documentContent = new ArrayList<String>(); 
	}		
	
	public List<String> getDocumentContent() {
		return documentContent;
	}	

}
