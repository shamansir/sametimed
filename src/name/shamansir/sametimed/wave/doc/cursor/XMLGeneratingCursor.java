package name.shamansir.sametimed.wave.doc.cursor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

public class XMLGeneratingCursor implements ICursorWithResult<List<String>> {
	
	private final AtomicBoolean gotEnd;
	private final AtomicBoolean gotCharacters;	
	
	private final StringBuilder curXMLLine;
	private final StringBuilder curElmType;
	private final StringBuilder curElmContent;
	
	private Queue<String> xmlLines;
	
	public XMLGeneratingCursor() {
		this.curXMLLine = new StringBuilder();
		this.curElmType = new StringBuilder();
		this.curElmContent = new StringBuilder();
		
		this.gotCharacters = new AtomicBoolean(false);	// used to determine the flow order
		this.gotEnd = new AtomicBoolean(false); // used to determine the flow order
		
		this.xmlLines = new ConcurrentLinkedQueue<String>();
	}


	@Override
	public void elementStart(String type, Attributes attrs) {
		this.gotCharacters.set(false);		
		this.gotEnd.set(false);
		
		curElmType.replace(0, curElmType.length(), type); // replace with type
		curElmContent.delete(0, curElmContent.length()); // clear
		
		if (attrs.isEmpty()) {
			curXMLLine.append("<" + type + ">");
		} else {
			curXMLLine.append("<" + type + " ");
            for (String key : attrs.keySet()) {
            	curXMLLine.append(key + "=\"" + attrs.get(key) + "\" ");
            }
            curXMLLine.append(">");
		}
		
	}	
		
	@Override
	public void characters(String chars) {
		this.gotCharacters.set(true);		
		curElmContent.replace(0, curElmContent.length(), chars); // replace with chars
		
		if (this.gotEnd.get()) {
			curXMLLine.append(curElmContent + "</" + curElmType + ">");
			xmlLines.add(curXMLLine.toString());
		}
	}

	@Override
	public void elementEnd() {
		this.gotEnd.set(true);
		
		if (this.gotCharacters.get()) {
			curXMLLine.append(curElmContent + "</" + curElmType + ">");
			xmlLines.add(curXMLLine.toString());
		}
	}
	
	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) { }	
	
	@Override
	public List<String> getResult() { // FIXME: do not create a new list?
		return Collections.unmodifiableList(new LinkedList<String>(xmlLines));
	}	

}
