package name.shamansir.sametimed.wave.modules.editor.doc.cursor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import name.shamansir.sametimed.wave.doc.cursor.AbstractElementsScannerCursor;
import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;
import name.shamansir.sametimed.wave.model.base.atom.TextChunk;
import name.shamansir.sametimed.wave.modules.editor.EditorTag;

import org.waveprotocol.wave.model.document.operation.Attributes;

public class DocumentChunksExtractionCursor extends AbstractElementsScannerCursor<EditorTag> implements ICursorWithResult<List<TextChunk>> {
	
	private final Queue<TextChunk> textChunks;
	
	public DocumentChunksExtractionCursor() {
		super();
		this.textChunks = new ConcurrentLinkedQueue<TextChunk>();
	}
		
	protected void applyTag(EditorTag tag) {
		textChunks.add(new TextChunk(tag.getID(), 
									 tag.getContent(), 
									 (tag.getStyle() != null) ? tag.getStyle().toString() : "", 
									 (tag.getAuthor() != null) ? tag.getAuthor().toString() : "", 
									 tag.isReserved()));
	}
	
	public List<TextChunk> getResult() { // FIXME: do not create a new list?
		return Collections.unmodifiableList(new LinkedList<TextChunk>(textChunks));
	}

	@Override
	protected EditorTag createTag(String tagName, Attributes attrs) throws IllegalArgumentException {
		// FIXME: must use static method or factory
		EditorTag newTag = new EditorTag();
		newTag.initFromElement(tagName, attrs);
		return newTag;
	}
 

}
