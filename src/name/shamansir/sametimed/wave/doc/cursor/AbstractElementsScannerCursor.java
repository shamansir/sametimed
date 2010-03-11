package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicBoolean;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;

public abstract class AbstractElementsScannerCursor<TagType extends AbstractDocumentTag> implements DocInitializationCursor {
	
    private static final Log LOG = LogFactory.getLog(AbstractElementsScannerCursor.class);
	
	private AtomicBoolean gotEnd;
	private AtomicBoolean gotCharacters;
	private AtomicBoolean skipElement; 
	
	private TagType currentTag;
	
	public AbstractElementsScannerCursor() {			
		this.currentTag = null;
		
		this.skipElement = new AtomicBoolean(false);
		this.gotCharacters = new AtomicBoolean(false);	// used to determine the flow order
		this.gotEnd = new AtomicBoolean(false); // used to determine the flow order
		
	}
	
	protected abstract TagType createTag(TagID id, String tagName, Attributes attrs) throws IllegalArgumentException;	
	protected abstract void applyTag(TagType tag);
	
	@Override
	public void elementStart(String type, Attributes attrs) {
		this.skipElement.set(false);
		this.gotCharacters.set(false);		
		this.gotEnd.set(false);
		
		try {
			currentTag = createTag(
					TagID.valueOf(attrs.get(AbstractDocumentTag.ID_ATTR_NAME)), 
								  type, attrs);
			if (currentTag == null) this.skipElement.set(true);
		} catch (IllegalArgumentException iae) {
			LOG.warn("exception thrown while parsing element " + type +
					" with attrs " + attrs + ": " + iae.getMessage());
			this.skipElement.set(true);
		}
	}	

	@Override
	public void characters(String chars) {
		if (!this.skipElement.get()) {
			currentTag.setContent(chars);			
			if (this.gotEnd.get()) applyTag(currentTag);			
			this.gotCharacters.set(true);
		}
	}

	@Override
	public void elementEnd() {
		if (!this.skipElement.get()) {
			if (this.gotCharacters.get()) applyTag(currentTag);
			this.gotEnd.set(true);
		}
	}
	
	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) { } 
	

}
