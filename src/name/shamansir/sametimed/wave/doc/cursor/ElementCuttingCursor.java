package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicBoolean;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;

public class ElementCuttingCursor implements
		ICursorWithResult<AbstractDocumentTag> {
	
	private final String tagID;
	
	private String tagName;
	private Attributes attrs;
	private String content; 

	private AtomicBoolean deleted = new AtomicBoolean(false);
	private AtomicBoolean insideElmToDelete = new AtomicBoolean(false);
	private final DocOpBuilder elmDeletion = new DocOpBuilder();
	
	public ElementCuttingCursor(TagID tagID) {
		this.tagID = tagID.getValue();
	}

	@Override
	public AbstractDocumentTag getResult() {
		elmDeletion.finish(); // FIXME: where to pass?
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
	}
}
