package name.shamansir.sametimed.wave.doc.cursor;

import java.util.logging.Logger;

import name.shamansir.sametimed.wave.doc.ADocumentTag;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;

public abstract class AElementsScannerCursor<TagType extends ADocumentTag> implements DocInitializationCursor {
	
	private static final Logger LOG = Logger.getLogger(AElementsScannerCursor.class.getName());
	
	private boolean gotEnd;
	private boolean gotCharacters;
	private boolean skipElement; 
	
	private TagType currentTag;
	
	public AElementsScannerCursor() {			
		this.currentTag = null;
		
		this.skipElement = false;
		this.gotCharacters = false;	// used to determine the flow order
		this.gotEnd = false; // used to determine the flow order
		
	}
	
	protected abstract TagType createTag(String tagName, Attributes attrs) throws IllegalArgumentException;	
	protected abstract void applyTag(TagType tag);
	
	@Override
	public void elementStart(String type, Attributes attrs) {
		this.skipElement = false;
		this.gotCharacters = false;		
		this.gotEnd = false;
		
		try {
			currentTag = createTag(type, attrs);
			if (currentTag == null) this.skipElement = true;
		} catch (IllegalArgumentException iae) {
			LOG.warning("exception thrown while parsing element " + type +
					" with attrs " + attrs + ": " + iae.getMessage());
			this.skipElement = true;
		}
	}	

	@Override
	public void characters(String chars) {
		if (!this.skipElement) {
			currentTag.setContent(chars);			
			if (this.gotEnd) applyTag(currentTag);			
			this.gotCharacters = true;
		}
	}

	@Override
	public void elementEnd() {
		if (!this.skipElement) {
			if (this.gotCharacters) applyTag(currentTag);
			this.gotEnd = true;
		}
	}
	
	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) {
		
	} 
	

}
