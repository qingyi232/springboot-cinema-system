package com.project.platform.bootstrap;

import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DatabaseBootstrap {
    private enum Mode {
        NONE,
        CHECK,
        INIT
    }

    private static final Pattern MYSQL_URL_PATTERN = Pattern.compile(
            "^jdbc:mysql://([^/:?]+)(?::(\\d+))?/([^?]+).*$"
    );
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB_NAME = "movie_booking_system";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "root";
    private static final String JDBC_QUERY =
            "useUnicode=true&useSSL=false&characterEncoding=utf8"
                    + "&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";

    private DatabaseBootstrap() {
    }

    public static boolean shouldRun(String[] args) {
        return detectMode(args) != Mode.NONE;
    }

    public static int runFromEnvironment(String[] args) {
        return switch (detectMode(args)) {
            case CHECK -> checkFromEnvironment();
            case INIT -> initFromEnvironment();
            case NONE -> 0;
        };
    }

    private static Mode detectMode(String[] args) {
        if (Boolean.parseBoolean(System.getenv("APP_INIT_DB")) || Boolean.getBoolean("app.init-db")) {
            return Mode.INIT;
        }
        if (Boolean.parseBoolean(System.getenv("APP_CHECK_DB")) || Boolean.getBoolean("app.check-db")) {
            return Mode.CHECK;
        }
        if (args == null) {
            return Mode.NONE;
        }
        for (String arg : args) {
            if ("--app.init-db=true".equalsIgnoreCase(arg) || "app.init-db=true".equalsIgnoreCase(arg)) {
                return Mode.INIT;
            }
            if ("--app.check-db=true".equalsIgnoreCase(arg) || "app.check-db=true".equalsIgnoreCase(arg)) {
                return Mode.CHECK;
            }
        }
        return Mode.NONE;
    }

    private static int initFromEnvironment() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DbConfig config = DbConfig.fromEnvironment();
            Path sqlDirectory = resolveSqlDirectory();
            Path baseScript = sqlDirectory.resolve("sql.sql");
            Path upgradeScript = sqlDirectory.resolve("2026-04-feature-upgrade.sql");

            if (!Files.exists(baseScript)) {
                System.err.println("[ERROR] Base SQL file not found: " + baseScript.toAbsolutePath());
                return 1;
            }
            if (!Files.exists(upgradeScript)) {
                System.err.println("[ERROR] Upgrade SQL file not found: " + upgradeScript.toAbsolutePath());
                return 1;
            }

            System.out.println("[INFO] Initializing database: " + config.databaseName);
            createDatabaseIfNeeded(config);
            executeScript(config.databaseUrl, config.username, config.password, baseScript);
            executeScript(config.databaseUrl, config.username, config.password, upgradeScript);
            System.out.println("[SUCCESS] Database initialization completed.");
            return 0;
        } catch (Exception exception) {
            printSQLException(exception, null, true);
            return 1;
        }
    }

    private static int checkFromEnvironment() {
        DbConfig config = DbConfig.fromEnvironment();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("[INFO] Checking MySQL server: " + config.host + ":" + config.port);
            try (Connection ignored = DriverManager.getConnection(config.serverUrl, config.username, config.password)) {
                System.out.println("[INFO] MySQL server connection successful.");
            }

            System.out.println("[INFO] Checking database: " + config.databaseName);
            try (Connection connection = DriverManager.getConnection(
                    config.databaseUrl,
                    config.username,
                    config.password
            )) {
                if (!hasTable(connection, "admin")
                        || !hasTable(connection, "movie")
                        || !hasTable(connection, "goods")
                        || !hasTable(connection, "goods_order")
                        || !hasColumn(connection, "movie_order", "reserve_expire_time")
                        || !hasColumn(connection, "movie_order", "ticket_code")
                        || !hasColumn(connection, "movie_order", "verify_time")
                        || !hasColumn(connection, "goods_order", "evaluate_content")
                        || !hasColumn(connection, "goods_order", "evaluate_rate")
                        || !hasColumn(connection, "goods_order", "evaluate_time")) {
                    System.err.println("[ERROR] Database schema is incomplete.");
                    System.err.println("[ERROR] Please run init-db-windows.bat first.");
                    return 1;
                }
            }

            System.out.println("[SUCCESS] Database connectivity check passed.");
            return 0;
        } catch (Exception exception) {
            printSQLException(exception, config, false);
            return 1;
        }
    }

    private static void createDatabaseIfNeeded(DbConfig config) throws SQLException {
        String sql = "CREATE DATABASE IF NOT EXISTS `" + config.databaseName + "` "
                + "DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
        try (Connection connection = DriverManager.getConnection(config.serverUrl, config.username, config.password);
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static void executeScript(
            String databaseUrl,
            String username,
            String password,
            Path scriptPath
    ) throws SQLException {
        System.out.println("[INFO] Executing SQL script: " + scriptPath.toAbsolutePath());
        try (Connection connection = DriverManager.getConnection(databaseUrl, username, password)) {
            EncodedResource encodedResource = new EncodedResource(
                    new FileSystemResource(scriptPath),
                    StandardCharsets.UTF_8
            );
            ScriptUtils.executeSqlScript(connection, encodedResource);
        }
    }

    private static boolean hasTable(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(connection.getCatalog(), null, tableName, new String[]{"TABLE"})) {
            return tables.next();
        }
    }

    private static boolean hasColumn(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(connection.getCatalog(), null, tableName, columnName)) {
            return columns.next();
        }
    }

    private static void printSQLException(Exception exception, DbConfig config, boolean initMode) {
        String detail = exception.getMessage();
        if (detail == null) {
            detail = exception.getClass().getSimpleName();
        }
        String lowerDetail = detail.toLowerCase();

        if (lowerDetail.contains("communications link failure")
                || lowerDetail.contains("connection refused")
                || lowerDetail.contains("connect timed out")) {
            String host = config == null ? DEFAULT_HOST : config.host;
            String port = config == null ? DEFAULT_PORT : config.port;
            System.err.println("[ERROR] Cannot connect to MySQL at " + host + ":" + port + ".");
            System.err.println("[ERROR] Please make sure the MySQL or phpStudy MySQL service is running.");
            System.err.println("[ERROR] Detail: " + detail);
            return;
        }

        if (lowerDetail.contains("access denied")) {
            System.err.println("[ERROR] MySQL username or password is incorrect.");
            System.err.println("[ERROR] Please check DB_USER and DB_PASSWORD in config-windows.bat.");
            System.err.println("[ERROR] Detail: " + detail);
            return;
        }

        if (lowerDetail.contains("unknown database")) {
            String databaseName = config == null ? DEFAULT_DB_NAME : config.databaseName;
            System.err.println("[ERROR] Database does not exist: " + databaseName);
            if (initMode) {
                System.err.println("[ERROR] Initialization could not continue.");
            } else {
                System.err.println("[ERROR] Please run init-db-windows.bat first.");
            }
            System.err.println("[ERROR] Detail: " + detail);
            return;
        }

        System.err.println("[ERROR] Database operation failed.");
        System.err.println("[ERROR] Detail: " + detail);
    }

    private static Path resolveSqlDirectory() {
        Path current = Paths.get("").toAbsolutePath().normalize();
        Path direct = current.resolve("sql");
        if (Files.isDirectory(direct)) {
            return direct;
        }
        Path parent = current.getParent();
        if (parent != null) {
            Path sibling = parent.resolve("sql");
            if (Files.isDirectory(sibling)) {
                return sibling;
            }
        }
        return direct;
    }

    private static String env(String key) {
        String value = System.getenv(key);
        return value == null ? null : value.trim();
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private static String buildDatabaseUrl(String host, String port, String databaseName) {
        return "jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?" + JDBC_QUERY;
    }

    private static String buildServerUrl(String host, String port) {
        return "jdbc:mysql://" + host + ":" + port + "/?" + JDBC_QUERY;
    }

    private static ParsedMysqlUrl parseMysqlUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        Matcher matcher = MYSQL_URL_PATTERN.matcher(url.trim());
        if (!matcher.matches()) {
            return null;
        }
        String host = matcher.group(1);
        String port = matcher.group(2) == null ? DEFAULT_PORT : matcher.group(2);
        String databaseName = matcher.group(3);
        int queryIndex = databaseName.indexOf('?');
        if (queryIndex >= 0) {
            databaseName = databaseName.substring(0, queryIndex);
        }
        return new ParsedMysqlUrl(host, port, databaseName);
    }

    private static final class DbConfig {
        private final String host;
        private final String port;
        private final String serverUrl;
        private final String databaseUrl;
        private final String username;
        private final String password;
        private final String databaseName;

        private DbConfig(
                String host,
                String port,
                String serverUrl,
                String databaseUrl,
                String username,
                String password,
                String databaseName
        ) {
            this.host = host;
            this.port = port;
            this.serverUrl = serverUrl;
            this.databaseUrl = databaseUrl;
            this.username = username;
            this.password = password;
            this.databaseName = databaseName;
        }

        private static DbConfig fromEnvironment() {
            String envDatabaseUrl = firstNonBlank(env("DB_URL"));
            ParsedMysqlUrl parsedMysqlUrl = parseMysqlUrl(envDatabaseUrl);

            String host = firstNonBlank(
                    env("DB_HOST"),
                    parsedMysqlUrl == null ? null : parsedMysqlUrl.host,
                    DEFAULT_HOST
            );
            String port = firstNonBlank(
                    env("DB_PORT"),
                    parsedMysqlUrl == null ? null : parsedMysqlUrl.port,
                    DEFAULT_PORT
            );
            String databaseName = firstNonBlank(
                    env("DB_NAME"),
                    parsedMysqlUrl == null ? null : parsedMysqlUrl.databaseName,
                    DEFAULT_DB_NAME
            );
            String databaseUrl = firstNonBlank(envDatabaseUrl, buildDatabaseUrl(host, port, databaseName));
            String serverUrl = buildServerUrl(host, port);
            String username = firstNonBlank(env("DB_USERNAME"), env("DB_USER"), DEFAULT_USERNAME);
            String password = firstNonBlank(env("DB_PASSWORD"), DEFAULT_PASSWORD);

            return new DbConfig(host, port, serverUrl, databaseUrl, username, password, databaseName);
        }
    }

    private record ParsedMysqlUrl(String host, String port, String databaseName) {
    }
}
