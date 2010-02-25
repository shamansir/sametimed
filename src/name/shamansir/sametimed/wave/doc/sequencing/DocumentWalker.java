package name.shamansir.sametimed.wave.doc.sequencing;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;

public class DocumentWalker extends DocumentState implements IDocumentWalker {
    
	private int curPos = 0; // TODO: make atomic
	private int curSrcPos = 0; // TODO: make atomic // pos in the unchanged source
	private int curPosElms = 0; // TODO: make atomic
	private int curPosChars = 0; // TODO: make atomic
	private int curPosTags = 1; // TODO: make atomic // staring from 1
	
	public DocumentWalker(BufferedDocOp sourceDoc) {
	    super(sourceDoc);
	    collectDocumentData();
	    // super(sourceDoc); 
	    // collectDocumentData(this); // collectData walks through document as a result,
	                                  // but it is not required
        // resetPosition();
	}
	
	public void foundElmStart() {
	    super.addElmStart(curPos);
		stepElmFwd(false);
	}
	
	public void foundElmEnd() {
	    super.addElmEnd(curPos);
		stepElmFwd(true);
	}		
	
	public void foundChars(int howMany) {
	    super.addElmChars(curPos, howMany);
		stepCharsFwd(howMany);
	}
	
	@Override
	public void deleteElmStart() {
	    super.deleteElmStart(curPos);
	    curSrcPos++;
	}
	
	@Override
	public void deleteElmEnd() {
	    super.deleteElmStart(curPos);
        curPosTags++; curSrcPos++;
    }	
	
	@Override
	public void deleteElmChars(int howMany) {
	    super.deleteElmChars(curPos, howMany);
	    curSrcPos++;
    }	
	
	@Override
	public void resetPosition() {
		curPos = curPosElms = curSrcPos = curPosChars = 0;
		curPosTags = 1;
	}
	
	@Override
    public void clear() {
	    super.clear();
		curPos = curPosElms = curSrcPos = curPosChars = 0;
	    curPosTags = 1;
	}
	
	@Override
	public int curPos() {
		return curPosChars;
	}
	
	@Override
	public int curPosElms() {
	    return curPosElms;
	}
	
	@Override
    public int curPosTags() {
        return curPosTags;
    }	

	@Override
	// returns required step in elements 
	public int scrollToEnd() {
		int size = data.size();
		int prevPos = curPosElms;
		while (curPos < size) {
			int value = data.get(curPos);
			if (value >= 0) {
			    stepCharsFwd(value);
			} else {
			    stepElmFwd(value == DocumentState.ELM_END_CODE);
			}
		}
		return curPosElms - prevPos;
	}
	
    @Override	
	// returns required step in elements
	public int scrollTo(int chars) {
	    if (chars > sizeInChars) return (sizeInChars - chars);
	    if (chars < curPosChars) return (chars - curPosChars);
	    
		int prevPos = curPosElms;
		int size = data.size();		
		
		while ((curPosChars < chars) && (curPos < size)) {
			int value = data.get(curPos); 
            if (value >= 0) {
                stepCharsFwd(value);
            } else {
                stepElmFwd(value == DocumentState.ELM_END_CODE);
            }		
		}
		if ((curPosChars == chars) && (curPos < size) 
			&& (data.get(curPos) == DocumentState.ELM_END_CODE)) {
			stepElmFwd(true);
		}
		
		return curPosElms - prevPos;
	}

	protected void stepElmFwd(boolean isEnd) {
		curPos++; curSrcPos++; curPosElms++; 
		if (isEnd) curPosTags++;
	}

	protected void stepCharsFwd(int chars) {
	    if (chars > 0) {
    		curPos++; curSrcPos++;
    		curPosElms += chars;
    		curPosChars += chars;
	    }
	}

	@Override
    /**
     * @param source
     * @param cursor
     */
    public void walkWithCursor(AbstractOperatingCursor cursor) {
        // FIXME: assert that source conforms with the state?
        int size = data.size();
        BufferedDocOp source = getSource();
        while (cursor.doContinue() && (curSrcPos < size)) {
            int value = data.get(curPos);
            if (value > 0) {
                cursor.characters(source.getCharactersString(curSrcPos)); 
            } else if (value == DocumentState.ELM_START_CODE) {
                cursor.elementStart(source.getElementStartTag(curSrcPos), 
                                    source.getElementStartAttributes(curSrcPos));
            } else cursor.elementEnd();
        }
    }
		
}
