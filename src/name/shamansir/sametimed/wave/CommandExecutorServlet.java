package name.shamansir.sametimed.wave;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waveprotocol.wave.model.operation.wave.WaveletOperation;

@SuppressWarnings("serial")
public class CommandExecutorServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
	
	protected boolean executeCommand(int clientId, WaveletOperation command) {
		return false;
	}
	
}
