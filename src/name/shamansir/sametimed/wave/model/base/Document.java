package name.shamansir.sametimed.wave.model.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import name.shamansir.sametimed.wave.model.base.atom.TextChunk;

public class Document implements IModelValue {
	
	private List<TextChunk> documentContent;
	
	public Document(List<TextChunk> documentContent) {
		this.documentContent = documentContent; 
	}
	
	public Document() {
		this.documentContent = new ArrayList<TextChunk>(); 
	}		
	
	public List<TextChunk> getDocumentContent() {
		return documentContent;
	}	

	@Override
	public String asJSON() {
		String jsonString = "[";
		for (Iterator<TextChunk> iter = documentContent.iterator(); iter.hasNext(); ) {
			TextChunk textChunk = iter.next();
			jsonString += "{'text':" + textChunk.getText() + "," +
						   "'style':" + textChunk.getStyle() + "}";
			if (iter.hasNext()) jsonString += ",";
		}
		return jsonString + "]";
	}
	
	
}
