package io.dakich.spring.hibernate.custom.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;

class CacheManagerTest {

  @Test
  void testConstructorThrowsError()
      throws NoSuchMethodException {
    Constructor<CacheManager> pcc = CacheManager.class.getDeclaredConstructor();
    pcc.setAccessible(true);
    final InvocationTargetException invocationTargetException = assertThrows(
        InvocationTargetException.class, pcc::newInstance);// Empty constructor
    assertSame(invocationTargetException.getCause().getClass(),
        UnsupportedOperationException.class);
   assertEquals("REFLECTION !? :)",invocationTargetException.getCause().getMessage());
  }

}
