package name.shamansir.sametimed.wave.modules.chat.doc.cursor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.waveprotocol.wave.model.document.operation.Attributes;

import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.doc.cursor.AbstractElementsScannerCursor;
import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;
import name.shamansir.sametimed.wave.model.base.atom.ChatLine;
import name.shamansir.sametimed.wave.modules.chat.ChatTag;

public class ChatLinesExtractionCursor extends AbstractElementsScannerCursor<ChatTag> implements ICursorWithResult<List<ChatLine>> {
	
	private final Queue<ChatLine> chatLines;
	
	public ChatLinesExtractionCursor() {
		super();
		this.chatLines = new ConcurrentLinkedQueue<ChatLine>();
	}
		
	protected void applyTag(ChatTag tag) {
		chatLines.add(new ChatLine(tag.getAuthor().toString(), tag.getContent()));
	}
	
	public List<ChatLine> getResult() { // FIXME: do not create a new list?
		return Collections.unmodifiableList(new LinkedList<ChatLine>(chatLines));
	}

	@Override
	protected ChatTag createTag(TagID id, String tagName, Attributes attrs)
			throws IllegalArgumentException {
		// FIXME: must use static method or factory
		ChatTag newTag = new ChatTag(id);
		newTag.initFromElement(tagName, attrs);
		return newTag;
	}

}
