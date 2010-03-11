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
	
	public boolean foundElmStart() {
	    boolean result = super.addElmStart(curPos);
		stepElmFwd(false, false);
		return result;
	}
	
	public boolean foundElmEnd() {
	    boolean result = super.addElmEnd(curPos);
		stepElmFwd(true, false);
        return result;		
	}		
	
	public boolean foundChars(int howMany) {
	    boolean result = super.addElmChars(curPos, howMany);
		stepCharsFwd(howMany, false);
        return result;		
	}
	
	@Override
	public boolean deleteElmStart() {
	    boolean result = super.deleteElmStart(curPos);
	    curSrcPos++;
	    return result;
	}
	
	@Override
	public boolean deleteElmEnd() {
	    boolean result = super.deleteElmStart(curPos);
        curSrcPos++;
        return result;
    }	
	
	@Override
	public boolean deleteElmChars(int howMany) {
	    boolean result = super.deleteElmChars(curPos, howMany);
	    curSrcPos++;
	    return result;
    }	
	
	@Override
	public boolean resetPosition() {
		curPos = curPosElms = curSrcPos = curPosChars = 0;
		curPosTags = 1;
		return true;
	}
	
	@Override
    public boolean clear() {
	    boolean result = super.clear();
		curPos = curPosElms = curSrcPos = curPosChars = 0;
	    curPosTags = 1;
	    return result;
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
    // FIXME: make all of these methods synchronized?
    public int scrollTo(int chars) {
        if (chars > sizeInChars) return (sizeInChars - chars);
        if (chars < curPosChars) return (chars - curPosChars);
        
        int prevPos = curPosElms;
        int size = data.size();     
        
        int charsLeft = chars - curPosChars;
        
        while ((charsLeft > 0) && (curPos < size)) {
            int value = data.get(curPos); 
            if (value >= 0) {
                stepCharsFwd((charsLeft < value) ? charsLeft : value);
                charsLeft -= value;
            } else {
                stepElmFwd(value == DocumentState.ELM_END_CODE);
            }       
        }
        if ((charsLeft == 0) && (curPosChars == chars) && (curPos < size) 
            && (data.get(curPos) == DocumentState.ELM_END_CODE)) {
            stepElmFwd(true);
        }
        
        return curPosElms - prevPos;
    }
    
    private void scrollDataTo(int arrPos) {
        if ((arrPos - curPos) <= 0) return;
        while (curPos <= arrPos) {
            int value = data.get(curPos);
            if (value >= 0) {
                stepCharsFwd(value);
            } else {
                stepElmFwd(value == DocumentState.ELM_END_CODE);
            }
        }        
    }

    // performs 'a look further', like regular expressions do
    // returns required step to reach element start, in elements
    public int findElmStart(int chars) {               
        if (chars > sizeInChars) return (sizeInChars - chars);
        if (chars < curPosChars) return (chars - curPosChars);
        
        int prevPos = curPosElms;        
        int size = data.size();
        
        int lookPos = curPos;
        int prevElmStart = 0;
        
        int charsLeft = chars - curPosChars;              
        
        // aligns to the end:
        
        // FIXME: ensure it works it tree-structured documents 
        
        // acts like a snake: stays at the end of the current element, 
        // looks at the next element, if chars count is reached inside it -
        // then it just returns current position, if not - moves to the end
        // of the next element and repeats procedure
        
        if ((charsLeft == 0) && (lookPos == curPos) && 
            data.get(lookPos) != DocumentState.ELM_START_CODE) {
            return -1;
        }        
        
        while ((charsLeft > 0) && (lookPos < size)) {
            int value = data.get(lookPos);
            if (value > 0) {
                charsLeft -= value;
                if (charsLeft == 0) { 
                    int elmNext = data.get(lookPos + 1); 
                    if (elmNext == DocumentState.ELM_END_CODE) {
                        scrollDataTo(lookPos + 1);
                        return curPosElms - prevPos;
                    } else if (elmNext == DocumentState.ELM_START_CODE) { // for tree-structured documents
                        scrollDataTo(lookPos);
                        return curPosElms - prevPos;
                    } else return prevElmStart - prevPos;
                }
            } else if (value == DocumentState.ELM_START_CODE) {
                if (charsLeft < 0) {
                    return prevElmStart - prevPos;
                } else if (charsLeft == 0) {
                    scrollDataTo(lookPos - 1);
                    return curPosElms - prevPos;
                } else if (charsLeft > 0) {
                    scrollDataTo(lookPos - 1);
                    prevElmStart = curPosElms;                    
                }
            }
            lookPos++;
        }
        
        return curPosElms - prevPos;
    }    
    
	protected void stepElmFwd(boolean isEnd, boolean withSrc) {
		curPos++; curPosElms++; 
		if (isEnd) curPosTags++;
		if (withSrc) curSrcPos++;
	}
	
    protected void stepElmFwd(boolean isEnd) {
        stepElmFwd(isEnd, true);
    }	

	protected void stepCharsFwd(int chars, boolean withSrc) {
	    if (chars > 0) {
    		curPos++; 
    		if (withSrc) curSrcPos++;
    		curPosElms += chars;
    		curPosChars += chars;
	    }
	}
	
    protected void stepCharsFwd(int chars) {
        stepCharsFwd(chars, true);
    }   	

	@Override
    public void walkWithCursor(AbstractOperatingCursor cursor) throws DocumentProcessingException {
        BufferedDocOp source = getSource();
        while (cursor.attached() && (curPos < data.size())) {            
            cursor.beforeStep();
            int value = data.get(curPos);            
            if (value > 0) {
                cursor.characters(source.getCharactersString(curSrcPos)); 
            } else if (value == DocumentState.ELM_START_CODE) {
                cursor.elementStart(source.getElementStartTag(curSrcPos), 
                                    source.getElementStartAttributes(curSrcPos));
            } else cursor.elementEnd();
            cursor.afterStep();
            // if (curPos == data.size()) cursor.onDocEndReached();
        }
    }
		
}
