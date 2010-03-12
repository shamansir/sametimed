/**
 * 
 */
package name.shamansir.sametimed.test;

import org.junit.*;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.test.mock.*;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.NoAttrsTag;
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
        "[{word:" + ID_ATTR + "=a;" + BY_ATTR + "=0@a.com" + "}" + "ijk" + "]" + // 1
        "[{word:" + ID_ATTR + "=b;" + BY_ATTR + "=a@a.com" + "}" + "lmn" + "]" + // 2
        "[{word:" + ID_ATTR + "=c;" + BY_ATTR + "=a@a.com" + "}" + "op"  + "]" + // 3
        "[{word:" + ID_ATTR + "=d;" + BY_ATTR + "=a@a.com" + "}" + "qrs" + "]" + // 4
        "[{word:" +                   BY_ATTR + "=e@a.com" + "}" + "tuv" + "]" + // 5
        "[{word:" + ID_ATTR + "=d;" +                        "}" + "wxy" + "]" + // 6
        "[{word:" +                                          "}" + "zab" + "]" + // 7
        "[{text:" +                                          "}" + "cde" + "]" + // 8
        "[{text:" + ID_ATTR + "=mm;" +                        "}" + "fgh" + "]" + // 9
        "[{word:" + ID_ATTR + "=e;" + BY_ATTR + "=b@a.com" + "}" + "ijk" + "]" + // 10
        "[{text:" + ID_ATTR + "=f;" + BY_ATTR + "=c@a.com" + "}" + "lm"  + "]" + // 11
        "[{word:" + ID_ATTR + "=g;" + BY_ATTR + "=b@a.com" + "}" + "no"  + "]" + // 12
        "[{word:" + ID_ATTR + "=h;" + BY_ATTR + "=" + UNKN_AUTHOR + "}" + "pqr" + "]" + // 13
        "[{test:" + ID_ATTR + "=c;" + BY_ATTR + "=b@a.com" + "}" + "stu" + "]" + // 14
        "[{word:" + ID_ATTR + "=i;" + BY_ATTR + "=d@a.com" + "}" + "vwx" + "]" + // 15
        "[{word:" + ID_ATTR + "=c;" + BY_ATTR + "=a@a.com" + "}" + "yza" + "]" + // 16
        "[{word:" + ID_ATTR + "=j;" + BY_ATTR + "=f@a.com" + "}" + "bcd" + "]" + // 17
        "[{word:" + ID_ATTR + "=k;" + BY_ATTR + "=g@a.com" + "}" + "efg" + "]" + // 18
        "[{word:" + ID_ATTR + "=l;" + BY_ATTR + "=g@a.com" + "}" + "hij" + "]"; // 19
    
    //         10        20        30        40        50        60        70
    // 123456789012345678901234567890123456789012345678901234567890123456789012
    // [ijk][lmn][op][qrs][tuv][wxy][zab][cde][fgh][ijk][lm][no][pqr][stu][vwx]
    //  123  456  78  901  234  567  890  123  456  789  01  23  456  789  012
    //                10              20                30                40     
    
    //       80        90
    // 34567890123456789012
    // [yza][bcd][efg][hij]
    //  345  678  901  234
    //            50
    
    // FIXME: tests for tree-structured documents    

    
    private static String getEncodedDocument() {
        return             
            // order of attributes is important here, AttributesImpl sorts them alphabetically
            "[{word:" + BY_ATTR + "=0@a.com;" + ID_ATTR + "=a;" + "}" + "ijk" + "]" +
            "[{word:" + BY_ATTR + "=a@a.com;" + ID_ATTR + "=b;" + "}" + "lmn" + "]" +
            "[{word:" + BY_ATTR + "=a@a.com;" + ID_ATTR + "=c;" + "}" + "op"  + "]" +
            "[{word:" + BY_ATTR + "=a@a.com;" + ID_ATTR + "=d;" + "}" + "qrs" + "]" +
            "[{word:" + BY_ATTR + "=e@a.com;" +                   "}" + "tuv" + "]" +
            "[{word:" +                         ID_ATTR + "=d;" + "}" + "wxy" + "]" +
            "[{word:" +                                           "}" + "zab" + "]" +
            "[{text:" +                                           "}" + "cde" + "]" +
            "[{text:" +                         ID_ATTR + "=mm;" + "}" + "fgh" + "]" +
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
    }
    
    private static String getEncodedDocumentOrdered() {
        return             
            "[{word:" + ID_ATTR + "=a;" + BY_ATTR + "=0@a.com" + "}" + "ijk" + "]" + // 1
            "[{word:" + ID_ATTR + "=b;" + BY_ATTR + "=a@a.com" + "}" + "lmn" + "]" + // 2
            "[{word:" + ID_ATTR + "=c;" + BY_ATTR + "=a@a.com" + "}" + "op"  + "]" + // 3
            "[{word:" + ID_ATTR + "=d;" + BY_ATTR + "=a@a.com" + "}" + "qrs" + "]" + // 4 
            /*  these tags are not valid so they are skipped 
            "[{word:" + ID_ATTR + "=-;" + BY_ATTR + "=e@a.com" + "}" + "tuv" + "]" + // 5
            "[{word:" + ID_ATTR + "=d;" + BY_ATTR + "=?"       + "}" + "wxy" + "]" + // 6
            "[{word:" + ID_ATTR + "=-;" + BY_ATTR + "=?"       + "}" + "zab" + "]" + // 7
            "[{text:" + ID_ATTR + "=-;" + BY_ATTR + "=?"       + "}" + "cde" + "]" + // 8
            "[{text:" + ID_ATTR + "=mm;" + BY_ATTR + "=?"      + "}" + "fgh" + "]" + // 9 */
            "[{word:" + ID_ATTR + "=e;" + BY_ATTR + "=b@a.com" + "}" + "ijk" + "]" + // 10
            "[{text:" + ID_ATTR + "=f;" + BY_ATTR + "=c@a.com" + "}" + "lm"  + "]" + // 11
            "[{word:" + ID_ATTR + "=g;" + BY_ATTR + "=b@a.com" + "}" + "no"  + "]" + // 12
            "[{word:" + ID_ATTR + "=h;" + BY_ATTR + "=-"       + "}" + "pqr" + "]" + // 13
            "[{test:" + ID_ATTR + "=c;" + BY_ATTR + "=b@a.com" + "}" + "stu" + "]" + // 14
            "[{word:" + ID_ATTR + "=i;" + BY_ATTR + "=d@a.com" + "}" + "vwx" + "]" + // 15
            "[{word:" + ID_ATTR + "=c;" + BY_ATTR + "=a@a.com" + "}" + "yza" + "]" + // 16
            "[{word:" + ID_ATTR + "=j;" + BY_ATTR + "=f@a.com" + "}" + "bcd" + "]" + // 17
            "[{word:" + ID_ATTR + "=k;" + BY_ATTR + "=g@a.com" + "}" + "efg" + "]" + // 18
            "[{word:" + ID_ATTR + "=l;" + BY_ATTR + "=g@a.com" + "}" + "hij" + "]"; // 19        
    }    
    
    private void reinitDocument() {
        resetRecorder();
        final BufferedDocOp encodedDoc = createDocument(DOCUMENT_CODE);
        documentHolder.setCurrentDocument(encodedDoc);        
    }
    
    @Test
    public void testDocumentEncodedOk() {
        reinitDocument();        
        documentHolder.getSource().apply(recordingCursor);
        Assert.assertEquals(getEncodedDocument(), recordingCursor.finish());        
    }
    
    @Test
    public void testElementsScannerCursor() {        
        reinitDocument();
        
        TestTagsScanningCursor testCursor = new TestTagsScanningCursor(); 
        documentHolder.applyCursor(testCursor);
        Assert.assertEquals(getEncodedDocumentOrdered(), testCursor.getResult());
    }
    
    @Test
    public void testLastElementSearchingCursor() {
        reinitDocument();
        
        TestLastElementSearchingCursor testCursor = 
                            new TestLastElementSearchingCursor("word"); 
        documentHolder.applyCursor(testCursor);
        Assert.assertEquals(new TagID("l"), testCursor.getResult());
        
        testCursor = new TestLastElementSearchingCursor("test"); 
        documentHolder.applyCursor(testCursor);
        Assert.assertEquals(new TagID("c"), testCursor.getResult());
        
        testCursor = new TestLastElementSearchingCursor("text"); 
        documentHolder.applyCursor(testCursor);
        Assert.assertEquals(new TagID("f"), testCursor.getResult());        
        
    }
    
    @Test
    public void testDeletionCursor() throws DocumentProcessingException {
        // at the beginning
        
        reinitDocument();
        documentHolder.startOperations();
        documentHolder.applyCursor(new DocumentElementDeletionCursor(new TagID("a")));
        Assert.assertEquals("(-[)(-ijk)(-])", 
                          getRecord(documentHolder.finishOperations()));
        
        // in the middle
        
        reinitDocument();
        documentHolder.startOperations();
        documentHolder.applyCursor(new DocumentElementDeletionCursor(new TagID("g")));
        // 40        50       
        // 901234567890123 ----
        // ][fgh][ijk][lm] [no]
        Assert.assertEquals("(*53)(-[)(-no)(-])", 
                          getRecord(documentHolder.finishOperations()));
        
        // in the end
        
        reinitDocument();        
        documentHolder.startOperations();
        documentHolder.applyCursor(new DocumentElementDeletionCursor(new TagID("l")));
        // ...      80
        // ...345678901234567 ----
        // ...[yza][bcd][efg] [hij]
        Assert.assertEquals("(*87)(-[)(-hij)(-])", 
                          getRecord(documentHolder.finishOperations()));
        
        // tag without author
                
        reinitDocument();        
        documentHolder.startOperations();
        documentHolder.applyCursor(new DocumentElementDeletionCursor(new TagID("mm")));
        // ...30         40   
        // ...90123456789 -----
        // ...][zab][cde] [fgh]                
        Assert.assertEquals("(*39)(-[)(-fgh)(-])", 
                          getRecord(documentHolder.finishOperations()));  
    }    
    
    @Test
    public void testDeletionByPosCursor() throws DocumentProcessingException {
        // at the beginning
        
        reinitDocument();        
        documentHolder.startOperations();
        documentHolder.applyCursor(new DocumentElementDeletionByPosCursor(0));
        Assert.assertEquals("(-[)(-ijk)(-])", 
                getRecord(documentHolder.finishOperations()));        
        
        // in the middle
        
        // ...20        30    
        // ...90123456789 -----
        // ...][tuv][wxy] [zab]
        // 901  234  567   890 
        // 10               20
        reinitDocument();        
        documentHolder.startOperations();
        documentHolder.applyCursor(new DocumentElementDeletionByPosCursor(17));
        Assert.assertEquals("(*29)(-[)(-zab)(-])", 
                getRecord(documentHolder.finishOperations()));
        
        // in the end
            
        // ...      80
        // ...345678901234567 -----
        // ...[yza][bcd][efg] [hij]
        // ... 345  678  901  -----
        //               50         
        reinitDocument();        
        documentHolder.startOperations();
        documentHolder.applyCursor(new DocumentElementDeletionByPosCursor(51));
        Assert.assertEquals("(*87)(-[)(-hij)(-])", 
                           getRecord(documentHolder.finishOperations()));   
         
         // next after specified pos
         
        // ... 60         70
        // ...8901234567 -----
        // ...[pqr][stu] [vwx]
        // ... 456  789  -----
        //              40            
        reinitDocument();        
        documentHolder.startOperations();
        documentHolder.applyCursor(new DocumentElementDeletionByPosCursor(38));
        Assert.assertEquals("(*67)(-[)(-vwx)(-])", 
                            getRecord(documentHolder.finishOperations()));   
    }    
    
    @Test
    public void testCuttingCursor() throws DocumentProcessingException {
        
        // at the beginning
        
        reinitDocument();
        documentHolder.startOperations();
        Assert.assertTrue(
               easyTag("a", "word", "0@a.com", "ijk").equals( 
               documentHolder.applyCursor(new DocumentElementCuttingCursor(new TagID("a")))
            ));
        Assert.assertEquals("(-[)(-ijk)(-])", 
                          getRecord(documentHolder.finishOperations()));
        
        // in the middle
        
        reinitDocument();
        documentHolder.startOperations();
        Assert.assertTrue(
               easyTag("c", "word", "a@a.com", "op").equals( 
               documentHolder.applyCursor(new DocumentElementCuttingCursor(new TagID("c")))
            ));
        // 1234567890 ----
        // [ijk][lmn] [op]
        Assert.assertEquals("(*10)(-[)(-op)(-])", 
                          getRecord(documentHolder.finishOperations()));
        
        // in the end
        
        reinitDocument();        
        documentHolder.startOperations();
        Assert.assertTrue(
               easyTag("l", "word", "g@a.com", "hij").equals( 
               documentHolder.applyCursor(new DocumentElementCuttingCursor(new TagID("l")))
            ));
        // ...      80
        // ...345678901234567 ----
        // ...[yza][bcd][efg] [hij]
        Assert.assertEquals("(*87)(-[)(-hij)(-])", 
                          getRecord(documentHolder.finishOperations()));
        
        // tag without author
                
        reinitDocument();        
        documentHolder.startOperations();                       
        Assert.assertTrue(
               easyTag("mm", "text", "?", "fgh").equals( 
               documentHolder.applyCursor(new DocumentElementCuttingCursor(new TagID("mm")) {
                   
                   // to pass checking required author attribute 
                   
                   @Override
                   protected AbstractDocumentTag makeResultTag(TagID tagID, String tagName, Attributes attrs, String content) {
                       return AbstractDocumentTag.createDirty(tagID, tagName, attrs, content);
                   }
                   
               })
            ));
        // ...30         40   
        // ...90123456789 -----
        // ...][zab][cde] [fgh]                
        Assert.assertEquals("(*39)(-[)(-fgh)(-])", 
                          getRecord(documentHolder.finishOperations()));         
    }
    
    @Test
    public void testCuttingByPosCursor() throws DocumentProcessingException {
        
        // at the beginning
        
        reinitDocument();        
        documentHolder.startOperations();
        Assert.assertTrue(
                easyTag("a", "word", "0@a.com", "ijk").equals(
                documentHolder.applyCursor(new DocumentElementCuttingByPosCursor(0))
            ));
        Assert.assertEquals("(-[)(-ijk)(-])", 
                getRecord(documentHolder.finishOperations()));        
        
        // in the middle
        
        // 12345678901234 -----
        // [ijk][lmn][op] [qrs]
        //  123  456  78  -----     
        reinitDocument();        
        documentHolder.startOperations();
        Assert.assertTrue(
                easyTag("d", "word", "a@a.com", "qrs").equals(
                documentHolder.applyCursor(new DocumentElementCuttingByPosCursor(8))
            ));
        Assert.assertEquals("(*14)(-[)(-qrs)(-])", 
                getRecord(documentHolder.finishOperations()));
        
        // in the end
            
        // ...      80
        // ...345678901234567 -----
        // ...[yza][bcd][efg] [hij]
        // ... 345  678  901  -----
        //               50         
        reinitDocument();        
        documentHolder.startOperations();
        Assert.assertTrue(
                easyTag("l", "word", "g@a.com", "hij").equals( 
                documentHolder.applyCursor(new DocumentElementCuttingByPosCursor(51))
             ));
         Assert.assertEquals("(*87)(-[)(-hij)(-])", 
                           getRecord(documentHolder.finishOperations()));   
         
         // next after specified pos
         
         // ...40         50   
         // ...90123456789 ----
         // ...][fgh][ijk] [lm]
         // ...  456  789  ----
         //                30             
         reinitDocument();        
         documentHolder.startOperations();
         Assert.assertTrue(
                 easyTag("f", "text", "c@a.com", "lm").equals( 
                 documentHolder.applyCursor(new DocumentElementCuttingByPosCursor(27))
              ));
          Assert.assertEquals("(*49)(-[)(-lm)(-])", 
                            getRecord(documentHolder.finishOperations()));          
        
    }
        
    @Test
    public void testCounterCursor() throws DocumentProcessingException {
        reinitDocument();
        
        Assert.assertTrue(new Integer(19).equals(
                 documentHolder.applyCursor(new DocumentElementsCounterCursor())
               ));
        
        reinitDocument();
        
        documentHolder.startOperations();
        Assert.assertEquals(new Integer(19),
                 documentHolder.applyCursor(new DocumentElementsCounterCursor())
              );
        documentHolder.scrollToPos(5);
        Assert.assertEquals("(*8)", 
                getRecord(documentHolder.finishOperations()));
        
        reinitDocument();
        
        Assert.assertEquals(new Integer(15),
                documentHolder.applyCursor(new DocumentElementsCounterCursor("word"))
              );
        Assert.assertEquals(new Integer(3),
                documentHolder.applyCursor(new DocumentElementsCounterCursor("text"))
              );
        Assert.assertEquals(new Integer(1),
                documentHolder.applyCursor(new DocumentElementsCounterCursor("test"))
              );        
       
    }
    
    @Test
    public void testStartPosSearchingCursors() throws DocumentProcessingException {
        reinitDocument();
        
        Assert.assertEquals(new Integer(19),
                documentHolder.applyCursor(new DocumentElementStartPosSearchingCursor(13))
              );
        Assert.assertEquals(new Integer(44),
                documentHolder.applyCursor(new DocumentElementStartPosSearchingCursor(29))
              );
        Assert.assertEquals(new Integer(57),
                documentHolder.applyCursor(new DocumentElementStartPosSearchingCursor(34))
              );         
       
       reinitDocument();
       
       documentHolder.startOperations();
       Assert.assertEquals(new Integer(34),
                documentHolder.applyCursor(new DocumentElementStartPosSearchingCursor(23))
             );
       documentHolder.scrollToPos(11);
       Assert.assertEquals("(*19)", 
               getRecord(documentHolder.finishOperations()));
       
       reinitDocument();
       
       Assert.assertEquals(new Integer(11),
               documentHolder.applyCursor(new DocumentElementStartPosSearchingCursor(13, true))
             );
       Assert.assertEquals(new Integer(29),
               documentHolder.applyCursor(new DocumentElementStartPosSearchingCursor(29, true))
             );
       Assert.assertEquals(new Integer(33),
               documentHolder.applyCursor(new DocumentElementStartPosSearchingCursor(34, true))
             );         
      
      reinitDocument();
      
      documentHolder.startOperations();
      Assert.assertEquals(new Integer(23),
               documentHolder.applyCursor(new DocumentElementStartPosSearchingCursor(23, true))
            );
      documentHolder.scrollToPos(11);
      Assert.assertEquals("(*19)", 
              getRecord(documentHolder.finishOperations()));       
        
    }
    
    @Test
    public void testLastTagIDCursor() {
        reinitDocument();
        
        Assert.assertEquals(new TagID("l"), 
                documentHolder.applyCursor(new DocumentLastTagIDCursor()));
    }
    
    @Test
    public void testLastUserElementCursor() throws DocumentProcessingException {
        reinitDocument();     
        
        Assert.assertEquals(new TagID("a"), 
                documentHolder.applyCursor(new DocumentLastUserElementCursor("0@a.com")));
        Assert.assertEquals(new TagID(null), 
                documentHolder.applyCursor(new DocumentLastUserElementCursor("e@a.com")));       
        Assert.assertEquals(new TagID("f"), 
                documentHolder.applyCursor(new DocumentLastUserElementCursor("c@a.com")));        
        Assert.assertEquals(new TagID("c"), 
                documentHolder.applyCursor(new DocumentLastUserElementCursor("b@a.com")));
        Assert.assertEquals(new TagID("i"), 
                documentHolder.applyCursor(new DocumentLastUserElementCursor("d@a.com")));
        Assert.assertEquals(new TagID("c"), 
                documentHolder.applyCursor(new DocumentLastUserElementCursor("a@a.com")));
        Assert.assertEquals(new TagID("j"), 
                documentHolder.applyCursor(new DocumentLastUserElementCursor("f@a.com")));
        Assert.assertEquals(new TagID("l"), 
                documentHolder.applyCursor(new DocumentLastUserElementCursor("g@a.com")));      
    }
    
    /*
    @Test
    public void testXMLGeneratingCursor() {
        
    } */   
    
    @Test 
    public void testCursorsSequence() {
        Assert.fail(); // cursors sequences are mutations, may be test them?
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
    
    private final class TestLastElementSearchingCursor extends AbstractLastElementSearchingCursor {
        
        private final String tagName;

        public TestLastElementSearchingCursor(String tagName) {
            this.tagName = tagName;
        }
        
        @Override
        protected boolean areAttrsApproved(Attributes attrs) {
            return true;
        }


        @Override
        protected boolean isElementApproved(String elementName) {
            return elementName.equals(tagName);
        }
        
    }
    
    private final class TestTagsScanningCursor extends AbstractElementsScannerCursor<TestTag> implements ICursorWithResult<String> {
        
        private final StringBuffer tagsInfo = new StringBuffer();
        
        @Override
        protected void applyTag(TestTag tag) {
            tagsInfo.append("[{" + tag.getName() + ":" +
                        ID_ATTR + "=" + tag.getAttribute(ID_ATTR) + ";" +
                        BY_ATTR + "=" + tag.getAttribute(BY_ATTR) +                        
            		"}" + tag.getContent() + "]");
            
        }

        // FIXME: a factory should be used to create tags
        @Override
        protected TestTag createTag(TagID id, String tagName,
                Attributes attrs) throws IllegalArgumentException {
            TestTag newTag = new TestTag(id, tagName);
            newTag.initFromElement(tagName, attrs);
            return newTag;
        }

        @Override
        public String getResult() {
            return tagsInfo.toString();
        }
               
    }
    
    private class TestTag extends NoAttrsTag {
        
        public TestTag(TagID id, String tagName, String author, String content) {
            super(id, tagName, author);
            this.setContent(content);            
        }
        
        public TestTag(TagID id, String tagName) {
            super(id, tagName, (ParticipantId)null);
        }

        protected boolean checkTagName(String tagName) {
            return true;
        }
    }
    
}
