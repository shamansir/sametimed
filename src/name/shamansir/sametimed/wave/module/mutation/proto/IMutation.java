package name.shamansir.sametimed.wave.module.mutation.proto;

import name.shamansir.sametimed.wave.module.IMutableModule;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public interface IMutation {

	public WaveletDocumentOperation compile(BufferedDocOp sourceDoc, IMutableModule targetModule) throws MutationCompilationException;
	
}
