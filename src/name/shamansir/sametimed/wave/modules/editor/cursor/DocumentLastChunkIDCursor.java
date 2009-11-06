package name.shamansir.sametimed.wave.modules.editor.cursor;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;
import name.shamansir.sametimed.wave.modules.editor.EditorTag;

// may extend ALastElementSearchingCursor ?

public class DocumentLastChunkIDCursor implements ICursorWithResult<Integer> {

	private int lastID = 0;
	
	@Override
	public void characters(String chars) { }

	@Override
	public void elementEnd() { }

	@Override
	public void elementStart(String type, Attributes attrs) {
		if (type.equals(EditorTag.TAG_NAME)) {
			if (attrs.get(EditorTag.ID_ATTR_NAME) != null) {
				lastID = Integer.valueOf(attrs.get(EditorTag.ID_ATTR_NAME));
			}
		}
	}
	
	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) { }
	
	
	@Override
	public Integer getResult() {
		return lastID;
	}	

}
