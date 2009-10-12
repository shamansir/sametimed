package name.shamansir.sametimed.wave.chat;

import java.util.ArrayList;
import java.util.List;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ConsoleUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.ImmutableMap;

import name.shamansir.sametimed.wave.ADocumentsWavelet;
import name.shamansir.sametimed.wave.chat.cursor.LastUserLineCursor;
import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

/**
 * @see #addUpdatesListener(IUpdatesListener)
 * 
 * @see IWavesClientRenderer
 * @see IUpdatesListener
 * @see WavesClientInfoProvider
 * 
 * @see WaveletModel
 * @see ClientWaveView 
 * @see InboxWaves
 * @see Chat 
 * 
 */


public class WaveletWithChat extends ADocumentsWavelet {	
	
	/* models */
	private Chat chatView = null;	

	public WaveletWithChat(int clientID, IWavesClientRenderer renderer) {
		super(clientID, renderer);		
	}
	
	public WaveletWithChat(int clientID) {
		super(clientID);
	}
	
	@Override
	protected void clear() {
		super.clear();
		chatView = null;
	}	
	
	@Override	
	protected void prepareDocuments(ClientWaveView currentWave) {
		chatView = new Chat(currentWave);		
	}
	
	@Override	
	protected void updateDocumentsModels() {
		if (isChatReady()) {
			updateModel(ModelID.CHAT_MODEL, chatView.getChatLines());
		}		
	}	
	
	@Override
	protected List<ModelID> getDocumentsModelsTypes() {
		List<ModelID> modelsTypes = new ArrayList<ModelID>();
		modelsTypes.add(ModelID.CHAT_MODEL);
		return modelsTypes;
	}	
	
	@Override	
	protected void resetDocuments() {
		chatView = null;
	}
	
	@Override
	protected void changeDocumentsOutputMode(RenderMode mode) { 
		chatView.setOutputMode(mode);
	}	
	
	private boolean isChatReady() {
		return chatView != null;
	}	
	
	private BufferedDocOp getChatDocument() {
		return getDocument(Chat.DOCUMENT_ID);
	}
	
	@Override
	protected boolean documentPerformUndo(String userId) {
		if (getChatDocument() == null) {
			registerError("Error: document is empty");
			return false;
		}
		
		LastUserLineCursor lastLineCursor = new LastUserLineCursor(userId); 		
		getChatDocument().apply(new InitializationCursorAdapter(lastLineCursor));		
		int lastLine = lastLineCursor.getComputedNum();

		// Delete the line
		if (lastLine >= 0) {
			WaveletDocumentOperation undoOp = new WaveletDocumentOperation(
					Chat.DOCUMENT_ID, ConsoleUtils.createLineDeletion(
							getChatDocument(), lastLine));
			getOperationsSender().sendWaveletOperation(getOpenWavelet().getWaveletName(),
					undoOp);
			return true;
		} else {
			registerError("Error: " + userId + " hasn't written anything yet");
			return false;
		}
	}
	
	@Override
	protected void documentPerformAppend(String text, ParticipantId author) {
		BufferedDocOp openDoc = getChatDocument();
		int docSize = (openDoc == null) ? 0 : ClientUtils
				.findDocumentSize(openDoc);
		DocOpBuilder docOp = new DocOpBuilder();

		if (docSize > 0) {
			docOp.retain(docSize);
		}

		docOp.elementStart(Chat.LINE_TAG_NAME, new AttributesImpl(
				ImmutableMap.of(Chat.AUTHOR_ATTR_NAME, author.getAddress())));
		docOp.characters(text);
		docOp.elementEnd();
		
		// FIXME:
		// ATTENTION: this differs from console, first characters, then - elementEnd
		//            console does end before characters 

		getOperationsSender().sendWaveletOperation(getOpenWavelet().getWaveletName(),
				new WaveletDocumentOperation(Chat.DOCUMENT_ID, docOp
						.finish()));
	}

}
