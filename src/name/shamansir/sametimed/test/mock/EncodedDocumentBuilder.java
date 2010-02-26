/**
 * 
 */
package name.shamansir.sametimed.test.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuffer;

/**
 * EncodedDocumentBuilder
 *
 * @author shamansir
 * @date Feb 26, 2010 8:42:28 PM
 *
 */
public class EncodedDocumentBuilder {
    
    // FORMAT: [{a:id=a;user=test} info][{b:id=b}fun][{c}like][test]
    //         ELM RE: \[(?:\{(\w*)(?:\:([\w=;]*))?\})?\s*([^\s\]]*)\]  
    //         ATTRS RE: (\w*)=(\w*)(?:;|$)
    
    protected DocOpBuffer document = new DocOpBuffer();
    
    protected final String defaultTagName;    
    
    private final Pattern elmPattern = 
        Pattern.compile("\\[(?:\\{(\\w*)(?:\\:([\\w=;]*))?\\})?" + 
                        "\\s*([^\\s\\]]*)\\]");
    private final Pattern attrsPattern = 
        Pattern.compile("(\\w*)=(\\w*)(?:;|$)");    
    protected final String code; 

    public EncodedDocumentBuilder(String code, String defaultTagName) {
        this.code = code;
        this.defaultTagName = defaultTagName;
    }
    
    public EncodedDocumentBuilder(String code) {
        this(code, "a");
    }    
    
    public BufferedDocOp compile() {        
        Matcher elm_m = elmPattern.matcher(code);        
        
        String tagName;
        Map<String, String> attrsMap = new HashMap<String, String>();
        AttributesImpl attrs;
        String chars;
        
        while (elm_m.matches()) {
            tagName = defaultTagName;
            attrs = AttributesImpl.EMPTY_MAP;
            chars = "";
            
            if (elm_m.groupCount() == 1) {
                chars = elm_m.group(1);
            } else if (elm_m.groupCount() == 2) {
                tagName = elm_m.group(1);
                chars = elm_m.group(2);
            } else if (elm_m.groupCount() == 3) {
                tagName = elm_m.group(1);
                chars = elm_m.group(2);                
                                
                final String attrsText = elm_m.group(2);
                Matcher attrs_m = attrsPattern.matcher(attrsText);
                attrsMap.clear();
                
                while (attrs_m.matches()) {
                    attrsMap.put(attrs_m.group(1), attrs_m.group(2));
                }
            }
            
            document.elementStart(tagName, attrs);
            document.characters(chars);
            document.elementEnd();
            
        }
        
        return document.finish();
    }
    
}    

