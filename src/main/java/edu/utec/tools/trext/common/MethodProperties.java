package edu.utec.tools.trext.common;

public class MethodProperties {

  private static java.util.Properties properties;

  private static void initialize() throws Exception {
    properties = new java.util.Properties();
    try {
      properties.load(MethodProperties.class.getResourceAsStream("/methods_enhancer.properties"));
    } catch (Exception e) {
      throw new Exception("Failed to read: assert_enhancer.properties", e);
    }
  }

  public static String getProperty(String key) throws Exception {
    if (properties == null) {
      initialize();
    }
    return properties.getProperty(key);
  }

}
