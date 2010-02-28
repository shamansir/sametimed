/**
 * 
 */
package name.shamansir.sametimed.test.mock;

import java.util.LinkedHashMap;
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
    //         ELM RE: \[(?:\{(\w*)(?:\:([\w=;@\.\?]*))?\})?\s*([^\s\]]*)\]  
    //         ATTRS RE: (\w*)=([\w@\.\?]*)(?:;|$)
    
    protected DocOpBuffer document = new DocOpBuffer();
    
    protected final String defaultTagName;    
    
    private final Pattern elmPattern = 
        Pattern.compile("\\[(?:\\{(\\w*)(?:\\:([\\w=;@\\.\\?]*))?\\})?" + 
                        "\\s*([^\\s\\]]*)\\]");
    private final Pattern attrsPattern = 
        Pattern.compile("(\\w*)=([\\w@\\.\\?]*)(?:;|$)");    
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
        
        String tagName; String chars;
        Map<String, String> attrsMap = new LinkedHashMap<String, String>();
        String attrsText;
        
        while (elm_m.find()) {
            attrsMap.clear();
            tagName = (elm_m.group(1) != null) ? elm_m.group(1) : defaultTagName;
            attrsText = (elm_m.group(2) != null) ? elm_m.group(2) : "";
            chars = (elm_m.group(3) != null) ? elm_m.group(3) : "";            

            if (attrsText.length() > 0) {
                Matcher attrs_m = attrsPattern.matcher(attrsText);                
                
                while (attrs_m.find()) {
                    attrsMap.put(attrs_m.group(1), attrs_m.group(2));
                }
            }
            
            document.elementStart(tagName, new AttributesImpl(attrsMap));
            document.characters(chars);
            document.elementEnd();
            
        }
        
        return document.finish();
    }
    
}    

