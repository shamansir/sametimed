/**
 * 
 */
package name.shamansir.sametimed.test;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;

import name.shamansir.sametimed.test.mock.DetailedOperationsRecordingCursor;
import name.shamansir.sametimed.test.mock.DocumentHolder;
import name.shamansir.sametimed.test.mock.EncodedDocumentBuilder;

/**
 * TestCursors
 *
 * @author shamansir
 * @date Feb 26, 2010 8:22:44 PM
 *
 */
public class TestCursors {
    
    private DocumentHolder documentHolder = new DocumentHolder();
    DetailedOperationsRecordingCursor/*EvaluatingDocOpCursor<String>*/ recordingCursor = new DetailedOperationsRecordingCursor();
    BufferedDocOp encodedDoc = createDocument("[abcd]");
    
    public void setUp() {
        final String docInitCode = "[abcde][fghij][klm][nopqrs][tuvw]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);
    }
    
    public void tearDown() {
        
    }
    
    private BufferedDocOp createDocument(String documentCode) {
        return (new EncodedDocumentBuilder(documentCode)).compile();
    }    
        
}
