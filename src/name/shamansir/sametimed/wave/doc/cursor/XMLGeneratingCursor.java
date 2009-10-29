package name.shamansir.sametimed.wave.doc.cursor;

import java.util.List;
import java.util.ArrayList;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

public class XMLGeneratingCursor implements ICursorWithResult<List<String>> {
	
	private boolean gotEnd;
	private boolean gotCharacters;	
	
	private String curXMLLine;
	private String curElmType;
	private String curElmContent;
	
	private List<String> xmlLines;
	
	public XMLGeneratingCursor() {
		this.curXMLLine = "";
		this.curElmType = "";
		this.curElmContent = "";
		
		this.gotCharacters = false;	// used to determine the flow order
		this.gotEnd = false; // used to determine the flow order
		
		this.xmlLines = new ArrayList<String>();
	}


	@Override
	public void elementStart(String type, Attributes attrs) {
		this.gotCharacters = false;		
		this.gotEnd = false;
		
		curElmType = type;
		curElmContent = "";
		
		if (attrs.isEmpty()) {
			curXMLLine = "<" + type + ">";
		} else {
			curXMLLine = "<" + type + " ";
            for (String key : attrs.keySet()) {
            	curXMLLine += key + "=\"" + attrs.get(key) + "\"";
            }
            curXMLLine += ">";
		}
		
	}	
		
	@Override
	public void characters(String chars) {
		this.gotCharacters = true;		
		curElmContent = chars;
		
		if (this.gotEnd) {
			curXMLLine += curElmContent + "</" + curElmType + ">";
			xmlLines.add(curXMLLine);
		}
	}

	@Override
	public void elementEnd() {
		this.gotEnd = true;
		
		if (this.gotCharacters) {
			curXMLLine += curElmContent + "</" + curElmType + ">";
			xmlLines.add(curXMLLine);
		}
	}
	
	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) { }	
	
	public List<String> getResult() {
		return xmlLines;
	}	
	

}
