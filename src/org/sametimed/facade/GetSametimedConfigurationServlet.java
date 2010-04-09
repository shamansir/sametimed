/**
 * 
 */
package org.sametimed.facade;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Project: sametimed
 * Package: org.sametimed.facade
 *
 * GetSametimedConfigurationServlet
 * 
 * Returns JSON object, containing the current configuration of Sametimed or
 * loads it before returning, if it wasn't loaded for current moment.
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 26, 2010 11:06:44 AM 
 *
 */
@SuppressWarnings("serial")
public class GetSametimedConfigurationServlet extends GenericServlet {

    /**
     * Returns JSON object, containing the Sametimed configuration data
     * Used to get this configuration data at client (JavaScript, for example) 
     * part. 
     * 
     * @see javax.servlet.GenericServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        StringBuffer responseStr = new StringBuffer();
        SametimedConfig.loadConfig(getServletContext()).addJSON(responseStr);

        PrintWriter responseWriter = response.getWriter();
        responseWriter.write(responseStr.toString());
        responseWriter.close();
        
        response.flushBuffer();        
    }
    
}
