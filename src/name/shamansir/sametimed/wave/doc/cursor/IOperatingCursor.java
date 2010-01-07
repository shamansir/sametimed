package name.shamansir.sametimed.wave.doc.cursor;

import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;

public interface IOperatingCursor extends DocInitializationCursor{

	public void setWalkStart(int pos);

	public DocOpBuilder takeDocOp();

	public void useDocOp(DocOpBuilder curDocOp);

}
