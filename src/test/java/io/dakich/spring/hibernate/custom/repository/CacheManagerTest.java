package io.dakich.spring.hibernate.custom.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;

class CacheManagerTest {

  @Test
  void testConstructorThrowsError()
      throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    Constructor<CacheManager> pcc = CacheManager.class.getDeclaredConstructor();
    pcc.setAccessible(true);
    final InvocationTargetException invocationTargetException = assertThrows(
        InvocationTargetException.class, () ->
            pcc.newInstance());// Empty constructor
    assertTrue(invocationTargetException.getCause().getClass()==UnsupportedOperationException.class);
   assertEquals("REFLECTION !? :)",invocationTargetException.getCause().getMessage());
  }

}
