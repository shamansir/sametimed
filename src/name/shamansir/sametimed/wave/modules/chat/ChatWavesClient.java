package name.shamansir.sametimed.wave.modules.chat;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;

import name.shamansir.sametimed.wave.modules.chat.WaveletWithChat;
import name.shamansir.sametimed.wave.doc.ADocumentsWavesClient;
import name.shamansir.sametimed.wave.messaging.Command;
import name.shamansir.sametimed.wave.messaging.CommandTypeID;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

public class ChatWavesClient extends ADocumentsWavesClient<WaveletWithChat> {

	@Override
	protected WaveletWithChat createWavelet(IWavesClientRenderer renderer) {
		return new WaveletWithChat(getViewID(), renderer);
	}
	
	@Override
	public boolean doCommand(Command command) {
		WaveletWithChat curWavelet = getWavelet();
		ClientBackend backend = getBackend();
		
		if (command.getType() == CommandTypeID.CMD_SAY) {
			return curWavelet.onDocumentAppendMutation(
					command.getRelatedDocumentID(),
					StringEscapeUtils.unescapeXml(command.getArgument("text")),
					backend.getUserId());			
		} else {
			return super.doCommand(command);
		}
	}

}
