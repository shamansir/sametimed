package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.module.mutation.proto.AbstractModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

public class UndoMutation extends AbstractModuleDocumentMutation {

	private final ParticipantId userID;
	
	public UndoMutation(ParticipantId userID) {
		this.userID = userID;
	}

	@Override
	public WaveletDocumentOperation applyTo(IMutableModule module,
			BufferedDocOp sourceDoc) throws MutationCompilationException {
		Integer lastLine = module.getLastUserTagPos(sourceDoc, userID.getAddress()); 

		// Delete the line
		if (lastLine >= 0) {
			return createDocumentOperation(module.getDocumentID(), 
					module.deleteTagByPos(sourceDoc, lastLine));
		} else {
			return null;
		}		
	}

}
