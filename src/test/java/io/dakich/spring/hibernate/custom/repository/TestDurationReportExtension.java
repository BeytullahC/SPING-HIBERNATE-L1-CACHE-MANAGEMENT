package io.dakich.spring.hibernate.custom.repository;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;


public class TestDurationReportExtension implements InvocationInterceptor {

  @Override
  public void interceptBeforeEachMethod(Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext)
      throws Throwable {
    InvocationInterceptor.super.interceptBeforeEachMethod(invocation, invocationContext,
        extensionContext);
    cleanupDatabase();
  }

  /**@Override
  public void interceptTestMethod(Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) throws Throwable {
    AtomicReference<Throwable> t = new AtomicReference<>(null);
    long beforeTest = System.currentTimeMillis();
    try {
      final PlatformTransactionManager bean = ApplicationContextUtil.getBean(
          PlatformTransactionManager.class);
      new TransactionTemplate(bean).executeWithoutResult((a)-> {
            try {
              invocation.proceed();
            } catch (Throwable e) {
              System.err.println(e);
              t.set(e);
            }
          }
          );

    }catch (Throwable tt){
      System.err.println(tt);
      t.set(tt);
    }
    if(t.get()!=null)
      throw new RuntimeException(t.get());


  }*/



  private void cleanupDatabase() throws SQLException {
    final DataSource bean = ApplicationContextUtil.getBean(DataSource.class);
    Connection c = bean.getConnection();
    Statement s = c.createStatement();

    // Disable FK
    s.execute("SET REFERENTIAL_INTEGRITY FALSE");

    // Find all tables and truncate them
    Set<String> tables = new HashSet<>();
    ResultSet rs = s.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
    while (rs.next()) {
      tables.add(rs.getString(1));
    }
    rs.close();
    for (String table : tables) {
      s.executeUpdate("TRUNCATE TABLE " + table);
    }

    // Idem for sequences
    Set<String> sequences = new HashSet<>();
    rs = s.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'");
    while (rs.next()) {
      sequences.add(rs.getString(1));
    }
    rs.close();
    for (String seq : sequences) {
      s.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
    }

    // Enable FK
    s.execute("SET REFERENTIAL_INTEGRITY TRUE");
    s.close();
    c.close();
    System.out.println("\n\nCLEANUP SUCCESS\n\n");
  }
}
