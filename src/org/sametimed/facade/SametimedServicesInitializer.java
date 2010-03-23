/**
 * 
 */
package org.sametimed.facade;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.cometd.Bayeux;

/**
 * Project: sametimed
 * Package: org.sametimed.facade
 *
 * SametimedServicesInitializer
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 22, 2010 5:48:47 PM 
 *
 */
public class SametimedServicesInitializer extends GenericServlet {
    
    private static final long serialVersionUID = -8428887423512182828L;

    /**
     * Initialization code
     */
    @Override
    public void init() throws ServletException
    {
        // Grab the Bayeux object
        Bayeux bayeux = (Bayeux)getServletContext().getAttribute(Bayeux.ATTRIBUTE);
        // Grab configuration file
        SametimedConfig config = new SametimedConfig(
                getServletContext().getResourceAsStream("/sametimed.xml")); 
        
        new SametimedService(bayeux, config);

        // TODO: extensions
    }
    
    /**
     * {@code service} method is not supported for Initializer servlet
     * 
     * @param request request
     * @param response response
     * 
     * @throws ServletException always
     * @throws IOException never
     */
    @Override    
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        throw new ServletException("'service' method is not appliable to this servlet");
    }

}
