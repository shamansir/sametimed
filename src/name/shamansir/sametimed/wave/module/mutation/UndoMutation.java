package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.doc.sequencing.DocumentProcessingException;
import name.shamansir.sametimed.wave.module.AbstractModuleWithDocument;
import name.shamansir.sametimed.wave.module.mutation.proto.IModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

public class UndoMutation implements IModuleDocumentMutation {

	private final ParticipantId userID;
	
	public UndoMutation(ParticipantId userID) {
		this.userID = userID;
	}

	@Override
	public WaveletDocumentOperation applyTo(AbstractModuleWithDocument<?> module)
			throws MutationCompilationException, DocumentProcessingException {
		module.startOperations();		
		TagID lastLineID = module.getLastUserTagID(userID.getAddress());
		if (lastLineID != null) {
			module.deleteTag(lastLineID);
			return module.finishOperations();
		} else {
			module.finishOperationsEmpty();
			return null;
		}
		/*
		// Delete the line
		if (lastLineID != null) {
			return createDocumentOperation(module.getDocumentID(), 
					module.deleteTag(lastLineID));
		} else {
			return null;
		} */		
	}

}
