package name.shamansir.sametimed.wave.doc.cursor;


import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

// FIXME: create appropriate method in docWalker

@Deprecated
public class DocumentElementStartPosSearchingCursor implements
    ICursorWithResult<Integer> {
    
    private final int searchPos;  
    
    private int elmsPassed = 0;
    private int charsPassed = 0;
    private int lastTagStart = 0;
    private int foundPos = -1; // TODO: make atomic
    
    private boolean found = false;

	public DocumentElementStartPosSearchingCursor(int pos) {
		this.searchPos = pos;
	}

	@Override
	public Integer getResult() {
		return foundPos;
	}

	@Override
	public void characters(String chars) {
	    if (!found) {
	        charsPassed += chars.length();
	        elmsPassed += chars.length();	        
	    }
	}

	@Override
	public void elementEnd() { 
	    if (!found) elmsPassed++;
	}

	@Override
	public void elementStart(String type, Attributes attrs) {
	    if (!found) {
    		if (charsPassed >= searchPos) {
    		    foundPos = lastTagStart;
    		    found = true;
    		}
    		lastTagStart = elmsPassed;
    		elmsPassed++;
	    }
	}

    @Override
    public void annotationBoundary(AnnotationBoundaryMap map) { }

}
