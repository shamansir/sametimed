package name.shamansir.sametimed.wave.doc.sequencing;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;

public class DocumentWalker extends DocumentState implements IDocumentWalker {
    
	private int curPos = 0; // TODO: make atomic
	private int curPosElms = 0; // TODO: make atomic
	private int curPosChars = 0; // TODO: make atomic
	private int curPosTags = 1; // TODO: make atomic // staring from 1
	
	public DocumentWalker(BufferedDocOp sourceDoc) {
	    super(sourceDoc);
	    collectDocumentData(this); // FIXME: collectData walks through document as a result,
	                               //        but it is not required
	    resetPosition();  
	}
	
	@Override
	public void addElmStart() {
	    super.addElmStart(curPos);
		stepElmFwd(false);
	}
	
	@Override
	public void addElmEnd() {
	    super.addElmEnd(curPos);
		stepElmFwd(true);
	}		
	
	@Override
	public void addElmChars(int howMany) {
	    super.addElmChars(curPos, howMany);
		stepCharsFwd(howMany);
	}
	
	@Override
	public void deleteElmStart() {
	    super.deleteElmStart(curPos);
	}
	
	@Override
	public void deleteElmEnd() {
	    super.deleteElmStart(curPos);
        curPosTags++;
    }	
	
	@Override
	public void deleteElmChars(int howMany) {
	    super.deleteElmChars(curPos, howMany);
    }	
	
	@Override
	public void resetPosition() {
		curPos = curPosElms = curPosChars = 0;
		curPosTags = 1;
	}
	
	@Override
    public void clear() {
	    super.clear();
		curPos = curPosElms = curPosChars = 0;
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

	@Override
    /**
     * @param source
     * @param cursor
     */
    public void walkWithCursor(AbstractOperatingCursor cursor) {
        // FIXME: assert that source conforms with the state?
        int size = data.size();
        while (cursor.doContinue() && (curPos < size)) {
            int value = data.get(curPos);
            // FIXME: implement            
            if (value == DocumentState.ELM_START_CODE) {
               cursor.elementStart(source.getElementStartTag(curPos), 
                                   source.getElementStartAttributes(curPos));
               //stepElmFwd(false);
            } else if (value == DocumentState.ELM_END_CODE) {
               cursor.elementEnd();
               //stepElmFwd(true);
            } else {
               final String chars = source.getCharactersString(curPos); 
               cursor.characters(chars);
               //stepCharsFwd(chars.length());
            }
            curPos++;
        }
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
