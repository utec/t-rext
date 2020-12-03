package edu.utec.tools.trext.common;

import static org.junit.Assert.assertEquals;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;

public class DataTypeHelperTest {

  @Before
  public void setup() {
    new DataTypeHelper();
  }
  
  @Test
  public void isInteger() throws Exception {
    assertEquals(false, DataTypeHelper.isInteger(null));
    assertEquals(true, DataTypeHelper.isInteger(Integer.MAX_VALUE));
    assertEquals(true, DataTypeHelper.isInteger("1"));
    assertEquals(true, DataTypeHelper.isInteger("0"));
    assertEquals(false, DataTypeHelper.isInteger("1.a"));
    assertEquals(false, DataTypeHelper.isInteger(new Date()));
  }
  
  @Test
  public void isDouble() throws Exception {
    assertEquals(false, DataTypeHelper.isDouble(null));
    assertEquals(true, DataTypeHelper.isDouble(Double.MAX_VALUE));
    assertEquals(true, DataTypeHelper.isDouble("1.1"));
    assertEquals(true, DataTypeHelper.isDouble("0.5"));
    assertEquals(false, DataTypeHelper.isDouble("1"));
    assertEquals(false, DataTypeHelper.isDouble("aa.a"));
    assertEquals(false, DataTypeHelper.isDouble(new Date()));
  }
  
  @Test
  public void isBoolean() throws Exception {
    assertEquals(false, DataTypeHelper.isBoolean(null));
    assertEquals(true, DataTypeHelper.isBoolean(Boolean.TRUE));
    assertEquals(true, DataTypeHelper.isBoolean(false));
    assertEquals(true, DataTypeHelper.isBoolean("true"));
    assertEquals(false, DataTypeHelper.isBoolean("verdadero"));
    assertEquals(false, DataTypeHelper.isBoolean(new Date()));
  }
  
  @Test
  public void isQuotedString() throws Exception {
    assertEquals(false, DataTypeHelper.isQuotedString(null));
    assertEquals(true, DataTypeHelper.isQuotedString("\"hello\""));
  }

  @Test
  public void isString() throws Exception {
    assertEquals(false, DataTypeHelper.isString(null));
    assertEquals(true, DataTypeHelper.isString("1"));
  }
}
