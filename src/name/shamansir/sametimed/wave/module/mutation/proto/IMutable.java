package name.shamansir.sametimed.wave.module.mutation.proto;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public interface IMutable {
	
	public WaveletDocumentOperation compileMutation(BufferedDocOp sourceDoc, IMutation mutation) throws MutationCompilationException;	

}
