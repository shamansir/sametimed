/**
 * 
 */
package name.shamansir.sametimed.test.mock;

import java.util.Map;

import org.waveprotocol.wave.model.document.operation.Attributes;

/**
 * DetailedOperationsRecordingCursor
 *
 * @author shamansir
 * @date Feb 26, 2010 11:03:01 PM
 *
 */
public class DetailedOperationsRecordingCursor extends
        OperationsRecordingCursor {

    @Override
    public void deleteElementStart(String type, Attributes attrs) {
        // operationsRecorder.append("(-{" + type + ":" + compileAttrs(attrs) + ")");
        operationsRecorder.append("(-[)");
    }
    
    @Override
    public void deleteElementEnd() { 
        operationsRecorder.append("(-])"); 
    }    
    
    public void elementStart(String type, Attributes attrs) {
        operationsRecorder.append("[{" + type + ":" + compileAttrs(attrs) + "}");
    }
    
    @Override
    public void elementEnd() {
        operationsRecorder.append("]");         
    }    
    
    private String compileAttrs(Attributes attrs) {
        String attrsStr = "";
        for (Map.Entry<String, String> attr: attrs.entrySet()) {
            attrsStr += attr.getKey() + ":" + attr.getValue() + ";";
        }
        return attrsStr;
    }
    
}
