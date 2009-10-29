package name.shamansir.sametimed.wave.modules.chat.cursor;

import name.shamansir.sametimed.wave.doc.cursor.LineDeletionCursor;
import name.shamansir.sametimed.wave.modules.chat.ChatTag;

public class ChatLineDeletionCursor extends LineDeletionCursor {

	public ChatLineDeletionCursor(int lineToDelete) {
		super(lineToDelete, ChatTag.LINE_TAG_NAME);
	}

}
