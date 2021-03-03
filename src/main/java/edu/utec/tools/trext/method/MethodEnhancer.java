package edu.utec.tools.trext.method;

import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.utec.tools.trext.common.DataTypeHelper;
import edu.utec.tools.trext.common.MethodEnhancers;
import edu.utec.tools.trext.common.StringHelper;

public class MethodEnhancer {

  private final Logger logger = LogManager.getLogger(this.getClass());
  private final String spaceEnhancer = "@space";

  public String rawStringToConsecutiveMethodsWithSingleArgument(String rawMethodArgumentLine,
      HashMap<String, Object> variables, String httpBodyRawString) throws Exception {

    logger.debug("convert raw string into consecutive methods with single argument");

    logger.debug("Line to evaluate: " + rawMethodArgumentLine);
    String rawMethodArgumentLineSpacesFixed =
        StringHelper.enhanceSpacesInQuotedString(rawMethodArgumentLine, spaceEnhancer);
    logger.debug("Line fixed to evaluate: " + rawMethodArgumentLineSpacesFixed);
    String[] rawMethodArgumentLinePartials = rawMethodArgumentLineSpacesFixed.split("\\s+");
    String finalAssertLine = "";
    String singleArgumentTemplate = "(%s)";

    for (int a = 1; a < rawMethodArgumentLinePartials.length; a++) {
      String rawArgumentOrMethod = rawMethodArgumentLinePartials[a].trim();
      logger.debug("Partial to evaluate: " + rawArgumentOrMethod);
      if (isMethod(rawArgumentOrMethod)) {
        logger.debug("Partial is a method");
        String evaluatedPartial = MethodEnhancers.getProperty(rawArgumentOrMethod);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        finalAssertLine += evaluatedPartial;
      } else if (DataTypeHelper.isQuotedString(rawArgumentOrMethod)) {
        logger.debug("Partial is quoted string");
        String rawArgumentPayload = StringHelper.getPayloadFromQuotedString(rawArgumentOrMethod);
        String readyArgument =
            convertVariableToArgument(rawArgumentPayload, variables, httpBodyRawString, true);
        // add the initial quotes
        String evaluatedPartial = String.format(singleArgumentTemplate, readyArgument);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        finalAssertLine += evaluatedPartial;
      } else {
        logger.debug("Partial is not a method, nor a quoted string. Is just a value");
        String readyArgument =
            convertVariableToArgument(rawArgumentOrMethod, variables, httpBodyRawString, false);
        String evaluatedPartial = String.format(singleArgumentTemplate, readyArgument);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        finalAssertLine += evaluatedPartial;
      }
    }

    // if line contained a quoted string with spaces : "Hello World"
    // it was converted to "Hello@spaceWorld"
    // so, at the end, we need to revert it
    finalAssertLine = finalAssertLine.replaceAll(spaceEnhancer, " ");
    logger.debug("Final assert line: " + finalAssertLine);
    return rawMethodArgumentLinePartials[0].trim() + finalAssertLine;
  }

  public String rawStringToOneMethodWithSeveralArguments(String rawMethodArgumentLine,
      HashMap<String, Object> variables, String httpBodyRawString) throws Exception {

    logger.debug("convert raw string into one method with several variables");
    logger.debug("Line to evaluate: " + rawMethodArgumentLine);
    // TODO: ensure not spaces in the first argument or variable name
    String[] rawMethodArgumentLinePartials = rawMethodArgumentLine.split("\\s+");
    String args = "";
    String methodName = null;
    String methodTemplate = "%s(%s)";
    for (int a = 0; a < rawMethodArgumentLinePartials.length; a++) {
      String rawArgumentOrMethod = rawMethodArgumentLinePartials[a].trim();
      logger.debug("Partial to evaluate: " + rawArgumentOrMethod);
      if (a == 0 && isMethod(rawArgumentOrMethod)) {
        methodName = MethodEnhancers.getProperty(rawArgumentOrMethod).trim();
        logger.debug("Partial is method");
        logger.debug("Partial evaluated: " + methodName);
      } else if (DataTypeHelper.isQuotedString(rawArgumentOrMethod)) {
        logger.debug("Partial is quoted string");
        String rawArgumentPayload = StringHelper.getPayloadFromQuotedString(rawArgumentOrMethod);
        String evaluatedPartial =
            convertVariableToArgument(rawArgumentPayload, variables, httpBodyRawString, true);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        args += evaluatedPartial;
      } else {
        logger.debug("Partial is not a method, nor a quoted string. Is just a value");
        String evaluatedPartial =
            convertVariableToArgument(rawArgumentOrMethod, variables, httpBodyRawString, false);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        args += evaluatedPartial;
      }

      if (a > 0 && a < rawMethodArgumentLinePartials.length - 1) {
        args += ",";
      }
    }

    String enhancedLine = String.format(methodTemplate, methodName, args);
    logger.debug("Final enhanced line: " + enhancedLine);
    return enhancedLine;
  }

