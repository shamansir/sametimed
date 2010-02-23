package name.shamansir.sametimed.test;

import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;
import name.shamansir.sametimed.wave.doc.sequencing.AbstractDocumentOperationsSequencer;
import name.shamansir.sametimed.wave.doc.sequencing.AbstractOperatingCursor;
import name.shamansir.sametimed.wave.doc.sequencing.AbstractOperatingCursorWithResult;
import name.shamansir.sametimed.wave.doc.sequencing.DocumentProcessingException;

import org.junit.Assert;
import org.junit.Test;
import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.AttributesUpdate;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.EvaluatingDocOpCursor;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuffer;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;

public class TestDocumentSequencer {
    
    private static final String DEFAULT_TAG_NAME = "a"; 
	
	private DocumentHolder documentHolder = new DocumentHolder();
	SimpleOperationsRecordingCursor/*EvaluatingDocOpCursor<String>*/ recordingCursor = new SimpleOperationsRecordingCursor();
	
	@Test
	public void testSequencing() {
		BufferedDocOp encodedDoc = createDocument("[abcd]");
		
		try {
		    documentHolder.setCurrentDocument(encodedDoc);		
		    documentHolder.startOperations();
			documentHolder.finishOperations();
			encodedDoc.apply(recordingCursor);
			Assert.assertEquals("{abcd}", recordingCursor.finish());
		} catch (DocumentProcessingException dpe) {
			Assert.fail(dpe.getMessage());
		}
		
		// finish, when already finished
		try {
			documentHolder.finishOperations();
			Assert.fail("exception expected");
		} catch (DocumentProcessingException dpe) { }
		
		// start
		try {
			documentHolder.startOperations();
		} catch (DocumentProcessingException dpe) {
			Assert.fail(dpe.getMessage());
		}
		
		// try to start again
		try {
			documentHolder.startOperations();
			Assert.fail("exception expected");
		} catch (DocumentProcessingException dpe) { }
		
		// finish
		try {
			documentHolder.finishOperations();		
		} catch (DocumentProcessingException dpe) {
			Assert.fail(dpe.getMessage());
		}
		
		// apply cursor while not started
		try {
			documentHolder.applyCursor(new EmptyCursor());
			Assert.fail("exception expected");
		} catch (DocumentProcessingException dpe) { }			
		
		// start
		try {
			documentHolder.startOperations();
		} catch (DocumentProcessingException dpe) {
			Assert.fail(dpe.getMessage());
		}		
		
		// apply null cursor
		try {
			documentHolder.applyCursor((AbstractOperatingCursorWithResult<String>)null);
			Assert.fail("exception expected");
		} catch (DocumentProcessingException dpe) { }				
		
		// finish
		try {
			documentHolder.finishOperations();
		} catch (DocumentProcessingException dpe) {
			Assert.fail(dpe.getMessage());
		}				
	}	
	
	@Test
	public void testScrolling() throws DocumentProcessingException {
		
		// FIXME: check when characters added not by sequence of 'characters(char)' call
		//        but with characters(several-chars) calls
		
		BufferedDocOp opWasBuilt = null; 
		
		// test scrolling to 5 chars pos
		final String docInitCode = "[abcdefgh][ijkl][mnop]";  
		BufferedDocOp encodedDoc = createDocument(docInitCode);
		documentHolder.setCurrentDocument(encodedDoc);
		
		documentHolder.startOperations();
		documentHolder.scrollToPos(5); // 5 chars, between 'e' and 'f'
		
		opWasBuilt = documentHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor); //                     123456
		
		Assert.assertEquals("(*6)", recordingCursor.finish()); // [abcde
		
		// test scrolling to 11 chars pos while scrolled before
		recordingCursor.erase();
		documentHolder.startOperations();
		
		documentHolder.scrollToPos(5); //  to 5  chars, between 'e' and 'f'
		documentHolder.scrollToPos(11); // to 11 chars, between 'k' and 'l'
		
