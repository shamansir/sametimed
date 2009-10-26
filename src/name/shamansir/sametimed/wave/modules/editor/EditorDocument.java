package name.shamansir.sametimed.wave.modules.editor;

import java.util.List;

import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.doc.IOperableDocument;
import name.shamansir.sametimed.wave.model.base.atom.TextChunk;

public class EditorDocument implements IOperableDocument {
	
	protected static final String DOCUMENT_ID = "document";
	
	private RenderMode outputMode = RenderMode.NORMAL;
	
	public List<TextChunk> getFullContent() {
		// FIXME: implement
		return null;
	}

	@Override
	public WaveletDocumentOperation getAppendOp(BufferedDocOp srcDoc,
			ParticipantId author, String text) {
		// FIXME: implement
		return null;
	}

	@Override
	public WaveletDocumentOperation getUndoOp(BufferedDocOp srcDoc,
			ParticipantId userId) {
		// FIXME: implement
		return null;
	}
	
	protected void setOutputMode(RenderMode outputMode) {
		this.outputMode  = outputMode;		
	}	

	@Override
	public void handleRenderModeChange(BufferedDocOp srcDoc, RenderMode mode) {
		setOutputMode(mode);
	}

}
