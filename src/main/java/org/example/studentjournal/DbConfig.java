package org.example.studentjournal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbConfig {
    @Bean
    public DbManager dbManager() throws Exception {
        Config config = new Config("settings.xml");
        return new DbManager(config.jdbcUrl, config.jdbcUser, config.jdbcPassword, true);
    }

}
