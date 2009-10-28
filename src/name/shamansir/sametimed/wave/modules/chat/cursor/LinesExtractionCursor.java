package name.shamansir.sametimed.wave.modules.chat.cursor;

import java.util.ArrayList;
import java.util.List;

import org.waveprotocol.wave.model.document.operation.Attributes;

import name.shamansir.sametimed.wave.doc.cursor.AElementsScannerCursor;
import name.shamansir.sametimed.wave.model.base.atom.ChatLine;
import name.shamansir.sametimed.wave.modules.chat.ChatTag;

public class LinesExtractionCursor extends AElementsScannerCursor<ChatTag> {
	
	private final List<ChatLine> chatLines;
	
	public LinesExtractionCursor() {
		super();
		this.chatLines = new ArrayList<ChatLine>();
	}
		
	protected void applyTag(ChatTag tag) {
		chatLines.add(new ChatLine(tag.getAuthor().toString(), tag.getContent()));
	}
	
	public List<ChatLine> getExtractedLines() {
		return chatLines;
	}

	@Override
	protected ChatTag createTag(String tagName, Attributes attrs) throws IllegalArgumentException {
		// FIXME: must use static method or factory
		ChatTag newTag = new ChatTag();
		return (ChatTag)newTag.initFromElement(tagName, attrs);
	}

}
