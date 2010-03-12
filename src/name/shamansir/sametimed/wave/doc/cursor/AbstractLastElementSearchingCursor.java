package name.shamansir.sametimed.wave.doc.cursor;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

// this cursor can't predict what element is last in fact and detach there, so it is not operating

public abstract class AbstractLastElementSearchingCursor implements ICursorWithResult<TagID> {

	private String lastElementID;	
	
	public AbstractLastElementSearchingCursor() { 
		this.lastElementID = null; 
	}
	
	@Override
	public void elementStart(String type, Attributes attrs) {		
		if (isElementApproved(type) && areAttrsApproved(attrs)) { 
			lastElementID = attrs.get(AbstractDocumentTag.ID_ATTR_NAME);
		}
	}

	@Override
	public void characters(String s) { }

	@Override
	public void elementEnd() { }
	
	@Override
	public TagID getResult() {
		return TagID.valueOf(lastElementID);
	}
	
	public void annotationBoundary(AnnotationBoundaryMap map) { }

	protected abstract boolean areAttrsApproved(Attributes attrs);
	protected abstract boolean isElementApproved(String elementName);
		
}
