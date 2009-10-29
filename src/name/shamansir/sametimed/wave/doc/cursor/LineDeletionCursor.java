package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicInteger;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;

public class LineDeletionCursor implements ICursorWithResult<BufferedDocOp> {

	private final DocOpBuilder lineDeletion = new DocOpBuilder();
	private final AtomicInteger currentLine = new AtomicInteger(-1);

	private final int lineToDelete;
	private final boolean filterLines;
	private final String filterName;

	public LineDeletionCursor(int lineToDelete) {
		this.lineToDelete = lineToDelete;
		this.filterLines = false;
		this.filterName = null;
	}
	
	public LineDeletionCursor(int lineToDelete, String elmNameFilter) {
		this.lineToDelete = lineToDelete;
		this.filterLines = true;
		this.filterName = elmNameFilter;
	}	
	
	@Override
	public void elementStart(String key, Attributes attrs) {
		if (filterLines) {
			if (key.equals(filterName)) {
				currentLine.incrementAndGet();
			}
		} else {
			currentLine.incrementAndGet();
		}

		if (currentLine.get() == lineToDelete) {
			lineDeletion.deleteElementStart(key, attrs);
		} else {
			lineDeletion.retain(1);
		}
	}	

	@Override
	public void characters(String s) {
		if (currentLine.get() == lineToDelete) {
			lineDeletion.deleteCharacters(s);
		} else {
			lineDeletion.retain(s.length());
		}
	}

	@Override
	public void elementEnd() {
		if (currentLine.get() == lineToDelete) {
			lineDeletion.deleteElementEnd();
		} else {
			lineDeletion.retain(1);
		}
	}

	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) {
	}
	
	public BufferedDocOp getResult() {
		return lineDeletion.finish();
	}

}
