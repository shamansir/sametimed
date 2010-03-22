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

    @Override
    public void init() throws ServletException
    {
        // Grab the Bayeux object
        Bayeux bayeux = (Bayeux)getServletContext().getAttribute(Bayeux.ATTRIBUTE);
        new SametimedService(bayeux);

        // TODO: configuration and extensions
    }

    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        throw new ServletException("'service' method is not appliable to this servlet");
    }

}
