package io.dakich.spring.hibernate.custom.repository.config;

import io.dakich.spring.hibernate.custom.repository.CacheManager;
import io.dakich.spring.hibernate.custom.repository.SimpleJpaRepositoryImpl;
import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "io.dakich.spring.hibernate.custom.repository.sample",
    repositoryBaseClass = SimpleJpaRepositoryImpl.class)
@EnableTransactionManagement
public class TestConfig {
  private static final Logger LOG= LoggerFactory.getLogger(TestConfig.class);
  @Autowired
  private Environment env;

  @Bean
  public DataSource dataSource() {
    final DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName","not found"));
    dataSource.setUrl(env.getProperty("jdbc.url"));
    dataSource.setUsername(env.getProperty("jdbc.user"));
    dataSource.setPassword(env.getProperty("jdbc.pass"));

    return dataSource;
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan("io.dakich.spring.hibernate.custom.domain");
    em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    em.setJpaProperties(additionalProperties());
    return em;
  }

  @Bean
  JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory);
    return transactionManager;
  }

  final Properties additionalProperties() {
    final Properties hibernateProperties = new Properties();

    hibernateProperties.setProperty("hibernate.hbm2ddl.auto",
        env.getProperty("hibernate.hbm2ddl.auto"));
    hibernateProperties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
    hibernateProperties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));

    return hibernateProperties;
  }

  @EventListener
  public void handleContextRefreshEvent(ContextRefreshedEvent ctxStartEvt) {
    CacheManager.setCacheStrategy(env.getProperty("hibernate.l1.cache.strategy", "default"));
    LOG.error("CACHE STRATEGY:" + CacheManager.getCacheStrategy());
  }

  @Bean
  public AsyncTaskExecutor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(6);
    executor.setThreadNamePrefix("test_task_executor");
    executor.initialize();
    return executor;
  }

}
