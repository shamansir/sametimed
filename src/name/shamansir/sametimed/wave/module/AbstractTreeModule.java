package name.shamansir.sametimed.wave.module;

import java.text.ParseException;

import name.shamansir.sametimed.wave.AbstractUpdatingWavelet;

public abstract class AbstractTreeModule<InnerType> extends AbstractMutableModule<InnerType> {

	public AbstractTreeModule(AbstractUpdatingWavelet parent, String moduleID, String documentID) throws ParseException {
		super(parent, moduleID, documentID, true, true);
	}

}
