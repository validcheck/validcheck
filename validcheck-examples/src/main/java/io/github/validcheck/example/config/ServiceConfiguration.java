package io.github.validcheck.example.config;

import static io.github.validcheck.Check.batch;
import static io.github.validcheck.Check.check;

import io.github.validcheck.ValueValidator;
import java.util.List;

/**
 * Service configuration example demonstrating 3-level nested validation.
 *
 * <p>This example shows how to validate a typical microservice configuration with nested objects
 * using Java records and default validation config.
 */
public class ServiceConfiguration {

  public record DatabaseConfig(
      String host,
      int port,
      String database,
      String username,
      String password,
      ConnectionPool pool) {
    public DatabaseConfig {
      check("host", host).notNullOrEmpty().lengthBetween(3, 255);
      check("port", port).between(1, 65535);
      check("database", database).notNullOrEmpty().matches("[a-zA-Z0-9_]+");
      check("username", username).notNullOrEmpty().lengthBetween(2, 50);
      check("password", password)
          .withMessage("Database password must be at least 8 characters long for security")
          .notNull()
          .minLength(8);
      check("pool", pool).notNull();
    }
  }

  public record ConnectionPool(
      int minSize, int maxSize, int timeoutSeconds, HealthCheck healthCheck) {
    public ConnectionPool {
      // Use batch validation to collect all size-related errors
      var validation = batch();
      validation.check("minSize", minSize).isPositive().max(100);
      validation.check("maxSize", maxSize).isPositive().max(1000);
      validation.check("timeoutSeconds", timeoutSeconds).between(1, 3600);
      validation.validate();

      check("healthCheck", healthCheck).notNull();
    }
  }

  public record HealthCheck(boolean enabled, int intervalSeconds, String query) {
    public HealthCheck {
      check("intervalSeconds", intervalSeconds).between(5, 300);
      check("query", query)
          .whenString(enabled, validator -> validator.notNullOrEmpty().startsWith("SELECT"));
    }
  }

  public record ServerConfig(
      String host, int port, boolean sslEnabled, SslConfig ssl, List<String> allowedOrigins) {
    public ServerConfig {
      check("host", host).notNullOrEmpty();
      check("port", port).between(1024, 65535);
      check("ssl", ssl).when(sslEnabled, ValueValidator::notNull);
      check("allowedOrigins", allowedOrigins).notNull().minSize(1);
    }
  }

  public record SslConfig(String keystorePath, String keystorePassword, String protocol) {
    public SslConfig {
      check("keystorePath", keystorePath).notNullOrEmpty().endsWith(".jks");
      check("keystorePassword", keystorePassword).notNull().minLength(6);
      check("protocol", protocol).oneOf("TLS", "TLSv1.2", "TLSv1.3");
    }
  }

  public record ApplicationConfig(
      String name,
      String version,
      String environment,
      DatabaseConfig database,
      ServerConfig server,
      LoggingConfig logging) {
    public ApplicationConfig {
      check("name", name).notNullOrEmpty().matches("[a-z-]+");
      check("version", version).notNull().matches("\\d+\\.\\d+\\.\\d+");
      check("environment", environment).oneOf("development", "staging", "production");
      check("database", database).notNull();
      check("server", server).notNull();
      check("logging", logging).notNull();
    }
  }

  public record LoggingConfig(String level, String pattern, FileConfig file) {
    public LoggingConfig {
      check("level", level).oneOf("DEBUG", "INFO", "WARN", "ERROR");
      check("pattern", pattern).notNullOrEmpty();
      check("file", file).notNull();
    }
  }

  public record FileConfig(String path, String maxSize, int maxHistory) {
    public FileConfig {
      check("path", path).notNullOrEmpty().endsWith(".log");
      check("maxSize", maxSize).notNull().matches("\\d+[KMG]B");
      check("maxHistory", maxHistory).between(1, 365);
    }
  }

  public static void main(String[] args) {
    // Create a valid 3-level configuration
    var healthCheck = new HealthCheck(true, 60, "SELECT 1");
    var connectionPool = new ConnectionPool(5, 20, 30, healthCheck);
    var database =
        new DatabaseConfig(
            "localhost", 5432, "myapp_db", "dbuser", "securepass123", connectionPool);

    var sslConfig = new SslConfig("/etc/ssl/keystore.jks", "sslpass123", "TLSv1.3");
    var server =
        new ServerConfig(
            "0.0.0.0",
            8443,
            true,
            sslConfig,
            List.of("https://example.com", "https://app.example.com"));

    var fileConfig = new FileConfig("/var/log/myapp.log", "100MB", 30);
    var logging =
        new LoggingConfig(
            "INFO", "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n", fileConfig);

    var config =
        new ApplicationConfig("user-service", "1.2.3", "production", database, server, logging);

    System.out.println("Service configuration created and validated successfully!");
    System.out.println("Application: " + config.name() + " v" + config.version());
    System.out.println("Environment: " + config.environment());
    System.out.println("Database: " + config.database().host() + ":" + config.database().port());
    System.out.println("Server: " + config.server().host() + ":" + config.server().port());
    System.out.println("SSL enabled: " + config.server().sslEnabled());
  }
}
