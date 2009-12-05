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
	
	public boolean enumerateTags(); // do set numbers for tags // FIXME: always set
	
	public BufferedDocOp getSource(); // return source document
	
	public <ResultType> ResultType applyCursor(ICursorWithResult<ResultType> cursor);
	
	public WaveletDocumentOperation apply(IModuleMutation mutation) throws MutationCompilationException;

	public void setOutputMode(RenderMode targetMode);

	// tags-based mutations
	
	public AbstractDocumentTag makeTag(Integer id, ParticipantId author, String text);	
	
	public Integer getLastUserTagPos(String userName);

	public BufferedDocOp deleteTagByPos(Integer position);

	public Integer getLastTagPos();	

}
