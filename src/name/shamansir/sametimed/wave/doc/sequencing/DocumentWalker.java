package name.shamansir.sametimed.wave.doc.sequencing;

public class DocumentWalker extends DocumentState {
    
	private int curPos = 0; // TODO: make atomic
	private int curPosElms = 0; // TODO: make atomic
	private int curPosChars = 0; // TODO: make atomic
	
	public DocumentWalker() { super(); }
 	
	protected DocumentWalker(DocumentState initFrom) { super(initFrom); }
	
	@Override
	public DocumentState addElmStart() {
		super.addElmStart(curPos);
		stepElmFwd();
		return this;
	}
	
	@Override
	public DocumentState addElmEnd() {
        super.addElmEnd(curPos);
		stepElmFwd();
		return this;
	}		
	
	@Override
	public DocumentState addElmChars(int howMany) {
		super.addElmChars(curPos, howMany);
		stepCharsFwd(howMany);
		return this;
	}
	
	public DocumentWalker resetPosition() {
		curPos = curPosElms = curPosChars = 0;
		return this;
	}	
	
	@Override
	public DocumentWalker clear() {
	    super.clear();
		curPos = curPosElms = curPosChars = 0;
		return this;
	}
	
	public int curPos() {
		return curPosChars;
	}

	// returns required step in elements 
	public int scrollToEnd() {
		int size = data.size();
		int prevPos = curPosElms;
		while (curPos < size) {
			int value = data.get(curPos); 
			if ((value == ELM_START_CODE) ||
				(value == ELM_END_CODE)) {
				stepElmFwd();
			} else if (value >= 0) {
				stepCharsFwd(value);
			}
		}
		return curPosElms - prevPos;
	}
	
	// returns required step in elements
	protected int scrollTo(int chars) {
		int prevPos = curPosElms;
		int size = data.size();
		
		while ((curPosChars < chars) && (curPos < size)) {
			int value = data.get(curPos); 
			if ((value == ELM_START_CODE) ||
				(value == ELM_END_CODE)) {
				stepElmFwd();
			} else if (value >= 0) {
				stepCharsFwd(value);
			}			
		}
		if ((curPosChars == chars) && (curPos < size) 
			&& (data.get(curPos) == ELM_END_CODE)) {
			stepElmFwd();
		}
		
		return curPosElms - prevPos;
	}

	protected void stepElmFwd() {
		curPos++; curPosElms++;
	}
	
	protected void stepElmBkwd() {
		curPos--; curPosElms--;
	}

	protected void stepCharsFwd(int chars) {
		curPos++;
		curPosElms += chars;
		curPosChars += chars;				
	}
	
	protected void stepCharsBkwd(int chars) {
		curPos--;
		curPosElms -= chars;
		curPosChars -= chars;				
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
