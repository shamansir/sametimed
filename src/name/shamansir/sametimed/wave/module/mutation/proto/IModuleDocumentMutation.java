package name.shamansir.sametimed.wave.module.mutation.proto;

import name.shamansir.sametimed.wave.doc.sequencing.DocumentProcessingException;
import name.shamansir.sametimed.wave.module.AbstractModuleWithDocument;

import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public interface IModuleDocumentMutation {
	
	// FIXME: OperationsSequenceBrokenException must be unknown to IModuleDocumentMutation
	public WaveletDocumentOperation applyTo(AbstractModuleWithDocument<?> module) throws MutationCompilationException, DocumentProcessingException;
	
}
