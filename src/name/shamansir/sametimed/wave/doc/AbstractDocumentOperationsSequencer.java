package name.shamansir.sametimed.wave.doc;

import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;
import name.shamansir.sametimed.wave.doc.cursor.IOperatingCursor;
import name.shamansir.sametimed.wave.doc.cursor.IOperatingCursorWithResult;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.EvaluatingDocInitializationCursor;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public abstract class AbstractDocumentOperationsSequencer {
	
	private boolean started = false;
	private DocOpBuilder curDocOp = null;
	private /*int[]*/ DocumentWalker docWalker; 
	
	abstract protected BufferedDocOp getSource();
	abstract public String getDocumentID();
	
	// TODO: may be pass sourceDoc as a parameter to startOperations ?
	public void startOperations() throws DocumentProcessingException {
		if (started) throw new DocumentProcessingException("Operations sequence was already started and not finished");
		curDocOp = new DocOpBuilder();
		started = true;
		BufferedDocOp sourceDoc = getSource();
		if (sourceDoc == null) throw new DocumentProcessingException("Inner document must not be null to operate over");
		docWalker = collectDocumentData(sourceDoc);
	}
	
	private DocumentWalker collectDocumentData(BufferedDocOp sourceDoc) {
		EvaluatingDocInitializationCursor<DocumentState> evaluatingCursor =
				new DocStateEvaluatingCursor(new DocumentWalker());
		sourceDoc.apply(new InitializationCursorAdapter(evaluatingCursor));
		return ((DocumentWalker)evaluatingCursor.finish()).resetPosition();		
	}
	
	public WaveletDocumentOperation finishOperations() throws DocumentProcessingException {
		if (!started) throw new DocumentProcessingException("Operations sequence was not started, so nothing to finish");
		WaveletDocumentOperation resultOp = 
			new WaveletDocumentOperation(getDocumentID(), curDocOp.build());
		started = false;
		curDocOp = null;
		docWalker.clear();
		return resultOp;		
	}
	
	public void finishOperationsEmpty() {
		started = false;
		curDocOp = null;
		docWalker.clear();
	}
	
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
		if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");
		if (cursor == null) throw new DocumentProcessingException("Null cursor is passed");
		BufferedDocOp sourceDoc = getSource(); 
		if (sourceDoc == null) return cursor;		
		cursor.setWalkStart(docWalker.curPos());
		sourceDoc.apply(new InitializationCursorAdapter(cursor));
		curDocOp = cursor.takeDocOp();
		return cursor;
	}			
	public void alignDocToEnd() throws DocumentProcessingException {
		if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");
		curDocOp.retain(docWalker.scrollToEnd());		
	}
	
	// need to mention that passed and returned (external) positions are positions in characters, and all
	// the internal operations are made counting the entities (elm-start, chars, elm-end)	
	public int scrollToPos(int chars) throws DocumentProcessingException {
		if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");
		curDocOp.retain(docWalker.scrollBy(chars));
		return docWalker.curPos();
	}
	
	protected DocOpBuilder getCurOp() {		
		return curDocOp;
	}	
	
	protected void useAsCurOp(DocOpBuilder builtOp) throws DocumentProcessingException {
		curDocOp = builtOp;
	}	
	
	private class DocStateEvaluatingCursor implements EvaluatingDocInitializationCursor<DocumentState/*int[]*/> {
		
		private final DocumentState state;
		
		public DocStateEvaluatingCursor(DocumentState state) {
			this.state = state;
		}

		@Override
		public void annotationBoundary(AnnotationBoundaryMap map) { }

		@Override
		public void characters(String chars) { 
			state.addElmChars(chars.length());
		}

		@Override
		public void elementEnd() {
			state.addElmEnd();			
		}

		@Override
		public void elementStart(String type, Attributes attrs) {
			state.addElmStart();
		}

		@Override
		public DocumentState finish() {
			return state;
		}
		
	}

}
