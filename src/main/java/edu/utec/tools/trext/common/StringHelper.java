package edu.utec.tools.trext.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

public class StringHelper {

  private static final Logger logger = LogManager.getLogger(StringHelper.class);

  public static Object evaluateJsonExpression(String expresion, String json) throws Exception {

    if (json == null || json.isEmpty()) {
      throw new Exception("Http body is null."
          + "A valid json body is required to perform json_path expression:" + expresion);
    }

    Object result = null;
    try {
      result = JsonPath.parse(json).read(expresion);
    } catch (Exception e) {
      throw new Exception(String.format(
          "Error originated when json_path expression %s was executing on http response: %s"
              + " Json Path expression is wrong, response is not a valid json or json does not contain the required fields.",
          expresion, json), e);
    }

    if (result instanceof JSONArray) {
      JSONArray results = (JSONArray) result;
      if (results.size() > 1) {
        throw new Exception(String.format(
            "Error originated when json_path expression %s was executing on http response: %s"
                + " Json Path expression returns a JSONArray with more than one element. Which one I choose?",
            expresion, json));
      } else {
        return results.get(0);
      }
    } else {
      return result;
    }
  }

  public static String getValueAfterKeyWithSpaces(String rawLine) throws Exception {

    if (rawLine == null || rawLine.isEmpty()) {
      throw new Exception("Raw line is null or empty. Expected format is: key value");
    }

    String[] line = rawLine.split("\\s+");
    return line[1].trim();
  }

  public static Boolean getBooleanValueAfterKeyWithSpaces(String rawLine) throws Exception {
    if (rawLine == null) {
      return false;
    }

    String[] line = rawLine.split("\\s+");
    try {
      return Boolean.parseBoolean(line[1].trim());
    } catch (Exception e) {
      logger.warn("Raw line does not contains a valid boolean after spaces:" + rawLine);
      return false;
    }
  }

  public static String toLowerCaseWithSnakeCase(String line) {
    String[] words = line.trim().split("\\s+");
    String id = "";
    for (int a = 0; a < words.length; a++) {
      String word = words[a].toLowerCase().trim();
      if (a < words.length - 1) {
        id += word + "_";
      } else {
        id += word;
      }
    }
    return id;
  }


  public static String enhanceSpacesInQuotedString(String line, String spaceEnhancer) {
    Pattern pattern = Pattern.compile("\"([^\"]*)\"");
    Matcher matcher = pattern.matcher(line);
    while (matcher.find()) {
      String quotedString = matcher.group(1);
      String quotedEnhancedString = quotedString.replaceAll("\\s{1}", spaceEnhancer);
      line = line.replace(quotedString, quotedEnhancedString);
    }

    return line;
  }
}
