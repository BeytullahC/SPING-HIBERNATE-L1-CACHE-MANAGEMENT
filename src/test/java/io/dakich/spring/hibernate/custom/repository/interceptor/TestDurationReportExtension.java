package io.dakich.spring.hibernate.custom.repository.interceptor;

import io.dakich.spring.hibernate.custom.repository.ApplicationContextUtil;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;


public class TestDurationReportExtension implements InvocationInterceptor {

  @Override
  public void interceptBeforeEachMethod(Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext)
      throws Throwable {
    InvocationInterceptor.super.interceptBeforeEachMethod(invocation, invocationContext,
        extensionContext);
    cleanupDatabase();
  }


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
  }
}
