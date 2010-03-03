package name.shamansir.sametimed.wave.doc.cursor;

import org.waveprotocol.wave.model.document.operation.Attributes;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.sequencing.AbstractOperatingCursorWithResult;

public class DocumentElementCuttingByPosCursor extends
		AbstractOperatingCursorWithResult<AbstractDocumentTag> {

    private final int posBeforeTag; // FIXME: make atomic
    
    private boolean deleteCurrentTag = false;  // FIXME: make atomic
    private boolean deleted = false;  // FIXME: make atomic
    
    private String tagName; // FIXME: make atomic
    private Attributes attrs; // FIXME: make atomic
    private String content; // FIXME: make atomic    
    
	public DocumentElementCuttingByPosCursor(int position) {
		posBeforeTag = position;
	}
	
    @Override
    public void elementStart(String type, Attributes attrs) {
        if (docWalker.curPos() >= posBeforeTag) {
            deleteCurrentTag = true;
            docBuilder.deleteElementStart(type, attrs);
            this.tagName = type; this.attrs = attrs;           
        } else {
            docBuilder.retainElementStart();            
        }
    }	

	@Override
	public void characters(String chars) {
        if (deleteCurrentTag) {
            docBuilder.deleteCharacters(chars);
            this.content = chars;
        } else {
            docBuilder.retainCharacters(chars.length());
        }	    
	}

	@Override
	public void elementEnd() {
        if (deleteCurrentTag) {
            docBuilder.deleteElementEnd();
            deleted = true;
            detach();
        } else {
            docBuilder.retainElementEnd();
        }
	}
	
    @Override
    public AbstractDocumentTag getResult() {
        return deleted ?
               makeResultTag(tagName, attrs, content) :
               null;
    }	
	
}
