/**
 * 
 */
package name.shamansir.sametimed.wave.doc.sequencing;

/**
 * IDocumentWalker
 *
 * @author shamansir
 * @date Feb 24, 2010 3:23:25 PM
 *
 */
public interface IDocumentWalker extends IDocumentDataAssembler {
    
    public boolean deleteElmStart();
    public boolean deleteElmEnd();
    public boolean deleteElmChars(int howMany);
    
    public boolean resetPosition();
    
    public int curPos();
    public int curPosElms();
    public int curPosTags();
    
    public int scrollTo(int chars);
    public int scrollToEnd();
    
    public void walkWithCursor(AbstractOperatingCursor cursor) throws DocumentProcessingException;

}
