package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

public class DocumentElementStartCursor implements ICursorWithResult<Integer> {

	private final String tagIDToFind;
	
	private AtomicInteger currentPos = new AtomicInteger(0);
	private AtomicInteger tagStartPos = new AtomicInteger(0);
	private AtomicBoolean tagFound = new AtomicBoolean(false);
	
	public DocumentElementStartCursor(TagID tagToFind) {
		this.tagIDToFind = tagToFind.getValue();
	}

	@Override
	public Integer getResult() {
		return tagStartPos.get();
	}

	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) { }
	
	@Override
	public void elementStart(String type, Attributes attrs) { 		
		if (tagIDToFind.equals(attrs.get(AbstractDocumentTag.ID_ATTR_NAME))) {
			tagFound.set(true);
		}
	}	

	@Override
	public void characters(String chars) {
		if (tagFound.get()) tagStartPos.set(currentPos.get());
		tagFound.set(false);
		currentPos.addAndGet(chars.length());
	}

	@Override
	public void elementEnd() { }

}
