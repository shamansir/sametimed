package name.shamansir.sametimed.wave.doc.sequencing;

import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.EvaluatingDocInitializationCursor;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public abstract class AbstractDocumentOperationsSequencer {
    
    private static final Log LOG = LogFactory.getLog(AbstractDocumentOperationsSequencer.class);
	
	private boolean started = false;
	private WalkingDocOpBuilder walkingBuilder = null;
	private /*int[]*/ DocumentWalker docWalker; 
	
	abstract protected BufferedDocOp getSource();
	abstract public String getDocumentID();
	
	// TODO: may be pass sourceDoc as a parameter to startOperations ?
	public void startOperations() throws DocumentProcessingException {
		if (started) throw new DocumentProcessingException("Operations sequence was already started and not finished");		
		started = true;
		
		LOG.debug("Started operations sequence over document "/* + sourceDoc*/);
		
		docWalker = new DocumentWalker(collectDocumentData(getSource()));
		walkingBuilder = new WalkingDocOpBuilder(docWalker);        
	}
	
	private DocumentState collectDocumentData(BufferedDocOp sourceDoc) {
	    LOG.debug("Collecting document data");
	    
	    if (sourceDoc == null) return new DocumentState();
	    
		EvaluatingDocInitializationCursor<DocumentState> evaluatingCursor =
				new DocStateEvaluatingCursor(new DocumentState());
		sourceDoc.apply(new InitializationCursorAdapter(evaluatingCursor));
		return evaluatingCursor.finish();		
	}
	
	public WaveletDocumentOperation finishOperations() throws DocumentProcessingException {
		if (!started) throw new DocumentProcessingException("Operations sequence was not started, so nothing to finish");
		WaveletDocumentOperation resultOp = 
			new WaveletDocumentOperation(getDocumentID(), walkingBuilder.build());
		started = false;
		walkingBuilder = null;
		docWalker.clear();
		
		LOG.debug("Finished operations sequence");
		
		return resultOp;		
	}
	
	public void finishOperationsEmpty() {
		started = false;
		walkingBuilder = null;
		docWalker.clear();
		
		LOG.debug("Finished operations sequence");
	}
	
	public <ResultType> ResultType applyCursor(ICursorWithResult<ResultType> cursor) {
		BufferedDocOp sourceDoc = getSource(); 
		if (sourceDoc == null) return cursor.getResult();
		sourceDoc.apply(new InitializationCursorAdapter(cursor));
		
		LOG.debug("applied cursor " + cursor + " to the document");
		
		return cursor.getResult();
	}	
	
	@SuppressWarnings("unchecked")
	public <ResultType> ResultType applyCursor(AbstractOperatingCursorWithResult<ResultType> cursor) throws DocumentProcessingException {
		return ((AbstractOperatingCursorWithResult<ResultType>)(applyCursor((AbstractOperatingCursor)cursor))).getResult();		
	}
		
	public AbstractOperatingCursor applyCursor(AbstractOperatingCursor cursor) throws DocumentProcessingException {
		if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");
		if (cursor == null) throw new DocumentProcessingException("Null cursor is passed");
		
		LOG.debug("applying cursor " + cursor.toString() + " to the document");
		
		BufferedDocOp sourceDoc = getSource(); 
		if (sourceDoc == null) return cursor;
		cursor.useBuilder(walkingBuilder);
		sourceDoc.apply(new InitializationCursorAdapter(cursor));
		walkingBuilder = (WalkingDocOpBuilder)cursor.takeDocOp();
		
		LOG.debug("cursor applied starting at pos " + docWalker.curPos());
		
		return cursor;
	}			
	public void alignDocToEnd() throws DocumentProcessingException {
		if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");
		
		if (docWalker.docSizeInElms() > 0) {		
		    walkingBuilder.retain(docWalker.scrollToEnd());
		}
		
		LOG.debug("aligned to the end of the document");
	}
	
	// need to mention that passed and returned (external) positions are positions in characters, and all
	// the internal operations are made counting the entities (elm-start, chars, elm-end)	
	public int scrollToPos(int chars) throws DocumentProcessingException {
		if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");
		if (chars > docWalker.docSizeInChars()) throw new DocumentProcessingException("Can not scroll further the document end");
		
		int elmsStep = docWalker.scrollTo(chars);
		if (elmsStep > 0) {
		    walkingBuilder.retain(elmsStep);
		} else if (elmsStep < 0) {
		    throw new DocumentProcessingException("Can't scroll back");
		}
		
		LOG.debug("document scrolled to " + chars + " chars");
		
		return docWalker.curPos();
	}
	
	protected DocOpBuilder getDocBuilder() {		
		return walkingBuilder;
	}	
	
	protected void useDocBuilder(DocOpBuilder builder) throws DocumentProcessingException {
	    walkingBuilder = (WalkingDocOpBuilder)builder;
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
