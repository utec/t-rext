package edu.utec.tools.trext.launcher.cmd;

import java.util.HashMap;
import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.utec.tools.trext.common.LoggerHelper;

public class TrextCmdEntrypoint {

  private final static Logger logger = LogManager.getLogger(TrextCmdEntrypoint.class);

  public static void main(String[] args) throws Exception {
    ArgumentsHelper argumentsHelper = new ArgumentsHelper();
    CommandLine cmd = argumentsHelper.getArguments(args);

    logger.info("Parameters:");

    logger.info(argumentsHelper.simplePrint(cmd));

    if (cmd.hasOption("debug")) {
      LoggerHelper.setDebugLevel();
    }

    if (!(cmd.getOptionValue("mode").contentEquals("single")
        || cmd.getOptionValue("mode").contentEquals("multiple")
        || cmd.getOptionValue("mode").contentEquals("auto"))) {
      System.err.println(cmd.getOptionValue("mode")
          + " mode, is not supported. Allowed values are: single or multiple");
      System.exit(1);
    }

    String reportType = null;
    if (cmd.getOptionValue("report_type") == null || cmd.getOptionValue("report_type").isEmpty()) {
      logger.info("report_type is not provided. [html-compact] will be used.");
      reportType = "html-compact";
    } else if (!(cmd.getOptionValue("report_type").contentEquals("default")
        || cmd.getOptionValue("report_type").contentEquals("html-compact")
        || cmd.getOptionValue("report_type").contentEquals("html-uncompact"))) {
      logger.error(cmd.getOptionValue("report_type")
          + " report type, is not supported. Allowed values are: default, html-compac or html-uncompact");
      System.exit(1);
    } else {
      reportType = cmd.getOptionValue("report_type");
    }

    boolean status = false;

    if (cmd.getOptionValue("mode").contentEquals("single")) {
      SingleMode singleMode = new SingleMode();
      status = singleMode.run(reportType, cmd.getOptionValue("variables"),
          cmd.getOptionValue("feature_file_location"),
          cmd.getOptionValue("report_directory_location"), cmd.hasOption("debug"));
    } else if (cmd.getOptionValue("mode").contentEquals("multiple")) {
      MultipleMode multipleMode = new MultipleMode();
      status = multipleMode.run(reportType, cmd.getOptionValue("variables"),
          cmd.getOptionValue("features_directory_location"),
          cmd.getOptionValue("report_directory_location"), cmd.hasOption("debug"));
    } else if (cmd.getOptionValue("mode").contentEquals("auto")) {
      // IA mode will try to detect which mode is: single or multiple
      AutomaticModeHelper automaticModeHelper = new AutomaticModeHelper();
      HashMap<String, String> arguments = automaticModeHelper.analize(cmd.getOptionValue("directory"));
      logger.info("detected arguments:" + arguments);
      if (arguments.get("mode").contentEquals("single")) {
        SingleMode singleMode = new SingleMode();
        status = singleMode.run(reportType, arguments.get("variables"),
            arguments.get("feature_file_location"), arguments.get("report_directory_location"),
            cmd.hasOption("debug"));
      } else if (arguments.get("mode").contentEquals("multiple")) {
        MultipleMode multipleMode = new MultipleMode();
        status = multipleMode.run(reportType, arguments.get("variables"),
            arguments.get("features_directory_location"),
            arguments.get("report_directory_location"), cmd.hasOption("debug"));
      }

    }

    logger.info("By JRichardsz");
    if (!status) {
      System.err.println("Features ended with error. Check logs and reports.");
      System.exit(1);
    }

  }

}
