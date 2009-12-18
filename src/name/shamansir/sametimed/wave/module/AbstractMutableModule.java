package name.shamansir.sametimed.wave.module;

import java.text.ParseException;

import name.shamansir.sametimed.wave.AbstractUpdatingWavelet;
import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.doc.cursor.DocumentLastTagIDCursor;
import name.shamansir.sametimed.wave.doc.cursor.DocumentLastUserElementCursor;
import name.shamansir.sametimed.wave.doc.cursor.DocumentElementByPositionCursor;
import name.shamansir.sametimed.wave.doc.cursor.DocumentElementStartCursor;
import name.shamansir.sametimed.wave.doc.cursor.ElementCuttingCursor;
import name.shamansir.sametimed.wave.doc.cursor.ElementDeletionCursor;
import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;
import name.shamansir.sametimed.wave.module.mutation.proto.IModuleMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

// FIXME: make getSrcDoc method to get SourceDocument	

public abstract class AbstractMutableModule<InnerType> implements IMutableModule {
	
	private final AbstractUpdatingWavelet parent;
	private final String moduleID;
	private final String documentID;
	private final boolean structured;
		
	public AbstractMutableModule(AbstractUpdatingWavelet parent, String moduleID, String documentID, 
			boolean structured, boolean enumerateTags) throws ParseException {		
		this.moduleID = validateID(moduleID);
		this.documentID = validateID(documentID);		
		this.structured = structured;
		this.parent = parent;
	}
		
	@Override
	public String getModuleID() {
		return moduleID;
	}	
	
	@Override
	public String getDocumentID() {
		return documentID;
	}
	
	public BufferedDocOp getSource() {
		return parent.getSource(documentID);
	}	
	
	protected static String validateID(String idToValidate) throws ParseException {
		if (idToValidate.contains(" ")) throw new ParseException("ID (" + idToValidate + ") must not contain spaces", idToValidate.indexOf(" "));
		return idToValidate;
	}
	
	public abstract InnerType extract();
	// protected abstract ADocumentTag makeTagForAppend(ParticipantId author, String text);
	
	@Override
	public WaveletDocumentOperation apply(IModuleMutation mutation) throws MutationCompilationException {
		return mutation.applyTo(this);	
	}
	
	@Override
	public <ResultType> ResultType applyCursor(ICursorWithResult<ResultType> cursor) {
		BufferedDocOp sourceDoc = getSource(); 
		if (sourceDoc == null) return cursor.getResult();
		sourceDoc.apply(new InitializationCursorAdapter(cursor));
		return cursor.getResult();
	}	

	@Override
	public boolean isStructured() {
		return structured;
	}
	
	public AbstractUpdatingWavelet getParentWavelet() {
		return parent;
	}

	@Override
	public BufferedDocOp deleteTag(TagID tagID) {
		return applyCursor(new ElementDeletionCursor(tagID));
	}
	
	@Override
	public TagID getLastUserTagID(String userName) {
		return applyCursor(new DocumentLastUserElementCursor(userName));
	}
	
	@Override
	public TagID nextTagID() {
		return applyCursor(new DocumentLastTagIDCursor()).makeNext();
	}
	
	@Override
	public TagID getTagAtPosition(Integer pos) {
		return applyCursor(new DocumentElementByPositionCursor(pos));
	}

	@Override
	public Integer findTagStartPosition(TagID tagToFind) {
		return applyCursor(new DocumentElementStartCursor(tagToFind));
	}
	
	@Override
	public AbstractDocumentTag deleteTagAndGet(TagID tagID) {
		return applyCursor(new ElementCuttingCursor(tagID));
	}
	
}
