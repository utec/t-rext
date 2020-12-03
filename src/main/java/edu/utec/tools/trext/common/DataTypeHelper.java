package edu.utec.tools.trext.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataTypeHelper {

  private static final Logger logger = LogManager.getLogger(DataTypeHelper.class);

  private static boolean isNull(Object raw) {
    return raw == null;
  }

  public static boolean isInteger(Object raw) {

    if (isNull(raw)) {
      logger.debug("raw value to evaluate data type is null");
      return false;
    }

    if (raw instanceof Integer) {
      return true;
    }

    if (!(raw instanceof String)) {
      logger.debug("object class is not Integer nor String. Definitely is not an integer");
      return false;
    }

    Pattern pattern = Pattern.compile("^[0-9]*$");
    Matcher matcher = pattern.matcher((String) raw);
    return matcher.find();
  }

  public static boolean isDouble(Object raw) {

    if (isNull(raw)) {
      return false;
    }

    if (raw instanceof Double) {
      return true;
    }

    if (!(raw instanceof String)) {
      logger.debug("object class is not Double nor String. Definitely is not a double");
      return false;
    }

    Pattern pattern = Pattern.compile("\\d+\\.\\d+");
    Matcher matcher = pattern.matcher((String) raw);
    return matcher.find();
  }

  public static boolean isBoolean(Object raw) {

    if (isNull(raw)) {
      return false;
    }

    if (raw instanceof Boolean) {
      return true;
    }

    if (!(raw instanceof String)) {
      logger.debug("object class is not Boolean nor String. Definitely is not an boolean");
      return false;
    }

    return ((String) raw).matches("(true|false)");
  }

  public static boolean isQuotedString(String raw) {
    if (isNull(raw)) {
      return false;
    }

    Pattern pattern = Pattern.compile("\"[^\"]*\"");
    Matcher matcher = pattern.matcher(raw);
    return matcher.find();
  }

  public static boolean isString(Object raw) {
    if (isNull(raw)) {
      return false;
    }
    return raw instanceof String;
  }

}
