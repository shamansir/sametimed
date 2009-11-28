package name.shamansir.sametimed.wave.render;

import name.shamansir.sametimed.wave.messaging.IUpdatesListener;
import name.shamansir.sametimed.wave.messaging.UpdateMessage;

import org.directwebremoting.Browser;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSessions;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 *
 * The Updates Listener that informs all clients (browsers) about
 * the update, by calling JS updates handling function and
 * passing message in the XML format  
 * 
 * @see IUpdatesListener
 * @see UpdateMessage
 *  
 * @see Browser
 *
 */

public class JSUpdatesListener implements IUpdatesListener {
	
	private final String JS_UPDATES_HANDLER = "updateReceived";
	
	// private ServerContext sctx;

	/**
	 * Listener constructor (initializes server context)
	 */
	public JSUpdatesListener() {
	    // sctx = ServerContextFactory.get();
	    // FIXME: WavesClient.get(clientID).addUpdatesListener(this);
	}
	
	public void onUpdate(UpdateMessage updateMessage) {
		// FIXME: Updates are generated even for closed and sent to all clients
		
		final ScriptBuffer script = new ScriptBuffer(); 
		script.appendScript(JS_UPDATES_HANDLER + "(")
	    	  .appendScript("'" + updateMessage.encode() + "'")
	    	  .appendScript(");");
		
		/*
		Collection<ScriptSession> sessions = 
	            sctx.getAllScriptSessions();
	            
		for (ScriptSession session : sessions) {
			if (sctx.getScriptSessionById(session.getId()) != null) {
				session.addScript(script);
			}
		} */
		
	    Browser.withAllSessions(new Runnable() {
	    	public void run() {
	           
	    		ScriptSessions.addScript(script);

	        }
	    });
	} 		

}

