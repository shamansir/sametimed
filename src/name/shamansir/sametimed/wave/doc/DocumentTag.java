package name.shamansir.sametimed.wave.doc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.waveprotocol.wave.model.document.operation.Attributes;

public class DocumentTag {
	
	private final static String DEFAULT_CONTENT = "-empty-";
	
	private String name;
	// TODO: private Attributes attributes;
	private Map<String, String> attributes;
	private String content;
	
	protected DocumentTag(String name) {
		this.name = name;
		this.attributes = new HashMap<String, String>();
		this.content = DEFAULT_CONTENT;
		// this.attributes = AttributesImpl.EMPTY_MAP;
	}
	
	protected DocumentTag(String name, Attributes attrs) {
		this.name = name;
		this.attributes = loadAttributes(attrs);
		this.content = DEFAULT_CONTENT;
	}
	
	protected DocumentTag(String name, Attributes attrs, String content) {
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
	
	public DocumentTag initFromElement(String name, Attributes attrs) throws IllegalArgumentException {
		return initFromElement(name, attrs, DEFAULT_CONTENT);
	}
	
	public DocumentTag initFromElement(String name, Attributes attrs, String content) throws IllegalArgumentException {
		this.name = name;
		this.attributes = loadAttributes(attrs);	
		this.content = content;
		return this;
	}	
	
	private Map<String, String> loadAttributes(Attributes attrs) {
		Map<String, String> attrsMap = new HashMap<String, String>();
		// FIXME: possibly, there really is an easier way
		for (Entry<String, String> attrEntry: attrs.entrySet()) {
			attrsMap.put(attrEntry.getKey(), attrEntry.getValue());
		}
		return attrsMap; 
	}
	
}
