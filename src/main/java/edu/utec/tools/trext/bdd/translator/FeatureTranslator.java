package edu.utec.tools.trext.bdd.translator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.utec.tools.trext.common.FileHelper;
import edu.utec.tools.trext.common.RawLinesHelper;
import edu.utec.tools.trext.common.StringHelper;
import edu.utec.tools.trext.model.Feature;
import edu.utec.tools.trext.model.Scenario;

public class FeatureTranslator {

  private final Logger logger = LogManager.getLogger(this.getClass());

  public Feature parse(File featureFile) throws Exception {

    ArrayList<String> featureLines = FileHelper.getFileAsLines(featureFile, true, "#");

    logger.debug("initial feature lines. Commented lines will be excluded");
    logger.debug(featureLines);

    ArrayList<String> item = RawLinesHelper.getValueFromKey("Feature", featureLines, true);
    String featureName = item.get(0);
    String featureDescription = item.get(1);

    Feature feature = new Feature();
    feature.setName(featureName);
    feature.setDescription(featureDescription);

    feature.setScenarios(getScenarios(featureLines));

    return feature;
  }

  private ArrayList<ArrayList<String>> getRawScenarios(ArrayList<String> lines) throws Exception {
    String regex = "^\\s*Scenario\\s*:\\s*.+";
    return RawLinesHelper.getGroupLinesAfterLineThatMeetsRegexAndEndsWithBlankLine(regex, lines);
  }

  private ArrayList<Scenario> getScenarios(ArrayList<String> lines) throws Exception {

    ArrayList<ArrayList<String>> rawScenarios = getRawScenarios(lines);
    logger.debug("rawScenarios");
    logger.debug(rawScenarios);
    ArrayList<Scenario> scenarios = new ArrayList<Scenario>();

    for (ArrayList<String> rawScenarioLines : rawScenarios) {
      Scenario scenario = new Scenario();
      ArrayList<String> item = RawLinesHelper.getValueFromKey("Scenario", rawScenarioLines, false);
      scenario.setName((String) item.get(0));
      scenario.setId(StringHelper.toLowerCaseWithSnakeCase(scenario.getName()));
      scenario.setUrl(getUrl(rawScenarioLines));
      scenario.setMethod(getMethod(rawScenarioLines));
      scenario.setHeaders(getHeaders(rawScenarioLines));
      scenario.setBody(getBody(rawScenarioLines));
      scenario.setRawAsserts(getRawAsserts(rawScenarioLines));
      scenario.setRawContext(getRawContext(rawScenarioLines));
      scenario.setDisabled(getDisabled(rawScenarioLines));

      scenarios.add(scenario);
    }

    return scenarios;
  }

  private String getBody(ArrayList<String> rawLines) throws Exception {
    return RawLinesHelper.getMultilineValueBySimpleAndUniqueFieldName(rawLines, "body", "^```\\s*",
        "^```\\s*");
  }

  private String getUrl(ArrayList<String> rawLines) throws Exception {
    return StringHelper.getValueAfterKeyWithSpaces(
        RawLinesHelper.getUniqueRawLineWhichStartsWith("url", rawLines, true));
  }

  private String getMethod(ArrayList<String> rawLines) throws Exception {
    return StringHelper.getValueAfterKeyWithSpaces(
        RawLinesHelper.getUniqueRawLineWhichStartsWith("method", rawLines, true));
  }

  private boolean getDisabled(ArrayList<String> rawLines) throws Exception {
    return StringHelper.getBooleanValueAfterKeyWithSpaces(
        RawLinesHelper.getUniqueRawLineWhichStartsWith("disabled", rawLines, false));
  }

  private HashMap<String, String> getHeaders(ArrayList<String> rawLines) throws Exception {
    ArrayList<String> rawHeaders = RawLinesHelper.getRawLinesWichStartsWith("header", rawLines);
    return RawLinesHelper.getKeyValue(rawHeaders, "header", "=");
  }

  private ArrayList<String> getRawAsserts(ArrayList<String> lines) throws Exception {
    String regex1 = "^\\s*asserts\\s*";
    String regex2 = "^\\s*assertThat\\s*.+";
    return RawLinesHelper.getUniqueGroupLinesAfterLineThatMeetsRegex1AndNextLinesMeetsRegex2(regex1,
        regex2, lines, true);
  }

  private ArrayList<String> getRawContext(ArrayList<String> lines) throws Exception {
    String regex1 = "^\\s*context\\s*";
    String regex2 = "^\\s*setVar\\s*.+";
    return RawLinesHelper.getUniqueGroupLinesAfterLineThatMeetsRegex1AndNextLinesMeetsRegex2(regex1,
        regex2, lines, false);
  }

}
