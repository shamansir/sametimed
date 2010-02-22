package name.shamansir.sametimed.wave.doc.sequencing;

import org.apache.commons.lang.NotImplementedException;
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
	
	public DocOpBuilder retainElementStart() {
	   // FIXME: ensure stepping is required
	    docWalker.stepElmFwd(false);
	    return manualRetain(1);
	}
	
    public DocOpBuilder retainElementEnd() {
        // FIXME: ensure stepping is required
         docWalker.stepElmFwd(true);
         return manualRetain(1);
    }
    
    public DocOpBuilder retainCharacters(int chars) {
        // FIXME: ensure stepping is required
         docWalker.stepCharsFwd(chars);
         return manualRetain(chars);
    }    
	
    /**
     * Please use {@link #retainElementStart()}, {@link #retainElementEnd()},
     * {@link #retainCharacters(int)} in the appropriate conditions/situation,
     * this method always throws NotImplementedException and implemented just 
     * because of DocOpBuilder requirements 
     */
	@Override
	public DocOpBuilder retain(int itemCount) {
	    throw new NotImplementedException("Please don't use this method, use " +
	    		"retainElementStart, retainElementEnd or retainCharacters");
	    // FIXME: how I can say to docWalker what happens if retain is called from
	    // outside, do I need to wrap DocOpBuilder and remove retain from there?
	}
	
	protected DocOpBuilder manualRetain(int elmCount) {
	    // FIXME: implement
	    return super.retain(elmCount);
	}
	
	public DocumentWalker getWalker() {
	    return docWalker;
	}

}
