package com.jobseeker.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * Loads variables from a .env file in the project root into system properties
 * before Spring resolves ${...} placeholders in application.yml.
 *
 * The .env file is excluded from version control via .gitignore.
 * Example .env:
 *   RAPIDAPI_KEY=your_key_here
 */
@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadDotenv() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()   // won't fail if .env is absent (CI/CD uses real env vars)
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}
