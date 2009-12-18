package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicBoolean;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;

public class ElementDeletionCursor implements ICursorWithResult<BufferedDocOp> {

	private final DocOpBuilder elmDeletion = new DocOpBuilder();
	private final String elmToDeleteID;
	
	private AtomicBoolean insideElmToDelete = new AtomicBoolean(false);

	public ElementDeletionCursor(TagID tagID) {
		this.elmToDeleteID = tagID.getValue();
	}
	
	@Override
	public void elementStart(String key, Attributes attrs) {
		if (attrs.get(AbstractDocumentTag.ID_ATTR_NAME).equals(elmToDeleteID)) {
			insideElmToDelete.set(true);
			elmDeletion.deleteElementStart(key, attrs);			
		} else {
			insideElmToDelete.set(false);
			elmDeletion.retain(1);
		}
	}	

	@Override
	public void characters(String s) {
		if (insideElmToDelete.get()) {
			elmDeletion.deleteCharacters(s);
		} else {
			elmDeletion.retain(s.length());
		}
	}

	@Override
	public void elementEnd() {
		if (insideElmToDelete.get()) {
			elmDeletion.deleteElementEnd();
		} else {
			elmDeletion.retain(1);
		}
	}

	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) {
	}
	
	public BufferedDocOp getResult() {
		return elmDeletion.build();
	}

}
