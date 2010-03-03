package name.shamansir.sametimed.wave.doc.sequencing;


import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
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
    
    private static final Log LOG = LogFactory.getLog(AbstractOperatingCursor.class);
    
    protected WalkingDocOpBuilder docBuilder = null;
    protected DocumentWalker docWalker = null;
    
    private boolean isAttached = false;

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
        this.isAttached = true;
        this.onAttached();
    }

    public void annotationBoundary(AnnotationBoundaryMap map) { }
    
    protected void onAttached() { }
    
    protected final void detach() {
        LOG.debug("cursor detached");
        isAttached = false;
    }

    final boolean attached() {
        return isAttached;
    }

    public void beforeStep() {
        docBuilder.nextStep();        
    }

    public void afterStep() throws DocumentProcessingException {
        if (!docBuilder.actionPerformed()) {
            throw new DocumentProcessingException("No action was performed " +
            		"during the last step, please detach this cursor or do " +
            		" some retain / elements manipulation" +
            		" or check if some exceptions were fired");
        }
    }
    
    protected AbstractDocumentTag makeResultTag(TagID tagID, String tagName, Attributes attrs, String content) {
        // FIXME: in future (on xml-configuration implementation stage), it must use some TagFactory, 
        //        that receives module ID (passed from constructor) and ask module to create the tag
        //        or may be it is ok if empty cursor returned?
        return AbstractDocumentTag.createEmpty(tagID, tagName, attrs, content);        
    }

    /* public void onDocEndReached() { } */

}
