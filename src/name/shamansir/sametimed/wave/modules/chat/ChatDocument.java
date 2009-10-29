package name.shamansir.sametimed.wave.modules.chat;

import java.util.ArrayList;
import java.util.List;

import name.shamansir.sametimed.wave.doc.AOperableDocument;
import name.shamansir.sametimed.wave.doc.cursor.LastUserLineCursor;
import name.shamansir.sametimed.wave.doc.cursor.XMLGeneratingCursor;
import name.shamansir.sametimed.wave.model.base.atom.ChatLine;
import name.shamansir.sametimed.wave.modules.chat.cursor.ChatLineDeletionCursor;
import name.shamansir.sametimed.wave.modules.chat.cursor.ChatLinesExtractionCursor;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.ImmutableMap;


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

public class ChatDocument extends AOperableDocument {
	
	private static final String DOCUMENT_ID = "main";
	
	private RenderMode outputMode = RenderMode.NORMAL;
	
	public ChatDocument() {
		super(DOCUMENT_ID);
	}
	
	// FIXME: implement this as auto-generated/abstract method somehow, using document modeltype	
	public List<ChatLine> getChatLines(BufferedDocOp srcDoc) {		
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
			xmlChatLines.add(new ChatLine("-", xmlLine));
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
		// TODO: extract in method
		int docSize = (srcDoc == null) ? 0 : ClientUtils
				.findDocumentSize(srcDoc);
		DocOpBuilder docOp = new DocOpBuilder();

		if (docSize > 0) {
			docOp.retain(docSize);
		}

		docOp.elementStart(ChatTag.LINE_TAG_NAME, new AttributesImpl(
				ImmutableMap.of(ChatTag.AUTHOR_ATTR_NAME, author.getAddress())));
		docOp.characters(text);
		docOp.elementEnd();
		
		return createDocumentOperation(docOp.finish());

	}

	@Override
	public WaveletDocumentOperation getUndoOp(BufferedDocOp srcDoc, ParticipantId userId) {
		Integer lastLine = applyCursor(srcDoc, new LastUserLineCursor(userId.getAddress()));

		// Delete the line
		if (lastLine >= 0) {
			return createDocumentOperation(
					applyCursor(srcDoc, new ChatLineDeletionCursor(lastLine)));
		} else {
			return null;
		}
	}

}
