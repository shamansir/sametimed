/**
 * 
 */
package name.shamansir.sametimed.test;

import name.shamansir.sametimed.test.mock.DocumentHolder;
import name.shamansir.sametimed.test.mock.EasyEncodedDocumentBuilder;
import name.shamansir.sametimed.test.mock.OperationsRecordingCursor;
import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;
import name.shamansir.sametimed.wave.doc.sequencing.AbstractOperatingCursor;
import name.shamansir.sametimed.wave.doc.sequencing.AbstractOperatingCursorWithResult;
import name.shamansir.sametimed.wave.doc.sequencing.DocumentProcessingException;

import org.junit.Assert;
import org.junit.Test;
import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuffer;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

/**
 * TestDocumentSequencerWithTreeDocument
 *
 * @author shamansir
 * @date Mar 12, 2010 3:26:01 PM
 *
 */
public class TestDocumentSequencerWithTreeDocument {
    
    private static final String DEFAULT_TAG_NAME = "a"; 
    
    private DocumentHolder documentHolder = new DocumentHolder();
    private OperationsRecordingCursor recordingCursor = new OperationsRecordingCursor();    
    
    @Test
    public void testScrolling() throws DocumentProcessingException {
        
        // FIXME: check when characters added not by sequence of 'characters(char)' call
        //        but with characters(several-chars) calls
        
        useDocument("[abcd[ef[i]g]][[hjk][lmno[p[rst]q]]]");
        
        // test scrolling to 7 chars pos         
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(7); // 7 chars, after 'i'        
        // 12345678901
        // [abcd[ef[i]        
        Assert.assertEquals("(*11)", 
                            getRecord(documentHolder.finishOperations()));
                
        // test scrolling to 10 chars pos while scrolled before
        
        resetRecorder();        
        documentHolder.startOperations();        
        documentHolder.scrollToPos(8); //  to 8  chars, after 'g'
        documentHolder.scrollToPos(10); // to 10 chars, between 'j' and 'k'        
        // 123456789012345 123
        // [abcd[ef[i]g]][ [hj k][lmno[
        Assert.assertEquals("(*18)", 
                            getRecord(documentHolder.finishOperations()));
        
        // test scrolling between tags
        
        resetRecorder();    
        documentHolder.startOperations();
        documentHolder.scrollToPos(11); // to 11 chars, between 'k' and 'l'
        documentHolder.scrollToPos(16); // to 16 chars, between 'p' and 'r'
        // 12345678901234567890 1234567
        // [abcd[ef[i]g]][[hjk] [lmno[p [rst]q]]]
        //  1234 56 7 8    901   2345 6  789 0        
        Assert.assertEquals("(*27)", 
                            getRecord(documentHolder.finishOperations())); 
        
        // test scrolling to end manually
        
        resetRecorder();    
        documentHolder.startOperations();        
        documentHolder.scrollToPos(20); // to the end
        // 123456789012345678901234567890123456
        // [abcd[ef[i]g]][[hjk][lmno[p[rst]q]]]
        //  1234 56 7 8    901  2345 6 789 0        
        Assert.assertEquals("(*36)", 
                            getRecord(documentHolder.finishOperations()));
        
        // test scrolling skipping empty tags
        
        resetRecorder();
        useDocument("[ab[]cd[ef[i]g]][[][hj[]k]]");
        documentHolder.startOperations();        
        documentHolder.scrollToPos(3); // between 'c' and 'd'
        // 123456 
        // [ab[]c d[ef[i]g]][[][hj[]k]]
        //  12  3 4 56 7 8      90  1
        Assert.assertEquals("(*6)", 
                getRecord(documentHolder.finishOperations()));         
        
        resetRecorder();
        documentHolder.startOperations();        
        documentHolder.scrollToPos(8); // after 'g'
        // 1234567890123456789
        // [ab[]cd[ef[i]g]][[] [hj[]k]]
        //  12  34 56 7 8       90  1
        Assert.assertEquals("(*19)", 
                getRecord(documentHolder.finishOperations()));        
            
    }
    
