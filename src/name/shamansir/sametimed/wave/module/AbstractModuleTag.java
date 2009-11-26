package name.shamansir.sametimed.wave.module;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;

// TODO: use Atomic types? 
public abstract class AbstractModuleTag {
	
	private final static String DEFAULT_CONTENT = "-empty-";	
	private String name;
	// TODO: private Attributes attributes;
	private Map<String, String> attributes;
	private String content;
	
	protected AbstractModuleTag(String name) {
		this.name = name;
		this.attributes = new HashMap<String, String>();
		this.content = DEFAULT_CONTENT;
		// this.attributes = AttributesImpl.EMPTY_MAP;
	}
	
	protected AbstractModuleTag(String name, Attributes attrs) {
		this.name = name;
		this.attributes = loadAttributes(attrs);
		this.content = DEFAULT_CONTENT;
	}
	
	protected AbstractModuleTag(String name, Attributes attrs, String content) {
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
			if (!checkAttributes(attrs)) {
				throw new IllegalArgumentException(
						"There are not correct arguments in the tag " + name + " " + attrs);
			}			
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
	
	public DocOpBuilder createTagFor(DocOpBuilder docOp) {
		docOp.elementStart(name, compileAttributes());
		docOp.characters(getContent());
		docOp.elementEnd();
		return docOp;
	}
	
	protected abstract boolean checkTagName(String tagName);
	protected abstract boolean checkAttributes(Attributes attrs);
	
	protected abstract void initAttributes(Attributes attrs);
	protected abstract AttributesImpl compileAttributes();
	
}
