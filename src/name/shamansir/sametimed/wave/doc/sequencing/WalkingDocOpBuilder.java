package name.shamansir.sametimed.wave.doc.sequencing;

import org.apache.commons.lang.NotImplementedException;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.AttributesUpdate;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;

public class WalkingDocOpBuilder extends DocOpBuilder {
    
	private final DocumentWalker docWalker;
	
	private int savedRetain = 0;
	private String savedDelChars = "";
    private String savedAddChars = "";	
	
	public WalkingDocOpBuilder(DocumentWalker docWalker) {
		super();
		this.docWalker = docWalker; 
	}

	@Override
	public DocOpBuilder elementStart(String type, Attributes attrs) {
	    flushRetain(); flushChars();
		docWalker.foundElmStart();
		return super.elementStart(type, attrs);
	}	
	
	@Override
	public DocOpBuilder characters(String s) {
	    flushRetain();
		docWalker.foundChars(s.length());
		return manualAddChars(s);
	}	

	@Override
	public DocOpBuilder elementEnd() {
	    flushRetain(); flushChars();
		docWalker.foundElmEnd();
		return super.elementEnd();
	}
	
	@Override
	public DocOpBuilder deleteElementStart(String type, Attributes attrs) {
	    flushRetain(); flushChars();
		docWalker.deleteElmStart();
		return super.deleteElementStart(type, attrs);
	}
	
	@Override
	public DocOpBuilder deleteCharacters(String s) {
	    flushRetain();
	    docWalker.deleteElmChars(s.length());
		return manualDelChars(s);
	}	

	@Override
	public DocOpBuilder deleteElementEnd() {
	    flushRetain(); flushChars();
	    docWalker.deleteElmEnd();
		return super.deleteElementEnd();
	}
	
	@Override
	public DocOpBuilder replaceAttributes(Attributes oldAttrs, Attributes newAttrs) {
	    flushRetain(); flushChars();
	    return super.replaceAttributes(oldAttrs, newAttrs);
	}
	
	@Override 
	public DocOpBuilder updateAttributes(AttributesUpdate update) {
	    flushRetain(); flushChars();
        return super.updateAttributes(update);
	}
	
	public DocOpBuilder retainElementStart() {
	    flushChars();
	    docWalker.stepElmFwd(false);
	    return manualRetain(1);
	}
	
    public DocOpBuilder retainElementEnd() {
        flushChars();
        docWalker.stepElmFwd(true);
        return manualRetain(1);
    }
    
    public DocOpBuilder retainCharacters(int chars) {
        flushChars();
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
	    savedRetain += elmCount;
	    return this;
	}
	
	protected DocOpBuilder manualAddChars(String chars) {
	    // return super.characters(chars);
	    savedAddChars += chars;
	    return this;
	}
	
    protected DocOpBuilder manualDelChars(String chars) {
        // return super.deleteCharacters(chars);
        savedDelChars += chars;
        return this;
    }
	
	protected DocOpBuilder flushRetain() {
	    if (savedRetain > 0) {
	        super.retain(savedRetain);
	        savedRetain = 0;
	    }
	    return this;
	}
	
    protected DocOpBuilder flushChars() {
        if (savedAddChars.length() > 0) {
            super.characters(savedAddChars);
            savedAddChars = "";
        }
        if (savedDelChars.length() > 0) {
            super.deleteCharacters(savedDelChars);
            savedDelChars = "";
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