    @Test
    public void testScrollingAndAdding() throws DocumentProcessingException {
        
        // test scrolling to 14 chars and add tag there
        
        useDocument("[abcd[ef[i]g]][[hjk][lmno[p[rst]q]]]");  
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(4); //  to 4  chars, between 'd' and 'e'
        documentHolder.scrollToPos(14); // to 14 chars, between 'n' and 'o'
        
        DocOpBuilder doBuilder = documentHolder.unhideOp();
        doBuilder.elementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
        doBuilder.characters("qrst");
        doBuilder.elementEnd();
                
        // 12345 1234567890123456789
        // [abcd [ef[i]g]][[hjk][lmn o[p[rst]q]]]
        //  1234  56 7 8    901  234
        Assert.assertEquals("(*24){qrst}", 
                            getRecord(documentHolder.finishOperations()));
        // docCode now: [abcd[ef[i]g]][[hjk][lmn[qrst]o[p[rst]q]]]    

    }
            
    @Test
    public void testScrollingAddingAndScrollingAgain() throws DocumentProcessingException {
        
        // test scrolling to 6 chars, making tag and then scrolling to 24 chars
        
        useDocument("[abcd[ef[i]g]][[hjk][lmno[p[rst]q]]]");  
        
        documentHolder.startOperations();        
        documentHolder.scrollToPos(6); //  to 6 chars, between 'f' and 'i'
        
        DocOpBuilder doBuilder = documentHolder.unhideOp();
        doBuilder.elementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
        doBuilder.characters("qrst");
        doBuilder.elementEnd();
        // docCode now: [abcd[ef[qrst][i]g]][[hjk][lmno[p[rst]q]]]
        //               1234 56 7890  1 2    345  6789 0 123 4
        
        documentHolder.scrollToPos(11); // to 11 chars, between 'i' and 'g'
        documentHolder.scrollToPos(14); // to 14 chars, between 'j' and 'k'
        documentHolder.scrollToPos(16); // to 16 chars, between 'o' and 'p'
        documentHolder.scrollToPos(24); // to 24 chars, after 'q'
        
        try {
            documentHolder.scrollToPos(25);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass            
        }        
        
        // 12345678        123 1234567 1234567 12345678901
        // [abcd[ef [qrst] [i] g]][[hj k][lmno [p[rst]q]]]
        Assert.assertEquals("(*8){qrst}(*28)", 
                            getRecord(documentHolder.finishOperations()));
    }
    
    @Test
    public void testScrollingAndDeleting() throws DocumentProcessingException {

        // test scrolling to 6 chars and delete tag there
        
        useDocument("[abcd[ef[test][i]g]][[hjk][lmno[p[rst]q]]]");
        //            1234 56       7 8    901
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(6); //  to 6 chars, between 'f' and 't'
        
        DocOpBuilder doBuilder = documentHolder.unhideOp();
        doBuilder.deleteElementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
        doBuilder.deleteCharacters("test");
        doBuilder.deleteElementEnd();
        
        documentHolder.scrollToPos(8); //  to 8 chars, between 'g' and 'h'
        // FIX?ME: how to specify what element to delete here, wrapping or hjk?,
        //        or even not allow to delete wrapping tags?
        // SOLUTION: in situation like [[hjk - docWalker will scroll to position
        //           [.[hjk, and the next scrollTo will not raise an exception 
        //           about scrolling back, because the required number of chars is
        //           already reached, no way; when looking for tag start, when
        //           searching like [[hj?k it must also be positioned at [.[hjk; 
        //           for [[hjk]dj?jd[ and like this, it must be positioned before 
        //           the start of the wrapping tag: .[[hjk]djdj[]; so when deleting
        //           tags by char pos, in first variant [hjk] will be deleted and in
        //           second - the whole wrapping tag: [[hjk]djdj[...
        //            
        //           (dot is position of the scroller, q-mark is position of 
        //           search)
        // 
        //           or may be just return elements everywhere.
        // 
        // I'll postpone this tests to the time when tree documents will be
        // implemented
       
        
        doBuilder = documentHolder.unhideOp();
        doBuilder.deleteElementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
        doBuilder.deleteCharacters("hjk");
        doBuilder.deleteElementEnd();
        
        // 12345678 ------ 1234567 -----
        // [abcd[ef [test] [i]g]][ [hjk] [lmno
        Assert.assertEquals("(*8)(-{)(-test)(-})(*7)(-{)(-hjk)(-})", 
                            getRecord(documentHolder.finishOperations()));
        // docCode now: [abcd[ef[i]g]][[lmno[p[rst]q]]]
    }
    
