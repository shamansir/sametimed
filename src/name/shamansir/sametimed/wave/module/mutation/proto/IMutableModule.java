package name.shamansir.sametimed.wave.module.mutation.proto;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;
import name.shamansir.sametimed.wave.render.RenderMode;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

public interface IMutableModule {
	
	public boolean isStructured();
	
	public String getModuleID();	
	
	public String getDocumentID();
	
	public boolean enumerateTags();
	
	public AbstractDocumentTag makeTag(Integer id, ParticipantId author, String text);	
	
	public <ResultType> ResultType applyCursor(BufferedDocOp srcDoc, ICursorWithResult<ResultType> cursor);
	
	public WaveletDocumentOperation apply(BufferedDocOp sourceDoc, IModuleMutation mutation) throws MutationCompilationException;

	public void setOutputMode(RenderMode targetMode);	

}
