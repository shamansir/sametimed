package name.shamansir.sametimed.wave.modules.chat;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.ImmutableMap;

public class ChatTag extends AbstractDocumentTag {
	
	public static final String TAG_NAME = "line";
	
	public ChatTag(TagID id, ParticipantId author, String content) {
		super(id, TAG_NAME, author);
		this.setContent(content);
	}
	
	public ChatTag(TagID id, String authorName, String content) {
		this(id, new ParticipantId(authorName), content);
	}	
	
	public ChatTag(TagID id) {
		this(id, (ParticipantId)null, "");
	}

	@Override
	protected boolean checkAttributes(Attributes attrs) {
		return true;
	}

	@Override
	protected ImmutableMap<String, String> compileAttributes() {
		return ImmutableMap.of();
	}

	@Override
	protected void initAttributes(Attributes attrs) { }
	
}
