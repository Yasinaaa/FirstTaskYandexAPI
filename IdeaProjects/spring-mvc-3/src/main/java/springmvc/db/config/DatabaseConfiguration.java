package springmvc.db.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {

    @Value("${db.url}")
    private String dbUrl;

    public DatabaseConfiguration() {
    }

    public String getDbUrl() {
        return dbUrl;
    }
}
