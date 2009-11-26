package name.shamansir.sametimed.wave.modules.editor.doc.cursor;

import name.shamansir.sametimed.wave.doc.cursor.ElementDeletionCursor;
import name.shamansir.sametimed.wave.modules.editor.EditorTag;

public class DocumentChunkDeletionCursor extends ElementDeletionCursor {

	public DocumentChunkDeletionCursor(int lineToDelete) {
		super(lineToDelete, EditorTag.TAG_NAME);
	}	
	
}
