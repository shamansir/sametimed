package name.shamansir.sametimed.wave.module;

import java.text.ParseException;

public abstract class AbstractTreeModule<InnerType> extends AbstractMutableModule<InnerType> {

	public AbstractTreeModule(String moduleID, String documentID) throws ParseException {
		super(moduleID, documentID, true);
	}

}
