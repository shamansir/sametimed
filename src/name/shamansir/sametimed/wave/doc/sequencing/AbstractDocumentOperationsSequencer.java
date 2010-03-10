package name.shamansir.sametimed.wave.doc.sequencing;

import name.shamansir.sametimed.wave.doc.cursor.DocumentElementStartPosSearchingCursor;
import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
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
				
		// TODO: cache documentWalker (do not collect state at each operation)?
		docWalker = new DocumentWalker(getSource());
		walkingBuilder = new WalkingDocOpBuilder(docWalker);        
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
		walkingBuilder.trackCursor(cursor);
		//walkingBuilder = cursor.getBuilder(); // TODO: detachBuilder?
		
		LOG.debug("cursor applied starting at pos " + docWalker.curPos());
		
		return cursor;
	}
	
	public void alignDocToEnd() throws DocumentProcessingException {
		if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");
		
		if (docWalker.docSizeInElms() > 0) {		
		    walkingBuilder.manualRetain(docWalker.scrollToEnd());
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
		    walkingBuilder.manualRetain(elmsStep);
		} else if (elmsStep < 0) {
		    throw new DocumentProcessingException("Can't scroll back");
		}
		
		LOG.debug("document scrolled to " + chars + " chars");
		
		return docWalker.curPos();
	}
	
	public int searchElmStart(int chars) {
	    // return -1; // docWalker.searchElmStart(chars); // docWalker do not exists here
	    return applyCursor(new DocumentElementStartPosSearchingCursor(chars, true));
	}
	
	public int findElmStart(int chars) throws DocumentProcessingException {
	    if (!started) throw new DocumentProcessingException("Operations sequence must be started before scrolling over document");
	    if (chars > docWalker.docSizeInChars()) throw new DocumentProcessingException("Can not search further the document end");
	    
	    LOG.debug("searching for element start before " + chars + " chars");
	    
	    int elmsStep = docWalker.findElmStart(chars);
	    if (elmsStep > 0) {
            walkingBuilder.manualRetain(elmsStep);
        } else if (elmsStep < 0) {
            throw new DocumentProcessingException("Can't scroll back");
        }
	    
        LOG.debug("found element start at " + elmsStep  );	    
	    
	    return docWalker.curPos(); // docWalker.alignToElmStart(shars)	    
	}
	
	protected DocOpBuilder getDocBuilder() {		
		return walkingBuilder;
	}	
	
	protected void useDocBuilder(DocOpBuilder builder) throws DocumentProcessingException {
	    walkingBuilder = (WalkingDocOpBuilder)builder;
	}
	
}
