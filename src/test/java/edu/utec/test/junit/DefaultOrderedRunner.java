package edu.utec.test.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class DefaultOrderedRunner extends BlockJUnit4ClassRunner {

  private List<String> desiredOrder = null;

  public DefaultOrderedRunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  @Override
  protected List<FrameworkMethod> computeTestMethods() {
    List<FrameworkMethod> unmodifiableList = super.computeTestMethods();
    List<FrameworkMethod> modifiableList = new ArrayList<FrameworkMethod>(unmodifiableList);
    Collections.sort(modifiableList, new Comparator<FrameworkMethod>() {
      @Override
      public int compare(FrameworkMethod f1, FrameworkMethod f2) {
        if (desiredOrder == null) {
          ExplicitOrder explicitOrder =
              f1.getDeclaringClass().getAnnotationsByType(ExplicitOrder.class)[0];
          desiredOrder = Arrays.asList(explicitOrder.value());
        }
        // Order o1 = f1.getAnnotation(Order.class);
        // Order o2 = f2.getAnnotation(Order.class);
        //
        // if (o1 == null || o2 == null)
        // return -1;

        return desiredOrder.indexOf(f1.getName()) - desiredOrder.indexOf(f2.getName());
      }
    });
    return modifiableList;
  }
}
