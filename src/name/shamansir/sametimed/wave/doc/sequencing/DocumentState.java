package name.shamansir.sametimed.wave.doc.sequencing;

import java.util.ArrayList;
import java.util.List;

public class DocumentState {
	
	protected static final int ELM_START_CODE = -1;
	protected static final int ELM_END_CODE = -2;
	
	protected List<Integer> data = new ArrayList<Integer>();
	private int sizeInElms = 0; // TODO: make atomic
	private int sizeInChars = 0; // TODO: make atomic
	
	public DocumentState addElmStart() {
		data.add(ELM_START_CODE);
		sizeInElms++;
		return this;
	}
	
	public DocumentState addElmEnd() {			
		data.add(ELM_END_CODE);
		sizeInElms++;
		return this;
	}		
	
	public DocumentState addElmChars(int howMany) {
		data.add(howMany);
		sizeInChars += howMany;
		sizeInElms += howMany;
		return this;
	}
	
	public DocumentState clear() {
		data = new ArrayList<Integer>();
		sizeInChars = sizeInElms = 0;
		return this;
	}
	
	public int docSizeInChars() {
		return sizeInChars;
	}
	
	public int docSizeInElms() {
		return sizeInElms;
	}
	
}
