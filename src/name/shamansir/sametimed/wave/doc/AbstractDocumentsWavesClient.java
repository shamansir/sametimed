package name.shamansir.sametimed.wave.doc;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;
import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.AbstractUpdatingWavelet;
import name.shamansir.sametimed.wave.WavesClient;
import name.shamansir.sametimed.wave.messaging.Command;
import name.shamansir.sametimed.wave.messaging.CommandTypeID;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

public abstract class AbstractDocumentsWavesClient<WaveletType extends AbstractDocumentsWavelet> extends WavesClient<WaveletType> {
	
	public AbstractDocumentsWavesClient(IWavesClientRenderer renderer) {
		super(renderer);
		registerCommands();
	}
	
	public AbstractDocumentsWavesClient() {
		super();
		registerCommands();
	}	
	
	protected abstract void registerCommands();
	
	protected void registerNewCommand(CommandTypeID command) {
		// FIXME: implement
	}

	public boolean doCommand(Command command) {
		AbstractDocumentsWavelet curWavelet = getWavelet();
		ClientBackend backend = getBackend();	
		
		if (command.getType() == CommandTypeID.CMD_UNDO_OP) { 
			if (command.getArgument("user") != null) {
				return curWavelet.onUndoCall(
						command.getRelatedDocumentID(),
						new ParticipantId(command.getArgument("user")));
			} else if (backend != null) {
				return curWavelet.onUndoCall(
						command.getRelatedDocumentID(),
						new ParticipantId(backend.getUserId().getAddress()));
			} else {
				curWavelet.registerError(AbstractUpdatingWavelet.NOT_CONNECTED_ERR);
				return false;
			}			
		} 
		
		else {
			return super.doCommand(command);
		}		
	}
	
}
