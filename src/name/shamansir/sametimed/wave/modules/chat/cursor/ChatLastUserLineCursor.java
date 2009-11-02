package name.shamansir.sametimed.wave.modules.chat.cursor;

import name.shamansir.sametimed.wave.doc.cursor.ALastLineSearchingCursor;
import name.shamansir.sametimed.wave.modules.chat.ChatTag;

import org.waveprotocol.wave.model.document.operation.Attributes;

// Find the last line written by the participant given by userId (by
// counting the number of
// <line></line> elements, and comparing to their authors).

public class ChatLastUserLineCursor extends ALastLineSearchingCursor {
	
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
	protected boolean isTagApproved(String tagName) {
		return tagName.equals(ChatTag.TAG_NAME);
	}

}
