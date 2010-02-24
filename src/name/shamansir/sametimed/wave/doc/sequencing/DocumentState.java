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
    
    protected final BufferedDocOp source;
    protected final List<Integer> data = new ArrayList<Integer>();
    protected int sizeInElms = 0; // TODO: make atomic
    protected int sizeInChars = 0; // TODO: make atomic
    
    private DocumentState(BufferedDocOp sourceDoc) {
        this.source = sourceDoc;
    }
    
    /* private DocumentState(BufferedDocOp sourceDoc) {
        this(collectDocumentData(sourceDoc));
    } */
    
    /* protected DocumentState(DocumentState initFrom) {
        this.source = initFrom.source;
        this.data = initFrom.data;
        this.sizeInChars = initFrom.sizeInChars;
        this.sizeInElms = initFrom.sizeInElms;
    } */  
      
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
        
    @Override
    public void clear() {
        data.clear();
        sizeInChars = sizeInElms = 0;
    }
    
    public int docSizeInChars() {
        return sizeInChars;
    }
    
    public int docSizeInElms() {
        return sizeInElms;
    }
    
    protected final static DocumentState collectDocumentData(BufferedDocOp sourceDoc) {
        LOG.debug("Collecting document data");
        
        if (sourceDoc == null) return new DocumentState(sourceDoc);
        
        EvaluatingDocInitializationCursor<DocumentState> evaluatingCursor =
                new DocStateEvaluatingCursor(new DocumentState(sourceDoc));
        sourceDoc.apply(new InitializationCursorAdapter(evaluatingCursor));
        return evaluatingCursor.finish();       
    }
    
    private static class DocStateEvaluatingCursor implements EvaluatingDocInitializationCursor<DocumentState/*int[]*/> {
        
        private final DocumentState state;
        
        public DocStateEvaluatingCursor(DocumentState state) {
            this.state = state;
        }

        @Override
        public void annotationBoundary(AnnotationBoundaryMap map) { }

        @Override
        public void characters(String chars) { 
            state.addElmChars(chars.length());
        }

        @Override
        public void elementEnd() {
            state.addElmEnd();          
        }

        @Override
        public void elementStart(String type, Attributes attrs) {
            state.addElmStart();
        }

        @Override
        public DocumentState finish() {
            return state;
        }
        
    }
    
    
}
