package name.shamansir.sametimed.wave.doc;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;
import name.shamansir.sametimed.wave.doc.cursor.IOperatingCursor;
import name.shamansir.sametimed.wave.doc.cursor.IOperatingCursorWithResult;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.AttributesUpdate;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.DocOpCursor;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public abstract class AbstractDocumentOperationsSequencer {
	
	private boolean started = false;
	private DocOpBuilder curDocOp = null;
	private DocumentPosition curDocPos = new DocumentPosition();
	
	abstract protected BufferedDocOp getSource();
	abstract public String getDocumentID();
	
	public <ResultType> ResultType applyCursor(ICursorWithResult<ResultType> cursor) {
		BufferedDocOp sourceDoc = getSource(); 
		if (sourceDoc == null) return cursor.getResult();
		sourceDoc.apply(new InitializationCursorAdapter(cursor));
		return cursor.getResult();
	}	
	
	@SuppressWarnings("unchecked")
	public <ResultType> ResultType applyCursor(IOperatingCursorWithResult<ResultType> cursor) throws DocumentProcessingException {
		return ((IOperatingCursorWithResult<ResultType>)(applyCursor((IOperatingCursor)cursor))).getResult();		
	}
	
	public IOperatingCursor applyCursor(IOperatingCursor cursor) throws DocumentProcessingException {
		// FIXME: expand exception to be thrown and exit just after that
		if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");
		if (cursor == null) throw new DocumentProcessingException("Null cursor is passed");
		BufferedDocOp sourceDoc = getSource(); 
		if (sourceDoc == null) return cursor;		
		cursor.setWalkStart(curDocPos.inChars());
		sourceDoc.apply(new InitializationCursorAdapter(cursor));
		curDocOp = cursor.takeDocOp();
		return cursor;
	}		
	
	public void startOperations() throws DocumentProcessingException {
		if (started) throw new DocumentProcessingException("Operations sequence was already started and not finished");
		curDocOp = new DocOpBuilder();
		started = true;
		curDocPos.clear();
	}
	
	public WaveletDocumentOperation finishOperations() throws DocumentProcessingException {
		if (!started) throw new DocumentProcessingException("Operations sequence was not started, so nothing to finish");
		WaveletDocumentOperation resultOp = 
			new WaveletDocumentOperation(getDocumentID(), curDocOp.build());
		started = false;
		curDocOp = null;
		curDocPos.clear();
		return resultOp;
		
	}
	
	public void finishOperationsEmpty() {
		started = false;
		curDocOp = null;
	}
	
	public void alignDocToEnd() throws DocumentProcessingException {
		// FIXME: expand exception to be thrown and exit just after that
		if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");
		BufferedDocOp sourceDoc = getSource();
		int docSize = (sourceDoc == null) ? 0 : findDocumentSize(sourceDoc); // in entities		
		if (docSize > 0) {
			int items = docSize - curDocPos.inItems();
			if (items < 0) throw new DocumentProcessingException("Document size conflict while operating over document");
			curDocOp.retain(scrollDocument(sourceDoc, curDocPos, items));			
		} else if (docSize != 0) throw new DocumentProcessingException("Document size conflict while operating over document");
	}
	
	// need to mention that passed and returned (external) positions are positions in characters, and all
	// the internal operations are made counting the entities (elm-start, chars, elm-end)	
	public int scrollToPos(int chars) throws DocumentProcessingException {
		// FIXME: expand exception to be thrown and exit just after that
		if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");
		BufferedDocOp sourceDoc = getSource();
		if (sourceDoc == null) throw new DocumentProcessingException("Document is lost while making operations");
		curDocOp.retain(scrollDocumentByChars(sourceDoc, curDocPos, chars));
		return curDocPos.inChars();
	}
	
	protected DocOpBuilder getCurOp() {		
		return curDocOp;
	}	
	
	protected void useAsCurOp(DocOpBuilder builtOp) throws DocumentProcessingException {
		curDocOp = builtOp;
	}	
	
	private static int findDocumentSize(BufferedDocOp doc) {
		final AtomicInteger size = new AtomicInteger(0);

		doc.apply(new InitializationCursorAdapter(new DocOpCursor() {
		     @Override public void characters(String s) {
		       size.getAndAdd(s.length());
		     }

		     @Override public void elementStart(String key, Attributes attrs) {
		        size.incrementAndGet();
		     }

		     @Override public void elementEnd() {
		        size.incrementAndGet();
		     }

		     @Override public void annotationBoundary(AnnotationBoundaryMap map) {}
		     @Override public void retain(int itemCount) {}
		     @Override public void deleteCharacters(String chars) {}
		     @Override public void deleteElementStart(String type, Attributes attrs) {}
		     @Override public void deleteElementEnd() {}
		     @Override public void replaceAttributes(Attributes oldAttrs, Attributes newAttrs) {}
		     @Override public void updateAttributes(AttributesUpdate attrUpdate) {}
		}));

		return size.get();		
	}
	
	private static int scrollDocument(BufferedDocOp doc, final
			DocumentPosition curDocPos, final int steps) {
		if (steps <= 0) return steps;
		
		final int start = curDocPos.inItems(); // AtomicInteger?
		final AtomicBoolean done = new AtomicBoolean(false);
		final AtomicBoolean doingSteps = new AtomicBoolean(false);
		final AtomicInteger passed = new AtomicInteger(0);
		final AtomicInteger stepsToDo = new AtomicInteger(0);
		final AtomicInteger stepsDone = new AtomicInteger(0);
		
		doc.apply(new InitializationCursorAdapter(new DocOpCursor() {
		     @Override public void characters(String s) {
		    	 if (done.get()) return;
		    	 int len = s.length();
		    	 if (doingSteps.get()) {
			    	 if (stepsToDo.get() == 0) {
			    		 done.set(true);
			    		 return;
			    	 } else if (stepsToDo.get() < len) {
			    		 done.set(true);
			    		 curDocPos.addElmChars(stepsToDo.get());
			    		 stepsDone.addAndGet(stepsToDo.get());
			    	 } else {
			    		 curDocPos.addElmChars(len);
			    		 stepsToDo.set(stepsToDo.get() - len);
			    		 stepsDone.addAndGet(len);
			    	 }
			    	 return;
		    	 } else if ((passed.get() + len) > start) { // at start
		    		 doingSteps.set(true);
		    		 stepsToDo.set(steps);
		    		 if (stepsToDo.get() < len) {
			    		 done.set(true);
			    		 curDocPos.addElmChars(stepsToDo.get());
			    		 stepsDone.addAndGet(stepsToDo.get());
			    	 } else {
			    		 curDocPos.addElmChars(len);
			    		 stepsToDo.set(stepsToDo.get() - len);
			    		 stepsDone.addAndGet(len);
			    	 }
		    		 return;
		    	 }
		    	 passed.addAndGet(len);
		     }

		     @Override public void elementStart(String key, Attributes attrs) {
		    	 elementEnd(); // same as on element end
		     }

		     @Override public void elementEnd() {
		    	 if (done.get()) return;
		    	 if (doingSteps.get()) {
		    		 if (stepsToDo.get() == 0) {
		    			 done.set(true); 
		    			 return;
		    		 } else {
		    			curDocPos.addElmStart();
		    			stepsToDo.decrementAndGet();		    			
		    			stepsDone.incrementAndGet();
		    			return;
		    		 }		    		 
		    	 } else if (passed.get() == start) { // at start
		    		 doingSteps.set(true);
		    		 curDocPos.addElmStart();
		    		 stepsToDo.set(steps - 1);
		    		 stepsDone.set(1);
		    		 return;
		    	 }
		    	 passed.incrementAndGet();
		     }

		     @Override public void annotationBoundary(AnnotationBoundaryMap map) {}
		     @Override public void retain(int itemCount) {}
		     @Override public void deleteCharacters(String chars) {}
		     @Override public void deleteElementStart(String type, Attributes attrs) {}
		     @Override public void deleteElementEnd() {}
		     @Override public void replaceAttributes(Attributes oldAttrs, Attributes newAttrs) {}
		     @Override public void updateAttributes(AttributesUpdate attrUpdate) {}
		}));
		
		return stepsDone.get();		
	}
	
	private static int scrollDocumentByChars(BufferedDocOp doc, final
			DocumentPosition curDocPos, final int chars) {
		if (chars <= 0) return chars;
		
		final int start = curDocPos.inItems(); // AtomicInteger?
		final AtomicBoolean done = new AtomicBoolean(false);
		final AtomicBoolean doingSteps = new AtomicBoolean(false);
		final AtomicInteger passed = new AtomicInteger(0);
		final AtomicInteger charsToDo = new AtomicInteger(0);
		final AtomicInteger charsDone = new AtomicInteger(0);
		
		doc.apply(new InitializationCursorAdapter(new DocOpCursor() {
		     @Override public void characters(String s) {
		    	 if (done.get()) return;
		    	 int len = s.length();
		    	 if (doingSteps.get()) {
			    	 if (charsToDo.get() == 0) {
			    		 done.set(true);
			    		 return;
			    	 } else if (charsToDo.get() < len) {
			    		 done.set(true);
			    		 curDocPos.addElmChars(charsToDo.get());
			    		 charsDone.addAndGet(charsToDo.get());
			    	 } else {
			    		 curDocPos.addElmChars(len);
			    		 charsToDo.set(charsToDo.get() - len);
			    		 charsDone.addAndGet(len);
			    	 }
			    	 return;
		    	 } else if ((passed.get() + len) > start) { // at start
		    		 doingSteps.set(true);
		    		 charsToDo.set(chars);
		    		 if (charsToDo.get() < len) {
			    		 done.set(true);
			    		 curDocPos.addElmChars(charsToDo.get());
			    		 charsDone.addAndGet(charsToDo.get());
			    	 } else {
			    		 curDocPos.addElmChars(len);
			    		 charsToDo.set(charsToDo.get() - len);
			    		 charsDone.addAndGet(len);
			    	 }
		    		 return;
		    	 }
		    	 passed.addAndGet(len);
		     }

		     @Override public void elementStart(String key, Attributes attrs) {
		    	 elementEnd(); // same as on element end
		     }

		     @Override public void elementEnd() {
		    	 if (done.get()) return;
		    	 if (!doingSteps.get() && (passed.get() == start)) { // at start
		    		 doingSteps.set(true);
		    		 curDocPos.addElmStart();
		    		 return;
		    	 }
		    	 passed.incrementAndGet();
		     }

		     @Override public void annotationBoundary(AnnotationBoundaryMap map) {}
		     @Override public void retain(int itemCount) {}
		     @Override public void deleteCharacters(String chars) {}
		     @Override public void deleteElementStart(String type, Attributes attrs) {}
		     @Override public void deleteElementEnd() {}
		     @Override public void replaceAttributes(Attributes oldAttrs, Attributes newAttrs) {}
		     @Override public void updateAttributes(AttributesUpdate attrUpdate) {}
		}));
		
		return charsDone.get();		
		
	}	
	
	private class DocumentPosition {
		private AtomicInteger inItems = new AtomicInteger(0);
		private AtomicInteger inChars = new AtomicInteger(0);
		
		public void addElmStart() {
			addElmEnd(); // the same as recording the end
		}
		
		public int addElmChars(int howMany) {
			inItems.addAndGet(howMany);
			return inChars.addAndGet(howMany);
		}
		
		public void addElmEnd() {
			inItems.addAndGet(1);
		}		
		
		public int inItems() {
			return inItems.get();
		}
		
		public void clear() {
			inItems.set(0);
			inChars.set(0);
		}
		
		public int inChars() {
			return inChars.get();
		}
	}

}
