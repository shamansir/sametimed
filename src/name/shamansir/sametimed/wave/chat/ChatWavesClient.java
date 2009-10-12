package name.shamansir.sametimed.wave.chat;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;
import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.AUpdatingWavelet;
import name.shamansir.sametimed.wave.WavesClient;
import name.shamansir.sametimed.wave.messaging.Command;
import name.shamansir.sametimed.wave.messaging.CommandTypeID;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

public class ChatWavesClient extends WavesClient<WaveletWithChat> {

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
					StringEscapeUtils.unescapeXml(command.getArgument("text")),
					backend.getUserId());			
		} 
		
		else if (command.getType() == CommandTypeID.CMD_UNDO_OP) { 
			if (command.getArgument("user") != null) {
				return curWavelet.onUndoCall(
						new ParticipantId(command.getArgument("user")));
			} else if (backend != null) {
				return curWavelet.onUndoCall(
						new ParticipantId(backend.getUserId().getAddress()));
			} else {
				curWavelet.registerError(AUpdatingWavelet.NOT_CONNECTED_ERR);
				return false;
			}			
		} 
		
		else {
			return super.doCommand(command);
		}
	}

}
