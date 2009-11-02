package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicInteger;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

public abstract class ALastLineSearchingCursor implements ICursorWithResult<Integer> {

	private final AtomicInteger totalLines;
	private final AtomicInteger lastLine;	
	
	public ALastLineSearchingCursor() {
		this.totalLines = new AtomicInteger(0); 
		this.lastLine = new AtomicInteger(-1); 
	}
	
	@Override
	public void elementStart(String type, Attributes attrs) {
		if (isTagApproved(type)) {
			totalLines.incrementAndGet();

			if (areAttrsApproved(attrs)) {
				lastLine.set(totalLines.get() - 1);
			}
		}
	}

	@Override
	public void characters(String s) {
	}

	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) {
	}

	@Override
	public void elementEnd() {
	}
	
	public Integer getResult() {
		return Integer.valueOf(lastLine.get());
	}	
	
	protected abstract boolean isTagApproved(String tagName);
	protected abstract boolean areAttrsApproved(Attributes attrs);
	
}
