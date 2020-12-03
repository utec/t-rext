package edu.utec.tools.trext.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RawLinesHelper {

  private static final Logger logger = LogManager.getLogger(RawLinesHelper.class);

  public static ArrayList<ArrayList<String>> getGroupLinesAfterLineThatMeetsRegexAndEndsWithBlankLine(
      String regex, ArrayList<String> lines) throws Exception {

    logger.debug("getGroupLinesAfterLineThatMeetsRegexAndEndsWithBlankLine");
    logger.debug(lines.toString());

    ArrayList<ArrayList<String>> rawScenario = new ArrayList<ArrayList<String>>();

    for (int a = 0; a < lines.size();) {
      String line = lines.get(a);
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(line);
      if (matcher.find()) {
        // a scenario is detected. next lines will be considered
        // part of this scenario
        ArrayList<String> groupLines =
            getLinesFromIndexAndBeforeLineWhichMeetsRegex(lines, a, "^\\s*$");

        rawScenario.add(groupLines);
        a = a + groupLines.size() + 1;
      } else {
        a++;
      }
    }

    return rawScenario;
  }

  private static ArrayList<String> getLinesFromIndexAndBeforeLineWhichMeetsRegex(
      ArrayList<String> lines, int startIndex, String regex) throws Exception {

    logger.debug("getLinesFromIndexAndBeforeLineWhichMeetsRegex");
    logger.debug(lines.toString());
    ArrayList<String> foundLines = new ArrayList<String>();
    for (int a = startIndex; a < lines.size(); a++) {
      String line = lines.get(a).trim();
      logger.debug("searching regex:" + regex + " in:" + line + " index:" + a);
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(line);
      if (matcher.find()) {
        // this line meet with regex which indicates the end
        break;
      } else {
        foundLines.add(line);
      }
    }

    if (foundLines.size() == 0) {
      throw new Exception(
          String.format("no one line meets the end regex:%s starting from :%s", regex, startIndex));
    }

    return foundLines;
  }

  public static String getMultilineValueBySimpleAndUniqueFieldName(ArrayList<String> lines,
      String fieldName, String regexStart, String regexEnd) throws Exception {

    int fieldDetectedCound = 0;
    int fieldIndex = -1;

    for (int a = 0; a < lines.size(); a++) {
      String line = lines.get(a);
      // search fieldName
      String regex = String.format("^%s", fieldName);
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(line);
      if (matcher.find()) {
        fieldDetectedCound++;
        fieldIndex = a;
      }
    }

    if (fieldDetectedCound == 0) {
      logger.debug(String.format("Field [%s] was not found", fieldName));
      return null;
    }

    if (fieldDetectedCound > 1) {
      throw new Exception(String.format(
          "more than one line contains field [%s]. Just one is allowd \n[%s]", fieldName, lines));
    }

    if (lines.get(fieldIndex + 1) == null || lines.get(fieldIndex + 1).contentEquals("")
        || !lines.get(fieldIndex + 1).matches(regexStart)) {
      throw new Exception(
          String.format("Field [%s] was found, but the next line does not meet the start regex[%s]",
              fieldName, regexStart));
    }

    // search the final line and recolect multiline string
    StringBuffer str = new StringBuffer();
    for (int a = fieldIndex + 2; a < lines.size(); a++) {
      String line = lines.get(a);
      Pattern pattern = Pattern.compile(regexEnd);
      Matcher matcher = pattern.matcher(line);
      if (matcher.find()) {
        break;
      } else {
        str.append(line);
      }
    }

    return str.toString();
  }

  public static String getUniqueRawLineWhichStartsWith(String key, ArrayList<String> lines,
      boolean isRequired) throws Exception {

    logger.debug("getUniqueRawLineWhichStartsWith: " + key);
    logger.debug(lines.toString());

    String value = null;
    int found = 0;
    for (int a = 0; a < lines.size(); a++) {

      if (found > 1) {
        throw new Exception(
            String.format("More than one [%s] in scenario. Just one is allowed.", key));
      }

      String line = lines.get(a);
      String regex = String.format("^%s\\s+.+", key);
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(line);

      if (matcher.find()) {
        value = matcher.group();
        found++;
      }
    }

    if (found > 1) {
      throw new Exception(String.format("Lines has more than one [%s]: \n[%s]", key, lines));
    }

    if (value == null) {
      if (isRequired) {
        throw new Exception(String.format("key [%s] was not found in lines \n[%s]", key, lines));
      } else {
        return value;
      }
    } else {
      return value.trim();
    }

  }

  public static ArrayList<String> getRawLinesWichStartsWith(String key, ArrayList<String> lines)
      throws Exception {

    logger.debug("getRawLinesFromInitialString: " + key);
    logger.debug(lines.toString());

    ArrayList<String> rawLines = new ArrayList<String>();

    for (int a = 0; a < lines.size(); a++) {

      String line = lines.get(a);
      String regex = String.format("^%s\\s+.+", key);
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(line);

      if (matcher.find()) {
        rawLines.add(line.trim());
      }
    }

    return rawLines;
  }

  public static ArrayList<String> getUniqueGroupLinesAfterLineThatMeetsRegex1AndNextLinesMeetsRegex2(
      String regex1, String regex2, ArrayList<String> lines, boolean isMandatory) throws Exception {

    logger.debug("getUniqueGroupLinesAfterLineThatMeetsRegex1AndNextLinesMeetsRegex2");
    logger.debug(lines.toString());

    int foundCount = 0;
    int foundLineIndex = 0;
    for (int a = 0; a < lines.size(); a++) {

      if (foundCount > 1) {
        throw new Exception(
            String.format("more than one line match with regex [%s]: \n[%s]", regex1, lines));
      }

      String line = lines.get(a);
      logger.debug("searching regex:" + regex1 + " in:" + line + " index:" + a);
      Pattern pattern = Pattern.compile(regex1);
      Matcher matcher = pattern.matcher(line);

      if (matcher.find()) {
        foundCount++;
        foundLineIndex = a;
        logger.debug("start regex was found in index:" + foundLineIndex);
      }
    }

    if (foundCount > 1) {
      throw new Exception(
          String.format("more than one line match with regex [%s]: \n[%s]", regex1, lines));
    }

    ArrayList<String> uniqueGroup =
        getLinesFromIndexAndJustLinesWhichMeetsRegex(lines, foundLineIndex, regex2, isMandatory);

    // add the assert line
    uniqueGroup.add(0, lines.get(foundLineIndex));

    return uniqueGroup;
  }

  private static ArrayList<String> getLinesFromIndexAndJustLinesWhichMeetsRegex(
      ArrayList<String> lines, int startIndex, String regex, boolean isMandatory) throws Exception {

    logger.debug("getLinesFromIndexAndJustLinesWhichMeetsRegex");
    logger.debug(lines.toString());

    ArrayList<String> foundLines = new ArrayList<String>();
    for (int a = startIndex + 1; a < lines.size(); a++) {
      String line = lines.get(a).trim();
      logger.debug("searching regex:" + regex + " in:" + line + " index:" + a);
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(line);
      if (matcher.find()) {
        foundLines.add(line);
      } else {
        // this line does not meet regex. This indicates the end
        break;
      }
    }

    if (foundLines.size() == 0) {
      if (isMandatory) {
        throw new Exception(
            String.format("no one line meet regex:%s starting from :%s", regex, startIndex));
      } else {
        logger
            .debug(String.format("no one line meet regex:%s starting from :%s", regex, startIndex));
      }

    }

    return foundLines;
  }

  public static ArrayList<String> getValueFromKey(String key, ArrayList<String> lines,
      boolean returnNextLine) throws Exception {

    logger.debug("getSimpleItemByKey: " + key);
    logger.debug(lines.toString());

    String foundName = null;
    int foundIndex = -1;

    for (int a = 0; a < lines.size(); a++) {
      String line = lines.get(a);
      String regex = String.format("^%s\\s*:\\s*.+", key);
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(line);

      if (matcher.find()) {
        String rawMatch = matcher.group();
        foundName = rawMatch.split(":")[1].trim();
        foundIndex = a;
        break;
      }
    }

    if (foundName == null || foundIndex < 0) {
      throw new Exception("key was not found: " + key);
    }

    logger.debug("value:" + foundName);
    logger.debug("foundIndex:" + foundIndex);

    ArrayList<String> founded = new ArrayList<String>();
    founded.add(foundName);
    if (returnNextLine) {
      logger.debug("returnNextLine:" + lines.get(foundIndex + 1));
      founded.add(lines.get(foundIndex + 1));
    }

    return founded;
  }

  public static HashMap<String, String> getKeyValue(ArrayList<String> subSetRawLines,
      String initialString, String charSeparator) throws Exception {

    // ^header\\s+[a-zA-Z_\\-\\d]+\\s*=\\s*.+
    String regex =
        String.format("^%s\\s+[a-zA-Z_\\-\\d]+\\s*%s\\s*.+", initialString, charSeparator);

    logger.debug("regex to evaluate:" + regex);

    HashMap<String, String> keyValuePairs = new HashMap<String, String>();
    for (int a = 0; a < subSetRawLines.size(); a++) {
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(subSetRawLines.get(a));
      if (matcher.find()) {
        // split by space = space
        String[] rawPair = subSetRawLines.get(a).split(String.format("\\s+%s\\s+", charSeparator));
        // get key from raw pair array
        // expeted raw key is: header token
        // a string splitted by space, so the expetec value is [1]
        String key = rawPair[0].split("\\s")[1].trim();
        // get value
        String value = rawPair[1].trim();
        keyValuePairs.put(key, value);
      } else {
        logger.debug(String.format("line has a wrong format: [%s]. Expected format is: %s",
            subSetRawLines.get(a), "Sample: header key = value"));
      }
    }

    return keyValuePairs;
  }
}
