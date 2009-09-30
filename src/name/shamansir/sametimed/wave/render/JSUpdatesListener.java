package name.shamansir.sametimed.wave.render;

import java.util.Collection;

import name.shamansir.sametimed.wave.messaging.IUpdatesListener;
import name.shamansir.sametimed.wave.messaging.UpdateMessage;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ServerContext;
import org.directwebremoting.ServerContextFactory;

public class JSUpdatesListener implements IUpdatesListener {
	
	private ServerContext sctx;

	public JSUpdatesListener() {
	    sctx = ServerContextFactory.get();
	    // FIXME: WavesClient.get(clientId).addUpdatesListener(this);
	}
	
	public void onUpdate(UpdateMessage updateMessage) {
		ScriptBuffer script = new ScriptBuffer(); 
		script.appendScript("updateReceived(")
	    		.appendScript("'" + updateMessage.toXMLString() + "'")
	    		.appendScript(");");
		
		Collection<ScriptSession> sessions = 
	            sctx.getAllScriptSessions();
	            
		for (ScriptSession session : sessions) {
			if (sctx.getScriptSessionById(session.getId()) != null) {
				session.addScript(script);
			}
		}		

	}

}
