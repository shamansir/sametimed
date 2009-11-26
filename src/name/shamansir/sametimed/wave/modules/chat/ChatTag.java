package name.shamansir.sametimed.wave.modules.chat;

import name.shamansir.sametimed.wave.module.AbstractModuleTag;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.ImmutableMap;

public class ChatTag extends AbstractModuleTag {
	
	public static final String AUTHOR_ATTR_NAME = "by";
	public static final String TAG_NAME = "line";
	
	private ParticipantId author;
	
	public ChatTag(ParticipantId author, String content) {
		super(TAG_NAME);
		this.setAuthor(author);
		this.setContent(content);
	}
	
	public ChatTag(String authorName, String content) {
		this(new ParticipantId(authorName), content);
	}	
	
	public ChatTag() {
		this((ParticipantId)null, "");
	}	

	public void setAuthor(ParticipantId author) {
		this.author = author;
		setAttribute(AUTHOR_ATTR_NAME, (author != null) ? author.toString() : "-");
	}
	
	public void setAuthor(String authorName) {
		this.author = new ParticipantId(authorName);
		setAttribute(AUTHOR_ATTR_NAME, authorName);
	}	

	public ParticipantId getAuthor() {
		return author;
	}

	@Override
	protected boolean checkAttributes(Attributes attrs) {
		return attrs.containsKey(AUTHOR_ATTR_NAME);
	}

	@Override
	protected boolean checkTagName(String tagName) {
		return tagName.equals(TAG_NAME);
	}

	@Override
	protected AttributesImpl compileAttributes() {
		return new AttributesImpl(ImmutableMap.of(AUTHOR_ATTR_NAME, author.getAddress()));
	}

	@Override
	protected void initAttributes(Attributes attrs) {
		setAuthor(attrs.get(AUTHOR_ATTR_NAME));
	}

	/*
	protected static DocOpBuilder createTagFor(DocOpBuilder docOp,
			ParticipantId author, String text) {
		docOp.elementStart(TAG_NAME, new AttributesImpl(
				ImmutableMap.of(AUTHOR_ATTR_NAME, author.getAddress())));
		docOp.characters(text);
		docOp.elementEnd();
		return docOp;
	} */
	
}
