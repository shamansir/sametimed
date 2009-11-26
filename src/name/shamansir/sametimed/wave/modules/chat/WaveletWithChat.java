package name.shamansir.sametimed.wave.modules.chat;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import name.shamansir.sametimed.wave.doc.AbstractDocumentsWavelet;
import name.shamansir.sametimed.wave.doc.IMutableDocument;
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


public class WaveletWithChat extends AbstractDocumentsWavelet {	
	
	/* models */
	private ChatDocument chatView = null;	

	public WaveletWithChat(int clientID, IWavesClientRenderer renderer) {
		super(clientID, renderer);		
	}
	
	public WaveletWithChat(int clientID) {
		super(clientID);
	}
	
	@Override	
	protected void prepareDocuments() throws ParseException {
		chatView = new ChatDocument();		
	}
	
	@Override
	protected Map<String, IMutableDocument> registerDocuments(
			Map<String, IMutableDocument> curHandlers) {
		curHandlers.put(chatView.getDocumentID(), chatView);
		return curHandlers;
	}
	
	@Override
	protected List<ModelID> registerDocumentsModelsTypes(List<ModelID> currentTypes) {
		currentTypes.add(ModelID.CHAT_MODEL);
		return currentTypes;
	}		
	
	@Override	
	protected void updateDocumentsModels() {
		if (isChatReady()) {
			updateModel(ModelID.CHAT_MODEL, chatView.extract(
										getSource(chatView.getDocumentID())));
		}		
	}
	
	@Override	
	protected void resetDocuments() {
		chatView = null;
	}
	
	private boolean isChatReady() {
		return chatView != null;
	}
	
}
