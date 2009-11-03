package name.shamansir.sametimed.wave.modules.editor;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;

import name.shamansir.sametimed.wave.messaging.Command;
import name.shamansir.sametimed.wave.messaging.CommandTypeID;
import name.shamansir.sametimed.wave.modules.chat.ChatWavesClient;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

public class EditorWavesClient extends ChatWavesClient {

	@Override
	protected void registerCommands() {
		super.registerCommands();
		registerNewCommand(CommandTypeID.CMD_PUT);
		registerNewCommand(CommandTypeID.CMD_DELETE);
		registerNewCommand(CommandTypeID.CMD_RESERVE);
		registerNewCommand(CommandTypeID.CMD_STYLE);
	}	
	
	@Override
	protected WaveletWithEditor createWavelet(IWavesClientRenderer renderer) {
		return new WaveletWithEditor(getViewID(), renderer);
	}
	
	@Override
	public boolean doCommand(Command command) {
		WaveletWithEditor curWavelet = (WaveletWithEditor)getWavelet();
		ClientBackend backend = getBackend();
		
		switch(command.getType()) {
			case CMD_PUT: return curWavelet.onDocumentAppendMutation(
								command.getRelatedDocumentID(),
								StringEscapeUtils.unescapeXml(command.getArgument("text")),
								backend.getUserId());
			case CMD_DELETE:
					// FIXME: implement
			case CMD_RESERVE:
				    // FIXME: implement
			case CMD_STYLE:
					// FIXME: implement
			default: return super.doCommand(command);
		}
	}	
	
}
