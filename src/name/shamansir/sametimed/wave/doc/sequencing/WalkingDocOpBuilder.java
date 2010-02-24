package name.shamansir.sametimed.wave.doc.sequencing;

import org.apache.commons.lang.NotImplementedException;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.AttributesUpdate;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;

public class WalkingDocOpBuilder extends DocOpBuilder {
    
	private final DocumentWalker docWalker;
	
	private int savedRetain = 0;
	
	public WalkingDocOpBuilder(DocumentWalker docWalker) {
		super();
		this.docWalker = docWalker; 
	}

	@Override
	public DocOpBuilder elementStart(String type, Attributes attrs) {
	    flushRetain();
		docWalker.addElmStart();
		return super.elementStart(type, attrs);
	}	
	
	@Override
	public DocOpBuilder characters(String s) {
	    flushRetain();
		docWalker.addElmChars(s.length());
		return super.characters(s);
	}	

	@Override
	public DocOpBuilder elementEnd() {
	    flushRetain();
		docWalker.addElmEnd();
		return super.elementEnd();
	}
	
	@Override
	public DocOpBuilder deleteElementStart(String type, Attributes attrs) {
	    flushRetain();
		docWalker.deleteElmStart();
		return super.deleteElementStart(type, attrs);
	}
	
	@Override
	public DocOpBuilder deleteCharacters(String s) {
	    flushRetain();
	    docWalker.deleteElmChars(s.length());
		return super.deleteCharacters(s);
	}	

	@Override
	public DocOpBuilder deleteElementEnd() {
	    flushRetain();
	    docWalker.deleteElmEnd();
		return super.deleteElementEnd();
	}
	
	@Override
	public DocOpBuilder replaceAttributes(Attributes oldAttrs, Attributes newAttrs) {
	    flushRetain();
	    return super.replaceAttributes(oldAttrs, newAttrs);
	}
	
	@Override 
	public DocOpBuilder updateAttributes(AttributesUpdate update) {
	    flushRetain();
        return super.updateAttributes(update);
	}
	
	public DocOpBuilder retainElementStart() {
	    docWalker.stepElmFwd(false);
	    return manualRetain(1);
	}
	
    public DocOpBuilder retainElementEnd() {
         docWalker.stepElmFwd(true);
         return manualRetain(1);
    }
    
    public DocOpBuilder retainCharacters(int chars) {
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
	    // return super.retain(elmCount);  
	    // FIXME: be sure to flush retain in the end of operations with builder
	    savedRetain += elmCount;
	    return this;
	}
	
	private DocOpBuilder flushRetain() {
	    if (savedRetain > 0) {
	        super.retain(savedRetain);
	        savedRetain = 0;
	    }
	    return this;
	}
	
    protected void trackCursor(AbstractOperatingCursor cursor) throws DocumentProcessingException {
        cursor.assignBuilder(this);
        docWalker.walkWithCursor(cursor);
    }
	
	@Override
    public BufferedDocOp build() {
	    flushRetain();
	    return super.build();
    }
	
    @Override
    public BufferedDocOp buildUnchecked() {
        flushRetain();
        return super.buildUnchecked();
    }	
	
	public DocumentWalker getWalker() {
	    return docWalker;
	}

}
