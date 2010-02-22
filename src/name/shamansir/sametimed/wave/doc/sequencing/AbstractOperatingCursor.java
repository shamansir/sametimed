package name.shamansir.sametimed.wave.doc.sequencing;


import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;

/**
 * AbstractOperatingCursor
 *
 * @author shaman.sir <shaman.sir@gmail.com>
 * @date Feb 22, 2010 3:10:07 PM
 *
 */
public abstract class AbstractOperatingCursor implements DocInitializationCursor {
    
    protected WalkingDocOpBuilder walkingBuilder = null;

    public final DocOpBuilder takeDocOp() { 
        return walkingBuilder; 
    }

    protected final void useBuilder(WalkingDocOpBuilder builder) throws DocumentProcessingException {
        if (walkingBuilder != null) 
            throw new DocumentProcessingException("Operation for this cursor can be set only once");
        this.walkingBuilder = builder; 
    }

    public void annotationBoundary(AnnotationBoundaryMap map) { }

}
