package name.shamansir.sametimed.wave.modules.chat;

import java.util.ArrayList;
import java.util.List;

import name.shamansir.sametimed.wave.doc.IOperableDocument;
import name.shamansir.sametimed.wave.model.base.atom.ChatLine;
import name.shamansir.sametimed.wave.modules.chat.cursor.LastUserLineCursor;
import name.shamansir.sametimed.wave.modules.chat.cursor.LinesExtractionCursor;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ConsoleUtils;
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

public class ChatDocument implements IOperableDocument {
	
	protected static final String DOCUMENT_ID = "main";
	
	private RenderMode outputMode = RenderMode.NORMAL;
	
	public ChatDocument() {
	}
	
	// FIXME: implement this as auto-generated/abstract method somehow, using document modeltype	
	public List<ChatLine> getChatLines(BufferedDocOp srcDoc) {		
	    if (srcDoc != null) {
	    	LinesExtractionCursor linesCursor = new LinesExtractionCursor(outputMode);
	    	srcDoc.apply(new InitializationCursorAdapter(linesCursor));
	    	return linesCursor.getExtractedLines();
	    } else {
	    	return new ArrayList<ChatLine>();
	    }
		
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
		
		// FIXME:
		// ATTENTION: this differs from console, first characters, then - elementEnd
		//            console does end before characters
		
		return new WaveletDocumentOperation(DOCUMENT_ID, docOp.finish());

	}

	@Override
	public WaveletDocumentOperation getUndoOp(BufferedDocOp srcDoc, ParticipantId userId) {
		LastUserLineCursor lastLineCursor = new LastUserLineCursor(userId.getAddress()); 		
		srcDoc.apply(new InitializationCursorAdapter(lastLineCursor));		
		int lastLine = lastLineCursor.getComputedNum();

		// Delete the line
		if (lastLine >= 0) {
			return new WaveletDocumentOperation(
					DOCUMENT_ID, ConsoleUtils.createLineDeletion(srcDoc, lastLine));
		} else {
			return null;
		}
	}

}
