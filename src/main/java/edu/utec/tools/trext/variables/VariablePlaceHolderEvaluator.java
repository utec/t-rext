package edu.utec.tools.trext.variables;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.utec.tools.trext.common.VariablePlaceHolderHelper;

public class VariablePlaceHolderEvaluator {

  private final Logger logger = LogManager.getLogger(VariablePlaceHolderEvaluator.class);

  public String replaceVariablesAndJockersInString(String rawString, HashMap<String, ?> variables)
      throws Exception {

    if (rawString == null || rawString.isEmpty()) {
      throw new Exception("rawString is required to evaluate regex. Current value:" + rawString);
    }

    String regex = "(\\$\\{[\\w\\^\\$\\s]+\\})";
    Matcher m = Pattern.compile(regex).matcher(rawString);
    while (m.find()) {

      String key = m.group(0).replace("${", "").replace("}", "");
      logger.debug(String.format("variable was detected: %s in raw string: %s", key, rawString));

      if (key == null || key.equals("")) {
        continue;
      }

      if (variables.containsKey(key)) {
        logger.debug("variable exists in context map");
        rawString = rawString.replace(String.format("${%s}", key),
            VariablePlaceHolderHelper.getStringRepresentation(variables.get(key)));
      } else if (VariablePlaceHolderHelper.containsJockers(key)) {
        logger.debug("variable is a jocker");
        rawString = rawString.replace(String.format("${%s}", key),
            VariablePlaceHolderHelper.parseJocker(key));
      } else if (System.getenv(key) != null && !System.getenv(key).isEmpty()) {
        logger.debug("variable is a system environment variable");
        rawString = rawString.replace(String.format("${%s}", key),
            VariablePlaceHolderHelper.getStringRepresentation(System.getenv(key)));
      } else {
        logger.debug(
            "Variable is not a jocker and does not exist in T-Rext context or s.o environment"
                + variables);
      }
    }
    return rawString;
  }

  public HashMap<String, Object> replaceVariablesAndJockersInMap(HashMap<String, ?> rawMap,
      HashMap<String, ?> variables) throws Exception {

    HashMap<String, Object> newMap = new HashMap<String, Object>();

    for (Entry<String, ?> entry : rawMap.entrySet()) {
      logger.debug("variable name: " + entry.getKey());
      newMap.put((String) entry.getKey(),
          replaceVariablesAndJockersInString((String) entry.getValue(), variables));
    }

    return newMap;
  }

  /*
   * Lookup values in the operative system environment.
   */
  public String evaluteValueIfIsEnvironmentVariable(String rawKey) throws Exception {
    String regex = "(\\$\\{[\\w\\^\\$\\s]+\\})";
    Matcher m = Pattern.compile(regex).matcher(rawKey);
    while (m.find()) {
      String key = m.group(0).replace("${", "").replace("}", "");
      logger.debug(String.format("variable was detected: %s in raw key: %s", key, rawKey));

      if (key == null || key.equals("")) {
        continue;
      }

      if (System.getenv(key) != null && !System.getenv(key).isEmpty()) {
        logger.debug("variable is a system variable");
        // TODO
        // rawKey = rawKey.replace(String.format("${%s}", key),
        // VariablePlaceHolderHelper.getStringRepresentation(System.getenv(key)));
        rawKey = rawKey.replace(String.format("${%s}", key), System.getenv(key));
      }
    }

    return rawKey;
  }
}
