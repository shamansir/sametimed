package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicInteger;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

public abstract class AbstractLastElementSearchingCursor implements ICursorWithResult<Integer> {

	private final AtomicInteger totalElements;
	private final AtomicInteger lastElement;	
	
	public AbstractLastElementSearchingCursor() {
		this.totalElements = new AtomicInteger(0); 
		this.lastElement = new AtomicInteger(-1); 
	}
	
	@Override
	public void elementStart(String type, Attributes attrs) {
		if (isElementApproved(type)) {
			totalElements.incrementAndGet();

			if (areAttrsApproved(attrs)) {
				lastElement.set(totalElements.get() - 1);
			}
		}
	}

	@Override
	public void characters(String s) { }

	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) { }

	@Override
	public void elementEnd() { }
	
	@Override
	public Integer getResult() {
		return Integer.valueOf(lastElement.get());
	}
	
	protected abstract boolean isElementApproved(String elementName);
	protected abstract boolean areAttrsApproved(Attributes attrs);
	
}
