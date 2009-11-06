package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicInteger;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;

public class ElementDeletionCursor implements ICursorWithResult<BufferedDocOp> {

	private final DocOpBuilder elmDeletion = new DocOpBuilder();
	private final AtomicInteger currentElmPos = new AtomicInteger(-1);

	private final int elmDeletionPos;
	private final boolean filterElements;
	private final String filterName;

	public ElementDeletionCursor(int elmDeletionPos) {
		this.elmDeletionPos = elmDeletionPos;
		this.filterElements = false;
		this.filterName = null;
	}
	
	public ElementDeletionCursor(int elmDeletionPos, String elmNameFilter) {
		this.elmDeletionPos = elmDeletionPos;
		this.filterElements = true;
		this.filterName = elmNameFilter;
	}	
	
	@Override
	public void elementStart(String key, Attributes attrs) {
		if (filterElements) {
			if (key.equals(filterName)) {
				currentElmPos.incrementAndGet();
			}
		} else {
			currentElmPos.incrementAndGet();
		}

		if (currentElmPos.get() == elmDeletionPos) {
			elmDeletion.deleteElementStart(key, attrs);
		} else {
			elmDeletion.retain(1);
		}
	}	

	@Override
	public void characters(String s) {
		if (currentElmPos.get() == elmDeletionPos) {
			elmDeletion.deleteCharacters(s);
		} else {
			elmDeletion.retain(s.length());
		}
	}

	@Override
	public void elementEnd() {
		if (currentElmPos.get() == elmDeletionPos) {
			elmDeletion.deleteElementEnd();
		} else {
			elmDeletion.retain(1);
		}
	}

	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) {
	}
	
	public BufferedDocOp getResult() {
		return elmDeletion.finish();
	}

}
