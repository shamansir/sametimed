package name.shamansir.sametimed.wave.doc;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.doc.mutation.IMutable;

public interface IMutableModule extends IMutable {
	
	public boolean isStructured();
	
	public String getDocumentID();
	
	public WaveletDocumentOperation getUndoOp(BufferedDocOp srcDoc, ParticipantId userId);
	
	public WaveletDocumentOperation getAppendOp(BufferedDocOp srcDoc, ParticipantId author, String text);

	public void handleRenderModeChange(BufferedDocOp srcDoc, name.shamansir.sametimed.wave.render.RenderMode mode);
	

}
