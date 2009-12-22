package name.shamansir.sametimed.wave.doc;

@SuppressWarnings("serial")
public class DocumentProcessingException extends Exception {
	
	public DocumentProcessingException(String cause) {
		super("Document processing exception; " + cause);
	}
	
}
