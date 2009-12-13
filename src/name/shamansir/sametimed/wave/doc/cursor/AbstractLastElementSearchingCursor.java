package name.shamansir.sametimed.wave.doc.cursor;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

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
	public void annotationBoundary(AnnotationBoundaryMap map) { }

	@Override
	public void elementEnd() { }
	
	@Override
	public TagID getResult() {
		return TagID.valueOf(lastElementID);
	}
	
	// FIXME: this checking is not so required, if we use ID-s, remove
	protected abstract boolean isElementApproved(String elementName);
	protected abstract boolean areAttrsApproved(Attributes attrs); 
	
}
