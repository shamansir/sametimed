package name.shamansir.sametimed.wave;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.mortbay.util.ajax.Continuation;
import org.mortbay.util.ajax.ContinuationSupport;

@SuppressWarnings("serial")
public class CommandExecutorServlet extends HttpServlet {
	
	private static final Logger LOG = Logger.getLogger(CommandExecutorServlet.class.getName());	

	/*
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		int clientId = Integer.valueOf(request.getParameter("clientId"));
		String commandXML = request.getParameter("cmdXML");
		Command command = null;
		try {
			 command = Command.fromXMLString(commandXML, clientId);
			 command.execute();
		} catch (DocumentException e) {
			LOG.severe("Failed to extract command data on the server side from " + commandXML);
		}
		PrintWriter responseWriter = response.getWriter();
		if (command != null) {
			// responseWriter.write(command.getAffectedHoldersIds());
			// responseWriter.write(command.getPanelsToRedrawContent());			
		} else {
			responseWriter.write("");
		}
		
	} */
	
	/*
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) {
		if (request.getRequestURI().endsWith("/time")) {
			
			Continuation cc = ContinuationSupport.getContinuation(request, null);
			
			response.setContentType("text/html");
			
			while (true) {
				cc.suspend(1000);
				String script = "<script>parent.printTime(new Date())</script>";
				response.getWriter().println(script);
				response.getWriter().flush();
			}
			
		}
	} */
}
