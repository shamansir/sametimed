package name.shamansir.sametimed.wave.model.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import name.shamansir.sametimed.wave.model.base.atom.TextChunk;

public class Document extends JSONiableValue implements IModelValue {
	
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
	public String asJSON(boolean useEscapedQuotes) {
		String jsonString = "[";
		String quot = useEscapedQuotes ? "\\\"" : "\"";	
		for (Iterator<TextChunk> iter = documentContent.iterator(); iter.hasNext(); ) {
			TextChunk textChunk = iter.next();
			jsonString += "{" + quot + "text"  + quot + ":" + quot + escapeJSONString(textChunk.getText())  + quot + "," +
							    quot + "style" + quot + ":" + quot + escapeJSONString(textChunk.getStyle()) + quot + "}";
			if (iter.hasNext()) jsonString += ",";
		}
		return jsonString + "]";
	}
	
	@Override	
	public String asJSON() {
		return asJSON(false);
	}	
	
}
