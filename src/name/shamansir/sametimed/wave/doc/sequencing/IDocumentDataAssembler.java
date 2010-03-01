/**
 * 
 */
package name.shamansir.sametimed.wave.doc.sequencing;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;

/**
 * IDocumentDataAssembler
 *
 * @author shamansir
 * @date Feb 24, 2010 1:57:00 PM
 *
 */
public interface IDocumentDataAssembler {
    
    public boolean addElmStart();
    public boolean addElmEnd();
    public boolean addElmChars(int howMany);

    public boolean clear();
    
    public int docSizeInChars();
    public int docSizeInElms();
    
    public BufferedDocOp getSource();
    
}
