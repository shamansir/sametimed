package name.shamansir.sametimed.wave.doc;

import org.waveprotocol.wave.model.document.operation.Attributes;

import com.google.common.collect.ImmutableMap;

public class EmptyTag extends AbstractDocumentTag {

	public EmptyTag(TagID tagID) {
		super(tagID, "%", UNKNOWN_AUTHOR);
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
	
	@Override 
	protected boolean checkTagName(String tagName) {
		return true;
	}

}
