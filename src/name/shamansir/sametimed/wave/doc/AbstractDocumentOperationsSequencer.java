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

// TODO: may be 

public abstract class AbstractDocumentOperationsSequencer {
	
	private boolean started = false;
	private DocOpBuilder curDocOp = null;
	private AtomicInteger curDocPos = new AtomicInteger(0);
	
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
		return ((IOperatingCursorWithResult<ResultType>)applyCursor(cursor)).getResult();		
	}
	
	public IOperatingCursor applyCursor(IOperatingCursor cursor) throws DocumentProcessingException {
		// FIXME: expand exception to be thrown and exit just after that
		if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");		
		BufferedDocOp sourceDoc = getSource(); 
		if (sourceDoc == null) return cursor;		
		cursor.setWalkStart(curDocPos.get());
		sourceDoc.apply(new InitializationCursorAdapter(cursor));
		curDocOp = cursor.takeDocOp();
		return cursor;
	}		
	
	public void startOperations() {
		curDocOp = new DocOpBuilder();
		started = true;
		curDocPos = new AtomicInteger(0);
	}
	
	public WaveletDocumentOperation finishOperations() {
		WaveletDocumentOperation resultOp = 
			new WaveletDocumentOperation(getDocumentID(), curDocOp.build());
		started = false;
		curDocOp = null;
		curDocPos = new AtomicInteger(0);
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
		int docSize = (sourceDoc == null) ? 0 : findDocumentSize(sourceDoc);		
		if (docSize > 0) {
			int steps = docSize - curDocPos.get();
			if (steps < 0) throw new DocumentProcessingException("Document size conflict while operating over document");
			curDocOp.retain(steps);
			curDocPos.set(curDocPos.addAndGet(steps));
		} else if (docSize != 0) throw new DocumentProcessingException("Document size conflict while operating over document");
	}
	
	public int scrollToPos(int pos) throws DocumentProcessingException {
		// FIXME: expand exception to be thrown and exit just after that
		if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");
		BufferedDocOp sourceDoc = getSource();
		if (sourceDoc == null) throw new DocumentProcessingException("Document is lost while making operations");
		int steps = scrollTo(sourceDoc, curDocPos.get(), pos);
		// TODO: check, may be negative values for retain are supported
		if (steps < 0) throw new DocumentProcessingException("Can not scroll back in the document, start new sequence to fix");
		curDocOp.retain(steps);
		curDocPos.set(curDocPos.addAndGet(steps));
		return curDocPos.get();
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
	
	private static int scrollTo(BufferedDocOp doc, final int start, final int pos) {
		final AtomicInteger result = new AtomicInteger(0);
		final AtomicInteger lastStartPos = new AtomicInteger(0);
		final AtomicInteger passed = new AtomicInteger(0);
		final AtomicBoolean skip = new AtomicBoolean(false);

		doc.apply(new InitializationCursorAdapter(new DocOpCursor() {
		     @Override public void characters(String s) {
		        if (!skip.get()) passed.getAndAdd(s.length());
		     }

		     @Override public void elementStart(String key, Attributes attrs) {
		    	 lastStartPos.set(passed.get());
		    	 if (!skip.get() && (passed.get() > pos)) { // FIXME: check trice
		    	     result.set(pos - start - passed.get());
		    		 skip.set(true);
		    	 }
		    	 if (!skip.get()) {
		    		 lastStartPos.set(passed.get());
		    		 passed.incrementAndGet();
		    	 }
		     }

		     @Override public void elementEnd() {
		    	 if (!skip.get()) passed.incrementAndGet();
		     }

		     @Override public void annotationBoundary(AnnotationBoundaryMap map) {}
		     @Override public void retain(int itemCount) {}
		     @Override public void deleteCharacters(String chars) {}
		     @Override public void deleteElementStart(String type, Attributes attrs) {}
		     @Override public void deleteElementEnd() {}
		     @Override public void replaceAttributes(Attributes oldAttrs, Attributes newAttrs) {}
		     @Override public void updateAttributes(AttributesUpdate attrUpdate) {}
		}));

		return result.get();		
	}	
	

}
