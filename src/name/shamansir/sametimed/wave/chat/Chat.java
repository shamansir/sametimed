package name.shamansir.sametimed.wave.chat;

import java.util.ArrayList;
import java.util.List;

import name.shamansir.sametimed.wave.chat.cursor.LinesExtractionCursor;
import name.shamansir.sametimed.wave.model.base.atom.ChatLine;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;


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

// based on ScrollableWaveView
public class Chat {
	
	protected static final String DOCUMENT_ID = "main";	
	
	public static final String AUTHOR_ATTR_NAME = "by";
	public static final String LINE_TAG_NAME = "line";
	
	private ClientWaveView sourceWave;
	private RenderMode outputMode = RenderMode.NORMAL;
	
	public Chat(ClientWaveView sourceWave) {
		this.sourceWave = sourceWave;
	}
	
	public List<ChatLine> getChatLines() {		
	    BufferedDocOp document = ClientUtils.getConversationRoot(
				sourceWave).getDocuments().get(DOCUMENT_ID);
	    
	    if (document != null) {
	    	LinesExtractionCursor linesCursor = new LinesExtractionCursor(outputMode);
	    	document.apply(new InitializationCursorAdapter(linesCursor));
	    	return linesCursor.getExtractedLines();
	    } else {
	    	return new ArrayList<ChatLine>();
	    }
		
	}

	public void setOutputMode(RenderMode outputMode) {
		this.outputMode  = outputMode;		
	}

}
