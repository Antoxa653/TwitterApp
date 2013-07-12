package twitter.app;

import java.io.File;

import org.apache.log4j.Logger;

public class PropertiesExist {
	public static final Logger LOG = Logger.getLogger(PropertiesExist.class);
	public boolean isPropertiesExist() {
		File file = new File("twitter4j.properties");
		boolean exist = file.exists();
		if (exist) {
			LOG.info("Properties file exist");
		}
		if (!exist) {
			LOG.info("Properties file not exist");
		}
		return exist;
	}
}
