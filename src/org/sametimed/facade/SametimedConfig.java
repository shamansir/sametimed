/**
 * 
 */
package org.sametimed.facade;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project: sametimed
 * Package: org.sametimed.facade
 *
 * SametimedConfig
 *
 * @author Ulric Wilfred <shaman.sir@gmail.com>
 * @date Mar 23, 2010 11:17:11 PM 
 *
 */
public class SametimedConfig {
    
    private static final Logger log = LoggerFactory
            .getLogger(SametimedConfig.class);

    // TODO: load channels paths from sametimed.xml    
    
    /**
     * Initiate and Load data from configuration file 
     * 
     * @param confFile configuration file
     */
    public SametimedConfig(InputStream confFile) {
        
        log.debug(confFile.toString());
    }
    
    

}
