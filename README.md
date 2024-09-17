# Spring Hibernate L1 Cache Management

![build workflow](https://github.com/BeytullahC/SPING-HIBERNATE-L1-CACHE-MANAGEMENT/actions/workflows/test.yaml/badge.svg) ![Coverage](.github/badges/jacoco.svg) ![Branches](.github/badges/branches.svg) [![License: Unlicense](https://img.shields.io/badge/license-Unlicense-blue.svg)](http://unlicense.org/)

## Overview

This project demonstrates efficient management of **Hibernate Level 1 (L1) Cache** in a Spring Boot application through a custom implementation of `SimpleJpaRepository`. The goal is to control when L1 cache is cleared, preventing Hibernate from retaining stale data even after sub-transactions are committed. This ensures that the application always fetches up-to-date data from the database, especially beneficial in **batch processing** and **concurrent read/write operations**, thus guaranteeing data consistency across multiple transactions.

By enabling flexible cache management, the project optimizes resource usage and ensures the freshness of data, solving a common Hibernate L1 cache issue where entities are kept in cache even when they should be reloaded from the database.

## Features

- **Evict Strategy**: Explicitly removes entities from the L1 cache to force Hibernate to reload the entity from the database on future accesses.
- **Detach Strategy**: Detaches an entity from the Hibernate session, meaning Hibernate will no longer track changes to the entity.
- **Default Strategy**: Hibernate’s default behavior, where entities are cached for the duration of the session and flushed to the database upon commit.
- **Transaction Support**: Ensures that caching strategies are applied correctly within the boundaries of transactions.
- **Log Tracking**: Logs cache operations for better transparency during cache evictions and detachment processes.

## Technologies Used

- **Spring Boot 3.0**
- **Hibernate ORM**
- **JPA (Java Persistence API)**
- **Caffeine Cache** for extended cache management
- **H2 Database** for testing purposes
- **JUnit & Mockito** for unit and integration testing
- **Awaitility** for asynchronous testing

## Installation and Setup

### Prerequisites

- **Java 17** or higher
- **Maven 3.x**

### Steps to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/BeytullahC/SPRING-HIBERNATE-L1-CACHE-MANAGEMENT.git
   ```
2. Navigate to the project directory:
   ```bash
   cd SPRING-HIBERNATE-L1-CACHE-MANAGEMENT
   ```
3. Build the project
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Dependencies

The project uses the following key dependencies, as specified in the `pom.xml` file:

- **Spring Boot Starter Data JPA**
- **Hibernate ORM**
- **Caffeine Cache**
- **JUnit** and **Mockito** for testing
- **H2 Database** for integration testing

## Key Components and Cache Strategies

### Evict Strategy

- **Method**: `evict(T entity)`
- **Purpose**: Removes the specified entity from the L1 cache. Ensures that the next query reloads the entity from the database, ensuring up-to-date data.
- **Use Case**: Useful when stale data must be avoided, especially in multi-transaction scenarios.

### Detach Strategy

- **Method**: `detach(T entity)`
- **Purpose**: Detaches the entity from the Hibernate session, meaning it is no longer tracked. Changes to the detached entity are not automatically persisted.
- **Use Case**: Prevents unnecessary persistence of read-only entities and helps manage memory usage by detaching non-critical entities.

### Default JPA Strategy

- **Method**: Standard Hibernate behavior without evict/detach strategies.
- **Purpose**: Entities are cached for the duration of the session, and Hibernate synchronizes changes upon transaction commit.
- **Use Case**: This is Hibernate’s out-of-the-box L1 cache behavior, where entities are retained in the cache until the session ends or changes are flushed.

### clear() Method

- **Method**: `clear()`
- **Purpose**: Clears the entire L1 cache for the current session, forcing Hibernate to reload all entities from the database on the next request.
- **Use Case**: Useful for large transactions or when cache data is no longer needed.

### Log Tracking for Cache Strategies

The project also includes log tracking capabilities that allow you to monitor cache operations such as evictions and detachments. These operations are logged for better debugging and transparency, especially in batch processing scenarios.

### Example Configuration for Transaction Strategy

Below is an example configuration file (`application-default-trx.properties`) that configures the application to use an in-memory H2 database and the default L1 cache strategy:

```properties
jdbc.url=jdbc:h2:mem:db-default-trx;DB_CLOSE_DELAY=-1
hibernate.l1.cache.strategy=evict #default,detach
logging.level.root=INFO
logging.level.io.dakich=DEBUG
```

### afterPropertiesSet() Method

The `afterPropertiesSet()` method is a part of the Spring `InitializingBean` interface and is used to initialize the cache strategy after all properties are set in the Spring context. 

#### Key Actions:
- **Cache Strategy Initialization**: The method reads the cache strategy from the environment (`env.getProperty`) using the key `CacheManager.L1_CACHE_STRATEGY_KEY`. If no specific strategy is defined, it defaults to `"default"`.
- **Logging**: It logs the initialized cache strategy using `LOG.error` for debugging purposes.

This ensures that the cache strategy is configured at runtime, based on the properties defined in the environment or application configuration files.


```java
package io.dakich.spring.hibernate.custom.repository.config;

import io.dakich.spring.hibernate.custom.repository.CacheManager;
import io.dakich.spring.hibernate.custom.repository.JpaRepositoryFactoryBean;
import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@EnableJpaRepositories(
    basePackages = "io.dakich.spring.hibernate.custom.repository.sample",
    repositoryFactoryBeanClass = JpaRepositoryFactoryBean.class
)
@EnableTransactionManagement
public class TestConfig implements InitializingBean {
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

  @Bean
  public AsyncTaskExecutor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(6);
    executor.setThreadNamePrefix("test_task_executor");
    executor.initialize();
    return executor;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    CacheManager.setCacheStrategy(env.getProperty(CacheManager.L1_CACHE_STRATEGY_KEY, "default"));
    LOG.error("CACHE STRATEGY:" + CacheManager.getCacheStrategy());
  }
}
```
## Testing

The project includes a comprehensive suite of unit and integration tests that validate the caching strategies (evict, detach, and default) and the transactional behavior.

### Test Cases

- **Evict Tests**: 
  - **`SimpleJpaRepositoryImplEvictIT`**: Verifies that entities are properly evicted from the L1 cache and reloaded from the database when accessed again.
  - **`EvictTransactionIT`**: Tests the eviction process within transaction boundaries, ensuring cache consistency across transactions.
  
- **Detach Tests**: 
  - **`SimpleJpaRepositoryImplDetachIT`**: Ensures that detached entities are no longer tracked by the Hibernate session, preventing automatic persistence.
  - **`DetachTransactionIT`**: Validates detachment behavior within transaction contexts.

- **Logger Tests**: 
  - **`SimpleJpaRepositoryImplEvictLoggerInfoIT`**: Ensures proper logging during entity eviction for debugging and monitoring purposes.
  - **`SimpleJpaRepositoryImplDetachLoggerInfoIT`**: Logs information during detachment processes, making it easier to track cache operations.

### Running Tests

To run all tests, use the following Maven command:
```bash
mvn test
```
### Important Note on L1 Cache Cleanup

Please note that **L1 cache cleanup will not occur for methods in `SimpleJpaRepositoryImpl` that have not been extended**. Only methods explicitly overridden or extended in the `SimpleJpaRepositoryImpl` will handle cache cleaning operations such as eviction or detachment. Any other inherited methods will follow the default Hibernate behavior and may not trigger cache management.

### Disclaimer

This software is provided "as-is" without any warranties, express or implied. The authors are not responsible for any damages or issues that may arise from the use of this software. Use at your own risk. The cache management features implemented in this project, including L1 cache eviction and detachment strategies, may not cover all edge cases or fit all use scenarios. For enterprise or commercial usage, please refer to the commercial licensing terms.
