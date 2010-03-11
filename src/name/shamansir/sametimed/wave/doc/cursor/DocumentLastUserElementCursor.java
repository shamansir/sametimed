package name.shamansir.sametimed.wave.doc.cursor;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

//Find the last chunk created by the participant with given userID (by
//counting the number of
//<line></line> elements, and comparing to their authors).

public class DocumentLastUserElementCursor extends AbstractLastElementSearchingCursor {
	
	private final String userID;	
	
	public DocumentLastUserElementCursor(String userID) {
		super();
		this.userID = userID;
	}

	@Override
	protected boolean areAttrsApproved(Attributes attrs) {
		return userID.equals(attrs.get(AbstractDocumentTag.AUTHOR_ATTR_NAME));
	}

	@Override
	protected boolean isElementApproved(String elementName)  {
		return true;
	}

    @Override
    public void annotationBoundary(AnnotationBoundaryMap map) { }

}
