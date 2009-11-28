package name.shamansir.sametimed.wave.modules.editor.doc.cursor;

import name.shamansir.sametimed.wave.doc.cursor.AbstractLastElementSearchingCursor;
import name.shamansir.sametimed.wave.modules.editor.EditorTag;

import org.waveprotocol.wave.model.document.operation.Attributes;

//Find the last chunk created by the participant with given userID (by
//counting the number of
//<line></line> elements, and comparing to their authors).

public class DocumentLastUserChunkCursor extends AbstractLastElementSearchingCursor {
	
	private final String userID;	
	
	public DocumentLastUserChunkCursor(String userID) {
		super();
		this.userID = userID;
	}

	@Override
	protected boolean areAttrsApproved(Attributes attrs) {
		return userID.equals(attrs.get(EditorTag.AUTHOR_ATTR_NAME));
	}

	@Override
	protected boolean isElementApproved(String elementName) {
		return elementName.equals(EditorTag.TAG_NAME);
	}

}
