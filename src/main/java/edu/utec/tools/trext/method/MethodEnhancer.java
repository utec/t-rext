package edu.utec.tools.trext.method;

import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.utec.tools.trext.common.DataTypeHelper;
import edu.utec.tools.trext.common.MethodProperties;
import edu.utec.tools.trext.common.StringHelper;

public class MethodEnhancer {

  private final Logger logger = LogManager.getLogger(this.getClass());
  private final String spaceEnhancer = "@space";

  public String rawStringToConsecutiveMethodsWithSingleArgument(String rawMethodArgumentLine,
      HashMap<String, Object> variables, String httpBodyRawString) throws Exception {
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
        String evaluatedPartial = MethodProperties.getProperty(rawArgumentOrMethod);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        finalAssertLine += evaluatedPartial;
      } else if (DataTypeHelper.isInteger(rawArgumentOrMethod)
          || DataTypeHelper.isDouble(rawArgumentOrMethod)
          || DataTypeHelper.isBoolean(rawArgumentOrMethod)
          || DataTypeHelper.isQuotedString(rawArgumentOrMethod)) {

        String evaluatedPartial = String.format(singleArgumentTemplate, rawArgumentOrMethod);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        finalAssertLine += evaluatedPartial;
      } else if (DataTypeHelper.isQuotedString(rawArgumentOrMethod)) {
        String evaluatedPartial = String.format(singleArgumentTemplate, rawArgumentOrMethod);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        finalAssertLine += evaluatedPartial;
      } else if (rawArgumentOrMethod.startsWith("$.")) {
        // placeholder is a json expression
        Object jsonPathEvaluatedValue =
            StringHelper.evaluateJsonExpression(rawArgumentOrMethod, httpBodyRawString);
        logger.debug(String.format("Partial is jsonpath exp: %s, output %s which is %s",
            rawArgumentOrMethod, jsonPathEvaluatedValue, jsonPathEvaluatedValue.getClass()));
        String evaluatedPartial =
            convertValueToArgument(jsonPathEvaluatedValue, rawArgumentOrMethod);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        finalAssertLine += evaluatedPartial;
      } else if (rawArgumentOrMethod.startsWith("${") && rawArgumentOrMethod.endsWith("}")) {
        // placeholder is a variable
        Object evaluatedValue =
            variables.get(rawArgumentOrMethod.replaceFirst("\\$\\{", "").replace("}", ""));
        if (evaluatedValue == null) {
          throw new Exception(rawArgumentOrMethod + " was not found as variable.");
        }
        String evaluatedPartial = convertValueToArgument(evaluatedValue, rawArgumentOrMethod);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        finalAssertLine += evaluatedPartial;
      } else {
        throw new Exception(String.format(
            "value [%s] or class [%s] is not supported."
                + " Is not a method, integer, double, quoted string, boolean nor jsonpath exp",
            rawArgumentOrMethod, rawArgumentOrMethod.getClass()));
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
      HashMap<String, Object> variables, String rawString) throws Exception {

    String[] rawMethodArgumentLinePartials = rawMethodArgumentLine.split("\\s+");
    String args = "";
    String methodName = null;
    String methodTemplate = "%s(%s)";
    for (int a = 0; a < rawMethodArgumentLinePartials.length; a++) {
      String rawArgumentOrMethod = rawMethodArgumentLinePartials[a].trim();
      logger.debug("Partial to evaluate: " + rawArgumentOrMethod);
      if (a == 0 && isMethod(rawArgumentOrMethod)) {
        methodName = MethodProperties.getProperty(rawArgumentOrMethod).trim();
        logger.debug("Partial is method");
        logger.debug("Partial evaluated: " + methodName);
      } else if (DataTypeHelper.isInteger(rawArgumentOrMethod)
          || DataTypeHelper.isDouble(rawArgumentOrMethod)
          || DataTypeHelper.isQuotedString(rawArgumentOrMethod)
          || DataTypeHelper.isBoolean(rawArgumentOrMethod)) {
        args += rawArgumentOrMethod;
        logger.debug("Partial is integer, double, quoted string, boolean");
        logger.debug("Partial evaluated: " + rawArgumentOrMethod);
      } else if (rawArgumentOrMethod.startsWith("$.")) {
        logger.debug("Partial is jsonpath expression");
        // placeholder is a json expression
        Object jsonPathEvaluatedValue =
            StringHelper.evaluateJsonExpression(rawArgumentOrMethod, rawString);
        logger.debug(String.format("Partial jsonpath output %s which is %s", jsonPathEvaluatedValue,
            jsonPathEvaluatedValue.getClass()));
        String evaluatedPartial =
            convertValueToStringRepresentation(jsonPathEvaluatedValue, rawArgumentOrMethod);
        args += evaluatedPartial;
        logger.debug("Partial evaluated: " + evaluatedPartial);
      } else if (rawArgumentOrMethod.startsWith("${") && rawArgumentOrMethod.endsWith("}")) {
        logger.debug("Partial is a variable expression");
        // placeholder is a variable
        Object evaluatedValue =
            variables.get(rawArgumentOrMethod.replaceFirst("\\$\\{", "").replace("}", ""));
        if (evaluatedValue == null) {
          throw new Exception(rawArgumentOrMethod + " was not found as variable.");
        }
        String evaluatedPartial =
            convertValueToStringRepresentation(evaluatedValue, rawArgumentOrMethod);
        args += evaluatedPartial;
        logger.debug("Partial evaluated: " + evaluatedPartial);
      } else {
        throw new Exception(String.format("value [%s] or class [%s] is not supported",
            rawArgumentOrMethod, rawArgumentOrMethod.getClass()));
      }

      if (a > 0 && a < rawMethodArgumentLinePartials.length - 1) {
        args += ",";
      }
    }

    return String.format(methodTemplate, methodName, args);
  }


