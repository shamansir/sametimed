/**
 * 
 */
package name.shamansir.sametimed.wave.doc;

import org.waveprotocol.wave.model.document.operation.Attributes;

/**
 * DirtyTag
 *
 * @author shamansir
 * @date Mar 3, 2010 12:13:03 PM
 *
 */
public final class DirtyTag extends EmptyTag {
    
    protected DirtyTag(TagID tagID, String name, Attributes attrs, String content) {
        super(tagID, name);
        useData(attrs, content);
    }  
    
    protected void initFromElement(String name, Attributes attrs, String content) throws IllegalArgumentException {
        String id = attrs.get(ID_ATTR_NAME);
        String author = attrs.get(AUTHOR_ATTR_NAME);
        setID((id != null) ? parseIDAttr(id) : new TagID(null));
        setAuthor(parseAuthorAttr((author != null) ? author : UNKNOWN_AUTHOR));
        initAttributes(attrs);
            
        setContent((content != null) ? content : "");
    }    

}
