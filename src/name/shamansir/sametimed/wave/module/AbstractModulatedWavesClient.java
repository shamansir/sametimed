package name.shamansir.sametimed.wave.module;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;
import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.AbstractUpdatingWavelet;
import name.shamansir.sametimed.wave.WavesClient;
import name.shamansir.sametimed.wave.messaging.Command;
import name.shamansir.sametimed.wave.messaging.CommandTypeID;
import name.shamansir.sametimed.wave.module.mutation.UndoMutation;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

public abstract class AbstractModulatedWavesClient<WaveletType extends AbstractModulatedWavelet> extends WavesClient<WaveletType> {
	
	public AbstractModulatedWavesClient(IWavesClientRenderer renderer) {
		super(renderer);
		registerCommands();
	}
	
	public AbstractModulatedWavesClient() {
		super();
		registerCommands();
	}	
	
	protected abstract void registerCommands();
	
	protected void registerNewCommand(CommandTypeID command) {
		// FIXME: implement
	}

	public boolean doCommand(Command command) {
		AbstractModulatedWavelet curWavelet = getWavelet();
		ClientBackend backend = getBackend();	
		
		if (command.getType() == CommandTypeID.CMD_UNDO_OP) { 
			String undoTargetUser = command.getArgument("user");
			if ((undoTargetUser == null) && (backend != null)) {
				undoTargetUser = backend.getUserId().getAddress();
			} else {
				curWavelet.registerError(AbstractUpdatingWavelet.NOT_CONNECTED_ERR);
				return false;
			}
			return curWavelet.applyMutationToDocument(
					command.getRelatedDocumentID(),
					new UndoMutation(new ParticipantId(undoTargetUser)));
			} 		
		else {
			return super.doCommand(command);
		}		
	}
	
}
