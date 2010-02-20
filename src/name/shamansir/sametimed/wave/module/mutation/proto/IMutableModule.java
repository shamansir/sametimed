package name.shamansir.sametimed.wave.module.mutation.proto;

import name.shamansir.sametimed.wave.doc.sequencing.DocumentProcessingException;

import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public interface IMutableModule {	
	
	// public boolean isStructured();	
	
	public String getModuleID();	
	
	WaveletDocumentOperation apply(IModuleDocumentMutation mutation) throws MutationCompilationException, DocumentProcessingException;	

}
