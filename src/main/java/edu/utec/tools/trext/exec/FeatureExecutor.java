package edu.utec.tools.trext.exec;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.utec.tools.trext.bdd.translator.FeatureTranslator;
import edu.utec.tools.trext.common.ExceptionHelper;
import edu.utec.tools.trext.common.TimeHelper;
import edu.utec.tools.trext.context.Context;
import edu.utec.tools.trext.http.SmartHttpClient;
import edu.utec.tools.trext.logic.DynamicLogic;
import edu.utec.tools.trext.model.Feature;
import edu.utec.tools.trext.model.Scenario;
import edu.utec.tools.trext.variables.VariablePlaceHolderEvaluator;

public class FeatureExecutor {

  private final Logger logger = LogManager.getLogger(this.getClass());

  public HashMap<String, Object> singleSafeExecute(File singleFeature,
      HashMap<String, Object> globalVariables) {

    try {
      return singleExecute(singleFeature, globalVariables);
    } catch (Exception e) {
      logger.info("Failed to execute single feature " + singleFeature.getAbsolutePath(), e);
      HashMap<String, Object> stats = new HashMap<String, Object>();
      stats.put("internalError", "Failed to execute single feature "
          + singleFeature.getAbsolutePath() + "\n" + ExceptionHelper.summarizeCausesAsArray(e));
      stats.put("date", new Date());
      return stats;
    }
  }

