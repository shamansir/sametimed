package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicInteger;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;

public class DocumentElementByPositionCursor implements ICursorWithResult<TagID> {
	
	private final AtomicInteger posToSearch;
	
	private AtomicInteger currentPos = new AtomicInteger(0);
	private String currentTagID = null;
	private String foundTagID = null;

	public DocumentElementByPositionCursor(Integer pos) {
		posToSearch = new AtomicInteger(pos);
	}

	@Override
	public TagID getResult() {
		return TagID.valueOf((foundTagID != null) ? foundTagID : currentTagID);
	}

	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) { }
	
	@Override
	public void elementStart(String type, Attributes attrs) { 		
		currentTagID = attrs.get(AbstractDocumentTag.ID_ATTR_NAME);
	}	

	@Override
	public void characters(String chars) {
		if ((currentPos.get() + chars.length()) > posToSearch.get()) {
			foundTagID = currentTagID;
		} else {
			currentPos.addAndGet(chars.length());
		}
	}

	@Override
	public void elementEnd() { }

}
