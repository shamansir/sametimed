package name.shamansir.sametimed.wave.module.mutation.proto;

import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public interface IModuleMutation {
	
	public WaveletDocumentOperation applyTo(IMutableModule module) throws MutationCompilationException;
	
}