  private HashMap<String, Object> singleExecute(File singleFeature,
      HashMap<String, Object> globalVariables) throws Exception {

    FeatureTranslator translator = new FeatureTranslator();
    Feature feature = null;
    try {
      feature = translator.parse(singleFeature);
    } catch (Exception e) {
      throw new Exception("Failed to read feature:" + singleFeature.getAbsolutePath(), e);
    }

    SmartHttpClient httpClient = new SmartHttpClient();
    VariablePlaceHolderEvaluator variablePlaceHolderEvaluator = new VariablePlaceHolderEvaluator();

    // get scenarios
    ArrayList<Scenario> scenarios = feature.getScenarios();
    logger.info("T-Rext has detect a valid feature: "+singleFeature.getAbsolutePath());
    logger.info("Launching " + scenarios.size() + " scenarios");
    ArrayList<HashMap<String, Object>> scenariosStats = new ArrayList<HashMap<String, Object>>();
    int passedCount = 0;
    int failedCount = 0;
    long globalStart = new Date().getTime();
    long globalEnd = 0l;

    HashMap<String, Object> localHttpRequestVariables = null;
    HashMap<String, Object> localHttpResponseVariables = null;

    // execute new scenario and asserts
    for (Scenario scenario : scenarios) {

      if (scenario.isDisabled()) {
        logger.info(String.format("scenario [%s] is disabled", scenario.getName()));
        passedCount++;
        continue;
      }

      logger.info(String.format("scenario [%s] is starting", scenario.getName()));
      logger.debug(scenario.toString());
      Boolean hasHttpError = null;
      Boolean hasAssertError = null;
      Boolean hasContextError = null;
      ArrayList<String> httpErrorSummary = null;
      ArrayList<String> assertErrorSummary = null;
      ArrayList<String> contextErrorSummary = null;

      localHttpRequestVariables = new HashMap<String, Object>();
      localHttpResponseVariables = new HashMap<String, Object>();

      HashMap<String, Object> response = null;

      logger.debug(String.format("globalVariables [%s] ", globalVariables));

      String url =
          variablePlaceHolderEvaluator.replaceVariablesAndJockersInString(scenario.getUrl(), globalVariables);
      logger.debug(String.format("evaluated url [%s] ", url));
      String body = null;
      if (scenario.getBody() != null) {
        body = variablePlaceHolderEvaluator.replaceVariablesAndJockersInString(scenario.getBody(),
            globalVariables);
      }
      logger.debug(String.format("evaluated body [%s] ", body));
      HashMap<String, ?> headers = variablePlaceHolderEvaluator
          .replaceVariablesAndJockersInMap(scenario.getHeaders(), globalVariables);

      long start = new Date().getTime();
      long end = 0l;
      try {
        localHttpRequestVariables.put("method", scenario.getMethod());
        localHttpRequestVariables.put("url", url);
        localHttpRequestVariables.put("body", body);
        localHttpRequestVariables.put("headers", headers);
        response = httpClient.performRequest(scenario.getMethod(), url, body, headers);
        end = new Date().getTime();
        localHttpResponseVariables.putAll(response);
        hasHttpError = Boolean.FALSE;
        logger.debug("hasHttpError:" + hasHttpError);
      } catch (Exception e) {
        end = new Date().getTime();
        hasHttpError = Boolean.TRUE;
        httpErrorSummary = ExceptionHelper.summarizeCausesAsArray(e);
        logger.info(String.format(
            "scenario [%s] returns an http error. Request url [%s], "
                + "Request body [%s],Request headers [%s], Error: %s",
            scenario.getName(), url, body, headers, ExceptionHelper.summarizeCausesAsString(e)));
        logger.debug(String.format("scenario [%s] returns an http error", scenario.getName()), e);
        failedCount++;
      }


      if (!hasHttpError.booleanValue()) {
        logger.debug("Asserts evaluation is starting");
        DynamicLogic dynamicLogic = new DynamicLogic();
        try {
          dynamicLogic.perform(scenario.getRawAsserts(), globalVariables, localHttpRequestVariables,
              localHttpResponseVariables);
          hasAssertError = Boolean.FALSE;
          logger.debug("hasAssertError:" + hasAssertError);
        } catch (Exception e) {
          hasAssertError = Boolean.TRUE;
          assertErrorSummary = ExceptionHelper.summarizeCausesAsArray(e);
          logger.info(String.format("scenario [%s] does not meet one assert %s. Error: %s",
              scenario.getName(), scenario.getRawAsserts(),
              ExceptionHelper.summarizeCausesAsString(e)));
          logger.debug("Asserts error.", e);
          failedCount++;
        }
      }

      if (!hasHttpError.booleanValue() && !hasAssertError.booleanValue()) {
        try {
          Context context = new Context();
          context.evaluate(scenario.getRawContext(), localHttpResponseVariables, globalVariables);
          hasContextError = Boolean.FALSE;
          logger.debug("hasContextError:" + hasContextError);
        } catch (Exception e) {
          hasContextError = Boolean.TRUE;
          contextErrorSummary = ExceptionHelper.summarizeCausesAsArray(e);
          logger.info(String.format("scenario [%s] has an error in context %s. Error: %s",
              scenario.getName(), scenario.getRawContext(),
              ExceptionHelper.summarizeCausesAsString(e)));
          logger.debug(String.format("scenario [%s] has an error in context.", scenario.getName()),
              e);
        }
      }

      logger.debug(String.format("httpError [%s] assertsError [%s] contextError [%s]", hasHttpError,
          hasAssertError, hasContextError));

      // ensure a valid report
      HashMap<String, Object> scenarioStats = new HashMap<String, Object>();
      scenarioStats.put("name", scenario.getName());
      if (hasHttpError == null || hasHttpError.booleanValue()) {
        scenarioStats.put("http", "failed");
      } else {
        scenarioStats.put("http", "passed");
      }      
      
      if (hasAssertError == null || hasAssertError.booleanValue()) {
        scenarioStats.put("asserts", "failed");
      } else {
        scenarioStats.put("asserts", "passed");
      }

      if (hasContextError == null || hasContextError.booleanValue()) {
        scenarioStats.put("context", "failed");
      } else {
        scenarioStats.put("context", "passed");
      }

      scenarioStats.put("httpError", httpErrorSummary);
      scenarioStats.put("assertsError", assertErrorSummary);
      scenarioStats.put("contextError", contextErrorSummary);
      scenarioStats.put("duration", TimeHelper.elapsedMillisToHumanExpression(start, end));
      scenarioStats.put("durationMillis", end - start);
      scenariosStats.add(scenarioStats);

      // if there is no error, lets continue with the next excenario
      if (hasHttpError.booleanValue() || hasAssertError.booleanValue()
          || hasContextError.booleanValue()) {
        logger.info(
            String.format("scenario [%s] ends with error. Run again with -debug to get more log",
                scenario.getName()));
        break;
      }
      logger.info(String.format("scenario [%s] complete", scenario.getName()));
      passedCount++;
    }

    globalEnd = new Date().getTime();

    HashMap<String, Object> stats = new HashMap<String, Object>();
    stats.put("featureName", feature.getName());
    stats.put("featureDesc", feature.getDescription());
    stats.put("scenarioStats", scenariosStats);
    stats.put("total", scenarios.size());
    stats.put("passed", passedCount);
    stats.put("failed", failedCount);
    stats.put("pending", scenarios.size() - passedCount - failedCount);
    stats.put("date", new SimpleDateFormat("dd MMMMM yyyy HH:mm:ss").format(new Date()));
    stats.put("duration", TimeHelper.elapsedMillisToHumanExpression(globalStart, globalEnd));
    stats.put("durationMillis", globalEnd - globalStart);

    stats.put("status", (scenarios.size() == passedCount) ? "success" : "failure");
    stats.put("globalVariables", globalVariables);
    stats.put("localHttpRequestVariables", localHttpRequestVariables);
    stats.put("localHttpResponseVariables", localHttpResponseVariables);

    return stats;
  }

}
