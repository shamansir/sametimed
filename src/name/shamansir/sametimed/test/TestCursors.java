/**
 * 
 */
package name.shamansir.sametimed.test;

import org.junit.*;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

import name.shamansir.sametimed.test.mock.*;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.doc.cursor.*;
import name.shamansir.sametimed.wave.doc.sequencing.DocumentProcessingException;

/**
 * TestCursors
 *
 * @author shamansir
 * @date Feb 26, 2010 8:22:44 PM
 *
 */
public class TestCursors {
    
    private final DocumentHolder documentHolder = new DocumentHolder();
    private final OperationsRecordingCursor recordingCursor = new DetailedOperationsRecordingCursor();
    
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
        "[{test:" + ID_ATTR + "=c;" + BY_ATTR + "=b@a.com" + "}" + "stu" + "]" +
        "[{word:" + ID_ATTR + "=i;" + BY_ATTR + "=d@a.com" + "}" + "vwx" + "]" +
        "[{word:" + ID_ATTR + "=c;" + BY_ATTR + "=a@a.com" + "}" + "yza" + "]" +
        "[{word:" + ID_ATTR + "=j;" + BY_ATTR + "=f@a.com" + "}" + "bcd" + "]" +
        "[{word:" + ID_ATTR + "=k;" + BY_ATTR + "=g@a.com" + "}" + "efg" + "]" +
        "[{word:" + ID_ATTR + "=l;" + BY_ATTR + "=g@a.com" + "}" + "hij" + "]";
    
    // TODO: tests for tree-structured documents
    
    @Before
    public void setUp() {
        final BufferedDocOp encodedDoc = createDocument(DOCUMENT_CODE);
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
            "[{test:" + BY_ATTR + "=b@a.com;" + ID_ATTR + "=c;" + "}" + "stu" + "]" +
            "[{word:" + BY_ATTR + "=d@a.com;" + ID_ATTR + "=i;" + "}" + "vwx" + "]" +
            "[{word:" + BY_ATTR + "=a@a.com;" + ID_ATTR + "=c;" + "}" + "yza" + "]" +
            "[{word:" + BY_ATTR + "=f@a.com;" + ID_ATTR + "=j;" + "}" + "bcd" + "]" +
            "[{word:" + BY_ATTR + "=g@a.com;" + ID_ATTR + "=k;" + "}" + "efg" + "]" +
            "[{word:" + BY_ATTR + "=g@a.com;" + ID_ATTR + "=l;" + "}" + "hij" + "]";
        documentHolder.getSource().apply(recordingCursor);
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
    public void testCuttingCursor() throws DocumentProcessingException {
        documentHolder.startOperations();
        Assert.assertEquals(
                easyTag("c", "word", "a@a.com", "op"), 
                documentHolder.applyCursor(new DocumentElementCuttingCursor(new TagID("c"))));
        // 1234567890 ----
        // [ijk][lmn] [op]
        Assert.assertEquals("(*10)", 
                          getRecord(documentHolder.finishOperations()));
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
    
    @Test 
    public void testCursorsSequence() {
        Assert.fail();
    }
    
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
    
    private static AbstractDocumentTag easyTag(String tagID, String name, String author, String content) {
        return AbstractDocumentTag.createNoAttrs(tagID, name, author, content);
    }
}
