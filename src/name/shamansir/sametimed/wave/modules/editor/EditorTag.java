package name.shamansir.sametimed.wave.modules.editor;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.modules.editor.util.TextStyle;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.ImmutableMap;

// TODO: if editor will be tree-based, so this tag, may be, will
//       represent several tags or some EditorTagParser

public class EditorTag extends AbstractDocumentTag {
	
	public final static String TAG_NAME = "chunk";
	public final static String RESERVED_ATTR_NAME = "reserved";
	public final static String STYLE_ATTR_NAME = "style";
	
	private boolean isReserved;
	private TextStyle style = null;

	public EditorTag(TagID id, ParticipantId author, String content, TextStyle style, boolean isReserved) {
		super(id, TAG_NAME, author);
		setReserved(isReserved);
		setStyle(style);
		setContent(content);
	}
	
	public EditorTag(TagID id, String author, String content, String style, boolean isReserved) {
		this(id, parseAuthorAttr(author), content, TextStyle.fromString(style), isReserved);
	}	
	
	public EditorTag(TagID id, ParticipantId author, String content, boolean isReserved) {
		this(id, author, content, null, isReserved);
	}	
	
	public EditorTag(TagID id, String author, String content, boolean isReserved) {
		this(id, parseAuthorAttr(author), content, null, isReserved);
	}	
	
	public EditorTag(TagID id, String author, boolean isReserved) {
		this(id, parseAuthorAttr(author), "", null, isReserved);
	}		

	public EditorTag(TagID id) {
		this(id, (ParticipantId)null, "", false);
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
		
	
	@Override
	protected boolean checkAttributes(Attributes attrs) {
		return super.checkAttributes(attrs) &&
			   attrs.containsKey(RESERVED_ATTR_NAME) &&
			   attrs.containsKey(STYLE_ATTR_NAME);
	}

	@Override
	protected void initAttributes(Attributes attrs) {
		super.initAttributes(attrs);
		setReserved(parseReservedAttr(attrs.get(RESERVED_ATTR_NAME)));
		setStyle(parseStyleAttr(attrs.get(STYLE_ATTR_NAME)));
	}

	@Override
	protected ImmutableMap<String, String> compileAttributes() {
		return new ImmutableMap.Builder<String, String>()
				.putAll(super.compileAttributes())
				.put(RESERVED_ATTR_NAME, reservedAttr(isReserved))
				.put(STYLE_ATTR_NAME, styleAttr(style))
				.build();
	}
	
}
