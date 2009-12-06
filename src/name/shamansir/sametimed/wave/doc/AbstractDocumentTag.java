package name.shamansir.sametimed.wave.doc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;

import com.google.common.collect.ImmutableMap;

// TODO: use Atomic types? 
public abstract class AbstractDocumentTag {
	
	private final static String DEFAULT_CONTENT = "-empty-";
	
	public final static String ID_ATTR_NAME = "id";	
	
	private /*final*/ int id; // FIXME: use hashes of timestamps instead?
	private final String name;
	// TODO: private Attributes attributes;
	private Map<String, String> attributes;
	private String content;
	
	protected AbstractDocumentTag(int id, String name) {
		this.id = id;
		this.name = name;
		this.attributes = new HashMap<String, String>();
		this.content = DEFAULT_CONTENT;
		// this.attributes = AttributesImpl.EMPTY_MAP;
	}
	
	protected AbstractDocumentTag(int id, String name, Attributes attrs) {
		this.id = id;
		this.name = name;
		this.attributes = loadAttributes(attrs);
		this.content = DEFAULT_CONTENT;
	}
	
	protected AbstractDocumentTag(int id, String name, Attributes attrs, String content) {
		this.id = id;
		this.name = name;
		this.attributes = loadAttributes(attrs);
		this.content = content;
	}	
	
	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id = id; 
		setAttribute(ID_ATTR_NAME, idAttr(id));
	}
	
	private static String idAttr(int id) {
		return String.valueOf(id);
	}
	
	private static int parseIDAttr(String idString) {
		return Integer.valueOf(idString);
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
	
	protected abstract boolean checkAttributes(Attributes attrs);
	
	protected abstract void initAttributes(Attributes attrs);
	protected abstract ImmutableMap<String, String> compileAttributes();
	
}
