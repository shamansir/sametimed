package name.shamansir.sametimed.wave.doc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.ImmutableMap;

// TODO: use Atomic types? 
public abstract class AbstractDocumentTag {
	
	private final static String DEFAULT_CONTENT = "-empty-";
	public final static String UNKNOWN_AUTHOR = "?";
	
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
	
    protected AbstractDocumentTag(TagID id, String tagName, String author, String content) {
        this(id, tagName, parseAuthorAttr(author), new HashMap<String, String>(), content);
    }
    
    public AbstractDocumentTag(TagID id, String tagName,
            ParticipantId author, String content) {
        this(id, tagName, author, new HashMap<String, String>(), content);
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
		return (author != null) ? author.getAddress() : UNKNOWN_AUTHOR;
	}
	
	protected static ParticipantId parseAuthorAttr(String author) {
		return (!author.equals(UNKNOWN_AUTHOR)) ? new ParticipantId(author) : null;
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
	
	protected void initFromElement(String name, Attributes attrs, String content) throws IllegalArgumentException {
		if (checkTagName(name)) {
			
			if (!(attrs.containsKey(ID_ATTR_NAME) && attrs.containsKey(AUTHOR_ATTR_NAME) && checkAttributes(attrs))) {
				throw new IllegalArgumentException(
						"There are not correct arguments in the tag " + name + " " + attrs);
			}
			
			setID(parseIDAttr(attrs.get(ID_ATTR_NAME)));
			setAuthor(parseAuthorAttr(attrs.get(AUTHOR_ATTR_NAME)));
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
	
	public DocOpBuilder build(DocOpBuilder docOp) {
		AttributesImpl attrs = new AttributesImpl(
					new ImmutableMap.Builder<String, String>()
						.put(ID_ATTR_NAME, idAttr(id))
						.put(AUTHOR_ATTR_NAME, authorAttr(author))
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
	
	protected abstract boolean checkAttributes(Attributes attrs);
	
	protected abstract ImmutableMap<String, String> compileAttributes();

	protected abstract void initAttributes(Attributes attrs);
	
	public static AbstractDocumentTag createEmpty(String tagID, String name, Attributes attrs, String content) {
		EmptyTag emptyTag = new EmptyTag(parseIDAttr(tagID));
		emptyTag.initFromElement(name, attrs, content);
		return emptyTag;
	}
	
    public static AbstractDocumentTag createNoAttrs(String tagID, String name, String author, String content) {
        NoAttrsTag noAttrsTag = new NoAttrsTag(parseIDAttr(tagID), name, author, content);
        return noAttrsTag;
    }	
    
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AbstractDocumentTag)) return false;
        AbstractDocumentTag otherTag = (AbstractDocumentTag) other;
        return 
          ((id == null) ? (otherTag.id == null) : otherTag.id.equals(id)) && 
          ((name == null) ? (otherTag.name == null) : otherTag.name.equals(name)) &&
          ((author == null) ? (otherTag.author == null) : otherTag.author.equals(author)) &&
          ((content == null) ? (otherTag.content == null) : otherTag.content.equals(content))/* &&
          otherTag.attributes.equals(attributes) */;
          // FIXME: is checking attributes for equality required? 
        
    }
    
    @Override
    public int hashCode() { 
        int hash = 1;
        hash = hash * 31 + (id == null ? 0 : id.hashCode());
        hash = hash * 31 + (name == null ? 0 : name.hashCode());
        hash = hash * 31 + (author == null ? 0 : author.hashCode());
        hash = hash * 31 + (content == null ? 0 : content.hashCode());
        // hash = hash * 31 + (attributes == null ? 0 : attributes.hashCode());
        // FIXME: is getting hash from attributes required?
        return hash;
    }
		
}
