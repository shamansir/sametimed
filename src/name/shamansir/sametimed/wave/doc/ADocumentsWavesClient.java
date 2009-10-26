package name.shamansir.sametimed.wave.doc;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientBackend;
import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.AUpdatingWavelet;
import name.shamansir.sametimed.wave.WavesClient;
import name.shamansir.sametimed.wave.messaging.Command;
import name.shamansir.sametimed.wave.messaging.CommandTypeID;

public abstract class ADocumentsWavesClient<WaveletType extends ADocumentsWavelet> extends WavesClient<WaveletType> {

	public boolean doCommand(Command command) {
		ADocumentsWavelet curWavelet = getWavelet();
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
				curWavelet.registerError(AUpdatingWavelet.NOT_CONNECTED_ERR);
				return false;
			}			
		} 
		
		else {
			return super.doCommand(command);
		}		
	}
	
}
