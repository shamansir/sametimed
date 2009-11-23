package name.shamansir.sametimed.wave.messaging;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * The main servlet that receives (in special format) and executes a sequence of commands, 
 * passed from Waves Client (such as creating wave, opening wave, saying something,
 * adding someone & s.o.)
 * 
 * @see Command
 * @see CommandTypeID
 * 
 * @see HttpServlet
 */ 

@SuppressWarnings("serial")
public class CommandsSequenceReceiverServlet extends HttpServlet {
	
	private static final Logger LOG = Logger.getLogger(CommandsSequenceReceiverServlet.class.getName());	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// int clientId = Integer.valueOf(request.getParameter("clientId"));
		String errorString = null;
		int cmdCounter = -1;
		while (request.getParameter("cmd" + String.valueOf(++cmdCounter)) != null) {
			String encodedCmd = URLDecoder.decode(request.getParameter("cmd" + String.valueOf(cmdCounter)), "UTF-8");
			LOG.info("Command received as: " + encodedCmd);
			Command command = null;
			try {
				 command = Command.decode(encodedCmd);
				 LOG.info("Command decoded as: " + command.toString()); // FIXME: remove
				 command.execute();
			} catch (ParseException e) {
				errorString = "Failed to extract command data on the server side from " + encodedCmd;
				LOG.severe(errorString);
			}
		}
		if (cmdCounter == -1) {
			errorString = "No command were passed to the Commands Sequence Receiver Servlet";
			LOG.severe(errorString);
		}
		/* PrintWriter responseWriter = response.getWriter();
		if (command != null) {
			// responseWriter.write(command.getAffectedHoldersIds());
			// responseWriter.write(command.getPanelsToRedrawContent());			
		} else {
			responseWriter.write("");
		} */
		PrintWriter responseWriter = response.getWriter();
		if (errorString == null) {
			responseWriter.write(createOkStatus());
		} else {
			responseWriter.write(createErrorStatus(errorString));
		}
	}

	// FIXME: move to special response generator?
	private static String createOkStatus() {
		return "result(ok)";
	}
	
	// FIXME: move to special response generator?
	private static String createErrorStatus(String errorText) {
		return "result(error(\"" + errorText + "\"))"; 
	}	
	
}
