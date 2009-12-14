package name.shamansir.sametimed.wave.doc.cursor;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

public class DocumentLastTagIDCursor implements
		ICursorWithResult<TagID> {

	private String lastID;
	
	private final String tagName;
	
	public DocumentLastTagIDCursor(String tagName) {
		this.tagName = tagName; 
	}
	
	@Override
	public void characters(String chars) { }

	@Override
	public void elementEnd() { }

	@Override
	public void elementStart(String type, Attributes attrs) {
		if (type.equals(tagName)) {
			if (attrs.get(AbstractDocumentTag.ID_ATTR_NAME) != null) {
				lastID = attrs.get(AbstractDocumentTag.ID_ATTR_NAME);
			}
		}
	}
	
	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) { }
	
	
	@Override
	public TagID getResult() {
		return TagID.valueOf(lastID);
	}

}
