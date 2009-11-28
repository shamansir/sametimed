package name.shamansir.sametimed.wave.module;

import java.text.ParseException;

public abstract class AbstractVerticalModule<InnerType> extends AbstractMutableModule<InnerType> {

	public AbstractVerticalModule(String moduleID, String documentID) throws ParseException {
		super(moduleID, documentID, false);
	}
	
}
