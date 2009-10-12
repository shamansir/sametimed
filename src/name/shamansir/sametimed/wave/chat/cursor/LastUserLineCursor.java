package name.shamansir.sametimed.wave.chat.cursor;

import java.util.concurrent.atomic.AtomicInteger;

import org.waveprotocol.wave.examples.fedone.waveclient.console.ConsoleUtils;
import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;

// Find the last line written by the participant given by userId (by
// counting the number of
// <line></line> elements, and comparing to their authors).

public class LastUserLineCursor implements DocInitializationCursor {
	
	private final String userId;
	private final AtomicInteger totalLines;
	private final AtomicInteger lastLine;	
	
	public LastUserLineCursor(String userId) {
		this.userId = userId;
		this.totalLines = new AtomicInteger(0); 
		this.lastLine = new AtomicInteger(-1); 
	}
	
	@Override
	public void elementStart(String type, Attributes attrs) {
		if (type.equals(ConsoleUtils.LINE)) {
			totalLines.incrementAndGet();

			if (userId.equals(attrs
					.get(ConsoleUtils.LINE_AUTHOR))) {
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
	
	public int getComputedNum() {
		return lastLine.get();
	}

}