    @Test
    public void testDeletingAndScrolling() throws DocumentProcessingException {
        
        Assert.fail();
        
        // test scrolling to 3 chars, delete tag there, and then scroll more
        
        useDocument("[abc][def][ghijkl][mnop]");  
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(3); //  to 3 chars, between 'c' and 'd'
        
        DocOpBuilder doBuilder = documentHolder.unhideOp();
        doBuilder.deleteElementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
        doBuilder.deleteCharacters("def");
        doBuilder.deleteElementEnd();
        // docCode now: [abc][ghijkl][mnop]
        
        documentHolder.scrollToPos(7); //  to 7 chars, between 'j' and 'k'
        documentHolder.scrollToPos(9); //  to 9 chars, between 'l' and 'm'
        
        // 12345 ----- 12345 123 
        // [abc] [def] [ghij kl] [mnop]
        Assert.assertEquals("(*5)(-{)(-def)(-})(*8)", 
                            getRecord(documentHolder.finishOperations()));
        // docCode now: [abc][ghijkl][mnop]
    }
    
    @Test
    public void testAddingAndDeleting() throws DocumentProcessingException {
        
        Assert.fail();        
        
        // test scrolling to 5 chars, add tag there, then scroll more, and then 
        // delete tag and scroll to end
        
        useDocument("[abcde][fghi][jklm][nopqr]");
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(5); //  to 5 chars, between 'e' and 'f'
        
        DocOpBuilder doBuilder = documentHolder.unhideOp();
        doBuilder.elementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
        doBuilder.characters("123456");
        doBuilder.elementEnd();
        // docCode now: [abcde][123456][fghi][jklm][nopqr]
        
        documentHolder.scrollToPos(15); //  to 15 chars, between 'i' and 'j'
        
        doBuilder.deleteElementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
        doBuilder.deleteCharacters("jk");
        doBuilder.deleteCharacters("lm");
        doBuilder.deleteElementEnd();
        // docCode now: [abcde][123456][fghi][nopqr]       
        
        documentHolder.scrollToPos(20); //  to 20 chars, after 'r'
        try {
            documentHolder.scrollToPos(21);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass            
        }
        
        // 1234567 ++++++++ 123456 ------ 1234567
        // [abcde] [123456] [fghi] [jklm] [nopqr]
        Assert.assertEquals("(*7){123456}(*6)(-{)(-jklm)(-})(*7)", 
                            getRecord(documentHolder.finishOperations()));
        // docCode now: [abcde][123456][fghi][nopqr]
    }
    
