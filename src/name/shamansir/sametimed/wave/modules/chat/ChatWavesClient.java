package name.shamansir.sametimed.wave.modules.chat;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;

import name.shamansir.sametimed.wave.modules.chat.WaveletWithChat;
import name.shamansir.sametimed.wave.doc.AbstractModulatedWavesClient;
import name.shamansir.sametimed.wave.doc.mutation.AppendMutation;
import name.shamansir.sametimed.wave.messaging.Command;
import name.shamansir.sametimed.wave.messaging.CommandTypeID;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

public class ChatWavesClient extends AbstractModulatedWavesClient<WaveletWithChat> {
	
	@Override
	protected void registerCommands() {
		registerNewCommand(CommandTypeID.CMD_SAY);
	}

	@Override
	protected WaveletWithChat createWavelet(IWavesClientRenderer renderer) {
		return new WaveletWithChat(getViewID(), renderer);
	}
	
	@Override
	public boolean doCommand(Command command) {
		WaveletWithChat curWavelet = getWavelet();
		ClientBackend backend = getBackend();
		
		if (command.getType() == CommandTypeID.CMD_SAY) {
			return curWavelet.applyMutationToDocument(
					command.getRelatedDocumentID(), 
					new AppendMutation(backend.getUserId(), 
							           command.getArgument("text")));
		} else {
			return super.doCommand(command);
		}
	}

}
