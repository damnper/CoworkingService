package ru.y_lab.config;

import lombok.Data;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

@Data
public class DatabaseConfig {
    private String url;
    private String username;
    private String password;
    private String driverClassName;

    public static DatabaseConfig load() {
        Map<String, String> datasource;

        Yaml yaml = new Yaml();
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/application.yaml")) {
            Map<String, Object> obj = yaml.load(in);

            if (obj == null) {
                throw new RuntimeException("Failed to load database configuration from YAML");
            }

            Object datasourceObj = obj.get("datasource");
            if (datasourceObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> ds = (Map<String, String>) datasourceObj;
                datasource = ds;
            } else {
                throw new RuntimeException("Datasource configuration in YAML is not valid");
            }

            DatabaseConfig config = new DatabaseConfig();
            config.setUrl(datasource.get("url"));
            config.setUsername(datasource.get("username"));
            config.setPassword(datasource.get("password"));
            config.setDriverClassName(datasource.get("driver-class-name"));
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }
}
