package name.shamansir.sametimed.wave.doc.sequencing;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;

public class DocumentWalker implements IDocumentDataAssembler {
    
    private final DocumentState state;
    
	private int curPos = 0; // TODO: make atomic
	private int curPosElms = 0; // TODO: make atomic
	private int curPosChars = 0; // TODO: make atomic
	private int curPosTags = 1; // TODO: make atomic // staring from 1
	
	public DocumentWalker(BufferedDocOp sourceDoc) { 
	    state = DocumentState.collectDocumentData(sourceDoc); 
	}
	
	@Override
	public void addElmStart() {
	    state.addElmStart(curPos);
		stepElmFwd(false);
	}
	
	@Override
	public void addElmEnd() {
	    state.addElmEnd(curPos);
		stepElmFwd(true);
	}		
	
	@Override
	public void addElmChars(int howMany) {
	    state.addElmChars(curPos, howMany);
		stepCharsFwd(howMany);
	}
	
	public void deleteElmStart() {
	    state.deleteElmStart(curPos);
	}
	
	public void deleteElmEnd() {
        state.deleteElmStart(curPos);
        curPosTags++;
    }	
	
	public void deleteElmChars(int howMany) {
        state.deleteElmChars(curPos, howMany);
    }	
	
	public DocumentWalker resetPosition() {
		curPos = curPosElms = curPosChars = 0;
		curPosTags = 1;
		return this;
	}
	
	@Override
    public void clear() {
	    state.clear();
		curPos = curPosElms = curPosChars = 0;
	    curPosTags = 1;
	}
	
	public int curPos() {
		return curPosChars;
	}
	
	public int curPosElms() {
	    return curPosElms;
	}
	
    public int curPosTags() {
        return curPosTags;
    }	

	// returns required step in elements 
	protected int scrollToEnd() {
		int size = state.data.size();
		int prevPos = curPosElms;
		while (curPos < size) {
			int value = state.data.get(curPos);
			if (value >= 0) {
			    stepCharsFwd(value);
			} else {
			    stepElmFwd(value == DocumentState.ELM_END_CODE);
			}
		}
		return curPosElms - prevPos;
	}
	
	// returns required step in elements
	protected int scrollTo(int chars) {
	    if (chars > state.sizeInChars) return (state.sizeInChars - chars);
	    if (chars < curPosChars) return (chars - curPosChars);
	    
		int prevPos = curPosElms;
		int size = state.data.size();		
		
		while ((curPosChars < chars) && (curPos < size)) {
			int value = state.data.get(curPos); 
            if (value >= 0) {
                stepCharsFwd(value);
            } else {
                stepElmFwd(value == DocumentState.ELM_END_CODE);
            }		
		}
		if ((curPosChars == chars) && (curPos < size) 
			&& (state.data.get(curPos) == DocumentState.ELM_END_CODE)) {
			stepElmFwd(true);
		}
		
		return curPosElms - prevPos;
	}

	protected void stepElmFwd(boolean isEnd) {
		curPos++; curPosElms++;
		if (isEnd) curPosTags++;
	}

	protected void stepCharsFwd(int chars) {
	    if (chars > 0) {
    		curPos++;
    		curPosElms += chars;
    		curPosChars += chars;
	    }
	}

    /**
     * @param source
     * @param cursor
     */
    public void walkWithCursor(AbstractOperatingCursor cursor) {
        // FIXME: assert that source conforms with the state?
        int size = state.data.size();
        while (cursor.doContinue() && (curPos < size)) {
            int value = state.data.get(curPos);
            // FIXME: implement            
            if (value == DocumentState.ELM_START_CODE) {
               cursor.elementStart(state.source.getElementStartTag(curPos), 
                                   state.source.getElementStartAttributes(curPos));
               //stepElmFwd(false);
            } else if (value == DocumentState.ELM_END_CODE) {
               cursor.elementEnd();
               //stepElmFwd(true);
            } else {
               final String chars = state.source.getCharactersString(curPos); 
               cursor.characters(chars);
               //stepCharsFwd(chars.length());
            }
            curPos++;
        }
    }

    public DocumentState getState() {
        return state;
    }
	
	// returns required step in elements
	/*
	protected int scrollBy(int chars, boolean doAlign) {
		int prevPos = curPosElms;
		int size = data.size();
		while ((curPosChars < chars) && (curPos < size)) {
			int value = data.get(curPos); 
			if ((value == ELM_START_CODE) ||
				(value == ELM_END_CODE)) {
				curPosElms++;
			} else if (value >= 0) {
				curPosElms += value;
				curPosChars += value;
			}
			curPos++;			
		}
		if (doAlign && (curPosChars == chars) &&
			(curPos < size) && (data.get(curPos) == ELM_END_CODE)) {
			curPos++; curPosElms++;
		}
		return curPosElms - prevPos;
	}
	
	public int scrollBy(int chars) {
		return scrollBy(chars, true);
	} */

}
