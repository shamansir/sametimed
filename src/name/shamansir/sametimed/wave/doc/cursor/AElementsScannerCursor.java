package name.shamansir.sametimed.wave.doc.cursor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import name.shamansir.sametimed.wave.doc.ADocumentTag;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;

public abstract class AElementsScannerCursor<TagType extends ADocumentTag> implements DocInitializationCursor {
	
	private static final Logger LOG = Logger.getLogger(AElementsScannerCursor.class.getName());
	
	private AtomicBoolean gotEnd;
	private AtomicBoolean gotCharacters;
	private AtomicBoolean skipElement; 
	
	private TagType currentTag;
	
	public AElementsScannerCursor() {			
		this.currentTag = null;
		
		this.skipElement = new AtomicBoolean(false);
		this.gotCharacters = new AtomicBoolean(false);	// used to determine the flow order
		this.gotEnd = new AtomicBoolean(false); // used to determine the flow order
		
	}
	
	protected abstract TagType createTag(String tagName, Attributes attrs) throws IllegalArgumentException;	
	protected abstract void applyTag(TagType tag);
	
	@Override
	public void elementStart(String type, Attributes attrs) {
		this.skipElement.set(false);
		this.gotCharacters.set(false);		
		this.gotEnd.set(false);
		
		try {
			currentTag = createTag(type, attrs);
			if (currentTag == null) this.skipElement.set(true);
		} catch (IllegalArgumentException iae) {
			LOG.warning("exception thrown while parsing element " + type +
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
	public void annotationBoundary(AnnotationBoundaryMap map) {
		
	} 
	

}
