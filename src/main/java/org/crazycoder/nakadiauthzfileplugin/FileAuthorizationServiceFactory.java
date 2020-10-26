package org.crazycoder.nakadiauthzfileplugin;

import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.zalando.nakadi.plugin.api.SystemProperties;
import org.zalando.nakadi.plugin.api.authz.AuthorizationService;
import org.zalando.nakadi.plugin.api.authz.AuthorizationServiceFactory;
import org.zalando.nakadi.plugin.api.exceptions.PluginException;

/**
 * Creates {@link FileAuthorizationService} based on provided JSON configuration
 * file.
 * <p>
 * Configuration file example:
 * <p>
 * 
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
        return new FileAuthorizationService();
    }
}
