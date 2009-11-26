package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.module.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public class DeleteMutation implements IMutation {

	@Override
	public WaveletDocumentOperation compile(BufferedDocOp sourceDoc,
			IMutableModule targetModule) throws MutationCompilationException {
		// FIXME: implement
		return null;
	}

}
