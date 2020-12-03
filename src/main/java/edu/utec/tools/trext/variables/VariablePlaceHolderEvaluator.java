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
      logger.debug(String.format("var was found [%s] ", key));

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
      } else {
        logger.debug("variable does not exist in context nor is a jocker. Context:" + variables);
      }
    }
    return rawString;
  }

  public HashMap<String, Object> replaceVariablesAndJockersInMap(HashMap<String, ?> rawMap,
      HashMap<String, ?> variables) throws Exception {

    HashMap<String, Object> newMap = new HashMap<String, Object>();

    for (Entry<String, ?> entry : rawMap.entrySet()) {
      newMap.put((String) entry.getKey(),
          replaceVariablesAndJockersInString((String) entry.getValue(), variables));
    }

    return newMap;
  }
}
