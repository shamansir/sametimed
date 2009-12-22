package name.shamansir.sametimed.wave.module;

import java.text.ParseException;

import name.shamansir.sametimed.wave.AbstractUpdatingWavelet;

public abstract class AbstractVerticalModule<InnerType> extends AbstractModuleWithDocument<InnerType> {

	public AbstractVerticalModule(AbstractUpdatingWavelet parent, String moduleID, String documentID) throws ParseException {
		super(parent, moduleID, documentID, false, false);
	}
		
}
