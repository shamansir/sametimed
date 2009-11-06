package name.shamansir.sametimed.wave.modules.editor.cursor;

import java.util.ArrayList;
import java.util.List;

import name.shamansir.sametimed.wave.doc.cursor.AElementsScannerCursor;
import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;
import name.shamansir.sametimed.wave.model.base.atom.TextChunk;
import name.shamansir.sametimed.wave.modules.editor.EditorTag;

import org.waveprotocol.wave.model.document.operation.Attributes;

public class DocumentChunksExtractionCursor extends AElementsScannerCursor<EditorTag> implements ICursorWithResult<List<TextChunk>> {
	
	private final List<TextChunk> textChunks;
	
	public DocumentChunksExtractionCursor() {
		super();
		this.textChunks = new ArrayList<TextChunk>();
	}
		
	protected void applyTag(EditorTag tag) {
		textChunks.add(new TextChunk(tag.getID(), 
									 tag.getContent(), 
									 (tag.getStyle() != null) ? tag.getStyle().toString() : "", 
									 (tag.getAuthor() != null) ? tag.getAuthor().toString() : "", 
									 tag.isReserved()));
	}
	
	public List<TextChunk> getResult() {
		return textChunks;
	}

	@Override
	protected EditorTag createTag(String tagName, Attributes attrs) throws IllegalArgumentException {
		// FIXME: must use static method or factory
		EditorTag newTag = new EditorTag();
		newTag.initFromElement(tagName, attrs);
		return newTag;
	}
 

}
