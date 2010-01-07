package name.shamansir.sametimed.wave.doc.cursor;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;

public class DocumentElementDeletionByPosCursor implements
		IOperatingCursorWithResult<Object> {

	public DocumentElementDeletionByPosCursor(int position) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getResult() {
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

	@Override
	public void setWalkStart(int pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public DocOpBuilder takeDocOp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void useDocOp(DocOpBuilder curDocOp) {
		// TODO Auto-generated method stub
		
	}

}
