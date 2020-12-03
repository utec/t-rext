package edu.utec.test.common;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class TestResourceHelper {

  public static String getFileAsString(String classpathLocation) {
    ClassLoader classLoader = TestResourceHelper.class.getClassLoader();
    InputStream inputStreamGlobals = classLoader.getResourceAsStream(classpathLocation);
    Scanner scannerGlobals = new Scanner(inputStreamGlobals);
    scannerGlobals.useDelimiter("\\A");
    String text = scannerGlobals.hasNext() ? scannerGlobals.next() : "";
    scannerGlobals.close();
    scannerGlobals.close();
    return text;
  }

  public static File getFile(String classpathLocation) {
    ClassLoader classLoader = TestResourceHelper.class.getClassLoader();
    File file = new File(classLoader.getResource(classpathLocation).getFile());
    return file;
  }

  public static File getFile(Object testeInstance, String fileName) throws Exception {
    ClassLoader classLoader = TestResourceHelper.class.getClassLoader();
    String locationAsFilePath =
        testeInstance.getClass().getPackage().getName().replaceAll("\\.", "/");
    String testName = testeInstance.getClass().getSimpleName();
    String absoluteClasapathLocation =
        String.format("%s/%s.%s", locationAsFilePath, testName, fileName);

    URL url = classLoader.getResource(absoluteClasapathLocation);
    if (url == null) {
      throw new Exception("Failed to read internal resource: " + absoluteClasapathLocation);
    }

    File file = new File(url.getFile());
    return file;
  }

}
