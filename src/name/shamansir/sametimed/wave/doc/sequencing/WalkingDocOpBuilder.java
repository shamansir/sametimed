package name.shamansir.sametimed.wave.doc.sequencing;

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
		docWalker.addElmStart();
		return super.elementStart(type, attrs);
	}	
	
	@Override
	public DocOpBuilder characters(String s) {
		docWalker.addElmChars(s.length());
		return super.characters(s);
	}	

	@Override
	public DocOpBuilder elementEnd() {
		docWalker.addElmEnd();
		return super.elementEnd();
	}
	
	@Override
	public DocOpBuilder deleteElementStart(String type, Attributes attrs) {
		docWalker.deleteElmStart();
		return super.deleteElementStart(type, attrs);
	}
	
	@Override
	public DocOpBuilder deleteCharacters(String s) {
	    docWalker.deleteElmChars(s.length());
		return super.deleteCharacters(s);
	}

	@Override
	public DocOpBuilder deleteElementEnd() {
	    docWalker.deleteElmEnd();
		return super.deleteElementEnd();
	}
	

}
