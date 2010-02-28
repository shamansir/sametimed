/**
 * 
 */
package name.shamansir.sametimed.test;

import org.junit.*;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

import name.shamansir.sametimed.test.mock.*;

import name.shamansir.sametimed.wave.doc.cursor.*;

/**
 * TestCursors
 *
 * @author shamansir
 * @date Feb 26, 2010 8:22:44 PM
 *
 */
public class TestCursors {
    
    private static DocumentHolder documentHolder = new DocumentHolder();
    private static OperationsRecordingCursor recordingCursor = new DetailedOperationsRecordingCursor();
    
    private static final String DOCUMENT_CODE = "[abcd]";
    private static BufferedDocOp encodedDoc;
    
    @BeforeClass
    public static void prepareTests() {
        encodedDoc = createDocument(DOCUMENT_CODE);
        documentHolder.setCurrentDocument(encodedDoc);        
    }
        
    @After
    public void tearDown() {
        resetRecorder();
    }
    
    @Test
    public void testDocumentEncodedOk() {
        encodedDoc.apply(recordingCursor);
        Assert.assertEquals("[{a:}abcd]", recordingCursor.finish());        
    }
    
    @Test
    public void testElementsScannerCursor() {        
        Assert.fail();
    }
    
    @Test
    public void testLastElementSearchingCursor() {
        Assert.fail();        
    }
    
    @Test
    public void testCuttingCursor() {
        Assert.fail();        
    }
    
    @Test
    public void testCuttingByPosCursor() {
        Assert.fail();
    }
    
    @Test
    public void testDeletionCursor() {
        Assert.fail();
    }    
    
    @Test
    public void testDeletionByPosCursor() {
        Assert.fail();
    }
    
    @Test
    public void testCounterCursor() {
        Assert.fail();
    }
    
    @Test
    public void testStartPosSearchingCursor() {
        Assert.fail();
    }
    
    @Test
    public void testLastTagIDCursor() {
        Assert.fail();
    }
    
    @Test
    public void testLastUserElementCursor() {
        Assert.fail();
    }
    
    /*
    @Test
    public void testXMLGeneratingCursor() {
        
    } */   
    
    private static BufferedDocOp createDocument(String documentCode) {
        return (new EncodedDocumentBuilder(documentCode)).compile();
    }
    
    private String getRecord(WaveletDocumentOperation docOp) {
        BufferedDocOp opWasBuilt = docOp.getOperation();
        opWasBuilt.apply(recordingCursor);
        return recordingCursor.finish();
    }
    
    private void resetRecorder() {
        recordingCursor.erase();
    }    
        
}
