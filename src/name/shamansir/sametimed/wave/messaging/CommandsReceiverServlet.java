package name.shamansir.sametimed.wave.messaging;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * The main servlet that receives (in XML format) and executes commands, 
 * passed from Waves Client (such as creating wave, opening wave, saying something,
 * adding someone & s.o.)
 * 
 * @see Command
 * @see CommandTypeID
 * 
 * @see HttpServlet
 */ 

@SuppressWarnings("serial")
public class CommandsReceiverServlet extends HttpServlet {
	
	private static final Logger LOG = Logger.getLogger(CommandsReceiverServlet.class.getName());	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// int clientId = Integer.valueOf(request.getParameter("clientId"));
		if (request.getParameter("cmdXML") != null) {
			String commandXML = URLDecoder.decode(request.getParameter("cmdXML"), "UTF-8");
			LOG.info("Command: " + commandXML);
			Command command = null;
			try {
				 command = Command.fromXMLString(commandXML);
				 command.execute();
			} catch (DocumentException e) {
				LOG.severe("Failed to extract command data on the server side from " + commandXML);
			}
		} else {
			LOG.severe("No command were passed to the Commands Receiver Servlet");
		}
		/* PrintWriter responseWriter = response.getWriter();
		if (command != null) {
			// responseWriter.write(command.getAffectedHoldersIds());
			// responseWriter.write(command.getPanelsToRedrawContent());			
		} else {
			responseWriter.write("");
		} */		
	}
	
}
