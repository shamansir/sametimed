package name.shamansir.sametimed.wave.doc.cursor;

import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;

// FIXME: use this cursor only for cursors that require to scan the whole
//        document

public interface ICursorWithResult<ResultType> extends DocInitializationCursor {

	public ResultType getResult();
	
}
