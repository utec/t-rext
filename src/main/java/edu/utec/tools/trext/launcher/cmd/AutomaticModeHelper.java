package edu.utec.tools.trext.launcher.cmd;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.utec.tools.trext.common.FileHelper;

public class AutomaticModeHelper {

  private final Logger logger = LogManager.getLogger(AutomaticModeHelper.class);

  public HashMap<String, String> analize(String directory) throws Exception {

    logger.info(
        "Automatic mode will scan directory to detect which is the required mode: " + directory);

    // detect if has one or more than one feature

    File featuredDir = new File(directory);
    if (!featuredDir.exists()) {
      throw new Exception("main folder " + directory + " does not exist");
    }

    ArrayList<File> featureFiles = FileHelper.listFileTree(featuredDir,".feature");

    if (featureFiles.size() == 0) {
      throw new Exception("main folder " + directory + " does not contain any feature file");
    }

    HashMap<String, String> detectedValues = new HashMap<String, String>();

    if (featureFiles.size() == 1) {

      logger.info("detected mode: single");
      detectedValues.put("mode", "single");
      detectedValues.put("variables", directory + File.separator + "variables.properties");
      detectedValues.put("feature_file_location", featureFiles.get(0).getAbsolutePath());
      detectedValues.put("report_directory_location", directory);
    } else {
      // multiple files
      logger.info("detected mode: multiple");
      detectedValues.put("mode", "multiple");
      detectedValues.put("variables", directory + File.separator + "variables.properties");
      detectedValues.put("features_directory_location", directory);
      detectedValues.put("report_directory_location", directory);
    }

    return detectedValues;
  }

}
