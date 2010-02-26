/**
 * 
 */
package name.shamansir.sametimed.test.mock;

import name.shamansir.sametimed.wave.doc.sequencing.AbstractDocumentOperationsSequencer;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;

/**
 * DocumentHolder
 *
 * @author shamansir
 * @date Feb 26, 2010 8:46:06 PM
 *
 */
public class DocumentHolder extends AbstractDocumentOperationsSequencer {
    
    private BufferedDocOp curDocument = null;

    public void setCurrentDocument(BufferedDocOp curDocument) {
        this.curDocument = curDocument;
    }

    @Override
    public String getDocumentID() {
        return "test";
    }

    @Override
    protected BufferedDocOp getSource() {
        return curDocument;
    }
    
    public DocOpBuilder unhideOp() {
        return getDocBuilder();
    }
    
}
