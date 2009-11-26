package name.shamansir.sametimed.wave.module;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.module.mutation.proto.IMutable;

public interface IMutableModule extends IMutable {
	
	public boolean isStructured();
	
	public String getDocumentID();
	
	public WaveletDocumentOperation getUndoOp(BufferedDocOp srcDoc, ParticipantId userId);
	
	public WaveletDocumentOperation getAppendOp(BufferedDocOp srcDoc, ParticipantId author, String text);

	public void handleRenderModeChange(BufferedDocOp srcDoc, name.shamansir.sametimed.wave.render.RenderMode mode);
	

}
