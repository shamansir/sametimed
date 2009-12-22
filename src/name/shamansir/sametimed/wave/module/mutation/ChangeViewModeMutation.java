package name.shamansir.sametimed.wave.module.mutation;

import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

import name.shamansir.sametimed.wave.module.AbstractModuleWithDocument;
import name.shamansir.sametimed.wave.module.mutation.proto.IModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;
import name.shamansir.sametimed.wave.render.RenderMode;

public class ChangeViewModeMutation implements IModuleDocumentMutation {
	
	private final RenderMode targetMode; 

	public ChangeViewModeMutation(RenderMode targetMode) {
		this.targetMode = targetMode;
	}
	
	public RenderMode getTargetMode() {
		return targetMode;
	}

	@Override
	public WaveletDocumentOperation applyTo(AbstractModuleWithDocument<?> module) throws MutationCompilationException {
		// TODO Auto-generated method stub
		module.setOutputMode(targetMode);
		return null;
	}
	

}
