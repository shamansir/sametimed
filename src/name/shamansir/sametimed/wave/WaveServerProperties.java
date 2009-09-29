package name.shamansir.sametimed.wave;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public final class WaveServerProperties extends Properties {
	
	private static final Logger LOG = Logger.getLogger(WaveServerProperties.class.getName());
	
	private static final String PROPS_FILE_PATH = "/waveserver.properties";
	
	private static final String DOMAIN_KEY = "wave_server_domain";
	private static final String HOST_KEY   = "wave_server_host";
	private static final String PORT_KEY   = "wave_server_port";
	
	private static final String DEFAULT_DOMAIN = "localhost";
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 9876;
	
	private boolean useDefaults = false;
	
	private static final String NOT_FOUND_STR = "Can't locate Wave Server properties file: %s. Using default values";
	
	public WaveServerProperties() {		
        InputStream is = getClass().getResourceAsStream(PROPS_FILE_PATH);
        
        if (null == is) {
        	// throw new FileNotFoundException("Can't locate file:" +PROPS_FILENAME);        	
        	LOG.warning(String.format(NOT_FOUND_STR, PROPS_FILE_PATH));
        	System.err.println(String.format(NOT_FOUND_STR, PROPS_FILE_PATH));
        	useDefaults = true;
        }
        
        try {
            this.load(is);
        } catch (IOException ioe) {
            // throw new FileNotFoundException("Can't locate file:" +PROPS_FILENAME);        	
        	LOG.warning(String.format(NOT_FOUND_STR, PROPS_FILE_PATH));
            System.err.println(String.format(NOT_FOUND_STR, PROPS_FILE_PATH));
            useDefaults = true;
        }
		
	}
	
	public String getWaveDomain() {
		return useDefaults ? DEFAULT_DOMAIN : this.getProperty(DOMAIN_KEY);
	}
	
	public String getWaveServerHost() {
		return useDefaults ? DEFAULT_HOST : this.getProperty(HOST_KEY);
	}
	
	public int getWaveServerPort() throws NumberFormatException {
		return useDefaults ? DEFAULT_PORT : Integer.parseInt(this.getProperty(PORT_KEY));
	}	
	
}
