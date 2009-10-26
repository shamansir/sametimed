package name.shamansir.sametimed.wave.modules.editor;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;

import name.shamansir.sametimed.wave.messaging.Command;
import name.shamansir.sametimed.wave.messaging.CommandTypeID;
import name.shamansir.sametimed.wave.modules.chat.ChatWavesClient;
import name.shamansir.sametimed.wave.modules.chat.WaveletWithChat;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

public class EditorWavesClient extends ChatWavesClient {

	@Override
	protected void registerCommands() {
		super.registerCommands();
		registerNewCommand(CommandTypeID.CMD_PUT);
	}	
	
	@Override
	protected WaveletWithEditor createWavelet(IWavesClientRenderer renderer) {
		return new WaveletWithEditor(getViewID(), renderer);
	}
	
	@Override
	public boolean doCommand(Command command) {
		WaveletWithChat curWavelet = getWavelet();
		ClientBackend backend = getBackend();
		
		if (command.getType() == CommandTypeID.CMD_PUT) {
			return curWavelet.onDocumentAppendMutation(
					command.getRelatedDocumentID(),
					StringEscapeUtils.unescapeXml(command.getArgument("text")),
					backend.getUserId());			
		} else {
			return super.doCommand(command);
		}
	}	
	
}
