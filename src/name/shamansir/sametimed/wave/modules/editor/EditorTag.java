package name.shamansir.sametimed.wave.modules.editor;

import name.shamansir.sametimed.wave.doc.AbstractModuleTag;
import name.shamansir.sametimed.wave.modules.editor.util.TextStyle;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.ImmutableMap;

// TODO: if editor will be tree-based, so this tag, may be, will
//       represent several tags or some EditorTagParser

public class EditorTag extends AbstractModuleTag {
	
	public final static String TAG_NAME = "chunk";
	public final static String ID_ATTR_NAME = "id";
	public final static String AUTHOR_ATTR_NAME = "author"; // FIXME: just one author for a chunk
	public final static String RESERVED_ATTR_NAME = "reserved";
	public final static String STYLE_ATTR_NAME = "style";
	
	private int id;
	private ParticipantId author;
	private boolean isReserved;
	private TextStyle style = null;

	public EditorTag(int id, ParticipantId author, String content, TextStyle style, boolean isReserved) {
		super(TAG_NAME);
		setID(id);
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

	public EditorTag() {
		this(0, (ParticipantId)null, "", false);
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id = id; 
		setAttribute(ID_ATTR_NAME, idAttr(id));
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
	
	private static String idAttr(int id) {
		return String.valueOf(id);
	}
	
	private static int parseIDAttr(String idString) {
		return Integer.valueOf(idString);
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
	protected boolean checkTagName(String tagName) {
		return tagName.equals(TAG_NAME);
	}	
	
	@Override
	protected boolean checkAttributes(Attributes attrs) {
		return attrs.containsKey(ID_ATTR_NAME) &&
			   attrs.containsKey(AUTHOR_ATTR_NAME) &&
			   attrs.containsKey(RESERVED_ATTR_NAME) &&
			   attrs.containsKey(STYLE_ATTR_NAME);
	}

	@Override
	protected void initAttributes(Attributes attrs) {
		setID(parseIDAttr(attrs.get(ID_ATTR_NAME)));
		setAuthor(parseAuthorAttr(attrs.get(AUTHOR_ATTR_NAME)));
		setReserved(parseReservedAttr(attrs.get(RESERVED_ATTR_NAME)));
		setStyle(parseStyleAttr(attrs.get(STYLE_ATTR_NAME)));
	}

	@Override
	protected AttributesImpl compileAttributes() {
		return new AttributesImpl(
				ImmutableMap.of(ID_ATTR_NAME, idAttr(id),
							    AUTHOR_ATTR_NAME, authorAttr(author),
				                RESERVED_ATTR_NAME, reservedAttr(isReserved),
				                STYLE_ATTR_NAME, styleAttr(style)));
	}
	
}
