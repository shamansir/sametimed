package name.shamansir.sametimed.wave.doc.cursor;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.doc.sequencing.AbstractOperatingCursor;

import org.waveprotocol.wave.model.document.operation.Attributes;

public class DocumentElementDeletionCursor extends AbstractOperatingCursor {
    
    private final TagID tagID; // TODO: may be faster (but not correct) to compare by string?
    
    private boolean deleteCurrentTag = false; // FIXME: make atomic    
	
	public DocumentElementDeletionCursor(TagID tagID) {
        this.tagID = tagID;
	}
	
    @Override
    public void elementStart(String type, Attributes attrs) {
        if (AbstractDocumentTag.extractTagID(attrs).equals(tagID)) {
            deleteCurrentTag = true;
            docBuilder.deleteElementStart(type, attrs);
        } else {
            deleteCurrentTag = false;
            docBuilder.retainElementStart();
        }       
    }	

	@Override
	public void characters(String chars) {
        if (deleteCurrentTag) {
            docBuilder.deleteCharacters(chars);
        } else {
            docBuilder.retainCharacters(chars.length());
        }
	}

	@Override
	public void elementEnd() {
        if (deleteCurrentTag) {
            docBuilder.deleteElementEnd();
            detach();
        } else {
            docBuilder.retainElementEnd();
        }		
	}

}
