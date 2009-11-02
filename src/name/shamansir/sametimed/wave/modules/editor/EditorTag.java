package name.shamansir.sametimed.wave.modules.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import name.shamansir.sametimed.wave.doc.ADocumentTag;
import name.shamansir.sametimed.wave.modules.editor.util.TextStyle;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.ImmutableMap;

// TODO: if editor will be tree-based, so this tag, may be, will
//       represent several tags or some EditorTagParser

public class EditorTag extends ADocumentTag {
	
	private final static String TAG_NAME = "chunk";
	private final static String AUTHORS_ATTR_NAME = "authors"; // FIXME: just one author for a chunk
	private final static String RESERVED_ATTR_NAME = "reserved";
	private final static String STYLE_ATTR_NAME = "style";
	
	private List<ParticipantId> authors;
	private boolean isReserved;
	private TextStyle style = null;

	public EditorTag(List<ParticipantId> authors, boolean isReserved, TextStyle style, String content) {
		super(TAG_NAME);
		setAuthors(authors);
		setReserved(isReserved);
		setStyle(style);
		setContent(content);
	}
	
	public EditorTag(String authors, boolean isReserved, String style, String content) {
		this(parseAuthors(authors), isReserved, TextStyle.fromString(style), content);
	}	
	
	public EditorTag(List<ParticipantId> authors, boolean isReserved, String content) {
		this(authors, isReserved, null, content);
	}	
	
	public EditorTag(String authors, boolean isReserved, String content) {
		this(parseAuthors(authors), isReserved, null, content);
	}	
	
	public EditorTag(String authors, boolean isReserved) {
		this(parseAuthors(authors), isReserved, null, "");
	}		

	public List<ParticipantId> getAuthors() {
		return authors;
	}

	public void setAuthors(List<ParticipantId> authors) {
		this.authors = authors;
		setAttribute(AUTHORS_ATTR_NAME, compileAuthorsList(authors));
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
	
	private static List<ParticipantId> parseAuthors(String authorsList) {		
		String[] authors = authorsList.split(",");
		List<ParticipantId> resultList = new ArrayList<ParticipantId>();
		for (String author: authors) {
			resultList.add(new ParticipantId(author));
		}
		return resultList;
	}
	
	private static String compileAuthorsList(List<ParticipantId> authorsList) {
		String resultStr = "";
		for (Iterator<ParticipantId> iter = authorsList.iterator(); iter.hasNext(); ) {
			resultStr += iter.next().getAddress();
			if (iter.hasNext()) resultStr += ",";
		}
		return resultStr;
	}
	
	@Override
	protected boolean checkTagName(String tagName) {
		return tagName.equals(TAG_NAME);
	}	
	
	@Override
	protected boolean checkAttributes(Attributes attrs) {
		return attrs.containsKey(AUTHORS_ATTR_NAME) &&
			   attrs.containsKey(RESERVED_ATTR_NAME) &&
			   attrs.containsKey(STYLE_ATTR_NAME);
	}

	@Override
	protected void initFrom(Attributes attrs) {
		setAuthors(parseAuthors(attrs.get(AUTHORS_ATTR_NAME)));
		setReserved(parseReservedAttr(attrs.get(RESERVED_ATTR_NAME)));
		setStyle(parseStyleAttr(attrs.get(STYLE_ATTR_NAME)));
	}

	@Override
	protected AttributesImpl compileAttributes() {
		return new AttributesImpl(
				ImmutableMap.of(AUTHORS_ATTR_NAME, compileAuthorsList(authors),
				                RESERVED_ATTR_NAME, reservedAttr(isReserved),
				                STYLE_ATTR_NAME, styleAttr(style)));
	}
	
}
