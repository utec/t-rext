package edu.utec.test.common;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public class TestHelper {

  public static String getFileAsString(String classpathLocation) {
    ClassLoader classLoader = TestHelper.class.getClassLoader();
    InputStream inputStreamGlobals = classLoader.getResourceAsStream(classpathLocation);
    Scanner scannerGlobals = new Scanner(inputStreamGlobals);
    scannerGlobals.useDelimiter("\\A");
    String text = scannerGlobals.hasNext() ? scannerGlobals.next() : "";
    scannerGlobals.close();
    scannerGlobals.close();
    return text;
  }

  public static File getFile(String classpathLocation) {
    ClassLoader classLoader = TestHelper.class.getClassLoader();
    File file = new File(classLoader.getResource(classpathLocation).getFile());
    return file;
  }

  public static File getFile(Object testeInstance, String fileName) throws Exception {
    ClassLoader classLoader = TestHelper.class.getClassLoader();
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

  public static String getTestFileAsString(Object testeInstance, String fileName) throws Exception {
    String locationAsFilePath =
        testeInstance.getClass().getPackage().getName().replaceAll("\\.", "/");
    String testName = testeInstance.getClass().getSimpleName();
    String absoluteClasapathLocation =
        String.format("%s/%s.%s", locationAsFilePath, testName, fileName);
    return getFileAsString(absoluteClasapathLocation);
  }

  @SuppressWarnings("unchecked")
  public static void setEnvironmentVariable(String key, String value) throws Exception {
    Class<?> pe = Class.forName("java.lang.ProcessEnvironment");
    Method getenv = pe.getDeclaredMethod("getenv");
    getenv.setAccessible(true);
    Object unmodifiableEnvironment = getenv.invoke(null);
    Class<?> map = Class.forName("java.util.Collections$UnmodifiableMap");
    Field m = map.getDeclaredField("m");
    m.setAccessible(true);
    Map<String, String> env = (Map<String, String>) m.get(unmodifiableEnvironment);;
    env.put(key, value);
  }

}
