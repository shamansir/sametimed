package name.shamansir.sametimed.wave.modules.editor;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.modules.editor.util.TextStyle;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.ImmutableMap;

// TODO: if editor will be tree-based, so this tag, may be, will
//       represent several tags or some EditorTagParser

public class EditorTag extends AbstractDocumentTag {
	
	public final static String TAG_NAME = "chunk";
	public final static String AUTHOR_ATTR_NAME = "author"; // FIXME: just one author for a chunk
	public final static String RESERVED_ATTR_NAME = "reserved";
	public final static String STYLE_ATTR_NAME = "style";
	
	private ParticipantId author;
	private boolean isReserved;
	private TextStyle style = null;

	public EditorTag(int id, ParticipantId author, String content, TextStyle style, boolean isReserved) {
		super(id, TAG_NAME);
		setAuthor(author);
		setReserved(isReserved);
		setStyle(style);
		setContent(content);
	}
	
	public EditorTag(int id, String author, String content, String style, boolean isReserved) {
		this(id, parseAuthorAttr(author), content, TextStyle.fromString(style), isReserved);
	}	
	
	public EditorTag(int id, ParticipantId author, String content, boolean isReserved) {
		this(id, author, content, null, isReserved);
	}	
	
	public EditorTag(int id, String author, String content, boolean isReserved) {
		this(id, parseAuthorAttr(author), content, null, isReserved);
	}	
	
	public EditorTag(int id, String author, boolean isReserved) {
		this(id, parseAuthorAttr(author), "", null, isReserved);
	}		

	public EditorTag(int id) {
		this(id, (ParticipantId)null, "", false);
	}
	
	public ParticipantId getAuthor() {
		return author;
	}

	public void setAuthor(ParticipantId author) {
		this.author = author;
		setAttribute(AUTHOR_ATTR_NAME, authorAttr(author));
	}

	public boolean isReserved() {
		return isReserved;
	}
	
	public void setReserved(boolean isReserved) {
		this.isReserved = isReserved;
		setAttribute(RESERVED_ATTR_NAME, reservedAttr(isReserved));
	}

	public TextStyle getStyle() {
		return style;
	}

	public void setStyle(TextStyle style) {
		setAttribute(STYLE_ATTR_NAME, styleAttr(style));
	}
	
	private static String reservedAttr(boolean isReserved) {
		return isReserved ? "reserved" : "";
	}
	
	private static boolean parseReservedAttr(String reserved) {
		return reserved.equals("reserved");
	}	
	
	private static String styleAttr(TextStyle style) {
		return (style != null) ? style.toString() : "";
	}	
	
	private static TextStyle parseStyleAttr(String styleString) {
		return TextStyle.fromString(styleString);
	}
	
	private static String authorAttr(ParticipantId author) {
		return (author != null) ? author.getAddress() : "?";
	}
	
	private static ParticipantId parseAuthorAttr(String author) {
		return (!author.equals("?")) ? new ParticipantId(author) : null;
	}
		
	
	@Override
	protected boolean checkAttributes(Attributes attrs) {
		return attrs.containsKey(AUTHOR_ATTR_NAME) &&
			   attrs.containsKey(RESERVED_ATTR_NAME) &&
			   attrs.containsKey(STYLE_ATTR_NAME);
	}

	@Override
	protected void initAttributes(Attributes attrs) {
		setAuthor(parseAuthorAttr(attrs.get(AUTHOR_ATTR_NAME)));
		setReserved(parseReservedAttr(attrs.get(RESERVED_ATTR_NAME)));
		setStyle(parseStyleAttr(attrs.get(STYLE_ATTR_NAME)));
	}

	@Override
	protected ImmutableMap<String, String> compileAttributes() {
		return ImmutableMap.of(
				AUTHOR_ATTR_NAME, authorAttr(author),
                RESERVED_ATTR_NAME, reservedAttr(isReserved),
                STYLE_ATTR_NAME, styleAttr(style));
	}
	
}
