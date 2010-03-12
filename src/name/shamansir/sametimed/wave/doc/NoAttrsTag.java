/**
 * 
 */
package name.shamansir.sametimed.wave.doc;

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.ImmutableMap;

/**
 * NoAttrsTag
 *
 * @author shamansir
 * @date Feb 28, 2010 11:25:39 PM
 *
 */
public class NoAttrsTag extends AbstractDocumentTag {

    protected NoAttrsTag(TagID id, String tagName, String author, String content) {
        super(id, tagName, author, content);
    }
    
    public NoAttrsTag(TagID id, String tagName, ParticipantId author, String content) {
        super(id, tagName, author, content);
    }    

    public NoAttrsTag(TagID id, String tagName, ParticipantId author) {
        super(id, tagName, author);
    }
    
    public NoAttrsTag(TagID id, String tagName, String author) {
        super(id, tagName, author);
    }

    @Override
    protected boolean checkAttributes(Attributes attrs) { return true; }

    @Override
    protected ImmutableMap<String, String> compileAttributes() { return ImmutableMap.of(); }

    @Override
    protected void initAttributes(Attributes attrs) { }

}
