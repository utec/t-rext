package edu.utec.tools.trext.launcher.cmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import edu.utec.tools.trext.common.FileHelper;
import edu.utec.tools.trext.common.LoggerHelper;
import edu.utec.tools.trext.exec.FeatureExecutor;
import edu.utec.tools.trext.variables.VariablePlaceHolderEvaluator;

public class SingleMode {

  private final Logger logger = LogManager.getLogger(SingleMode.class);

  public boolean run(String reportType, String variablesFileLocation, String singleFeatureLocation,
      String reportLocation, boolean debug) throws Exception {

    if (debug) {
      LoggerHelper.setDebugLevel();
    }

    VariablePlaceHolderEvaluator variablePlaceHolderEvaluator = new VariablePlaceHolderEvaluator();

    HashMap<String, Object> globalVariables = null;

    if (variablesFileLocation == null || variablesFileLocation.isEmpty()) {
      logger.info("variables.properties file was not provided.");
      globalVariables = new HashMap<String, Object>();
    } else {
      File variablesFile = new File(variablesFileLocation);
      if (!variablesFile.exists()) {
        logger.info(variablesFileLocation + " does not exist");
        globalVariables = new HashMap<String, Object>();
      } else {
        globalVariables = FileHelper.loadVariablesFromProperties(variablesFile);
        globalVariables = variablePlaceHolderEvaluator
            .replaceVariablesAndJockersInMap(globalVariables, globalVariables);
      }
    }

    File reportDir = new File(reportLocation);
    if (!reportDir.exists()) {
      throw new Exception("report folder " + reportLocation + " does not exist");
    }

    File singleFeature = new File(singleFeatureLocation);
    if (!singleFeature.exists()) {
      throw new Exception(singleFeatureLocation + " does not exist");
    }

    HashMap<String, Object> singleFeatureStats =
        executeSingleFeature(singleFeature, globalVariables);

    if (reportType.contentEquals("default")) {
      createDefaultReport(singleFeatureStats, reportLocation, "t-rext");
    } else if (reportType.contentEquals("html-compact")) {
      createCompactHtmlReport(singleFeatureStats, reportLocation,
          "/report/html/single/compact/index.html", "t-rext");
    } else if (reportType.contentEquals("html-uncompact")) {
      createUnCompactHtmlReport(singleFeatureStats, reportLocation, "/report/html/single/uncompact",
          "t-rext");
    }
    String status = (String) singleFeatureStats.get("status");
    return status != null && status.contentEquals("success");

  }

  public HashMap<String, Object> executeSingleFeature(File singleFeature,
      HashMap<String, Object> globalVariables) {

    FeatureExecutor executor = new FeatureExecutor();
    return executor.singleSafeExecute(singleFeature, globalVariables);

  }

  public void createDefaultReport(HashMap<String, Object> report, String reportLocation,
      String type) throws Exception {
    String reportAbsoluthePath = reportLocation + File.separator + type + "_report.json";
    try {
      FileHelper.mapToJsonFile(report, reportAbsoluthePath);
      logger.info("Report was created: " + reportAbsoluthePath);
    } catch (Exception e) {
      throw new Exception("Failed to create default report", e);
    }
  }

  public void createCompactHtmlReport(HashMap<String, Object> report, String reportLocation,
      String reportTemplateLocation, String type) throws Exception {
    String reportTemplate = FileHelper.getFileAsStringFromClasspath(reportTemplateLocation);
    ObjectMapper objectMapper = new ObjectMapper();

    String finalReport = reportTemplate.replace("%s",
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report));
    String reportAbsoluthePath = reportLocation + File.separator + type + "_report.html";
    FileOutputStream out = new FileOutputStream(reportAbsoluthePath);
    out.write(finalReport.getBytes());
    out.close();
    logger.info("Report was created: " + reportAbsoluthePath);
  }

  public void createUnCompactHtmlReport(HashMap<String, Object> report, String reportLocation,
      String reportInternalAssertsLocation, String type) throws Exception {

    String indexHtml = FileHelper.getFileAsStringFromClasspath(
        reportInternalAssertsLocation + File.separator + "index.html");
    String chartJs = FileHelper
        .getFileAsStringFromClasspath(reportInternalAssertsLocation + File.separator + "Chart.js");
    String styleCss = FileHelper
        .getFileAsStringFromClasspath(reportInternalAssertsLocation + File.separator + "style.css");
    String jsTemplate = FileHelper
        .getFileAsStringFromClasspath(reportInternalAssertsLocation + File.separator + "main.js");

    ObjectMapper objectMapper = new ObjectMapper();

    String mainJs = jsTemplate.replace("%s",
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report));

    String reportAbsoluthePath = reportLocation + File.separator + type + "_report.html";

    writeFileFromString(indexHtml, reportAbsoluthePath);
    writeFileFromString(chartJs, reportLocation + File.separator + "Chart.js");
    writeFileFromString(styleCss, reportLocation + File.separator + "style.css");
    writeFileFromString(mainJs, reportLocation + File.separator + "main.js");

    logger.info("Report was created: " + reportAbsoluthePath);
  }

  public void writeFileFromString(String content, String absoluteLocation) throws IOException {
    FileOutputStream out = new FileOutputStream(absoluteLocation);
    out.write(content.getBytes());
    out.close();
  }

}
