package edu.utec.tools.trext.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import edu.utec.tools.trext.variables.VariablePlaceHolderEvaluator;

public class FileHelper {

  private final static Logger logger = LogManager.getLogger(FileHelper.class);

  private static boolean isCommentedLine(String line, String commentChar) {
    return line.startsWith(commentChar);
  }

  public static ArrayList<String> getFileAsLines(File file) throws Exception {
    return getFileAsLines(file, false, null);
  }

  public static ArrayList<String> getFileAsLines(File file, boolean excludeComments,
      String commentChar) throws Exception {
    ArrayList<String> result = new ArrayList<>();

    FileReader fileReader = null;
    try {
      fileReader = new FileReader(file);
      StringBuffer sb = new StringBuffer();
      while (fileReader.ready()) {
        char c = (char) fileReader.read();
        if (c == '\n') {
          String line = sb.toString();
          if (excludeComments) {
            if (!isCommentedLine(line, commentChar)) {
              result.add(line);
            }
          } else {
            result.add(line);
          }

          sb = new StringBuffer();
        } else {
          sb.append(c);
        }
      }
      if (sb.length() > 0) {
        String line = sb.toString();
        if (excludeComments) {
          if (!isCommentedLine(line, commentChar)) {
            result.add(line);
          }
        } else {
          result.add(line);
        }
      }
      fileReader.close();
      return result;
    } catch (FileNotFoundException e) {
      throw new Exception("Failed to read file: " + file.getAbsolutePath(), e);
    }

  }

  public static String getFileAsStringFromClasspath(String file) {
    InputStream classPathFileStream = FileHelper.class.getResourceAsStream(file);
    Scanner scanner = new Scanner(classPathFileStream);
    scanner.useDelimiter("\\A");
    String fileContent = scanner.hasNext() ? scanner.next() : "";
    scanner.close();
    return fileContent;
  }

  public static void mapToJsonFile(Map<?, ?> obj, String fileAbsolutePath) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
    try {
      writer.writeValue(new File(fileAbsolutePath), obj);
    } catch (Exception e) {
      throw new Exception("Failed to generate json from map", e);
    }
  }

  public static HashMap<String, Object> loadVariablesFromProperties(File dataFile) {

    HashMap<String, Object> variables = new HashMap<String, Object>();
    VariablePlaceHolderEvaluator variablePlaceHolderEvaluator = new VariablePlaceHolderEvaluator();
    try {
      Properties properties = new Properties();
      properties.load(new FileInputStream(dataFile));
      for (String key : properties.stringPropertyNames()) {
        String value = properties.getProperty(key);
        variables.put(key, variablePlaceHolderEvaluator.evaluteValueIfIsEnvironmentVariable(value));

      }

    } catch (Exception e) {
      logger.warn("Failed to read properties as map: " + dataFile.getAbsolutePath() + ". " + e);
    }

    return variables;
  }

  public static ArrayList<File> listFileTree(File dir, String ext, String regexExclude) {
    ArrayList<File> fileTree = new ArrayList<File>();
    Pattern pattern = null;
    if (regexExclude != null && !regexExclude.isEmpty()) {
      pattern = Pattern.compile(regexExclude);
    }

    if (dir == null || dir.listFiles() == null) {
      return fileTree;
    }
    for (File entry : dir.listFiles()) {
      if (entry.isFile() && entry.getName().endsWith(ext)) {
        if (pattern == null) {
          fileTree.add(entry);
        } else {
          Matcher isExcludeRegex = pattern.matcher(entry.getName());
          if (!isExcludeRegex.find()) {// if this file is not exclude
            fileTree.add(entry);
          }
        }

      } else
        fileTree.addAll(listFileTree(entry, ext, regexExclude));
    }

    Collections.sort(fileTree);

    return fileTree;
  }

  public static ArrayList<File> listFileTree(File dir, String ext) {
    return listFileTree(dir, ext, null);
  }


}
