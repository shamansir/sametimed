package name.shamansir.sametimed.wave.doc;

public class DocumentWalker extends DocumentState {
	
	private int curPos = 0; // TODO: make atomic
	private int curPosElms = 0; // TODO: make atomic
	private int curPosChars = 0; // TODO: make atomic
	
	@Override
	public DocumentState addElmStart() {
		super.addElmStart();
		curPos++; curPosElms++;
		return this;
	}
	
	@Override
	public DocumentState addElmEnd() {	
		super.addElmEnd();
		curPos++; curPosElms++;
		return this;
	}		
	
	@Override
	public DocumentState addElmChars(int howMany) {
		super.addElmChars(howMany);
		curPos++;
		curPosElms += howMany;
		curPosChars += howMany;
		return this;
	}
	
	public DocumentWalker resetPosition() {
		curPos = curPosElms = curPosChars = 0;
		return this;
	}	
	
	@Override
	public DocumentState clear() {
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
		for (; curPos < size; curPos++) {
			int value = data.get(curPos); 
			if ((value == ELM_START_CODE) ||
				(value == ELM_END_CODE)) {
				curPosElms++;
			} else if (value >= 0) {
				curPosElms += value;
				curPosChars += value;
			}
		}
		return curPosElms - prevPos;
	}

	// returns required step in elements
	public int scrollBy(int chars) {
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
		return curPosElms - prevPos;
	}

}
