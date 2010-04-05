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
import org.sametimed.facade.wave.WaveServerProperties;
import org.sametimed.message.CommandsFactory;
import org.sametimed.module.ModulesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private static final Logger log = LoggerFactory
            .getLogger(SametimedServicesInitializer.class);

    /**
     * Initialization code
     */
    @Override
    public void init() throws ServletException {
        
        // Grab the Bayeux object
        Bayeux bayeux = (Bayeux)getServletContext().getAttribute(Bayeux.ATTRIBUTE);        
        
        // Initialize and configure Sametimed service 
        final SametimedConfig sametimedConfig = 
                                SametimedConfig.loadConfig(getServletContext());
        final WaveServerProperties waveServerProps = 
                                WaveServerProperties.load(getServletContext());
        
        if (!waveServerProps.wereLoadErrors()) {            
            new SametimedService(bayeux, sametimedConfig, waveServerProps,
                   new CommandsFactory(sametimedConfig.getCommandsData()),
                   new ModulesFactory(sametimedConfig.getModulesData(),
                                      getServletContext()));
            // TODO: extensions    
            log.info("Launched and configured SametimedService");            
        } else {            
            log.error("Not initialized. There were errors while loading wave " +
            		                                       "server properties");
            throw new ServletException("Not initialized. There were errors" +
            		                   " while loading wave server properties");            
        }
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
