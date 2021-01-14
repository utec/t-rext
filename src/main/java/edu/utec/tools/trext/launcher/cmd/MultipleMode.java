package edu.utec.tools.trext.launcher.cmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import edu.utec.tools.trext.common.FileHelper;
import edu.utec.tools.trext.common.LoggerHelper;
import edu.utec.tools.trext.exec.FeatureExecutor;

public class MultipleMode {

  private final Logger logger = LogManager.getLogger(MultipleMode.class);

  public boolean run(String reportType, String variablesFileLocation,
      String multipleFeaturesDirectoryLocation, String reportLocation, String regexToExcludeFiles,
      boolean debug) throws Exception {

    if (debug) {
      LoggerHelper.setDebugLevel();
    }

    HashMap<String, Object> globalVariables = null;

    if (variablesFileLocation == null || variablesFileLocation.isEmpty()) {
      logger.info("varables.properties file was not provided.");
      globalVariables = new HashMap<String, Object>();
    } else {
      File variablesFile = new File(variablesFileLocation);
      if (!variablesFile.exists()) {
        logger.info(variablesFileLocation + " does not exist");
        globalVariables = new HashMap<String, Object>();
      } else {
        globalVariables = FileHelper.loadVariablesFromProperties(variablesFile);
      }
    }

    File reportDir = new File(reportLocation);
    if (!reportDir.exists()) {
      throw new Exception("report folder " + reportLocation + " does not exist");
    }

    File featuredDir = new File(multipleFeaturesDirectoryLocation);
    if (!featuredDir.exists()) {
      throw new Exception(
          "features folder " + multipleFeaturesDirectoryLocation + " does not exist");
    }

    ArrayList<File> featureFiles =
        FileHelper.listFileTree(featuredDir, ".feature", regexToExcludeFiles);

    HashMap<String, Object> featuresReport = new HashMap<String, Object>();
    ArrayList<HashMap<String, Object>> featuresStats = new ArrayList<HashMap<String, Object>>();
    int failedCount = 0;
    int passedCount = 0;
    logger.info(String.format("T-Rext detected [%s] valid feature files", featureFiles.size()));
    for (File singleFeature : featureFiles) {
      logger.info("Feature file: " + singleFeature.getAbsolutePath());
      HashMap<String, Object> singleFeatureStats =
          executeSingleFeature(singleFeature, globalVariables);
      // add file name multipleFeaturesDirectoryLocation
      singleFeatureStats.put("file",
          singleFeature.getAbsolutePath().replace(multipleFeaturesDirectoryLocation, ""));
      featuresStats.add(singleFeatureStats);
      String status = (String) singleFeatureStats.get("status");
      if (status.contentEquals("success")) {
        passedCount++;
      } else {
        failedCount++;
      }
    }

    featuresReport.put("featuresStats", featuresStats);
    featuresReport.put("total", featureFiles.size());
    featuresReport.put("passed", passedCount);
    featuresReport.put("failed", failedCount);
    featuresReport.put("pending", featureFiles.size() - passedCount - failedCount);
    featuresReport.put("date", new SimpleDateFormat("dd MMMMM yyyy HH:mm:ss").format(new Date()));

    if (reportType.contentEquals("default")) {
      createDefaultReport(featuresReport, reportLocation, "t-rext");
    } else if (reportType.contentEquals("html-compact")) {
      createCompactHtmlReport(featuresReport, reportLocation,
          "/report/html/multiple/compact/index.html", "t-rext");
    } else if (reportType.contentEquals("html-uncompact")) {
      createUnCompactHtmlReport(featuresReport, reportLocation, "/report/html/multiple/uncompact",
          "t-rext");
    }

    return isSuccess(featuresReport);

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

  public boolean isSuccess(HashMap<String, Object> featuresReport) {
    try {
      int total = ((Integer) featuresReport.get("total")).intValue();
      int passed = ((Integer) featuresReport.get("passed")).intValue();
      return (total == passed);
    } catch (Exception e) {
      logger.error("Error detected when reading total and approved fields.", e);
      return false;
    }
  }

}
