package name.shamansir.sametimed.wave.doc.cursor;

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
    
    protected DocOpBuilder curDocOp;
    protected int walkStart;

    public final void setWalkStart(int pos) { this.walkStart = pos; }

    public final DocOpBuilder takeDocOp() { return curDocOp; }

    public final void useDocOp(DocOpBuilder curDocOp) { this.curDocOp = curDocOp; }

    public void annotationBoundary(AnnotationBoundaryMap map) { }

}
