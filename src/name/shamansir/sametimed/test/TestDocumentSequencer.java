package name.shamansir.sametimed.test;

import name.shamansir.sametimed.wave.doc.cursor.IOperatingCursorWithResult;
import name.shamansir.sametimed.wave.doc.sequencing.AbstractDocumentOperationsSequencer;
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
	
	private DocumentsHolder documentsHolder = new DocumentsHolder();
	SimpleOperationsRecordingCursor/*EvaluatingDocOpCursor<String>*/ recordingCursor = new SimpleOperationsRecordingCursor();
	
	@Test
	public void testSequencing() {
		BufferedDocOp encodedDoc = createDocument("[abcd]");
		
		try {
			documentsHolder.setCurrentDocument(encodedDoc);		
			documentsHolder.startOperations();
			documentsHolder.finishOperations();
			encodedDoc.apply(recordingCursor);
			Assert.assertEquals("{abcd}", recordingCursor.finish());
		} catch (DocumentProcessingException dpe) {
			Assert.fail(dpe.getMessage());
		}
		
		// finish, when already finished
		try {
			documentsHolder.finishOperations();
			Assert.fail("exception expected");
		} catch (DocumentProcessingException dpe) { }
		
		// start
		try {
			documentsHolder.startOperations();
		} catch (DocumentProcessingException dpe) {
			Assert.fail(dpe.getMessage());
		}
		
		// try to start again
		try {
			documentsHolder.startOperations();
			Assert.fail("exception expected");
		} catch (DocumentProcessingException dpe) { }
		
		// finish
		try {
			documentsHolder.finishOperations();		
		} catch (DocumentProcessingException dpe) {
			Assert.fail(dpe.getMessage());
		}
		
		// apply cursor while not started
		try {
			documentsHolder.applyCursor(new EmptyCursor());
			Assert.fail("exception expected");
		} catch (DocumentProcessingException dpe) { }			
		
		// start
		try {
			documentsHolder.startOperations();
		} catch (DocumentProcessingException dpe) {
			Assert.fail(dpe.getMessage());
		}		
		
		// apply null cursor
		try {
			documentsHolder.applyCursor((IOperatingCursorWithResult<String>)null);
			Assert.fail("exception expected");
		} catch (DocumentProcessingException dpe) { }				
		
		// finish
		try {
			documentsHolder.finishOperations();
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
		documentsHolder.setCurrentDocument(encodedDoc);
		
		documentsHolder.startOperations();
		documentsHolder.scrollToPos(5); // 5 chars, between 'e' and 'f'
		
		opWasBuilt = documentsHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor); //                     123456
		
		Assert.assertEquals("(*6)", recordingCursor.finish()); // [abcde
		
		// test scrolling to 11 chars pos while scrolled before
		recordingCursor.erase();
		documentsHolder.startOperations();
		
		documentsHolder.scrollToPos(5); //  to 5  chars, between 'e' and 'f'
		documentsHolder.scrollToPos(11); // to 11 chars, between 'k' and 'l'
		
		opWasBuilt = documentsHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor); //                        123456 12345678
		Assert.assertEquals("(*6)(*8)", recordingCursor.finish());// [abcde fgh][ijk
		
		// test scrolling between tags
		recordingCursor.erase();	
		documentsHolder.startOperations();
		
		documentsHolder.scrollToPos(8); //  to 8  chars, between 'h' and 'i'
		documentsHolder.scrollToPos(12); // to 12 chars, between 'l' and 'm'
		
		opWasBuilt = documentsHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor); //                         1234567890 123456 
		Assert.assertEquals("(*10)(*6)", recordingCursor.finish());// [abcdefgh] [ijkl] [m
		
		// test scrolling to end manually
		recordingCursor.erase();	
		documentsHolder.startOperations();
		
		documentsHolder.scrollToPos(16); // to the end
		
		opWasBuilt = documentsHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor); 
		Assert.assertEquals("(*22)", recordingCursor.finish());
		
		// test scrolling to end using func
		recordingCursor.erase();
		documentsHolder.startOperations();
		
		documentsHolder.alignDocToEnd(); // to the end
		
		opWasBuilt = documentsHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor); 
		Assert.assertEquals("(*" + docInitCode.length() + ")", recordingCursor.finish());		
		
		// test scrolling further the end
		recordingCursor.erase();
		documentsHolder.startOperations();
		
		try {
			documentsHolder.scrollToPos(17); // the end + 1
			Assert.fail();
		} catch (DocumentProcessingException dpe) {
			// pass
		}
	
	}
	
	@Test
	public void testScrollingAndAdding() throws DocumentProcessingException {
		
		BufferedDocOp opWasBuilt = null; 
		
		// test scrolling to 11 chars and add tag there
		final String docInitCode = "[abcdefgh][ijkl][mnop]";  
		BufferedDocOp encodedDoc = createDocument(docInitCode);
		documentsHolder.setCurrentDocument(encodedDoc);		
		
		documentsHolder.startOperations();
		documentsHolder.scrollToPos(4); //  to 4  chars, between 'd' and 'e'
		documentsHolder.scrollToPos(14); // to 14 chars, between 'n' and 'o'
		
		DocOpBuilder doBuilder = documentsHolder.unhideOp();
		doBuilder.elementStart("a", AttributesImpl.EMPTY_MAP);
		doBuilder.characters("qrst");
		doBuilder.elementEnd();
		
		opWasBuilt = documentsHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor);
		
		// 12345 12345678901234
		// [abcd efgh][ijkl][mn
		Assert.assertEquals("(*5)(*14){qrst}", recordingCursor.finish());
		// docCode now: [abcdefgh][ijkl][mn[qrst]op]"		

	}
	
	@Test
	public void testAddingAndScrolling() throws DocumentProcessingException {
		BufferedDocOp opWasBuilt = null; 
		
		// test making tag and then scrolling to 11 chars
		final String docInitCode = "[abcdefgh][ijkl][mnop]";  
		BufferedDocOp encodedDoc = createDocument(docInitCode);
		documentsHolder.setCurrentDocument(encodedDoc);
		
		documentsHolder.startOperations();		
		
		DocOpBuilder doBuilder = documentsHolder.unhideOp();
		doBuilder.elementStart("a", AttributesImpl.EMPTY_MAP);
		doBuilder.characters("qrst");
		doBuilder.elementEnd();
		// docCode now: [qrst][abcdefgh][ijkl][mnop]
		
		// test scrolling back (to 3 chars or less) is not possible, 4 chars
		// must be positioned just before the 'a' char
		documentsHolder.scrollToPos(4); //  to 4  chars, between 't' and 'a'
		documentsHolder.scrollToPos(7); //  to 7  chars, between 'c' and 'd'
		documentsHolder.scrollToPos(12); // to 12 chars, between 'h' and 'i'
		
		opWasBuilt = documentsHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor);
		
		//        1 123 123456  
		// [qrst].[ abc defgh] [i
		Assert.assertEquals("{qrst}(*1)(*3)(*6)", recordingCursor.finish());
		
	}
	
	
	@Test
	public void testScrollingAddingAndScrollingAgain() throws DocumentProcessingException {
		Assert.fail();
	}
	
	@Test
	public void testScrollingAndDeleting() throws DocumentProcessingException {
		Assert.fail();
	}
	
	@Test
	public void testDeletingAndScrolling() throws DocumentProcessingException {
		Assert.fail();
	}
	
	@Test
	public void testAddingAndDeleting() throws DocumentProcessingException {
		Assert.fail();
	}
	
	@Test
	public void testDeletingAndAdding() throws DocumentProcessingException {
		Assert.fail();
	}	
		
	private BufferedDocOp createDocument(String documentCode) {
		return (new EncodedDocumentBuilder(documentCode)).compile();
	}	
	
	private class DocumentsHolder extends AbstractDocumentOperationsSequencer {
		
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
			return getCurOp();
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
					document.elementStart("n", AttributesImpl.EMPTY_MAP);
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
	
	private class EmptyCursor implements IOperatingCursorWithResult<String> {

		private DocOpBuilder docOp;
		
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
		@Override
		public void setWalkStart(int pos) { }
		@Override
		public DocOpBuilder takeDocOp() { return docOp; }
		@Override
		public void useDocOp(DocOpBuilder curDocOp) { docOp = curDocOp; }
		
	}
	
}
