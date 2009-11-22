package name.shamansir.sametimed.wave.doc;

import java.text.ParseException;

import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public abstract class AbstractOperableDocument<InnerType> implements IOperableDocument {
	
	private final String documentID;
	
	public AbstractOperableDocument(String documentID) throws ParseException {
		this.documentID = validateID(documentID);
	}
		
	protected <ResultType> ResultType applyCursor(BufferedDocOp srcDoc, ICursorWithResult<ResultType> cursor) {
		if (srcDoc == null) return cursor.getResult();
		srcDoc.apply(new InitializationCursorAdapter(cursor));
		return cursor.getResult();
	}
	
	protected WaveletDocumentOperation createDocumentOperation(BufferedDocOp operation) {
		return new WaveletDocumentOperation(getDocumentID(), operation);
	}
	
	protected static DocOpBuilder alignToTheDocumentEnd(DocOpBuilder docOp, BufferedDocOp srcDoc) {
		int docSize = (srcDoc == null) ? 0 : ClientUtils
				.findDocumentSize(srcDoc);		
		if (docSize > 0) {
			docOp.retain(docSize);
		}
		return docOp;
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
	
	public String getDocumentID() {
		return documentID;
	}
	
	protected static String validateID(String IDtoValidate) throws ParseException {
		if (IDtoValidate.contains(" ")) throw new ParseException("Document ID (" + IDtoValidate + ") must not contain spaces", IDtoValidate.indexOf(" "));
		return IDtoValidate;
	}
	
	public abstract InnerType extract(BufferedDocOp srcDoc);
	// protected abstract ADocumentTag makeTagForAppend(ParticipantId author, String text);		

}
