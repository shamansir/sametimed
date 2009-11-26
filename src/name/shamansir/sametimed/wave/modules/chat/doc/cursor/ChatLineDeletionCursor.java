package name.shamansir.sametimed.wave.modules.chat.doc.cursor;

import name.shamansir.sametimed.wave.doc.cursor.ElementDeletionCursor;
import name.shamansir.sametimed.wave.modules.chat.ChatTag;

public class ChatLineDeletionCursor extends ElementDeletionCursor {

	public ChatLineDeletionCursor(int lineToDelete) {
		super(lineToDelete, ChatTag.TAG_NAME);
	}

}
