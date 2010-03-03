package name.shamansir.sametimed.wave.doc.cursor;

import name.shamansir.sametimed.wave.doc.sequencing.AbstractOperatingCursor;

import org.waveprotocol.wave.model.document.operation.Attributes;

public class DocumentElementDeletionByPosCursor extends
		AbstractOperatingCursor {
    
    private final int posBeforeTag; // FIXME: make atomic
    
    private boolean deleteCurrentTag = false;  // FIXME: make atomic    

	public DocumentElementDeletionByPosCursor(int position) {
        posBeforeTag = position;
	}
	
    @Override
    public void elementStart(String type, Attributes attrs) {
        if (docWalker.curPos() >= posBeforeTag) {
            deleteCurrentTag = true;
            docBuilder.deleteElementStart(type, attrs);           
        } else {
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
