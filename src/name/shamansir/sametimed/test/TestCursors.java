/**
 * 
 */
package name.shamansir.sametimed.test;

import org.junit.*;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

import name.shamansir.sametimed.test.mock.*;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
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
    
    private static final String ID_ATTR = AbstractDocumentTag.ID_ATTR_NAME;
    private static final String BY_ATTR = AbstractDocumentTag.AUTHOR_ATTR_NAME;
    private static final String UNKN_AUTHOR = AbstractDocumentTag.UNKNOWN_AUTHOR;    
    
    private static final String DOCUMENT_CODE =
        "[{word:" + ID_ATTR + "=a;" + BY_ATTR + "=0@a.com" + "}" + "ijk" + "]" +
        "[{word:" + ID_ATTR + "=b;" + BY_ATTR + "=a@a.com" + "}" + "lmn" + "]" +
        "[{word:" + ID_ATTR + "=c;" + BY_ATTR + "=a@a.com" + "}" + "op"  + "]" +
        "[{word:" + ID_ATTR + "=d;" + BY_ATTR + "=a@a.com" + "}" + "qrs" + "]" +
        "[{word:" +                   BY_ATTR + "=e@a.com" + "}" + "tuv" + "]" +
        "[{word:" + ID_ATTR + "=d;" +                        "}" + "wxy" + "]" +
        "[{word:" +                                          "}" + "zab" + "]" +
        "[{text:" +                                          "}" + "cde" + "]" +
        "[{word:" + ID_ATTR + "=mm;" +                        "}" + "fgh" + "]" +
        "[{word:" + ID_ATTR + "=e;" + BY_ATTR + "=b@a.com" + "}" + "ijk" + "]" +
        "[{text:" + ID_ATTR + "=f;" + BY_ATTR + "=c@a.com" + "}" + "lm"  + "]" +
        "[{word:" + ID_ATTR + "=g;" + BY_ATTR + "=b@a.com" + "}" + "no"  + "]" +
        "[{word:" + ID_ATTR + "=h;" + BY_ATTR + "=" + UNKN_AUTHOR + "}" + "pqr" + "]" +
        "[{test:" + ID_ATTR + "=i;" + BY_ATTR + "=b@a.com" + "}" + "stu" + "]" +
        "[{word:" + ID_ATTR + "=j;" + BY_ATTR + "=d@a.com" + "}" + "vwx" + "]" +
        "[{word:" + ID_ATTR + "=k;" + BY_ATTR + "=a@a.com" + "}" + "yza" + "]" +
        "[{word:" + ID_ATTR + "=l;" + BY_ATTR + "=f@a.com" + "}" + "bcd" + "]";     
    private static BufferedDocOp encodedDoc;
    
    // TODO: tests for tree-structured documents
    
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
        final String EXPECTED_RECORDED_DOC_CODE =
            // order of attributes is important here, AttributesImpl sorts them alphabetically
            "[{word:" + BY_ATTR + "=0@a.com;" + ID_ATTR + "=a;" + "}" + "ijk" + "]" +
            "[{word:" + BY_ATTR + "=a@a.com;" + ID_ATTR + "=b;" + "}" + "lmn" + "]" +
            "[{word:" + BY_ATTR + "=a@a.com;" + ID_ATTR + "=c;" + "}" + "op"  + "]" +
            "[{word:" + BY_ATTR + "=a@a.com;" + ID_ATTR + "=d;" + "}" + "qrs" + "]" +
            "[{word:" + BY_ATTR + "=e@a.com;" +                   "}" + "tuv" + "]" +
            "[{word:" +                         ID_ATTR + "=d;" + "}" + "wxy" + "]" +
            "[{word:" +                                           "}" + "zab" + "]" +
            "[{text:" +                                           "}" + "cde" + "]" +
            "[{word:" +                         ID_ATTR + "=mm;" + "}" + "fgh" + "]" +
            "[{word:" + BY_ATTR + "=b@a.com;" + ID_ATTR + "=e;" + "}" + "ijk" + "]" +
            "[{text:" + BY_ATTR + "=c@a.com;" + ID_ATTR + "=f;" + "}" + "lm"  + "]" +
            "[{word:" + BY_ATTR + "=b@a.com;" + ID_ATTR + "=g;" + "}" + "no"  + "]" +
            "[{word:" + BY_ATTR + "=" + UNKN_AUTHOR + ';' + ID_ATTR + "=h;" + "}" + "pqr" + "]" +
            "[{test:" + BY_ATTR + "=b@a.com;" + ID_ATTR + "=i;" + "}" + "stu" + "]" +
            "[{word:" + BY_ATTR + "=d@a.com;" + ID_ATTR + "=j;" + "}" + "vwx" + "]" +
            "[{word:" + BY_ATTR + "=a@a.com;" + ID_ATTR + "=k;" + "}" + "yza" + "]" +
            "[{word:" + BY_ATTR + "=f@a.com;" + ID_ATTR + "=l;" + "}" + "bcd" + "]";
        encodedDoc.apply(recordingCursor);
        Assert.assertEquals(EXPECTED_RECORDED_DOC_CODE, recordingCursor.finish());        
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
