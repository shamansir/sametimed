package name.shamansir.sametimed.wave.module.mutation.proto;

import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public interface IModuleMutation {
	
	public <ModuleType extends IMutableModule> WaveletDocumentOperation applyTo(ModuleType module) throws MutationCompilationException;
	
}
