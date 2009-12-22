package name.shamansir.sametimed.wave.module;

import java.text.ParseException;

import name.shamansir.sametimed.wave.AbstractUpdatingWavelet;
import name.shamansir.sametimed.wave.doc.AbstractDocumentOperationsSequencer;
import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.doc.DocumentProcessingException;
import name.shamansir.sametimed.wave.doc.cursor.DocumentElementCuttingByPosCursor;
import name.shamansir.sametimed.wave.doc.cursor.DocumentElementCuttingCursor;
import name.shamansir.sametimed.wave.doc.cursor.DocumentElementDeletionByPosCursor;
import name.shamansir.sametimed.wave.doc.cursor.DocumentElementStartPosSearchingCursor;
import name.shamansir.sametimed.wave.doc.cursor.DocumentLastTagIDCursor;
import name.shamansir.sametimed.wave.doc.cursor.DocumentLastUserElementCursor;
import name.shamansir.sametimed.wave.doc.cursor.DocumentElementDeletionCursor;
import name.shamansir.sametimed.wave.module.mutation.proto.IModuleWithDocument;
import name.shamansir.sametimed.wave.module.mutation.proto.IModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;
import name.shamansir.sametimed.wave.render.RenderMode;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

// FIXME: make getSrcDoc method to get SourceDocument	

public abstract class AbstractModuleWithDocument<InnerType> extends AbstractDocumentOperationsSequencer implements IModuleWithDocument<InnerType> {
	
	private final AbstractUpdatingWavelet parent;
	private final String moduleID;
	private final String documentID;
	private final boolean structured;
	
	protected RenderMode outputMode = RenderMode.NORMAL;	
		
	public AbstractModuleWithDocument(AbstractUpdatingWavelet parent, String moduleID, String documentID, 
			boolean structured, boolean enumerateTags) throws ParseException {		
		this.moduleID = validateID(moduleID);
		this.documentID = validateID(documentID);		
		this.structured = structured;
		this.parent = parent;
	}
		
	public String getModuleID() {
		return moduleID;
	}	
	
	public String getDocumentID() {
		return documentID;
	}
	
	@Override
	protected BufferedDocOp getSource() {
		return parent.getSource(documentID);
	}	
	
	protected static String validateID(String idToValidate) throws ParseException {
		if (idToValidate.contains(" ")) throw new ParseException("ID (" + idToValidate + ") must not contain spaces", idToValidate.indexOf(" "));
		return idToValidate;
	}	
	
	@Override
	public WaveletDocumentOperation apply(IModuleDocumentMutation mutation) throws MutationCompilationException, DocumentProcessingException {
		return mutation.applyTo(this);	
	}

	public boolean isStructured() {
		return structured;
	}
	
	public AbstractUpdatingWavelet getParentWavelet() {
		return parent;
	}
	
	public void setOutputMode(RenderMode outputMode) {
		this.outputMode  = outputMode;		
	}

	@Override
	public TagID nextTagID() {
		return applyCursor(new DocumentLastTagIDCursor()).makeNext();
	}
	
	// TODO: make all these things able to make chains	

	public TagID getLastUserTagID(String userName) {
		return applyCursor(new DocumentLastUserElementCursor(userName));
	}
	
	public int findTagStart(int pos) {
		return applyCursor(new DocumentElementStartPosSearchingCursor(pos));
	}
	

	@Override
	public void addTag(AbstractDocumentTag newTag) throws DocumentProcessingException {
		if (getCurOp() == null) throw new DocumentProcessingException("The sequence was not started before adding tag or some similar error occured");
		useAsCurOp(newTag.build(getCurOp())); // adds tag building operations to the current operation
	}
	
	public AbstractDocumentTag makeTag(ParticipantId author, String text) {		
		return makeTag(nextTagID(), author, text);
	}
	
	@Override
	public void deleteTag(TagID tagID) throws DocumentProcessingException {
		applyCursor(new DocumentElementDeletionCursor(tagID));
	}
	
	public void deleteTag(int position) throws DocumentProcessingException {
		applyCursor(new DocumentElementDeletionByPosCursor(position));
	}
	
	public AbstractDocumentTag deleteTagAndGet(TagID tagID) throws DocumentProcessingException {
		return applyCursor(new DocumentElementCuttingCursor(tagID));
	}		
	
	public AbstractDocumentTag deleteTagAndGet(int position) throws DocumentProcessingException {
		return applyCursor(new DocumentElementCuttingByPosCursor(position));
	}	
	
	
}
