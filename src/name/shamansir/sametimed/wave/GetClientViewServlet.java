package name.shamansir.sametimed.wave;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.shamansir.sametimed.wave.chat.ChatWavesClient;
import name.shamansir.sametimed.wave.editor.WaveletWithEditor;
import name.shamansir.sametimed.wave.render.JSUpdatesListener;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Servlet, returning the JSON string, representing the model of the new client, 
 * using the passed username and formatting parameters. 
 * 
 * Connection to the wave server is performed here.
 * 
 */

@SuppressWarnings("serial")
public class GetClientViewServlet extends HttpServlet {
	
	private static final Logger LOG = Logger.getLogger(GetClientViewServlet.class.getName());	

	private static final String CONNECTION_ERR_STR = "Connection to the Wave Server (as user %s) is failed";	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String responseStr = ""; 
		
		String username = (String)request.getParameter("username");
		boolean useEscapedQuotes = Boolean.valueOf(request.getParameter("ueq"));
		
		/*
		WavesClient<SimpleWavelet> newClient = new WavesClient<SimpleWavelet>() {

			@Override
			protected SimpleWavelet createWavelet(IWavesClientRenderer renderer) {
				return new SimpleWavelet(getViewID(), renderer);
			}
			
		}; */
		
		ChatWavesClient newClient = new ChatWavesClient() {
			
			@Override
			protected WaveletWithEditor createWavelet(IWavesClientRenderer renderer) {
				return new WaveletWithEditor(getViewID(), renderer);
			}
			
		};
		newClient.getWavelet().addUpdatesListener(new JSUpdatesListener());
		
		String quot = useEscapedQuotes ? "\\\"" : "\"";

		response.setContentType("text/plain");		
		response.setCharacterEncoding("UTF-8");
		
		try {
			newClient.connect(username);
		} catch (Exception e) {
			LOG.severe(String.format(CONNECTION_ERR_STR + "; Exception thrown: %s, caused by %s", username, e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "nothing"));
			
			responseStr = // FIXME: create ErrorModel class
					"{\"error\": " + quot + String.format(CONNECTION_ERR_STR + "; Exception thrown: %s", username, e.getMessage()) + quot + "}"
				;
			
			sendResponseString(response, responseStr);
			return;			
		}
		
		if (!newClient.isConnected()) {
			LOG.severe(String.format(CONNECTION_ERR_STR, username));
			
			responseStr = // FIXME: create ErrorModel class
				"{" + quot + "error" + quot + ":" + quot + String.format(CONNECTION_ERR_STR, username) + quot + "}"
			;			

			sendResponseString(response, responseStr);
			return;			
		}
		
		responseStr = newClient.getWavelet().getModel().asJSON(useEscapedQuotes);
		
		sendResponseString(response, responseStr);
		
	}
	
	private void sendResponseString(HttpServletResponse response, String stringToSend) throws IOException {

		PrintWriter responseWriter = response.getWriter();
		responseWriter.write(stringToSend);
		responseWriter.close();
		
		response.flushBuffer();		
		
	}

}
