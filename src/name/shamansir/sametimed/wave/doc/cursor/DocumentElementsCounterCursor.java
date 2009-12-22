package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

public class DocumentElementsCounterCursor implements ICursorWithResult<Integer> {

	private final AtomicInteger elementsCount = new AtomicInteger(-1);

	private final AtomicBoolean filterElements;
	private final String filterName;

	public DocumentElementsCounterCursor() {
		this.filterElements = new AtomicBoolean(false);
		this.filterName = "";
	}
	
	public DocumentElementsCounterCursor(String elmNameFilter) {
		this.filterElements = new AtomicBoolean(true);
		this.filterName = elmNameFilter;
	}	
	
	@Override
	public void elementStart(String key, Attributes attrs) {
		if (filterElements.get()) {
			if (key.equals(filterName)) {
				elementsCount.incrementAndGet();
			}
		} else {
			elementsCount.incrementAndGet();
		}
	}	

	@Override
	public void characters(String s) { }

	@Override
	public void elementEnd() { }

	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) {
	}
	
	public Integer getResult() {
		return elementsCount.get();
	}

}

