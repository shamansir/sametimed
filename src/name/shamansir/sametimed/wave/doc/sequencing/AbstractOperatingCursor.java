package name.shamansir.sametimed.wave.doc.sequencing;


import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;

/**
 * AbstractOperatingCursor
 *
 * @author shaman.sir <shaman.sir@gmail.com>
 * @date Feb 22, 2010 3:10:07 PM
 *
 */

// FIXME: may be make IOperatingCursor interface to allow inheritance in cursors
public abstract class AbstractOperatingCursor implements DocInitializationCursor {
    
    protected WalkingDocOpBuilder docBuilder = null;
    protected DocumentWalker docWalker = null;
    
    private boolean isAttached = true;

    protected final WalkingDocOpBuilder getBuilder() { 
        return docBuilder; 
    }
    
    protected final DocumentWalker getWalker() {
        return docBuilder.getWalker();
    }

    final void assignBuilder(WalkingDocOpBuilder builder) throws DocumentProcessingException {
        if (this.docBuilder != null) 
            throw new DocumentProcessingException("Operation for this cursor can be set only once");
        if (builder == null) throw new DocumentProcessingException("Builder must not be null");
        this.docBuilder = builder; 
        this.docWalker = builder.getWalker();
        this.onAttached();
    }

    public void annotationBoundary(AnnotationBoundaryMap map) { }
    
    protected void onAttached() { }
    
    protected final void detach() {
        isAttached = false;
    }
    
    final boolean doContinue() {
        return isAttached;
    }

}
