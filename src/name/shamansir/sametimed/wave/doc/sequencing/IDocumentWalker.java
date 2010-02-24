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
    
    public void deleteElmStart();
    public void deleteElmEnd();
    public void deleteElmChars(int howMany);
    
    public void resetPosition();
    
    public int curPos();
    public int curPosElms();
    public int curPosTags();
    
    public int scrollTo(int chars);
    public int scrollToEnd();
    
    public void walkWithCursor(AbstractOperatingCursor cursor);

}
