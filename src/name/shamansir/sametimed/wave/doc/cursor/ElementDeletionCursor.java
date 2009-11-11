package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;

public class ElementDeletionCursor implements ICursorWithResult<BufferedDocOp> {

	private final DocOpBuilder elmDeletion = new DocOpBuilder();
	private final AtomicInteger currentElmPos = new AtomicInteger(-1);

	private final AtomicInteger elmDeletionPos;
	private final AtomicBoolean filterElements;
	private final String filterName;

	public ElementDeletionCursor(int elmDeletionPos) {
		this.elmDeletionPos = new AtomicInteger(elmDeletionPos);
		this.filterElements = new AtomicBoolean(false);
		this.filterName = "";
	}
	
	public ElementDeletionCursor(int elmDeletionPos, String elmNameFilter) {
		this.elmDeletionPos = new AtomicInteger(elmDeletionPos);
		this.filterElements = new AtomicBoolean(true);
		this.filterName = elmNameFilter;
	}	
	
	@Override
	public void elementStart(String key, Attributes attrs) {
		if (filterElements.get()) {
			if (key.equals(filterName)) {
				currentElmPos.incrementAndGet();
			}
		} else {
			currentElmPos.incrementAndGet();
		}

		if (currentElmPos.get() == elmDeletionPos.get()) {
			elmDeletion.deleteElementStart(key, attrs);
		} else {
			elmDeletion.retain(1);
		}
	}	

	@Override
	public void characters(String s) {
		if (currentElmPos.get() == elmDeletionPos.get()) {
			elmDeletion.deleteCharacters(s);
		} else {
			elmDeletion.retain(s.length());
		}
	}

	@Override
	public void elementEnd() {
		if (currentElmPos.get() == elmDeletionPos.get()) {
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
