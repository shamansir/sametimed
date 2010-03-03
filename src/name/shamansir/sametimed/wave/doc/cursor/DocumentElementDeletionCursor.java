package name.shamansir.sametimed.wave.doc.cursor;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.doc.sequencing.AbstractOperatingCursor;

import org.waveprotocol.wave.model.document.operation.Attributes;

public class DocumentElementDeletionCursor extends AbstractOperatingCursor {
    
    private final TagID tagID; // TODO: may be faster (but not correct) to compare by string?
    
    private boolean insideElmToDelete = false; // FIXME: make atomic    
	
	public DocumentElementDeletionCursor(TagID tagID) {
        this.tagID = tagID;
	}
	
    @Override
    public void elementStart(String type, Attributes attrs) {
        if (AbstractDocumentTag.extractTagID(attrs).equals(tagID)) {
            insideElmToDelete = true;
            docBuilder.deleteElementStart(type, attrs);
        } else {
            insideElmToDelete = false;
            docBuilder.retainElementStart();
        }       
    }	

	@Override
	public void characters(String chars) {
        if (insideElmToDelete) {
            docBuilder.deleteCharacters(chars);
        } else {
            docBuilder.retainCharacters(chars.length());
        }
	}

	@Override
	public void elementEnd() {
        if (insideElmToDelete) {
            docBuilder.deleteElementEnd();
            detach();
        } else {
            docBuilder.retainElementEnd();
        }		
	}

	/*
	
	@Override
	public void elementStart(String key, Attributes attrs) {
		if (attrs.get(AbstractDocumentTag.ID_ATTR_NAME).equals(elmToDeleteID)) {
			insideElmToDelete.set(true);
			elmDeletion.deleteElementStart(key, attrs);			
		} else {
			insideElmToDelete.set(false);
			elmDeletion.retain(1);
		}
	}	

	@Override
	public void characters(String s) {
		if (insideElmToDelete.get()) {
			elmDeletion.deleteCharacters(s);
		} else {
			elmDeletion.retain(s.length());
		}
	}

	@Override
	public void elementEnd() {
		if (insideElmToDelete.get()) {
			elmDeletion.deleteElementEnd();
		} else {
			elmDeletion.retain(1);
		}
	}

	@Override
	public void annotationBoundary(AnnotationBoundaryMap map) {
	}
	
	public BufferedDocOp getResult() {
		return elmDeletion.build();
	} */

}
