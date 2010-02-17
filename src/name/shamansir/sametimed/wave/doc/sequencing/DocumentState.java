package name.shamansir.sametimed.wave.doc.sequencing;

import java.util.ArrayList;
import java.util.List;

public class DocumentState {
	
	protected static final int ELM_START_CODE = -1;
	protected static final int ELM_END_CODE = -2;
	
	protected List<Integer> data = new ArrayList<Integer>();
	private int sizeInElms = 0; // TODO: make atomic
	private int sizeInChars = 0; // TODO: make atomic
	
	public DocumentState() {
	    
	}
	
	protected DocumentState(DocumentState initFrom) {
	    this.data = initFrom.data;
	    this.sizeInElms = initFrom.sizeInElms;
	    this.sizeInChars = initFrom.sizeInChars;
	}
	
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
	
    protected DocumentState addElmStart(int pos) {
        data.add(pos, ELM_START_CODE);
        sizeInElms++;
        return this;
    }
    
    protected DocumentState addElmEnd(int pos) {          
        data.add(pos, ELM_END_CODE);
        sizeInElms++;
        return this;
    }       
    
    protected DocumentState addElmChars(int pos, int howMany) {
        data.add(pos, howMany);
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
	
	/* protected DocumentState insElmStart(int pos) {
	    // TODO: implement
	    return this;
	}
	
    protected DocumentState insElmEnd(int pos) {
        // TODO: implement        
        return this;
    }
    
    protected DocumentState insElmChars(int pos, int howMany) {
        // TODO: implement   
        return this;
    } */  
	
}