		opWasBuilt = documentHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor); //                          123456 12345678
		//Assert.assertEquals("(*6)(*8)", recordingCursor.finish());// [abcde fgh][ijk
		Assert.assertEquals("(*14)", recordingCursor.finish());
		
		// test scrolling between tags
		recordingCursor.erase();	
		documentHolder.startOperations();
		
		documentHolder.scrollToPos(8); //  to 8  chars, between 'h' and 'i'
		documentHolder.scrollToPos(12); // to 12 chars, between 'l' and 'm'
		
		opWasBuilt = documentHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor); //                            1234567890 123456 
		// Assert.assertEquals("(*10)(*6)", recordingCursor.finish());// [abcdefgh] [ijkl] [m
		Assert.assertEquals("(*16)", recordingCursor.finish());
		
		// test scrolling to end manually
		recordingCursor.erase();	
		documentHolder.startOperations();
		
		documentHolder.scrollToPos(16); // to the end
		
		opWasBuilt = documentHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor); 
		Assert.assertEquals("(*22)", recordingCursor.finish());
			
	}
	
	@Test
	public void testScrollingAndAdding() throws DocumentProcessingException {
		
		BufferedDocOp opWasBuilt = null; 
		
		// test scrolling to 14 chars and add tag there
		final String docInitCode = "[abcdefgh][ijkl][mnop]";  
		BufferedDocOp encodedDoc = createDocument(docInitCode);
		documentHolder.setCurrentDocument(encodedDoc);		
		
		documentHolder.startOperations();
		documentHolder.scrollToPos(4); //  to 4  chars, between 'd' and 'e'
		documentHolder.scrollToPos(14); // to 14 chars, between 'n' and 'o'
		
		DocOpBuilder doBuilder = documentHolder.unhideOp();
		doBuilder.elementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
		doBuilder.characters("qrst");
		doBuilder.elementEnd();
		
		opWasBuilt = documentHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor);
		
		// 12345 12345678901234
		// [abcd efgh][ijkl][mn
		//Assert.assertEquals("(*5)(*14){qrst}", recordingCursor.finish());
		Assert.assertEquals("(*19){qrst}", recordingCursor.finish());
		// docCode now: [abcdefgh][ijkl][mn[qrst]op]"		

	}
	
	@Test
	public void testAddingAndScrolling() throws DocumentProcessingException {
		BufferedDocOp opWasBuilt = null; 
		
		// test making tag and then scrolling to 12 chars
		final String docInitCode = "[abcdefgh][ijkl][mnop]";  
		BufferedDocOp encodedDoc = createDocument(docInitCode);
		documentHolder.setCurrentDocument(encodedDoc);
		
		documentHolder.startOperations();		
		
		DocOpBuilder doBuilder = documentHolder.unhideOp();
		doBuilder.elementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
		doBuilder.characters("qrst");
		doBuilder.elementEnd();
		// docCode now: [qrst][abcdefgh][ijkl][mnop]
		
		// test scrolling back (to 3 chars or less) is not possible, 4 chars
		// must be positioned just before the 'a' char
		documentHolder.scrollToPos(4); //  to 4  chars, between 't' and 'a'
		documentHolder.scrollToPos(7); //  to 7  chars, between 'c' and 'd'
		documentHolder.scrollToPos(12); // to 12 chars, between 'h' and 'i'
		
		opWasBuilt = documentHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor);
		
		// 0-step will be skipped
		
		//        1234 123456  
		// [qrst].[abc defgh] [i
		//Assert.assertEquals("{qrst}(*4)(*6)", recordingCursor.finish());
		Assert.assertEquals("{qrst}(*10)", recordingCursor.finish());
	}
		
	@Test
	public void testScrollingAddingAndScrollingAgain() throws DocumentProcessingException {
        BufferedDocOp opWasBuilt = null; 
        
        // test scrolling to 5 chars, making tag and then scrolling to 12 chars
        final String docInitCode = "[abcdefgh][ijkl][mnop]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);
        
        documentHolder.startOperations();
        
        documentHolder.scrollToPos(5); //  to 5 chars, between 'e' and 'f'
        
        DocOpBuilder doBuilder = documentHolder.unhideOp();
        doBuilder.elementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
        doBuilder.characters("qrst");
        doBuilder.elementEnd();
        // docCode now: [abcde[qrst]fgh][ijkl][mnop]
        
        documentHolder.scrollToPos(10); //  to 10 chars, between 'f' and 'g'
        documentHolder.scrollToPos(14); //  to 14  chars, between 'j' and 'k'
        documentHolder.scrollToPos(16); // to 16 chars, between 'l' and 'm'
        documentHolder.scrollToPos(20); // to 20 chars, after 'p'
        
        try {
            documentHolder.scrollToPos(21);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass            
        }        
        
        opWasBuilt = documentHolder.finishOperations().getOperation();
        opWasBuilt.apply(recordingCursor);
        
        // 123456        1 123456 123 123456 
        // [abcde [qrst] f gh][ij kl] [mnop]
        //Assert.assertEquals("(*6){qrst}(*1)(*6)(*3)(*6)", recordingCursor.finish());
        Assert.assertEquals("(*6){qrst}(*16)", recordingCursor.finish());
	}
	
	@Test
	public void testScrollingAndDeleting() throws DocumentProcessingException {
        BufferedDocOp opWasBuilt = null; 
        
        // test scrolling to 6 chars and delete tag there
        final String docInitCode = "[abc][def][ghijkl][mnop]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);     
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(4); //  to 4 chars, between 'd' and 'e'
        documentHolder.scrollToPos(6); //  to 6 chars, between 'f' and 'g'
        
        DocOpBuilder doBuilder = documentHolder.unhideOp();
        doBuilder.deleteElementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
        doBuilder.deleteCharacters("ghijkl");
        doBuilder.deleteElementEnd();
        
        opWasBuilt = documentHolder.finishOperations().getOperation();
        opWasBuilt.apply(recordingCursor);
        
        // 1234567 123 -------- 
        // [abc][d ef] [ghijkl] [mnop]
        //Assert.assertEquals("(*7)(*3)(-{)(-ghijkl)(-})", recordingCursor.finish());
        Assert.assertEquals("(*10)(-{)(-ghijkl)(-})", recordingCursor.finish());
        // docCode now: [abc][def][mnop]
	}
	
	@Test
	public void testDeletingAndScrolling() throws DocumentProcessingException {
        BufferedDocOp opWasBuilt = null; 
        
        // test scrolling to 3 chars, delete tag there, and then scroll more
        final String docInitCode = "[abc][def][ghijkl][mnop]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);     
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(3); //  to 3 chars, between 'c' and 'd'
        
        DocOpBuilder doBuilder = documentHolder.unhideOp();
        doBuilder.deleteElementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
        doBuilder.deleteCharacters("def");
        doBuilder.deleteElementEnd();
        // docCode now: [abc][ghijkl][mnop]
        
        documentHolder.scrollToPos(7); //  to 7 chars, between 'j' and 'k'
        documentHolder.scrollToPos(9); //  to 9 chars, between 'l' and 'm'
        
        opWasBuilt = documentHolder.finishOperations().getOperation();
        opWasBuilt.apply(recordingCursor);
        
        // 12345 ----- 12345 123 
        // [abc] [def] [ghij kl] [mnop]
        //Assert.assertEquals("(*5)(-{)(-def)(-})(*5)(*3)", recordingCursor.finish());
        Assert.assertEquals("(*5)(-{)(-def)(-})(*8)", recordingCursor.finish());
        // docCode now: [abc][ghijkl][mnop]
	}
	
	@Test
	public void testAddingAndDeleting() throws DocumentProcessingException {
        BufferedDocOp opWasBuilt = null; 
        
        // test scrolling to 5 chars, add tag there, then scroll more, and then 
        // delete tag and scroll to end
        final String docInitCode = "[abcde][fghi][jklm][nopqr]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);     
        
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
        
        opWasBuilt = documentHolder.finishOperations().getOperation();
        opWasBuilt.apply(recordingCursor);
        
        // 1234567 ++++++++ 123456 ------ 1234567
        // [abcde] [123456] [fghi] [jklm] [nopqr]
        Assert.assertEquals("(*7){123456}(*6)(-{)(-jk)(-lm)(-})(*7)", recordingCursor.finish());
        // docCode now: [abcde][123456][fghi][nopqr]
	}
	
	@Test
	public void testDeletingAndAdding() throws DocumentProcessingException {
        BufferedDocOp opWasBuilt = null; 
        
        // test scrolling to 5 chars, delete tag there, then scroll more, and then add tag and scroll to end
        final String docInitCode = "[abcde][fghij][klm][nopqrs]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);
        
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
        
        opWasBuilt = documentHolder.finishOperations().getOperation();
        opWasBuilt.apply(recordingCursor);
        
        // 1234567 ------- 12345 +++++++++ 12345678
        // [abcde] [fghij] [klm] [1234567] [nopqrs]
        Assert.assertEquals("(*7)(-{)(-fghij)(-})(*5){1234567}(*8)", recordingCursor.finish());
        // docCode now: [abcde][123456][fghi][nopqr]
	}
	
    @Test
    public void testScrollingBack() throws DocumentProcessingException {
        final String docInitCode = "[abcde][fghij][klm][nopqrs]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);
        
        documentHolder.startOperations();
        
        documentHolder.scrollToPos(6);
        
        try {
            documentHolder.scrollToPos(5);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        
        try {
            documentHolder.scrollToPos(3);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        
        try {
            documentHolder.scrollToPos(0);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }        
        
        documentHolder.finishOperations();
        
    }
	
	@Test
    public void testScrollingFurtherEnd() throws DocumentProcessingException {
	    final String docInitCode = "[abcde][fghij][klm][nopqrs]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);
        
        documentHolder.startOperations();
        
        try {
            documentHolder.scrollToPos(45);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        
        documentHolder.finishOperations();
	}
	
    @Test
    public void testAligningToEnd() throws DocumentProcessingException {

        documentHolder.setCurrentDocument(createEmptyDocument());
        documentHolder.startOperations();
        documentHolder.alignDocToEnd();
        documentHolder.finishOperations();               
        
        final String docInitCode = "[abcde][fghij][klm][nopqrs]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);
        
        documentHolder.startOperations();
        documentHolder.alignDocToEnd();
        
        try {
            documentHolder.scrollToPos(3);
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        
        BufferedDocOp opWasBuilt = documentHolder.finishOperations().getOperation();
        opWasBuilt.apply(recordingCursor); 
        Assert.assertEquals("(*" + docInitCode.length() + ")", recordingCursor.finish());     
    }
    
    @Test
    public void testScrollBy0AndTo0Passes() throws DocumentProcessingException {
        final String docInitCode = "[abcde][fghij][klm][nopqrs]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(0);        
        documentHolder.scrollToPos(3);
        documentHolder.scrollToPos(3);
        documentHolder.scrollToPos(6);        
        
        BufferedDocOp opWasBuilt = documentHolder.finishOperations().getOperation();
        opWasBuilt.apply(recordingCursor);
        // 1234 12345
        // [abc de][f ghij]
        //Assert.assertEquals("(*4)(*5)", recordingCursor.finish());
        Assert.assertEquals("(*9)", recordingCursor.finish());
    }
	
	@Test
	public void testSequencingOverEmptyDoc() throws DocumentProcessingException {
	    documentHolder.setCurrentDocument(createEmptyDocument());
	    documentHolder.startOperations();
	    documentHolder.finishOperations();
	}
	
	// FIXME: add addTag/deleteTag tests
	
	@Test
	public void testScrollingReturnsCorrectValues() throws DocumentProcessingException {
	    String docInitCode = "[abcde][fghij][klm][nopqrs]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);
        documentHolder.startOperations();
        Assert.assertEquals(4, documentHolder.scrollToPos(4));
        Assert.assertEquals(9, documentHolder.scrollToPos(9));
        Assert.assertEquals(11, documentHolder.scrollToPos(11));
        Assert.assertEquals(15, documentHolder.scrollToPos(15));
        documentHolder.finishOperations();
	}
	
	@Test
	public void testApplyingCursorWithResult() throws DocumentProcessingException {
	    
        String docInitCode = "[abcde][fghij][klm][nopqrs]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);
        
        Assert.assertEquals(19, documentHolder.applyCursor(new CharsCountingCursor()).intValue());      
        
        docInitCode = "[abc][defghijk]";  
        encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);
        
        documentHolder.startOperations();
        Assert.assertEquals(11, documentHolder.applyCursor(new CharsCountingCursor()).intValue());
        documentHolder.finishOperations();
	}
	
    @Test
    public void testApplyingOperatingCursor() throws DocumentProcessingException {
        final String docInitCode = "[abcde][fghij][klm][nopqrs]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);
        
        try {
            documentHolder.applyCursor(new TagDeletingCursor(2));
            Assert.fail();
        } catch (DocumentProcessingException dpe) {
            // pass
        }
        
        documentHolder.startOperations();
        documentHolder.scrollToPos(6);
        documentHolder.applyCursor(new TagDeletingCursor(3)); // deletes third tag
        // docCode now: [abcde][fghij][nopqrs]
        documentHolder.scrollToPos(12);
        
        BufferedDocOp opWasBuilt = documentHolder.finishOperations().getOperation();
        opWasBuilt.apply(recordingCursor);
        // 123456789 12346 ----- 123
        // [abcde][f ghij] [klm] [no pqrs]
        Assert.assertEquals("(*9)(*6)(-{)(-klm)(-})(*3)", recordingCursor.finish());
    }	
	
    @Test
    public void testApplyingOperatingCursorWithResult() throws DocumentProcessingException {
        final String docInitCode = "[abcde][fghij][klm][nopqrs][tuvw]";  
        BufferedDocOp encodedDoc = createDocument(docInitCode);
        documentHolder.setCurrentDocument(encodedDoc);
        
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
        documentHolder.scrollToPos(16);
        
        BufferedDocOp opWasBuilt = documentHolder.finishOperations().getOperation();
        opWasBuilt.apply(recordingCursor);
        // 1234 123456789012345 -------- 1234
        // [abc de][fghij][klm] [nopqrs] [tuv w]
        Assert.assertEquals("(*4)(*15)(-{)(-nopqrs)(-})(*4)", recordingCursor.finish());
    }
		
	private BufferedDocOp createDocument(String documentCode) {
		return (new EncodedDocumentBuilder(documentCode)).compile();
	}
	
	private static BufferedDocOp createEmptyDocument() {
	    return new DocOpBuffer().finish();
	}
	
	private class DocumentHolder extends AbstractDocumentOperationsSequencer {
		
		private BufferedDocOp curDocument = null;

		public void setCurrentDocument(BufferedDocOp curDocument) {
			this.curDocument = curDocument;
		}

		@Override
		public String getDocumentID() {
			return "test";
		}

		@Override
		protected BufferedDocOp getSource() {
			return curDocument;
		}
		
		public DocOpBuilder unhideOp() {
			return getDocBuilder();
		}
		
	}	
	
	private class EncodedDocumentBuilder {

		private DocOpBuffer document = new DocOpBuffer();
		
		private final String code; 

		public EncodedDocumentBuilder(String code) {
			this.code = code;
		}
		
		public BufferedDocOp compile() {
			for (byte b: code.getBytes()) {
				if (b == (byte)'[') {
					document.elementStart(DEFAULT_TAG_NAME, AttributesImpl.EMPTY_MAP);
				} else if (b == (byte)']') {
					document.elementEnd();
				} else {
					document.characters(String.valueOf((char)b));
				}
			}
			return document.finish();
		}
		
	}
	
	private class SimpleOperationsRecordingCursor implements EvaluatingDocOpCursor<String> {
		
		private final StringBuffer operationsRecorder = new StringBuffer();
		
		private String escape(String input) {
			String output = input.replaceAll("\\}", "\\}");
			output = output.replaceAll("\\{", "\\{");
			output = output.replaceAll("\\)", "\\)");
			return output.replaceAll("\\(", "\\(");
		}

		@Override
		public String finish() { 
		    return operationsRecorder.toString(); 
		}
		
		public void erase() { 
		    operationsRecorder.delete(0, operationsRecorder.length()); 
		}

		@Override
		public void deleteCharacters(String chars) {
			operationsRecorder.append("(-" + escape(chars) + ")");
		}

		@Override
		public void deleteElementEnd() { 
		    operationsRecorder.append("(-})"); 
		}

		@Override
		public void deleteElementStart(String type, Attributes attrs) {
			operationsRecorder.append("(-{)");
		}

		@Override
		public void replaceAttributes(Attributes oldAttrs, Attributes newAttrs) { }

		@Override
		public void retain(int itemCount) {
			operationsRecorder.append("(*" + itemCount + ")");			
		}

		@Override
		public void updateAttributes(AttributesUpdate attrUpdate) { }

		@Override
		public void annotationBoundary(AnnotationBoundaryMap map) { }

		@Override
		public void characters(String chars) {
			operationsRecorder.append(escape(chars));
		}

		@Override
		public void elementEnd() {
			operationsRecorder.append("}");			
		}

		@Override
		public void elementStart(String type, Attributes attrs) {
			operationsRecorder.append("{");
		}		
		
	}
	
	private class EmptyCursor extends AbstractOperatingCursorWithResult<String> {

		@Override
		public String getResult() { return ""; }

		@Override
		public void annotationBoundary(AnnotationBoundaryMap map) { }
		@Override
		public void characters(String chars) { }
		@Override
		public void elementEnd() { }
		@Override
		public void elementStart(String type, Attributes attrs) { }
		
	}
	
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
	
    private class TagDeletingCursor extends AbstractOperatingCursor {
        
        private final int numToDelete;
        
        public TagDeletingCursor(int number) {
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
            } else {
                docBuilder.retainCharacters(chars.length());
            }
        }
        
        @Override
        public void elementEnd() { 
            if (docWalker.curPosTags() == numToDelete) {
                docBuilder.deleteElementEnd();
            } else {
                docBuilder.retainElementEnd(); // -> getWalker().stepElmFwd ??
            }
        }
        
    }	
	
    private class TagCuttingCursor extends AbstractOperatingCursorWithResult<String> {

        public TagCuttingCursor(int number) {
            
        }        
        
        @Override
        public String getResult() { return ""; }

        @Override
        public void annotationBoundary(AnnotationBoundaryMap map) { }
        @Override
        public void characters(String chars) { }
        @Override
        public void elementEnd() { }
        @Override
        public void elementStart(String type, Attributes attrs) { }
        
    }	
	
}
