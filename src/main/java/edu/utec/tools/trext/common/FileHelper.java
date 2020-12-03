package edu.utec.tools.trext.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

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
    } catch (Exception e) {
      if(fileReader!=null) {
        fileReader.close();  
      }      
      throw new Exception("Failed to read file: " + file.getAbsolutePath(), e);
    }
    return result;
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

    try {
      Properties properties = new Properties();
      properties.load(new FileInputStream(dataFile));
      for (String key : properties.stringPropertyNames()) {
        String value = properties.getProperty(key);
        variables.put(key, value);
      }

    } catch (Exception e) {
      logger.warn("Failed to read properties as map: " + dataFile.getAbsolutePath() + ". " + e);
    }

    return variables;
  }

  public static ArrayList<File> listFileTree(File dir, String ext) {
    ArrayList<File> fileTree = new ArrayList<File>();

    if (dir == null || dir.listFiles() == null) {
      return fileTree;
    }
    for (File entry : dir.listFiles()) {
      if (entry.isFile() && entry.getName().endsWith(ext)) {
        fileTree.add(entry);
      } else
        fileTree.addAll(listFileTree(entry, ext));
    }

    Collections.sort(fileTree);

    return fileTree;
  }


}
