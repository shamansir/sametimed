package name.shamansir.sametimed.wave.modules.chat;

import name.shamansir.sametimed.wave.doc.DocumentTag;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.wave.ParticipantId;

public class ChatTag extends DocumentTag {
	
	public static final String AUTHOR_ATTR_NAME = "by";
	public static final String LINE_TAG_NAME = "line";
	
	private ParticipantId author;
	
	public ChatTag(ParticipantId author, String content) {
		super(LINE_TAG_NAME);
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
	public DocumentTag initFromElement(String type, Attributes attrs, String characters) throws IllegalArgumentException {
		if (type.equals(LINE_TAG_NAME)) {
			if (!attrs
					.containsKey(AUTHOR_ATTR_NAME)) {
				throw new IllegalArgumentException(
						"Line element must have author");
			}
			return new ChatTag(attrs.get(AUTHOR_ATTR_NAME), characters);
		} else {
			throw new IllegalArgumentException(
					"Unsupported element type " + type);
		}
	}
	
	@Override
	public DocumentTag initFromElement(String type, Attributes attrs) throws IllegalArgumentException {
		if (type.equals(LINE_TAG_NAME)) {
			if (!attrs
					.containsKey(AUTHOR_ATTR_NAME)) {
				throw new IllegalArgumentException(
						"Line element must have author");
			}
			ChatTag newTag = new ChatTag();
			newTag.setAuthor(attrs.get(AUTHOR_ATTR_NAME));
			return newTag;
		} else {
			throw new IllegalArgumentException(
					"Unsupported element type " + type);
		}
	}
	

}
