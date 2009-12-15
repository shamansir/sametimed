package name.shamansir.sametimed.wave.doc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.ImmutableMap;

// TODO: use Atomic types? 
public abstract class AbstractDocumentTag {
	
	private final static String DEFAULT_CONTENT = "-empty-";
	
	public final static String ID_ATTR_NAME = "id";	
	public final static String AUTHOR_ATTR_NAME = "by";
	
	private TagID id;	
	private final String name;
	private ParticipantId author;
	private Map<String, String> attributes;	
	private String content;
	
	protected AbstractDocumentTag(TagID id, String name, ParticipantId author) {
		this(id, name, author, new HashMap<String, String>(), DEFAULT_CONTENT);
	}
	
	protected AbstractDocumentTag(TagID id, String name, String author) {
		this(id, name, parseAuthorAttr(author), 
					new HashMap<String, String>(), DEFAULT_CONTENT);
	}	
	
	protected AbstractDocumentTag(TagID id, String name, Attributes attrs) {
		this(id, name, null, loadAttributes(attrs), DEFAULT_CONTENT);
	}
	
	protected AbstractDocumentTag(TagID id, String name, Attributes attrs, String content) {
		this(id, name, null, loadAttributes(attrs), content);
	}
	
	private AbstractDocumentTag(TagID id, String name, ParticipantId author, Map<String, String> attrs, String content) {
		this.name = name;
		this.attributes = attrs;
		this.setID(id);
		this.setAuthor(author);
		this.setContent(content);
	}		
	
	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}
	
	public TagID getID() {
		return this.id;
	}
	
	public void setID(TagID id) {
		this.id = id; 
		setAttribute(ID_ATTR_NAME, idAttr(id));
	}	
	
	private static String idAttr(TagID id) {
		return id.getValue();
	}
	
	private static TagID parseIDAttr(String idString) {
		return TagID.valueOf(idString);
	}
	
	public void setAuthor(ParticipantId author) {
		this.author = author;
		setAttribute(AUTHOR_ATTR_NAME, (author != null) ? author.toString() : "-");
	}
	
	public void setAuthor(String authorName) {
		this.author = new ParticipantId(authorName);
		setAttribute(AUTHOR_ATTR_NAME, authorAttr(author));
	}	

	public ParticipantId getAuthor() {
		return author;
	}
	
	protected static String authorAttr(ParticipantId author) {
		return (author != null) ? author.getAddress() : "?";
	}
	
	protected static ParticipantId parseAuthorAttr(String author) {
		return (!author.equals("?")) ? new ParticipantId(author) : null;
	}	
	
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	public String getAttribute(String name) {
		return attributes.get(name);
	}	
	
	public void setAttribute(String name, String value) {
		attributes.put(name, value);
	}
	
	public String getContent() {
		return content;
	}
	
	public void initFromElement(String name, Attributes attrs) throws IllegalArgumentException {
		initFromElement(name, attrs, null);
	}
	
	public void initFromElement(String name, Attributes attrs, String content) throws IllegalArgumentException {
		if (checkTagName(name)) {
			
			if (!(attrs.containsKey(ID_ATTR_NAME) && checkAttributes(attrs))) {
				throw new IllegalArgumentException(
						"There are not correct arguments in the tag " + name + " " + attrs);
			}
			
			setID(parseIDAttr(attrs.get(ID_ATTR_NAME)));			
			initAttributes(attrs);
			
			setContent((content != null) ? content : "");
		} else {
			throw new IllegalArgumentException(
					"Unsupported element type " + name);
		}
	}
	
	private static Map<String, String> loadAttributes(Attributes attrs) {
		if (attrs == null) return null;
		Map<String, String> attrsMap = new HashMap<String, String>();
		// FIXME: possibly, there really is an easier way
		for (Entry<String, String> attrEntry: attrs.entrySet()) {
			attrsMap.put(attrEntry.getKey(), attrEntry.getValue());
		}
		return attrsMap; 
	}
	
	public DocOpBuilder buildOperation(DocOpBuilder docOp) {
		AttributesImpl attrs = new AttributesImpl(
					new ImmutableMap.Builder<String, String>()
						.put(ID_ATTR_NAME, idAttr(id))
						.putAll(compileAttributes())
						.build()
				);
		docOp.elementStart(name, attrs);
		docOp.characters(getContent());
		docOp.elementEnd();
		return docOp;
	}
	
	protected boolean checkTagName(String tagName) {
		return tagName.equals(name);
	}	
	
	protected boolean checkAttributes(Attributes attrs) {
		return attrs.containsKey(AUTHOR_ATTR_NAME);
	}
	
	protected ImmutableMap<String, String> compileAttributes() {
		return ImmutableMap.of(AUTHOR_ATTR_NAME, author.getAddress());
	}

	protected void initAttributes(Attributes attrs) {
		setAuthor(parseAuthorAttr(attrs.get(AUTHOR_ATTR_NAME)));
	}
	
}
