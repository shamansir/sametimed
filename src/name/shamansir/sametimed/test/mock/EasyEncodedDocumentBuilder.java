/**
 * 
 */
package name.shamansir.sametimed.test.mock;

import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;

/**
 * EasyEncodedDocumentBuilder
 *
 * @author shamansir
 * @date Feb 26, 2010 8:49:39 PM
 *
 */
public class EasyEncodedDocumentBuilder extends EncodedDocumentBuilder {
    
    // FORMAT: [abc][defg][hijkl][1234][30202][mnop]
    
    public EasyEncodedDocumentBuilder(String code, String tagsName) {
        super(code, tagsName);
    }
    
    @Override
    public BufferedDocOp compile() {
        for (byte b: code.getBytes()) {
            if (b == (byte)'[') {
                document.elementStart(defaultTagName, AttributesImpl.EMPTY_MAP);
            } else if (b == (byte)']') {
                document.elementEnd();
            } else {
                document.characters(String.valueOf((char)b));
            }
        }
        return document.finish();        
    }

}
