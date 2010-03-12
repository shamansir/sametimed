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
    private static final String UKN_ATHR = AbstractDocumentTag.UNKNOWN_AUTHOR;    
    
    private static final String DOCUMENT_CODE =
        tagStr("word", "a",  "0@a.com", "ijk") + // 1
        tagStr("word", "b",  "a@a.com", "lmn") + // 2
        tagStr("word", "c",  "a@a.com", "op" ) + // 3
        tagStr("word", "d",  "a@a.com", "qrs") + // 4
        tagStr("word", null, "e@a.com", "tuv") + // 5
        tagStr("word", "d",       null, "wxy") + // 6
        tagStr("word", null,      null, "zab") + // 7
        tagStr("text", null,      null, "cde") + // 8
        tagStr("text", "mm",      null, "fgh") + // 9
        tagStr("word", "e",  "b@a.com", "ijk") + // 10
        tagStr("text", "f",  "c@a.com", "lm" ) + // 11
        tagStr("word", "g",  "b@a.com", "no" ) + // 12
        tagStr("word", "h",   UKN_ATHR, "pqr") + // 13
        tagStr("test", "c",  "b@a.com", "stu") + // 14
        tagStr("word", "i",  "d@a.com", "vwx") + // 15
        tagStr("word", "c",  "a@a.com", "yza") + // 16
        tagStr("word", "j",  "f@a.com", "bcd") + // 17
        tagStr("word", "k",  "g@a.com", "efg") + // 18
        tagStr("word", "l",  "g@a.com", "hij");  // 19 
    
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
    
    private static String getEncodedDocument() {
        return             
            // order of attributes is important here, AttributesImpl sorts them alphabetically
            tagStrEnc("word", "a",  "0@a.com", "ijk") + // 1
            tagStrEnc("word", "b",  "a@a.com", "lmn") + // 2
            tagStrEnc("word", "c",  "a@a.com", "op" ) + // 3
            tagStrEnc("word", "d",  "a@a.com", "qrs") + // 4
            tagStrEnc("word", null, "e@a.com", "tuv") + // 5
            tagStrEnc("word", "d",       null, "wxy") + // 6
            tagStrEnc("word", null,      null, "zab") + // 7
            tagStrEnc("text", null,      null, "cde") + // 8
            tagStrEnc("text", "mm",      null, "fgh") + // 9
            tagStrEnc("word", "e",  "b@a.com", "ijk") + // 10
            tagStrEnc("text", "f",  "c@a.com", "lm" ) + // 11
            tagStrEnc("word", "g",  "b@a.com", "no" ) + // 12
            tagStrEnc("word", "h",   UKN_ATHR, "pqr") + // 13
            tagStrEnc("test", "c",  "b@a.com", "stu") + // 14
            tagStrEnc("word", "i",  "d@a.com", "vwx") + // 15
            tagStrEnc("word", "c",  "a@a.com", "yza") + // 16
            tagStrEnc("word", "j",  "f@a.com", "bcd") + // 17
            tagStrEnc("word", "k",  "g@a.com", "efg") + // 18
            tagStrEnc("word", "l",  "g@a.com", "hij");  // 19 
    }
    
    private static String getEncodedDocumentOrdered() {
        return
            tagStr("word", "a",  "0@a.com", "ijk") + // 1
            tagStr("word", "b",  "a@a.com", "lmn") + // 2
            tagStr("word", "c",  "a@a.com", "op" ) + // 3
            tagStr("word", "d",  "a@a.com", "qrs") + // 4
            //these tags are not valid so they are skipped
            /* tagStr("word", null, "e@a.com", "tuv") + // 5
            tagStr("word", "d",       null, "wxy") + // 6
            tagStr("word", null,      null, "zab") + // 7
            tagStr("text", null,      null, "cde") + // 8
            tagStr("text", "mm",      null, "fgh") + // 9 */ 
            tagStr("word", "e",  "b@a.com", "ijk") + // 10
            tagStr("text", "f",  "c@a.com", "lm" ) + // 11
            tagStr("word", "g",  "b@a.com", "no" ) + // 12
            tagStr("word", "h",        "-", "pqr") + // 13
            tagStr("test", "c",  "b@a.com", "stu") + // 14
            tagStr("word", "i",  "d@a.com", "vwx") + // 15
            tagStr("word", "c",  "a@a.com", "yza") + // 16
            tagStr("word", "j",  "f@a.com", "bcd") + // 17
            tagStr("word", "k",  "g@a.com", "efg") + // 18
            tagStr("word", "l",  "g@a.com", "hij");  // 19       
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
    
    /* behavior is same to TestTagsScanningCursor
    @Test
    public void testXMLGeneratingCursor() {
        
    } */   
    
    @Test 
    public void testCursorsSequence() throws DocumentProcessingException {
        reinitDocument();
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(documentHolder.findElmStart(10));
        documentHolder.applyCursor(new DocumentElementDeletionByPosCursor(17)); // delete 'zab'
        // 30 - 3 = 27, 49 - 5 = 44 : 3 chars/5 elements deleted, 'lm' element search
        documentHolder.scrollToPos(
                documentHolder.applyCursor(new DocumentElementStartPosSearchingCursor(27, true)));
        // documentHolder.scrollToPos(documentHolder.findElmStart(chars));        
        
        // (*14) + (*29 - *14) == (*29)
        // (*5) * 3 = (*15) // 3 of 3-chars elements after 'zab' to 'lm' 
        Assert.assertEquals("(*29)(-[)(-zab)(-])(*15)", 
                getRecord(documentHolder.finishOperations()));   
        
        // FIXME: there is no inserting cursors still, but may be they are required? 
        // cursors sequences are mutations, may be test them in separate test?
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
    
    private static AbstractDocumentTag easyTag(String tagID, String tagName, String author, String content) {
        return AbstractDocumentTag.createNoAttrs(tagID, tagName, author, content);
    }
    
    private static String tagStr(String tagName, String tagID, String author, String content) {
        return "[{" + tagName + ":" +
                    ((tagID != null) ? (ID_ATTR + "=" + tagID + ";") : "") +
                    ((author != null) ? (BY_ATTR + "=" + author) : "") +
                "}" + content + "]";        
    }
    
    private static String tagStrEnc(String tagName, String tagID, String author, String content) {
        return "[{" + tagName + ":" +
                    ((author != null) ? (BY_ATTR + "=" + author + ";") : "") +
                    ((tagID != null)  ? (ID_ATTR + "=" + tagID + ";") : "") +
                "}" + content + "]";        
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
