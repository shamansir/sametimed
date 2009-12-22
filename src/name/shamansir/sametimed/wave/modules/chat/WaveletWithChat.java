package name.shamansir.sametimed.wave.modules.chat;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.module.AbstractModulatedWavelet;
import name.shamansir.sametimed.wave.module.mutation.proto.IModuleWithDocument;
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


public class WaveletWithChat extends AbstractModulatedWavelet {	
	
	/* models */
	private ChatModule chatView = null;	

	public WaveletWithChat(int clientID, IWavesClientRenderer renderer) {
		super(clientID, renderer);		
	}
	
	public WaveletWithChat(int clientID) {
		super(clientID);
	}
	
	@Override	
	protected void prepareModules() throws ParseException {
		chatView = new ChatModule(this);		
	}
	
	@Override
	protected Map<String, IModuleWithDocument<?>> registerModules(
			  Map<String, IModuleWithDocument<?>> curModules) {
		curModules.put(chatView.getModuleID(), chatView);
		return curModules;
	}	
	
	@Override
	protected List<ModelID> registerModulesModels(List<ModelID> currentTypes) {
		currentTypes.add(ModelID.CHAT_MODEL);
		return currentTypes;
	}		
	
	@Override	
	protected void updateModulesModels() {
		if (isChatReady()) {
			updateModel(ModelID.CHAT_MODEL, chatView.extract());
		}		
	}
	
	@Override	
	protected void resetModules() {
		chatView = null;
	}
	
	private boolean isChatReady() {
		return chatView != null;
	}


	
}
