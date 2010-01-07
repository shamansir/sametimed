package name.shamansir.sametimed.wave.doc;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;

public class WalkingDocOpBuilder extends DocOpBuilder {
	
	private final DocumentWalker docWalker; 
	
	public WalkingDocOpBuilder(DocumentWalker docWalker) {
		super();
		this.docWalker = docWalker; 
	}

	@Override
	public DocOpBuilder elementStart(String type, Attributes attrs) {
		docWalker.stepElmFwd();
		return super.elementStart(type, attrs);
	}	
	
	@Override
	public DocOpBuilder characters(String s) {
		docWalker.stepCharsFwd(s.length());
		return super.characters(s);
	}	

	@Override
	public DocOpBuilder elementEnd() {
		docWalker.stepElmFwd();
		return super.elementEnd();
	}
	
	@Override
	public DocOpBuilder deleteElementStart(String type, Attributes attrs) {
		docWalker.stepElmBkwd();
		return super.deleteElementStart(type, attrs);
	}
	
	@Override
	public DocOpBuilder deleteCharacters(String s) {
		docWalker.stepCharsBkwd(s.length());
		return super.deleteCharacters(s);
	}

	@Override
	public DocOpBuilder deleteElementEnd() {
		docWalker.stepElmBkwd();
		return super.deleteElementEnd();
	}
	

}
