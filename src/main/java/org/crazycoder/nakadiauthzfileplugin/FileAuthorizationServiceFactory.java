package org.crazycoder.nakadiauthzfileplugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.zalando.nakadi.plugin.api.PluginException;
import org.zalando.nakadi.plugin.api.SystemProperties;
import org.zalando.nakadi.plugin.api.authz.AuthorizationService;
import org.zalando.nakadi.plugin.api.authz.AuthorizationServiceFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Creates {@link FileAuthorizationService} based on provided JSON configuration file.
 * <p>
 * Configuration file example:
 * <p>
 * <pre>
 * {
 *      "admins": [
 *          {
 *              "data_type": "service",
 *              "value": "nakadi-mock-service"
 *          }
 *      ],
 *      "writers": [],
 *      "readers": []
 * }
 * </pre>
 *
 * @author Andrey Dyachkov
 */
public class FileAuthorizationServiceFactory implements AuthorizationServiceFactory {

    @Override
    public AuthorizationService init(SystemProperties properties) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String mockedDataFile = properties.getProperty("nakadi.authz.file.plugin.authz-file");
            if (mockedDataFile == null || mockedDataFile.isEmpty()) {
                throw new PluginException("Configuration file was not found in classpath," +
                        " define as env var `nakadi.authz.file.plugin.authz-file`");
            }

            return new FileAuthorizationService(objectMapper.readTree(Paths.get(mockedDataFile).toFile()));
        } catch (IOException e) {
            throw new PluginException("Failed to load authz file", e);
        }
    }
}
