package name.shamansir.sametimed.wave.modules.chat;

import java.util.ArrayList;
import java.util.List;

import name.shamansir.sametimed.wave.doc.AbstractOperableDocument;
import name.shamansir.sametimed.wave.doc.cursor.XMLGeneratingCursor;
import name.shamansir.sametimed.wave.model.base.atom.ChatLine;
import name.shamansir.sametimed.wave.modules.chat.cursor.ChatLineDeletionCursor;
import name.shamansir.sametimed.wave.modules.chat.cursor.ChatLinesExtractionCursor;
import name.shamansir.sametimed.wave.modules.chat.cursor.ChatLastUserLineCursor;

import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Chat, gives the model of Chat in the List<ChatLine> form,
 * using the ClientWaveView as source
 * 
 * @see ClientWaveView
 * @see ChatLine
 * 
 */

public class ChatDocument extends AbstractOperableDocument<List<ChatLine>> {
	
	private static final String DOCUMENT_ID = "main";
	
	private RenderMode outputMode = RenderMode.NORMAL;
	
	public ChatDocument() {
		super(DOCUMENT_ID);
	}
	
	@Override	
	public List<ChatLine> extract(BufferedDocOp srcDoc) {		
	    if (srcDoc != null) {
	    	// TODO: use cursors as private variables?
	    	if (outputMode.equals(RenderMode.NORMAL)) {	    		
	    		return applyCursor(srcDoc, new ChatLinesExtractionCursor());
	    	} else if (outputMode.equals(RenderMode.XML)) {
		    	return makeXMLChatLines(
		    			applyCursor(srcDoc, new XMLGeneratingCursor()));
	    	} else {
	    		return new ArrayList<ChatLine>();
	    	}
	    } else {
	    	return new ArrayList<ChatLine>();
	    }
		
	}
	
	protected List<ChatLine> makeXMLChatLines(List<String> xmlLines) {
		List<ChatLine> xmlChatLines = new ArrayList<ChatLine>();
		for (String xmlLine: xmlLines) {
			xmlChatLines.add(ChatLine.justWithContent(xmlLine));
		}
		return xmlChatLines;
	}

	protected void setOutputMode(RenderMode outputMode) {
		this.outputMode  = outputMode;		
	}
	
	@Override
	public void handleRenderModeChange(BufferedDocOp srcDoc, RenderMode mode) {
		setOutputMode(mode);
	}	

	@Override
	public WaveletDocumentOperation getAppendOp(BufferedDocOp srcDoc, ParticipantId author,
			String text) {
		DocOpBuilder docOp = alignToTheDocumentEnd(new DocOpBuilder(), srcDoc);
		docOp = (new ChatTag(author, text)).createTagFor(docOp);		
		return createDocumentOperation(docOp.finish());

	}

	@Override
	public WaveletDocumentOperation getUndoOp(BufferedDocOp srcDoc, ParticipantId userId) {
		Integer lastLine = applyCursor(srcDoc, new ChatLastUserLineCursor(userId.getAddress()));

		// Delete the line
		if (lastLine >= 0) {
			return createDocumentOperation(
					applyCursor(srcDoc, new ChatLineDeletionCursor(lastLine)));
		} else {
			return null;
		}
	}

}
