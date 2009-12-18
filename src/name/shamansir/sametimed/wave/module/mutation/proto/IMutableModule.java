package name.shamansir.sametimed.wave.module.mutation.proto;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;
import name.shamansir.sametimed.wave.render.RenderMode;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

public interface IMutableModule {
	
	public boolean isStructured();
	
	public String getModuleID();	
	
	public String getDocumentID();
	
	public BufferedDocOp getSource(); // return source document
	
	public <ResultType> ResultType applyCursor(ICursorWithResult<ResultType> cursor);
	
	WaveletDocumentOperation apply(IModuleMutation mutation) throws MutationCompilationException;	

	public void setOutputMode(RenderMode targetMode);

	// tags-based mutations
	
	public AbstractDocumentTag makeTag(TagID id, ParticipantId author, String text);
	
	// FIXME: docOp must not be used here, may be make getLastDocOp method?
	public BufferedDocOp deleteTag(TagID id);	
	
	public AbstractDocumentTag deleteTagAndGet(TagID id);
	
	public TagID getLastUserTagID(String userName);

	public TagID nextTagID();

	public TagID getTagAtPosition(Integer pos);

	public Integer findTagStartPosition(TagID tagToFind);

}
