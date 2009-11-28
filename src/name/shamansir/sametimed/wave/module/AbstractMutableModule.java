package name.shamansir.sametimed.wave.module;

import java.text.ParseException;

import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;
import name.shamansir.sametimed.wave.module.mutation.proto.IModuleMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

// FIXME: make getSrcDoc method to get SourceDocument	

public abstract class AbstractMutableModule<InnerType> implements IMutableModule {
	
	private final String moduleID;
	private final String documentID;
	private final boolean structured;
	
	public AbstractMutableModule(String moduleID, String documentID, boolean structured) throws ParseException {
		this.moduleID = moduleID;
		this.documentID = validateID(documentID);
		this.structured = structured;
	}
	
	/* makes operation easier, but, because getUndoOp is implemented in child, must also be
	 * implemented there */ 
	/* 
	@Override
	public WaveletDocumentOperation getAppendOp(BufferedDocOp srcDoc, ParticipantId author,
			String text) {
		int docSize = (srcDoc == null) ? 0 : ClientUtils
				.findDocumentSize(srcDoc);
		DocOpBuilder docOp = new DocOpBuilder();

		if (docSize > 0) {
			docOp.retain(docSize);
		}

		docOp = makeTagForAppend(author, text).createTagFor(docOp);
		
		return createDocumentOperation(docOp.finish());

	} */	
	
	@Override
	public String getModuleID() {
		return moduleID;
	}	
	
	@Override
	public String getDocumentID() {
		return documentID;
	}
	
	protected static String validateID(String idToValidate) throws ParseException {
		if (idToValidate.contains(" ")) throw new ParseException("Document ID (" + idToValidate + ") must not contain spaces", idToValidate.indexOf(" "));
		return idToValidate;
	}
	
	public abstract InnerType extract(BufferedDocOp srcDoc);
	// protected abstract ADocumentTag makeTagForAppend(ParticipantId author, String text);
	
	@Override
	public WaveletDocumentOperation apply(BufferedDocOp sourceDoc, IModuleMutation mutation) throws MutationCompilationException {
		return mutation.applyTo(this, sourceDoc);	
	}
	
	@Override
	public <ResultType> ResultType applyCursor(BufferedDocOp srcDoc, ICursorWithResult<ResultType> cursor) {
		if (srcDoc == null) return cursor.getResult();
		srcDoc.apply(new InitializationCursorAdapter(cursor));
		return cursor.getResult();
	}	

	@Override
	public boolean isStructured() {
		return structured;
	}

}
