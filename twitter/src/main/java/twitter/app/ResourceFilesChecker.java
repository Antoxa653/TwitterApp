package twitter.app;

import java.io.File;

import org.apache.log4j.Logger;

public class ResourceFilesChecker {
	private static Logger log = Logger.getLogger(ResourceFilesChecker.class);

	public static boolean isFileExist(String fileName) {
		File file = new File(fileName);
		boolean exist = file.exists();
		if (exist) {
			log.debug(fileName + " - exist");
		}
		else {
			log.debug(fileName + " - NOT exist");
		}
		return exist;
	}

	public static void deleteFile(String fileName) {
		if (isFileExist(fileName)) {
			File file = new File(fileName);
			file.delete();
			log.debug(fileName + "- was deleted");
		}
		else {
			log.debug(fileName + "- can't be deleted");
		}
	}
}
