package name.shamansir.sametimed.wave.modules.chat.doc.cursor;

import name.shamansir.sametimed.wave.doc.cursor.AbstractLastElementSearchingCursor;
import name.shamansir.sametimed.wave.modules.chat.ChatTag;

import org.waveprotocol.wave.model.document.operation.Attributes;

// Find the last line written by the participant given by userId (by
// counting the number of
// <line></line> elements, and comparing to their authors).

public class ChatLastUserLineCursor extends AbstractLastElementSearchingCursor {
	
	private final String userId;	
	
	public ChatLastUserLineCursor(String userId) {
		super();
		this.userId = userId;
	}

	@Override
	protected boolean areAttrsApproved(Attributes attrs) {
		return userId.equals(attrs.get(ChatTag.AUTHOR_ATTR_NAME));
	}

	@Override
	protected boolean isElementApproved(String elementName) {
		return elementName.equals(ChatTag.TAG_NAME);
	}

}
