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
public abstract class AbstractOperatingCursor implements DocInitializationCursor {
    
    protected WalkingDocOpBuilder docBuilder = null;

    protected final WalkingDocOpBuilder getBuilder() { 
        return docBuilder; 
    }
    
    protected final DocumentWalker getWalker() {
        return docBuilder.getWalker();
    }

    final void useBuilder(WalkingDocOpBuilder builder) throws DocumentProcessingException {
        if (docBuilder != null) 
            throw new DocumentProcessingException("Operation for this cursor can be set only once");
        this.docBuilder = builder; 
    }

    public void annotationBoundary(AnnotationBoundaryMap map) { }

}
