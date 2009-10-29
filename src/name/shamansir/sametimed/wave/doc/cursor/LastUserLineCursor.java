package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicInteger;

import name.shamansir.sametimed.wave.modules.chat.ChatTag;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

// Find the last line written by the participant given by userId (by
// counting the number of
// <line></line> elements, and comparing to their authors).

public class LastUserLineCursor implements ICursorWithResult<Integer> {
	
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
		if (type.equals(ChatTag.LINE_TAG_NAME)) {
			totalLines.incrementAndGet();

			if (userId.equals(attrs
					.get(ChatTag.AUTHOR_ATTR_NAME))) {
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

}
