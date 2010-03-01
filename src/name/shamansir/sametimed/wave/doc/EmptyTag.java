package name.shamansir.sametimed.wave.doc;

import org.waveprotocol.wave.model.document.operation.Attributes;

import com.google.common.collect.ImmutableMap;

public class EmptyTag extends AbstractDocumentTag {
    
    private final static String NAME_STUB = "%";

	public EmptyTag(TagID tagID) {
		super(tagID, NAME_STUB, UNKNOWN_AUTHOR);
	}
	
    public EmptyTag(TagID tagID, String tagName) {
        super(tagID, tagName, UNKNOWN_AUTHOR);
    }  

	@Override
	protected void initAttributes(Attributes attrs) { }
	
	@Override
	protected ImmutableMap<String, String> compileAttributes() {
		return ImmutableMap.of();
	}
	
	@Override
	protected boolean checkAttributes(Attributes attrs) {
		return true;
	}
	
    void useData(Attributes attrs, String content) throws IllegalArgumentException {
        initFromElement(getName(), attrs, content);       
    }	
	
	/*
	@Override 
	protected boolean checkTagName(String tagName) {
		return true;
	} */

}
