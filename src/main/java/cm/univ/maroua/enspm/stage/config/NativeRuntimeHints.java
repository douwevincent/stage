package cm.univ.maroua.enspm.stage.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Provides runtime hints for GraalVM Native Image.
 * This is used to register reflection, resources, and proxies that are not
 * automatically
 * detected by Spring AOT.
 */
@Configuration
@ImportRuntimeHints(NativeRuntimeHints.NativeRuntimeHintsRegistrar.class)
public class NativeRuntimeHints {

    public static class NativeRuntimeHintsRegistrar implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Register reflection for MySQL and H2 drivers if needed
            // (Standard drivers are usually handled by Spring Boot, but we add them for
            // safety)
            try {
                hints.reflection().registerType(Class.forName("com.mysql.cj.jdbc.Driver"));
                hints.reflection().registerType(Class.forName("org.h2.Driver"));
            } catch (ClassNotFoundException e) {
                // Ignore if driver not found
            }

            // Add resource hints for Quartz if you add jobs later
            // hints.resources().registerPattern("quartz.properties");
        }
    }
}