    @Test
    public void testDeletingAndAdding() throws DocumentProcessingException {
        
        Assert.fail();        
        
        // test scrolling to 5 chars, delete tag there, then scroll more, and then add tag and scroll to end
        
        useDocument("[abcde][fghij][klm][nopqrs]");  
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(5); //  to 5 chars, between 'e' and 'f'
        
        DocOpBuilder doBuilder = documentHolder.unhideOp();
        doBuilder.deleteElementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
        doBuilder.deleteCharacters("fghij");
        doBuilder.deleteElementEnd();
        // docCode now: [abcde][klm][nopqrs]
        
        documentHolder.scrollToPos(8); //  to 8 chars, between 'm' and 'n'
        
        doBuilder.elementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
        doBuilder.characters("12");
        doBuilder.characters("34567");
        doBuilder.elementEnd();
        // docCode now: [abcde][klm][1234567][nopqrs]      
        
        documentHolder.scrollToPos(21); //  to 21 chars, after 'r'
        try {
            documentHolder.scrollToPos(22);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass            
        }
        
        // 1234567 ------- 12345 +++++++++ 12345678
        // [abcde] [fghij] [klm] [1234567] [nopqrs]
        Assert.assertEquals("(*7)(-{)(-fghij)(-})(*5){1234567}(*8)", 
                            getRecord(documentHolder.finishOperations()));
        // docCode now: [abcde][123456][fghi][nopqr]
    }
    
