package name.shamansir.sametimed.wave.doc.sequencing;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.EvaluatingDocInitializationCursor;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;

public class DocumentState implements IDocumentDataAssembler {
    
    private static final Log LOG = LogFactory.getLog(DocumentState.class);
    
    static final int ELM_START_CODE = -1;
    static final int ELM_END_CODE = -2;
    
    private BufferedDocOp source; // FIXME: must be final
    protected List<Integer> data = new ArrayList<Integer>();
    protected int sizeInElms = 0; // TODO: make atomic
    protected int sizeInChars = 0; // TODO: make atomic
    
    protected DocumentState(BufferedDocOp sourceDoc) {
        this.source = sourceDoc;
    }
          
    @Override
    public void addElmStart() {
        data.add(ELM_START_CODE);
        sizeInElms++;
    }
    
    @Override
    public void addElmEnd() {          
        data.add(ELM_END_CODE);
        sizeInElms++;
    }       
    
    @Override
    public void addElmChars(int howMany) {
        data.add(howMany); // TODO: may be accumulate? 
        sizeInChars += howMany;
        sizeInElms += howMany;
    }
    
    protected void addElmStart(int pos) {
        data.add(pos, ELM_START_CODE);
        sizeInElms++;
    }
    
    protected void addElmEnd(int pos) {          
        data.add(pos, ELM_END_CODE);
        sizeInElms++;
    }       
    
    protected void addElmChars(int pos, int howMany) {
        data.add(pos, howMany);
        sizeInChars += howMany;
        sizeInElms += howMany;
    }
    
    protected void deleteElmStart(int pos) {
        // TODO: is check == ELM_START_CODE required?
        data.remove(pos); 
        sizeInElms--;
    }
    
    protected void deleteElmEnd(int pos) {
        // TODO: is check == ELM_END_CODE required?
        data.remove(pos); 
        sizeInElms--;
    }    
    
    protected void deleteElmChars(int pos, int howMany) {
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
        
    @Override
    public void clear() {
        data.clear();
        sizeInChars = sizeInElms = 0;
    }
    
    @Override
    public int docSizeInChars() {
        return sizeInChars;
    }
    
    @Override
    public int docSizeInElms() {
        return sizeInElms;
    }
    
    @Override
    public BufferedDocOp getSource() {
        return source;
    }
    
    protected final void loadDataFrom(DocumentState other) { // FIXME: better be private
        this.source = other.source;
        this.data.clear();
        this.data.addAll(other.data);
        this.sizeInChars = other.sizeInChars;
        this.sizeInElms = other.sizeInElms;
    }
    
    protected final static IDocumentDataAssembler collectDocumentData(IDocumentDataAssembler with) {
        return collectDocumentData(with, with.getSource());      
    }    
    
    protected final static IDocumentDataAssembler collectDocumentData(IDocumentDataAssembler with, BufferedDocOp sourceDoc) {
        LOG.debug("Collecting document data");
        
        if (sourceDoc == null) return with;
        
        EvaluatingDocInitializationCursor<IDocumentDataAssembler> evaluatingCursor =
                new DocStateEvaluatingCursor(with);
        sourceDoc.apply(new InitializationCursorAdapter(evaluatingCursor));
        return evaluatingCursor.finish();
    }
    
    protected DocumentState collectDocumentData() {
        return (DocumentState)collectDocumentData(this);
    }
        
    private static class DocStateEvaluatingCursor implements EvaluatingDocInitializationCursor<IDocumentDataAssembler/*int[]*/> {
        
        private final IDocumentDataAssembler assembler;
        
        public DocStateEvaluatingCursor(IDocumentDataAssembler assembler) {
            this.assembler = assembler;
        }

        @Override
        public void annotationBoundary(AnnotationBoundaryMap map) { }

        @Override
        public void characters(String chars) { 
            assembler.addElmChars(chars.length());
        }

        @Override
        public void elementEnd() {
            assembler.addElmEnd();          
        }

        @Override
        public void elementStart(String type, Attributes attrs) {
            assembler.addElmStart();
        }

        @Override
        public IDocumentDataAssembler finish() {
            return assembler;
        }
        
    }
    
    
}
