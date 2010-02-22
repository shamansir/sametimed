package name.shamansir.sametimed.wave.doc.sequencing;

import java.util.ArrayList;
import java.util.List;

public class DocumentState {
    
    static final int ELM_START_CODE = -1;
    static final int ELM_END_CODE = -2;
    
    protected List<Integer> data = new ArrayList<Integer>();
    protected int sizeInElms = 0; // TODO: make atomic
    protected int sizeInChars = 0; // TODO: make atomic
    
    public DocumentState() {
        
    }
    
    protected DocumentState(DocumentState initFrom) {
        this.data = initFrom.data;
        this.sizeInElms = initFrom.sizeInElms;
        this.sizeInChars = initFrom.sizeInChars;
    }
    
    protected DocumentState addElmStart() {
        data.add(ELM_START_CODE);
        sizeInElms++;
        return this;
    }
    
    protected DocumentState addElmEnd() {          
        data.add(ELM_END_CODE);
        sizeInElms++;
        return this;
    }       
    
    protected DocumentState addElmChars(int howMany) {
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
    
    protected DocumentState deleteElmStart(int pos) {
        // TODO: is check == ELM_START_CODE required?
        data.remove(pos); 
        sizeInElms--;
        return this;
    }
    
    protected DocumentState deleteElmEnd(int pos) {
        // TODO: is check == ELM_END_CODE required?
        data.remove(pos); 
        sizeInElms--;
        return this;        
    }    
    
    protected DocumentState deleteElmChars(int pos, int howMany) {
        int deleted = 0;
        if (howMany > 0) {
            while (deleted <= howMany) {
                int val = data.get(pos);
                if (val > 0) {
                    deleted += val;
                    data.remove(pos);
                } else break;
            }
            sizeInElms -= deleted;
            sizeInChars -= deleted;
        }
        return this;
    }
    
    /*
    protected DocumentState deleteElm(int pos) {
        int val = data.get(pos);
        // FIXME: get num of chars and delete while it is not reached,
        //        because data can be -1, 1, 1, 1, -2, -1, 2, 3, -2 
        if ((val == ELM_START_CODE) || (val == ELM_END_CODE)) {
            data.remove(pos);            
            sizeInElms--;
        } else {
            data.remove(pos);
            sizeInChars -= val;
            sizeInElms -= val;
        }
        return this;
    } */
        
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
