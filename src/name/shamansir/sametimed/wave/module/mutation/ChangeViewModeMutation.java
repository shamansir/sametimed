package name.shamansir.sametimed.wave.module.mutation;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

import name.shamansir.sametimed.wave.module.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;
import name.shamansir.sametimed.wave.render.RenderMode;

public class ChangeViewModeMutation implements IMutation {
	
	private final RenderMode targetMode; 

	public ChangeViewModeMutation(RenderMode targetMode) {
		this.targetMode = targetMode;
	}
	
	public RenderMode getTargetMode() {
		return targetMode;
	}

	@Override
	public WaveletDocumentOperation compile(BufferedDocOp sourceDoc,
			IMutableModule targetModule) throws MutationCompilationException {
		targetModule.handleRenderModeChange(sourceDoc, targetMode);
		return null;
	}
	

}
