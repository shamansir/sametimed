/**
 * 
 */
package name.shamansir.sametimed.test.mock;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.AttributesUpdate;
import org.waveprotocol.wave.model.document.operation.EvaluatingDocOpCursor;

/**
 * OperationsRecordingCursor
 *
 * @author shamansir
 * @date Feb 26, 2010 8:44:34 PM
 *
 */
public class OperationsRecordingCursor implements EvaluatingDocOpCursor<String> {
    
    protected final StringBuffer operationsRecorder = new StringBuffer();
    
    private String escape(String input) {
        String output = input.replaceAll("\\}", "\\\\}");
        output = output.replaceAll("\\{", "\\\\{");
        output = output.replaceAll("\\)", "\\\\)");
        return output.replaceAll("\\(", "\\\\(");
    }

    @Override
    public String finish() { 
        return operationsRecorder.toString(); 
    }
    
    public void erase() { 
        operationsRecorder.delete(0, operationsRecorder.length()); 
    }

    @Override
    public void deleteElementStart(String type, Attributes attrs) {
        operationsRecorder.append("(-{)");
    }    
    
    @Override
    public void deleteCharacters(String chars) {
        operationsRecorder.append("(-" + escape(chars) + ")");
    }

    @Override
    public void deleteElementEnd() { 
        operationsRecorder.append("(-})"); 
    }

    @Override
    public void replaceAttributes(Attributes oldAttrs, Attributes newAttrs) { }

    @Override
    public void retain(int itemCount) {
        operationsRecorder.append("(*" + itemCount + ")");          
    }

    @Override
    public void updateAttributes(AttributesUpdate attrUpdate) { }

    @Override
    public void annotationBoundary(AnnotationBoundaryMap map) { }

    @Override
    public void elementStart(String type, Attributes attrs) {
        operationsRecorder.append("{");
    } 
    
    @Override
    public void characters(String chars) {
        operationsRecorder.append(escape(chars));
    }

    @Override
    public void elementEnd() {
        operationsRecorder.append("}");         
    } 
    
}    

