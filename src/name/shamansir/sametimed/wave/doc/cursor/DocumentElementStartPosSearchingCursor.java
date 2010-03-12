package name.shamansir.sametimed.wave.doc.cursor;


import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;

public class DocumentElementStartPosSearchingCursor implements
    ICursorWithResult<Integer> {
    
    private final int searchPos;  // in chars
    private final boolean returnChars;
    
    private int elmsPassed = 0; // TODO: make atomic
    private int charsPassed = 0; // TODO: make atomic
    private int lastTagStart = 0; // TODO: make atomic
    private int foundPos = -1; // TODO: make atomic // in chars or in elements, related to returnChars
    
    private boolean found = false;

	public DocumentElementStartPosSearchingCursor(int pos, boolean returnChars) {
		this.searchPos = pos; // in chars
		this.returnChars = returnChars;
	}
	
	public DocumentElementStartPosSearchingCursor(int pos) {
	    this(pos, false);
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
	    if (!found) {
	        elmsPassed++;
	        if (returnChars) {
                if (charsPassed > searchPos) {
                    foundPos = lastTagStart;
                    found = true;
                } else if (charsPassed == searchPos) {
                    foundPos = charsPassed;
                    found = true;
                }
	        }
	    }
	}

	@Override
	public void elementStart(String type, Attributes attrs) {
	    if (!found) {
    		if ((!returnChars) && (charsPassed >= searchPos)) {
                foundPos = lastTagStart;
                found = true;    		    
    		}
            lastTagStart = returnChars ? charsPassed : elmsPassed;
            elmsPassed++;    		
	    }
	}

    @Override
    public void annotationBoundary(AnnotationBoundaryMap map) { }

}
