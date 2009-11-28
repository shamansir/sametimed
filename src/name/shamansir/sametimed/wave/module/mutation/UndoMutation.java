package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.module.mutation.proto.AbstractModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;
import name.shamansir.sametimed.wave.modules.chat.doc.cursor.ChatLastUserLineCursor;
import name.shamansir.sametimed.wave.modules.chat.doc.cursor.ChatLineDeletionCursor;
import name.shamansir.sametimed.wave.modules.editor.doc.cursor.DocumentChunkDeletionCursor;
import name.shamansir.sametimed.wave.modules.editor.doc.cursor.DocumentLastUserChunkCursor;

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
		Integer lastLine = applyCursor(srcDoc, new DocumentLastUserChunkCursor(userId.getAddress()));

		// Delete the line
		if (lastLine >= 0) {
			return createDocumentOperation(
					applyCursor(srcDoc, new DocumentChunkDeletionCursor(lastLine)));
		} else {
			return null;
		}		
		Integer lastLine = module.applyCursor(sourceDoc, new ChatLastUserLineCursor(userID.getAddress()));

		// Delete the line
		if (lastLine >= 0) {
			return createDocumentOperation(
					module.getDocumentID(), 
					module.applyCursor(sourceDoc, new ChatLineDeletionCursor(lastLine)));
		} else {
			return null;
		}
	}

}
