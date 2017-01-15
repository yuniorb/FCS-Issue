package com.example;

import static org.assertj.core.api.Java6Assertions.assertThat;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.MethodAnnotationMatchProcessor;
import java.lang.reflect.Method;
import org.junit.Test;

public class ScanApplicationTests {

  @Test
  public void testJarFilter() {

    // Filter to only the -w JAR
    String filter = "jar:dep-w-0.0.1-SNAPSHOT.jar";
    FindDeprecated processor = new FindDeprecated();

    FastClasspathScanner scanner = new FastClasspathScanner(filter);
    scanner.verbose(true);
    scanner.matchClassesWithMethodAnnotation(Deprecated.class, processor);
    scanner.scan();

    // This should not fail. If it does the project is NOT
    // configured properly and the /jars folder is not in the classpath
    assertThat(processor.typeName).isNotNull().overridingErrorMessage("Add the jars folder to the classpath");

    // This SHOULD fail. This is the BUG.
    // processor should have captured only classes from -w JAR
    assertThat(processor.typeName).isEqualToIgnoringCase("com.example.TypeW");
  }

  class FindDeprecated implements MethodAnnotationMatchProcessor {

    public String typeName;

    public void processMatch(Class<?> aClass, Method method) {
      typeName = method.getParameterTypes()[0].getName();
    }
  }

}
