package name.shamansir.sametimed.wave.modules.chat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import name.shamansir.sametimed.wave.AbstractUpdatingWavelet;
import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.doc.cursor.XMLGeneratingCursor;
import name.shamansir.sametimed.wave.model.base.atom.ChatLine;
import name.shamansir.sametimed.wave.module.AbstractVerticalModule;
import name.shamansir.sametimed.wave.modules.chat.doc.cursor.ChatLinesExtractionCursor;
import name.shamansir.sametimed.wave.render.RenderMode;

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

public class ChatModule extends AbstractVerticalModule<List<ChatLine>> {
	
	private static final String DOCUMENT_ID = "main";
	private static final String MODULE_ID = "main";
	
	public ChatModule(AbstractUpdatingWavelet parent) throws ParseException {
		super(parent, MODULE_ID, DOCUMENT_ID);
	}
	
	@Override	
	public List<ChatLine> extract() {
	    if (getSource() != null) {
	    	// TODO: use cursors as private variables?
	    	if (outputMode.equals(RenderMode.NORMAL)) {	    		
	    		return applyCursor(new ChatLinesExtractionCursor());
	    	} else if (outputMode.equals(RenderMode.XML)) {
		    	return makeXMLChatLines(
		    			applyCursor(new XMLGeneratingCursor()));
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

	@Override
	public AbstractDocumentTag makeTag(TagID id, ParticipantId author, String text) {
		return new ChatTag(id, author, text);
	}

}
