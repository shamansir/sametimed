package name.shamansir.sametimed.wave.doc.cursor;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.doc.sequencing.AbstractOperatingCursorWithResult;

public class DocumentElementCuttingCursor extends
	AbstractOperatingCursorWithResult<AbstractDocumentTag> {

	public DocumentElementCuttingCursor(TagID tagID) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public AbstractDocumentTag getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void characters(String chars) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void elementEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void elementStart(String type, Attributes attrs) {
		// TODO Auto-generated method stub
		
	}
	
	/* private final String tagID;
	
	private String tagName;
	private Attributes attrs;
	private String content; 

	private AtomicBoolean deleted = new AtomicBoolean(false);
	private AtomicBoolean insideElmToDelete = new AtomicBoolean(false);
	private final DocOpBuilder elmDeletion = new DocOpBuilder();
	
	public DocumentElementCuttingCursor(TagID tagID) {
		this.tagID = tagID.getValue();
	}

	@Override
	public AbstractDocumentTag getResult() {
		// elmDeletion.finish(); // FIXME: where to pass?
		return deleted.get() 
				? AbstractDocumentTag.createEmpty(tagID, tagName, attrs, content) 
				: null;		
	}

	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) { }

	@Override
	public void elementStart(String key, Attributes attrs) {
		if (attrs.get(AbstractDocumentTag.ID_ATTR_NAME).equals(tagID)) {
			insideElmToDelete.set(true);
			elmDeletion.deleteElementStart(key, attrs);
			this.tagName = key; this.attrs = attrs;
		} else {
			insideElmToDelete.set(false);
			elmDeletion.retain(1);
		}
	}	

	@Override
	public void characters(String s) {
		if (insideElmToDelete.get()) {
			elmDeletion.deleteCharacters(s);
			this.content = s;
		} else {
			elmDeletion.retain(s.length());
		}
	}

	@Override
	public void elementEnd() {
		if (insideElmToDelete.get()) {
			elmDeletion.deleteElementEnd();
			deleted.set(true);
		} else {
			elmDeletion.retain(1);
		}
	} */

}