    @Test
    public void testAligningToEnd() throws DocumentProcessingException {
        
        Assert.fail();        

        documentHolder.setCurrentDocument(createEmptyDocument());
        
        documentHolder.startOperations();
        documentHolder.alignDocToEnd();
        documentHolder.finishOperations();               
        
        final String docInitCode = "[abcde][fghij][klm][nopqrs]";
        useDocument(docInitCode);
        
        documentHolder.startOperations();
        documentHolder.alignDocToEnd();
        
        try {
            documentHolder.scrollToPos(3);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        
        Assert.assertEquals("(*" + docInitCode.length() + ")", 
                            getRecord(documentHolder.finishOperations()));     
    }
    
    @Test
    public void testScrollBy0AndTo0Passes() throws DocumentProcessingException {
        
        Assert.fail();        
        
        useDocument("[abcde][fghij][klm][nopqrs]");  
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(0);        
        documentHolder.scrollToPos(3);
        documentHolder.scrollToPos(3);
        documentHolder.scrollToPos(6);  
        
        try {
            documentHolder.scrollToPos(0);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        
        // 1234 12345
        // [abc de][f ghij]
        Assert.assertEquals("(*9)", 
                            getRecord(documentHolder.finishOperations()));
    }
        
    @Test
    public void testScrollingReturnsCorrectValues() throws DocumentProcessingException {
        
        Assert.fail();        
        
        useDocument("[abcde][fghij][klm][nopqrs]");  
        documentHolder.startOperations();
        Assert.assertEquals(4, documentHolder.scrollToPos(4));
        Assert.assertEquals(9, documentHolder.scrollToPos(9));
        Assert.assertEquals(11, documentHolder.scrollToPos(11));
        Assert.assertEquals(15, documentHolder.scrollToPos(15));
        documentHolder.finishOperations();
    }
    
    @Test
    public void testApplyingCursorWithResult() throws DocumentProcessingException {
        
        Assert.fail();        
        
        useDocument("[abcde][fghij][klm][nopqrs]");  
        
        Assert.assertEquals(19, documentHolder.applyCursor(new CharsCountingCursor()).intValue());      
        
        useDocument("[abc][defghijk]");
        
        documentHolder.startOperations();
        Assert.assertEquals(11, documentHolder.applyCursor(new CharsCountingCursor()).intValue());
        documentHolder.finishOperations();
    }
    
    @Test
    public void testApplyingOperatingCursor() throws DocumentProcessingException {
        
        Assert.fail();        
        
        // delete in the middle of document
        
        useDocument("[abcde][fghij][klm][nopqrs]");
        
        try {
            documentHolder.applyCursor(new TagDeletingCursor(2));
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(6); // between 'f' and 'g'
        documentHolder.applyCursor(new TagDeletingCursor(3)); // deletes third tag
        // docCode now: [abcde][fghij][nopqrs]
        documentHolder.scrollToPos(12); // between 'o' and 'p'
        
        // 123456789 12345 ----- 123
        // [abcde][f ghij] [klm] [no pqrs]
        Assert.assertEquals("(*14)(-{)(-klm)(-})(*3)", 
                            getRecord(documentHolder.finishOperations()));
        
        // delete in the end of document        
        
        resetRecorder();
        
        useDocument("[abcde][fghij][klm][nopqrs]");
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(8); // between 'g' and 'h'
        documentHolder.applyCursor(new TagDeletingCursor(4)); // delete fourth tag
        // docCode now: [abcde][fghij][klm]
        documentHolder.scrollToPos(13); // end
        
        try {
            documentHolder.scrollToPos(14);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }         
        
        // 1234567890 123456789 --------
        // [abcde][fg hij][klm] [nopqrs]
        Assert.assertEquals("(*19)(-{)(-nopqrs)(-})", 
                            getRecord(documentHolder.finishOperations()));
        
        // delete in the beginning of document        
        
        resetRecorder();
        
        useDocument("[abcde][fghij][klm][nopqrs]");
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(0); // beginning
        documentHolder.applyCursor(new TagDeletingCursor(1)); // delete first tag
        // docCode now: [fghij][klm][nopqrs]
        documentHolder.scrollToPos(14); // end
        
        try {
            documentHolder.scrollToPos(15);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }        
        
        // ------- 12345678901234567890
        // [abcde] [fghij][klm][nopqrs]
        Assert.assertEquals("(-{)(-abcde)(-})(*20)", 
                            getRecord(documentHolder.finishOperations()));        
        
        // add in the middle of document        
        
        resetRecorder();
        useDocument("[abcde][fghij][nopqrs]");        
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(4); // between 'd' and 'e'
        documentHolder.applyCursor(new TagInsertCursor(2, "123")); // insert '123' after second tag
        // docCode now: [abcde][fghij][123][nopqrs]
        documentHolder.scrollToPos(19); // the end       
        
        // 12345 123456789 +++++ 12345678
        // [abcd e][fghij] [123] [nopqrs]
        Assert.assertEquals("(*14){123}(*8)", 
                            getRecord(documentHolder.finishOperations()));
        
        // add in the end of document 
        
        resetRecorder();
        
        useDocument("[abcde][fghij][nopqrs]");        
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(7); // between 'f' and 'g'
        documentHolder.applyCursor(new TagInsertCursor(3, "12345")); // insert '12345' after third tag
        documentHolder.scrollToPos(21); // to the end (already there, though)
        // docCode now: [abcde][fghij][nopqrs][12345]
        
        try {
            documentHolder.scrollToPos(22);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        
        // 123456789 1234567890123 +++++++
        // [abcde][f ghij][nopqrs] [12345]
        Assert.assertEquals("(*22){12345}", 
                            getRecord(documentHolder.finishOperations()));
        
        // add in the beginning of document 
        
        resetRecorder();
        useDocument("[abc][defgh][ij]");        
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(0); // beginning, just to test
        documentHolder.applyCursor(new TagInsertCursor(0, "&%$!")); // insert '&%$!' before first tag
        documentHolder.scrollToPos(10); // between 'f' and 'g'
        documentHolder.scrollToPos(14); // to the end
        // docCode now: [&%$!][abc][defgh][ij]
        
        try {
            documentHolder.scrollToPos(15);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        
        // ++++++ 123456789 1234567
        // [&%$!] [abc][def gh][ij]
        Assert.assertEquals("{&%$!}(*16)", 
                            getRecord(documentHolder.finishOperations()));
        
        // add and delete in one sequence        
        
        resetRecorder();
        useDocument("[abcde][fghij][klm][nopqrs][tuvw]");        
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(2); // between 'b' and 'c'
        documentHolder.applyCursor(new TagInsertCursor(2, "123")); // insert '123' after second tag
        // docCode now: [abcde][fghij][123][klm][nopqrs][tuvw]
        documentHolder.scrollToPos(15); // between 'l' and 'm'
        documentHolder.applyCursor(new TagDeletingCursor(5)); // delete fifth tag
        // docCode now: [abcde][fghij][123][klm][tuvw]
        documentHolder.scrollToPos(18); // between 'u' and 'v'
        
        // 123 12345678901 +++++ 123 12 -------- 123
        // [ab cde][fghij] [123] [kl m] [nopqrs] [tu vw]
        Assert.assertEquals("(*14){123}(*5)(-{)(-nopqrs)(-})(*3)", 
                            getRecord(documentHolder.finishOperations()));
        
        resetRecorder();
        useDocument("[abcde][fghij][klm][nopqrs][tuvw]");        
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(2); // between 'b' and 'c'
        documentHolder.applyCursor(new TagDeletingCursor(2)); // delete second tag
        // docCode now: [abcde][klm][nopqrs][tuvw]
        documentHolder.scrollToPos(15); // between 't' and 'u'
        documentHolder.applyCursor(new TagInsertCursor(4, "123")); // insert '123' after fourth tag
        // docCode now: [abcde][klm][nopqrs][tuvw][123]
        documentHolder.scrollToPos(21); // end
        
        // 123 1234 ------- 123456789012345 1234 +++++
        // [ab cde] [fghij] [klm][nopqrs][t uvw] [123]
        Assert.assertEquals("(*7)(-{)(-fghij)(-})(*19){123}", 
                            getRecord(documentHolder.finishOperations()));         
        
    }   
    
    @Test
    public void testApplyingOperatingCursorWithResult() throws DocumentProcessingException {
        
        Assert.fail();        
        
        // deleting cursor, with result
        
        useDocument("[abcde][fghij][klm][nopqrs][tuvw]");
        
        try {
            documentHolder.applyCursor(new TagCuttingCursor(5));
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(4);
        Assert.assertEquals("nopqrs", 
                documentHolder.applyCursor(new TagCuttingCursor(4))); // deletes fourth tag
        // docCode now: [abcde][fghij][klm][tuvw]
        documentHolder.scrollToPos(16);
        
        // 1234 123456789012345 -------- 1234
        // [abc de][fghij][klm] [nopqrs] [tuv w]
        Assert.assertEquals("(*19)(-{)(-nopqrs)(-})(*4)", 
                            getRecord(documentHolder.finishOperations()));
        
        // adding cursor, with result
        
        resetRecorder();
        useDocument("[abc][defgh][ij]");        
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(1);
        Assert.assertEquals("&%$!", 
                documentHolder.applyCursor(new TagPastingCursor(1, "&%$!"))); // insert '&%$!' before first tag
        // docCode now: [abc][&%$!][defgh][ij]
        documentHolder.scrollToPos(14); // end
                
        // 12 345 ++++++ 12345678901
        // [a bc] [&%$!] [defgh][ij]
        Assert.assertEquals("(*5){&%$!}(*11)", 
                            getRecord(documentHolder.finishOperations()));         
    }
    
    @Test
    public void testSearchingElmStart() throws DocumentProcessingException {
        
        Assert.fail();        
        
        useDocument("[abc][def][ghijkl][mnop]");

        Assert.assertEquals(0, documentHolder.searchElmStart(0));
        Assert.assertEquals(0, documentHolder.searchElmStart(1));
        Assert.assertEquals(3, documentHolder.searchElmStart(3));
        Assert.assertEquals(3, documentHolder.searchElmStart(4));
        Assert.assertEquals(6, documentHolder.searchElmStart(6));        
        Assert.assertEquals(6, documentHolder.searchElmStart(9));
        Assert.assertEquals(6, documentHolder.searchElmStart(11));
        Assert.assertEquals(12, documentHolder.searchElmStart(12));
        Assert.assertEquals(12, documentHolder.searchElmStart(13));
        Assert.assertEquals(16, documentHolder.searchElmStart(16));
        
        useDocument("[abcd]");
        
        Assert.assertEquals(0, documentHolder.searchElmStart(0));
        Assert.assertEquals(0, documentHolder.searchElmStart(1));
        Assert.assertEquals(0, documentHolder.searchElmStart(3));
        Assert.assertEquals(4, documentHolder.searchElmStart(4));        
        
        resetRecorder();
        useDocument("[abc][def][ghijkl][mnop]");
        
        try {
            documentHolder.findElmStart(10);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(0);
        Assert.assertEquals(0, documentHolder.findElmStart(0));
        Assert.assertEquals(0, documentHolder.findElmStart(1));
        Assert.assertEquals(3, documentHolder.findElmStart(3));
        Assert.assertEquals(3, documentHolder.findElmStart(3)); 
        
        Assert.assertEquals("(*5)", 
                getRecord(documentHolder.finishOperations()));   
        
        resetRecorder();
        useDocument("[abc][def][ghijkl][mnop]");        
        
        documentHolder.startOperations();
        Assert.assertEquals(6, documentHolder.findElmStart(7));
        Assert.assertEquals(6, documentHolder.findElmStart(6));
        Assert.assertEquals("(*10)", 
                getRecord(documentHolder.finishOperations()));          
        
        resetRecorder();
        useDocument("[abc][def][ghijkl][mnop]");
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(4);
        try {
            documentHolder.findElmStart(4); 
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        Assert.assertEquals(6, documentHolder.findElmStart(6));
        Assert.assertEquals(6, documentHolder.findElmStart(9));
        Assert.assertEquals(6, documentHolder.findElmStart(6));
        Assert.assertEquals(6, documentHolder.findElmStart(11));
        Assert.assertEquals(12, documentHolder.findElmStart(12));
        Assert.assertEquals(12, documentHolder.findElmStart(13));
        Assert.assertEquals(12, documentHolder.findElmStart(15));
        Assert.assertEquals(16, documentHolder.findElmStart(16));        
        
        Assert.assertEquals("(*24)", 
                getRecord(documentHolder.finishOperations()));
        
    }
    
    // ================== private methods ======================================
    
    private static BufferedDocOp createDocument(String documentCode) {  
        return (new EasyEncodedDocumentBuilder(documentCode, DEFAULT_TAG_NAME)).compile();
    }    
    
    private BufferedDocOp useDocument(String documentCode) {  
        BufferedDocOp encodedDoc = createDocument(documentCode);
        documentHolder.setCurrentDocument(encodedDoc);
        return encodedDoc;
    }
    
    private String getRecord(WaveletDocumentOperation docOp) {
        BufferedDocOp opWasBuilt = docOp.getOperation();
        opWasBuilt.apply(recordingCursor);
        return recordingCursor.finish();
    }
    
    private void resetRecorder() {
        recordingCursor.erase();
    }
    
    private static BufferedDocOp createEmptyDocument() {
        return new DocOpBuffer().finish();
    }
            
    // ================== CharsCountingCursor ==================================
    
    private class CharsCountingCursor implements ICursorWithResult<Integer> {
        
        private int charCount = 0;

        @Override
        public void annotationBoundary(AnnotationBoundaryMap map) { }
        @Override
        public void characters(String chars) { charCount += chars.length(); }
        @Override
        public void elementEnd() { }
        @Override
        public void elementStart(String type, Attributes attrs) { }
        
        @Override
        public Integer getResult() {
            return charCount;
        }
        
    }
    
    // ================== TagDeletingCursor ====================================
    
    private class TagDeletingCursor extends AbstractOperatingCursor {
        
        private final int numToDelete;
        
        public TagDeletingCursor(int number) {
            numToDelete = number;
        }
        
        @Override
        public void elementStart(String type, Attributes attrs) { 
            if (docWalker.curPosTags() == numToDelete) {
                docBuilder.deleteElementStart(type, attrs);
            } else {
                docBuilder.retainElementStart();
            }                    
        }        
        
        @Override
        public void characters(String chars) { 
            if (docWalker.curPosTags() == numToDelete) {
                docBuilder.deleteCharacters(chars);
            } else {
                docBuilder.retainCharacters(chars.length());
            }
        }
        
        @Override
        public void elementEnd() { 
            if (docWalker.curPosTags() == numToDelete) {
                docBuilder.deleteElementEnd();
                detach();
            } else {
                docBuilder.retainElementEnd();
            }
        }
        
    }   
    
    // ================== TagInsertCursor ======================================
    
    private class TagInsertCursor extends AbstractOperatingCursor {
        
        private final int insertPoint;
        private final String charsToIns;
        
        public TagInsertCursor(int pos, String chars) {
            insertPoint = pos + 1;
            charsToIns = chars;
        }
        
        @Override
        protected void onAttached() {
            if (docWalker.curPosTags() == insertPoint) { // if it is first tag
                buildTag();
                detach();                
            }
        }       
        
        @Override
        public void elementStart(String type, Attributes attrs) { 
            docBuilder.retainElementStart();                    
        }        
        
        @Override
        public void characters(String chars) { 
            docBuilder.retainCharacters(chars.length());
        }
        
        @Override
        public void elementEnd() {
            docBuilder.retainElementEnd(); // tags number incremented just after retain
            if (docWalker.curPosTags() == insertPoint) {                
                buildTag();
                detach();
            }
        }
        
        private void buildTag() {
            docBuilder.elementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
            docBuilder.characters(charsToIns);
            docBuilder.elementEnd();    
        }
        
    }       
    
    // ================== TagCuttingCursor =====================================
    
    private class TagCuttingCursor extends AbstractOperatingCursorWithResult<String> {

        private final int numToDelete;
        private String charsCut = "";
        
        public TagCuttingCursor(int number) {
            numToDelete = number;
        }

        @Override
        public void annotationBoundary(AnnotationBoundaryMap map) { }
        
        @Override
        public void elementStart(String type, Attributes attrs) { 
            if (docWalker.curPosTags() == numToDelete) {
                docBuilder.deleteElementStart(type, attrs);
            } else {
                docBuilder.retainElementStart();
            }                    
        }        
        
        @Override
        public void characters(String chars) { 
            if (docWalker.curPosTags() == numToDelete) {
                docBuilder.deleteCharacters(chars);
                charsCut += chars;
            } else {
                docBuilder.retainCharacters(chars.length());
            }
        }
        
        @Override
        public void elementEnd() { 
            if (docWalker.curPosTags() == numToDelete) {
                docBuilder.deleteElementEnd();
                detach();
            } else {
                docBuilder.retainElementEnd();
            }
        }

        @Override
        public String getResult() {
            return charsCut;
        }
        
    }
    
    // ================== TagPastingCursor =====================================
    
    private class TagPastingCursor extends AbstractOperatingCursorWithResult<String> {
        
        private final int insertPoint;
        private final String charsToIns;
        
        public TagPastingCursor(int pos, String chars) {
            insertPoint = pos + 1;
            charsToIns = chars;
        }
        
        @Override
        protected void onAttached() {
            if (docWalker.curPosTags() == insertPoint) { // if it is first tag
                buildTag();
                detach();                
            }
        }       
        
        @Override
        public void elementStart(String type, Attributes attrs) { 
            docBuilder.retainElementStart();                    
        }        
        
        @Override
        public void characters(String chars) { 
            docBuilder.retainCharacters(chars.length());
        }
        
        @Override
        public void elementEnd() {
            docBuilder.retainElementEnd(); // tags number incremented just after retain
            if (docWalker.curPosTags() == insertPoint) {                
                buildTag();
                detach();
            }
        }
        
        private void buildTag() {
            docBuilder.elementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
            docBuilder.characters(charsToIns);
            docBuilder.elementEnd();    
        }

        @Override
        public String getResult() {
            return charsToIns;
        }
        
    }   
    

}
