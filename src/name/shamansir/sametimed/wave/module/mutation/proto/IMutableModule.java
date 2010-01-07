package name.shamansir.sametimed.wave.module.mutation.proto;

import name.shamansir.sametimed.wave.doc.sequencing.DocumentProcessingException;

import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public interface IMutableModule {
	
	
	// public boolean isStructured();	
	
	public String getModuleID();	
	
	// public BufferedDocOp getSource(); // return source document
	
	// FIXME: OperationsSequenceBrokenException must be unknown to MutableModule
	WaveletDocumentOperation apply(IModuleDocumentMutation mutation) throws MutationCompilationException, DocumentProcessingException;	

	/* public void setOutputMode(RenderMode targetMode);

	// tags-based mutations
	
	public void addTag(AbstractDocumentTag tagToAdd);
	public AbstractDocumentTag makeTag(TagID id, ParticipantId author, String text);
	
	// FIXME: docOp must not be used here, may be make getLastDocOp method?
	public AbstractDocumentTag deleteTag(TagID id);	
	
	// public AbstractDocumentTag deleteTagAndGet(TagID id);
	
	public TagID getLastUserTagID(String userName);

	public TagID nextTagID();

	public TagID getTagAtPosition(Integer pos);

	public Integer findTagStartPosition(TagID tagToFind); */

}
