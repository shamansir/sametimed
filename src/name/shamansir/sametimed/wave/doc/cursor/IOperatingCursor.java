package name.shamansir.sametimed.wave.doc.cursor;

import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;

public interface IOperatingCursor extends DocInitializationCursor{

	void setWalkStart(int pos);

	DocOpBuilder takeDocOp();

}
