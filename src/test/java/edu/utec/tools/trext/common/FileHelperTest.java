package edu.utec.tools.trext.common;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import edu.utec.test.common.TestResourceHelper;

public class FileHelperTest {

  @Before
  public void setup() {
    new FileHelper();
  }

  @Test
  public void fileNotFound() throws Exception {
    File file = new File("im a tea pot");
    try {
      FileHelper.getFileAsLines(file);
    } catch (Exception e) {
      assertEquals(true, e.getMessage().startsWith("Failed to read file:"));
    }
  }

  @Test
  public void getFileAsLines() throws Exception {
    File file = TestResourceHelper.getFile(this, "getFileAsLines.txt");
    ArrayList<String> lines = FileHelper.getFileAsLines(file);
    assertEquals(4, lines.size());
  }

  @Test
  public void getFileAsLinesCommented() throws Exception {
    File file = TestResourceHelper.getFile(this, "getFileAsLinesCommented.txt");
    ArrayList<String> lines = FileHelper.getFileAsLines(file, true, "#");
    assertEquals(9, lines.size());
  }

  @Test
  public void getFileAsStringFromClasspath() throws Exception {
    String fileContent = FileHelper.getFileAsStringFromClasspath(
        "/edu/utec/tools/trext/common/FileHelperTest.getFileAsStringFromClasspath.txt");
    assertEquals(
        "When I wrote this code, only God and I understood what I did. Now only God knows.",
        fileContent);
    String fileEmptyContent = FileHelper.getFileAsStringFromClasspath(
        "/edu/utec/tools/trext/common/FileHelperTest.getFileAsStringFromClasspathEmpty.txt");
    assertEquals("", fileEmptyContent);
  }

  @Test
  public void mapToJsonFile() throws Exception {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("boolan", true);
    map.put("int", 5);
    map.put("string", "hello");

    String tempAbsolutePath =
        System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString();
    FileHelper.mapToJsonFile(map, tempAbsolutePath);

    assertEquals(true, new File(tempAbsolutePath).exists());
    new File(tempAbsolutePath).deleteOnExit();
  }

  @Test
  public void mapToJsonFileNull() throws Exception {
    try {
      FileHelper.mapToJsonFile(null, null);
    } catch (Exception e) {
      assertEquals(true, e.getMessage().startsWith("Failed to generate json from map"));
    }
  }

  @Test
  public void loadVariablesFromProperties() throws Exception {
    File file = TestResourceHelper.getFile(this, "loadVariablesFromProperties.txt");
    HashMap<String, Object> variables = FileHelper.loadVariablesFromProperties(file);
    assertEquals("http://localhost:8080/v1/book", variables.get("apiBaseUrl"));
    assertEquals("Flores para ALgernon ${srand}", variables.get("bookTitle"));
    assertEquals("Daniel Keyes ${srand}", variables.get("bookAuthor"));
    assertEquals("ISBN-${irand}", variables.get("bookIsbn"));

    HashMap<String, Object> emptyVariables =
        FileHelper.loadVariablesFromProperties(new File("imateapot"));
    assertEquals(0, emptyVariables.size());
  }

  // At the time of writing this test, structure folder was
  /// src/main/resources/report/html
  /// src/main/resources/report/html/single
  /// src/main/resources/report/html/single/uncompact
  /// src/main/resources/report/html/single/uncompact/style.css
  /// src/main/resources/report/html/single/uncompact/main.js
  /// src/main/resources/report/html/single/uncompact/index.html
  /// src/main/resources/report/html/single/uncompact/Chart.js
  /// src/main/resources/report/html/single/compact
  /// src/main/resources/report/html/single/compact/index.html
  /// src/main/resources/report/html/multiple
  /// src/main/resources/report/html/multiple/uncompact
  /// src/main/resources/report/html/multiple/uncompact/style.css
  /// src/main/resources/report/html/multiple/uncompact/main.js
  /// src/main/resources/report/html/multiple/uncompact/index.html
  /// src/main/resources/report/html/multiple/uncompact/Chart.js
  /// src/main/resources/report/html/multiple/compact
  /// src/main/resources/report/html/multiple/compact/index.html
  @Test
  public void listFileTree() throws Exception {
    String basePath = new File("").getAbsolutePath();
    String dir = basePath + File.separator + "src/main/resources/report/html";
    // read just html files
    ArrayList<File> files = FileHelper.listFileTree(new File(dir), ".html");
    assertEquals(4, files.size());
    assertEquals(
        basePath + File.separator + "src/main/resources/report/html/multiple/compact/index.html",
        files.get(0).getAbsolutePath());
    assertEquals(
        basePath + File.separator + "src/main/resources/report/html/multiple/uncompact/index.html",
        files.get(1).getAbsolutePath());
    assertEquals(
        basePath + File.separator + "src/main/resources/report/html/single/compact/index.html",
        files.get(2).getAbsolutePath());
    assertEquals(
        basePath + File.separator + "src/main/resources/report/html/single/uncompact/index.html",
        files.get(3).getAbsolutePath());
  }

  @Test
  public void listFileTreeEmpty() throws Exception {
    // null dir returns empty list
    ArrayList<File> nullDir = FileHelper.listFileTree(null, ".acme");
    assertEquals(0, nullDir.size());

    String tempAbsolutePath =
        System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString();
    File theDir = new File(tempAbsolutePath);
    if (!theDir.exists()) {
      theDir.mkdirs();
    }

    // folder without any files, must returns an empty list
    ArrayList<File> emptyDir = FileHelper.listFileTree(new File(tempAbsolutePath), ".acme");
    assertEquals(0, emptyDir.size());
  }

}
