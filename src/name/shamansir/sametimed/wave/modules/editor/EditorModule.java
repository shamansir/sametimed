package name.shamansir.sametimed.wave.modules.editor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.cursor.XMLGeneratingCursor;
import name.shamansir.sametimed.wave.model.base.atom.TextChunk;
import name.shamansir.sametimed.wave.module.AbstractTreeModule;
import name.shamansir.sametimed.wave.modules.editor.doc.cursor.DocumentChunkDeletionCursor;
import name.shamansir.sametimed.wave.modules.editor.doc.cursor.DocumentChunksExtractionCursor;
import name.shamansir.sametimed.wave.modules.editor.doc.cursor.DocumentLastChunkIDCursor;
import name.shamansir.sametimed.wave.modules.editor.doc.cursor.DocumentLastUserChunkCursor;
import name.shamansir.sametimed.wave.render.RenderMode;

// TODO: This must be a tree-based document, so it will contain not only tags
//       of one type, but the nested tags also. ChatModule (or Calendar
//       (if it does not support dates blocks)), either, is lined.

// FIXME: it must be possible to move models, related to the module
//        to module package (possibly, will be so when xml-config will be used)

public class EditorModule extends AbstractTreeModule<List<TextChunk>> {

	protected static final String MODULE_ID = "editor"; 
	protected static final String DOCUMENT_ID = "document";
	
	public EditorModule() throws ParseException {
		super(MODULE_ID, DOCUMENT_ID);
	}	
	
	private RenderMode outputMode = RenderMode.NORMAL;
	
	public List<TextChunk> extract(BufferedDocOp sourceDoc) {
	    if (sourceDoc != null) {
	    	// TODO: use cursors as private variables?
	    	if (outputMode.equals(RenderMode.NORMAL)) {	    		
	    		return applyCursor(sourceDoc, new DocumentChunksExtractionCursor());
	    	} else if (outputMode.equals(RenderMode.XML)) {
		    	return makeXMLTextChunks(
		    			applyCursor(sourceDoc, new XMLGeneratingCursor()));
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
	public void setOutputMode(RenderMode outputMode) {
		this.outputMode  = outputMode;		
	}

	@Override
	public AbstractDocumentTag makeTag(Integer id, ParticipantId author,
			String text) {
		return new EditorTag(id, author, text, false);
	}

	@Override
	public BufferedDocOp deleteTagByPos(BufferedDocOp sourceDoc,
			Integer position) {
		return applyCursor(sourceDoc, new DocumentChunkDeletionCursor(position));
	}

	@Override
	public Integer getLastTagPos(BufferedDocOp sourceDoc) {
		return applyCursor(sourceDoc, new DocumentLastChunkIDCursor());
	}

	@Override
	public Integer getLastUserTagPos(BufferedDocOp sourceDoc, String userName) {
		return applyCursor(sourceDoc, new DocumentLastUserChunkCursor(userName));
	}

}
