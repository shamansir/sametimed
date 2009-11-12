package name.shamansir.sametimed.wave.modules.editor;

import java.util.ArrayList;
import java.util.List;

import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.doc.AOperableDocument;
import name.shamansir.sametimed.wave.doc.cursor.XMLGeneratingCursor;
import name.shamansir.sametimed.wave.model.base.atom.TextChunk;
import name.shamansir.sametimed.wave.modules.editor.cursor.DocumentChunkDeletionCursor;
import name.shamansir.sametimed.wave.modules.editor.cursor.DocumentChunksExtractionCursor;
import name.shamansir.sametimed.wave.modules.editor.cursor.DocumentLastChunkIDCursor;
import name.shamansir.sametimed.wave.modules.editor.cursor.DocumentLastUserChunkCursor;

// TODO: This must be a tree-based document, so it will contain not only tags
//       of one type, but the nested tags also. ChatDocument (or Calendar
//       (if it does not support dates blocks)), either, is lined.

// FIXME: it must to be possible to move models, related to the module
//        to module package (possibly, will be so when xml-config will be used)

public class EditorDocument extends AOperableDocument<List<TextChunk>> {

	protected static final String DOCUMENT_ID = "document";
	
	public EditorDocument() {
		super(DOCUMENT_ID);
	}	
	
	private RenderMode outputMode = RenderMode.NORMAL;
	
	public List<TextChunk> extract(BufferedDocOp srcDoc) {
	    if (srcDoc != null) {
	    	// TODO: use cursors as private variables?
	    	if (outputMode.equals(RenderMode.NORMAL)) {	    		
	    		return applyCursor(srcDoc, new DocumentChunksExtractionCursor());
	    	} else if (outputMode.equals(RenderMode.XML)) {
		    	return makeXMLTextChunks(
		    			applyCursor(srcDoc, new XMLGeneratingCursor()));
	    	} else {
	    		return new ArrayList<TextChunk>();
	    	}
	    } else {
	    	return new ArrayList<TextChunk>();
	    }
	}

	private List<TextChunk> makeXMLTextChunks(List<String> xmlLines) {
		List<TextChunk> xmlTextChunks = new ArrayList<TextChunk>();
		for (String xmlLine: xmlLines) {
			xmlTextChunks.add(TextChunk.justWithContent(xmlLine));
		}
		return xmlTextChunks;
	}

	@Override
	public WaveletDocumentOperation getAppendOp(BufferedDocOp srcDoc,
			ParticipantId author, String text) {
		Integer lastID = applyCursor(srcDoc, new DocumentLastChunkIDCursor());
		DocOpBuilder docOp = alignToTheDocumentEnd(new DocOpBuilder(), srcDoc);
		// appending is performed only here (for the moment), so we can freely be sure
		// that chunks ID-s will increment one-by-one 
		docOp = (new EditorTag(lastID + 1, author, text, false)).createTagFor(docOp);		
		return createDocumentOperation(docOp.finish());
	}

	@Override
	public WaveletDocumentOperation getUndoOp(BufferedDocOp srcDoc,
			ParticipantId userId) {
		Integer lastLine = applyCursor(srcDoc, new DocumentLastUserChunkCursor(userId.getAddress()));

		// Delete the line
		if (lastLine >= 0) {
			return createDocumentOperation(
					applyCursor(srcDoc, new DocumentChunkDeletionCursor(lastLine)));
		} else {
			return null;
		}
	}
	
	protected void setOutputMode(RenderMode outputMode) {
		this.outputMode  = outputMode;		
	}	

	@Override
	public void handleRenderModeChange(BufferedDocOp srcDoc, RenderMode mode) {
		setOutputMode(mode);
	}

}
