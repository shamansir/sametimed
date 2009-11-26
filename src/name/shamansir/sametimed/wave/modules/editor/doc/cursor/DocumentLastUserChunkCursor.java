package name.shamansir.sametimed.wave.modules.editor.doc.cursor;

import name.shamansir.sametimed.wave.doc.cursor.AbstractLastElementSearchingCursor;
import name.shamansir.sametimed.wave.modules.editor.EditorTag;

import org.waveprotocol.wave.model.document.operation.Attributes;

//Find the last chunk created by the participant with given userId (by
//counting the number of
//<line></line> elements, and comparing to their authors).

public class DocumentLastUserChunkCursor extends AbstractLastElementSearchingCursor {
	
	private final String userId;	
	
	public DocumentLastUserChunkCursor(String userId) {
		super();
		this.userId = userId;
	}

	@Override
	protected boolean areAttrsApproved(Attributes attrs) {
		return userId.equals(attrs.get(EditorTag.AUTHOR_ATTR_NAME));
	}

	@Override
	protected boolean isElementApproved(String elementName) {
		return elementName.equals(EditorTag.TAG_NAME);
	}

}