  private String convertValueToArgument(Object value, Object rawvalue) throws Exception {

    String singleArgumentTemplate = "(%s)";
    String singleArgumentDoubleTemplate = "(#double)";
    String singleArgument = "";

    if (value instanceof String) {
      logger.debug(String.format("%s is string", rawvalue));
      String stringQuotedValue = String.format("\"%s\"", value);
      singleArgument = String.format(singleArgumentTemplate, stringQuotedValue);
    } else if (DataTypeHelper.isInteger(value)) {
      logger.debug(String.format("%s is integer", rawvalue));
      singleArgument = String.format(singleArgumentTemplate, ((Integer) value).intValue());
    } else if (DataTypeHelper.isDouble(value)) {
      logger.debug(String.format("%s is double", rawvalue));
      singleArgument =
          singleArgumentDoubleTemplate.replace("#double", "" + ((Double) value).doubleValue());
    } else if (DataTypeHelper.isBoolean(value)) {
      logger.debug(String.format("%s is boolean", rawvalue));
      singleArgument = String.format(singleArgumentTemplate, ((Boolean) value).booleanValue());
    } else {
      throw new Exception(String.format("value %s or class %s is not supported", rawvalue,
          rawvalue.getClass()));
    }

    return singleArgument;
  }

  private String convertValueToStringRepresentation(Object value, Object rawValue)
      throws Exception {
    logger.debug(String.format("convert %s to string representation", value));
    if (DataTypeHelper.isString(value)) {
      return String.format("\"%s\"", value);
    } else if (DataTypeHelper.isInteger(value)) {
      return "" + ((Integer) value).intValue();
    } else if (DataTypeHelper.isDouble(value)) {
      return "" + ((Double) value).doubleValue();
    } else if (DataTypeHelper.isBoolean(value)) {
      return "" + ((Boolean) value).booleanValue();
    } else {
      throw new Exception(String.format("value %s or its class %s is not supported", rawValue,
          rawValue.getClass()));
    }
  }

  private boolean isMethod(String raw) throws Exception {
    try {
      return MethodProperties.getProperty(raw) != null;
    } catch (Exception e) {
      return false;
    }
  }

}
