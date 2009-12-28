package name.shamansir.sametimed.test;

import name.shamansir.sametimed.wave.doc.AbstractDocumentOperationsSequencer;
import name.shamansir.sametimed.wave.doc.DocumentProcessingException;
import name.shamansir.sametimed.wave.doc.cursor.IOperatingCursorWithResult;

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
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public class TestDocumentSequencer {	
	
	private DocumentsHolder documentsHolder = new DocumentsHolder();
	EvaluatingDocOpCursor<String> recordingCursor = new SimpleOperationsRecordingCursor();
	
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
		
		BufferedDocOp opWasBuilt = null; 
		
		// scroll 5 chars
		final String docInitCode = "[abcdefgh][ijkl][mnop]";  
		BufferedDocOp encodedDoc = createDocument(docInitCode);
		documentsHolder.startOperations();
		documentsHolder.setCurrentDocument(encodedDoc);
		documentsHolder.scrollToPos(5);
		opWasBuilt = documentsHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor);
		Assert.assertEquals("(*5)", recordingCursor.finish());
		
		documentsHolder.startOperations();
		documentsHolder.setCurrentDocument(encodedDoc);
		documentsHolder.scrollToPos(5);
		documentsHolder.scrollToPos(11);
		opWasBuilt = documentsHolder.finishOperations().getOperation();
		opWasBuilt.apply(recordingCursor);
		Assert.assertEquals("(*5)(*6)", recordingCursor.finish());		
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


		@Override
		public void deleteCharacters(String chars) {
			// TODO Auto-generated method stub
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
	
	/*
	private class OperationsRecordingCursor implements EvaluatingDocOpCursor<String> {
		
		private final StringBuffer operationsRecorder = new StringBuffer();
		
		private String escape(String input) {
			String output = input.replaceAll("}", "\\}");
			output = input.replaceAll("{", "\\{");
			output = input.replaceAll(")", "\\)");
			return output.replaceAll("(", "\\(");
		}

		@Override
		public String finish() {
			return operationsRecorder.toString();
		}


		@Override
		public void deleteCharacters(String chars) {
			// TODO Auto-generated method stub
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
			operationsRecorder.append("(+" + escape(chars) + ")");
		}


		@Override
		public void elementEnd() {
			operationsRecorder.append("(+})");			
		}


		@Override
		public void elementStart(String type, Attributes attrs) {
			operationsRecorder.append("(+{)");
		}		
		
	} */
	
	private class EmptyCursor implements IOperatingCursorWithResult<String> {

		private final DocOpBuilder docOp = new DocOpBuilder();
		
		@Override
		public String getResult() {
			return "";
		}

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
		
	}
	
}