  private String convertValueToStringRepresentationSafe(Object value) throws Exception {
    logger.debug(String.format("transform %s to string safe representation", value));
    if (DataTypeHelper.isInteger(value)) {
      return "" + DataTypeHelper.getInt(value);
    } else if (DataTypeHelper.isLong(value)) {
      return "" + DataTypeHelper.getLong(value);
    } else if (DataTypeHelper.isDouble(value)) {
      return "" + DataTypeHelper.getDouble(value);
    } else if (DataTypeHelper.isBoolean(value)) {
      return "" + DataTypeHelper.getBoolean(value);
    } else if (DataTypeHelper.isString(value)) {

      return String.format("\"%s\"", value);
    } else {
      throw new Exception(
          String.format("value %s or its class %s is not supported", value, value.getClass()));
    }
  }

  private String convertValueToSimpleStringRepresentation(Object value) throws Exception {
    logger.debug(String.format("transform %s to string representation", value));
    if (DataTypeHelper.isString(value)) {
      return (String) value;
    } else if (DataTypeHelper.isInteger(value)) {
      return "" + DataTypeHelper.getInt(value);
    } else if (DataTypeHelper.isLong(value)) {
      return "" + DataTypeHelper.getLong(value);
    } else if (DataTypeHelper.isDouble(value)) {
      return "" + DataTypeHelper.getDouble(value);
    } else if (DataTypeHelper.isBoolean(value)) {
      return "" + DataTypeHelper.getBoolean(value);
    } else {
      throw new Exception(
          String.format("value %s or its class %s is not supported", value, value.getClass()));
    }
  }

  private boolean isMethod(String raw) throws Exception {
    try {
      return MethodEnhancers.getProperty(raw) != null;
    } catch (Exception e) {
      return false;
    }
  }

  private String convertVariableToArgument(String rawArgumentPayload,
      HashMap<String, Object> variables, String httpBodyRawString, boolean cameFromQuotedString)
      throws Exception {

    String evaluatedPartial = null;

    if (rawArgumentPayload.startsWith("$.")) {
      // placeholder is a json expression
      Object jsonPathEvaluatedValue =
          StringHelper.evaluateJsonExpression(rawArgumentPayload, httpBodyRawString);
      logger.debug(String.format("Partial is jsonpath exp: %s, output %s which is %s",
          rawArgumentPayload, jsonPathEvaluatedValue, jsonPathEvaluatedValue.getClass()));

      if (cameFromQuotedString) {
        evaluatedPartial = "\"" + jsonPathEvaluatedValue + "\"";
      } else {
        // value here is genuine from jsonpath result
        evaluatedPartial =
            StringHelper.convertGeniuneValueToStringRepresentationSafe(jsonPathEvaluatedValue);
      }

    } else if (rawArgumentPayload.startsWith("${") && rawArgumentPayload.endsWith("}")) {
      logger.debug("Partial is variable");
      // placeholder is a variable
      // all values readed from varialbes.properties are string
      // TODO: Review when is used a quotes string with prop var
      Object evaluatedValue =
          variables.get(rawArgumentPayload.replaceFirst("\\$\\{", "").replace("}", ""));
      if (evaluatedValue == null) {
        throw new Exception(rawArgumentPayload + " was not found as variable.");
      }

      if (cameFromQuotedString) {
        evaluatedPartial = "\"" + evaluatedValue + "\"";
      } else {
        // evaluatedPartial = (String)evaluatedValue;
        evaluatedPartial = convertValueToSimpleStringRepresentation(evaluatedValue);
      }

    } else {
      logger.debug("Partial is not jsonpath nor placeholder variable. Is just a value");
      logger.debug("Partial came from quotes string: " + cameFromQuotedString);
      if (cameFromQuotedString) {
        evaluatedPartial = "\"" + rawArgumentPayload + "\"";
      } else {
        evaluatedPartial = convertValueToStringRepresentationSafe(rawArgumentPayload);
      }
    }

    return evaluatedPartial;
  }

}
