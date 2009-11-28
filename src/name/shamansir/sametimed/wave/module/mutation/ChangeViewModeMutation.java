package name.shamansir.sametimed.wave.module.mutation;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

import name.shamansir.sametimed.wave.module.mutation.proto.AbstractModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;
import name.shamansir.sametimed.wave.render.RenderMode;

public class ChangeViewModeMutation extends AbstractModuleDocumentMutation {
	
	private final RenderMode targetMode; 

	public ChangeViewModeMutation(RenderMode targetMode) {
		this.targetMode = targetMode;
	}
	
	public RenderMode getTargetMode() {
		return targetMode;
	}

	@Override
	public WaveletDocumentOperation applyTo(IMutableModule module,
			BufferedDocOp sourceDoc) throws MutationCompilationException {
		// TODO Auto-generated method stub
		module.setOutputMode(targetMode);
		return null;
	}
	

}
