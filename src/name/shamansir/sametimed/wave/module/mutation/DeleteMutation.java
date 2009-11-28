package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.module.mutation.proto.AbstractModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public class DeleteMutation extends AbstractModuleDocumentMutation {

	@Override
	public WaveletDocumentOperation applyTo(IMutableModule module,
			BufferedDocOp sourceDoc) throws MutationCompilationException {
		// TODO Auto-generated method stub
		return null;
	}


}
