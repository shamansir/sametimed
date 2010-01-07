package name.shamansir.sametimed.wave.module.mutation.proto;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.doc.sequencing.DocumentProcessingException;

import org.waveprotocol.wave.model.wave.ParticipantId;

public interface IModuleWithDocument<InnerType> extends IMutableModule {

	public String getDocumentID();	

	TagID nextTagID();
	public void addTag(AbstractDocumentTag newTag) throws DocumentProcessingException;	
	public AbstractDocumentTag makeTag(TagID tagID, ParticipantId author, String text);
	void deleteTag(TagID tagID) throws DocumentProcessingException;
	
	public InnerType extract();
	
}
