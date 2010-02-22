package name.shamansir.sametimed.wave.doc.cursor;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.sequencing.AbstractOperatingCursorWithResult;

public class DocumentElementCuttingByPosCursor extends
		AbstractOperatingCursorWithResult<AbstractDocumentTag> {

	public DocumentElementCuttingByPosCursor(int position) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public AbstractDocumentTag getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) {
		// TODO Auto-generated method stub

	}

	@Override
	public void characters(String chars) {
		// TODO Auto-generated method stub

	}

	@Override
	public void elementEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void elementStart(String type, Attributes attrs) {
		// TODO Auto-generated method stub

	}
	
}
