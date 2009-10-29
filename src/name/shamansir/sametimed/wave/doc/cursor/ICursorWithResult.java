package name.shamansir.sametimed.wave.doc.cursor;

import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;

public interface ICursorWithResult<ResultType> extends DocInitializationCursor {

	public abstract ResultType getResult();
	
}
