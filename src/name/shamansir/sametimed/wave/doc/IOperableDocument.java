package name.shamansir.sametimed.wave.doc;

import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

public interface IOperableDocument {
	
	public abstract WaveletDocumentOperation getUndoOp(BufferedDocOp srcDoc, ParticipantId userId);
	
	public abstract WaveletDocumentOperation getAppendOp(BufferedDocOp srcDoc, ParticipantId author, String text);

	public abstract void handleRenderModeChange(BufferedDocOp srcDoc, RenderMode mode);	

}
