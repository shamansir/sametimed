package name.shamansir.sametimed.wave.doc;

import name.shamansir.sametimed.wave.doc.cursor.ICursorWithResult;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public abstract class AOperableDocument<InnerType> implements IOperableDocument {
	
	private final String documentID;
	
	public AOperableDocument(String documentID) {
		this.documentID = documentID;
	}
		
	protected <ResultType> ResultType applyCursor(BufferedDocOp srcDoc, ICursorWithResult<ResultType> cursor) {
		srcDoc.apply(new InitializationCursorAdapter(cursor));
		return (ResultType)cursor.getResult();
	}
	
	protected WaveletDocumentOperation createDocumentOperation(BufferedDocOp operation) {
		return new WaveletDocumentOperation(getDocumentID(), operation);
	}
	
	public String getDocumentID() {
		return documentID;
	}
	
	public abstract InnerType extract(BufferedDocOp srcDoc);

}
