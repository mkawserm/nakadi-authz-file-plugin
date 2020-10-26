package org.crazycoder.nakadiauthzfileplugin;

import org.zalando.nakadi.plugin.api.SystemProperties;
import org.zalando.nakadi.plugin.api.authz.AuthorizationService;
import org.zalando.nakadi.plugin.api.authz.AuthorizationServiceFactory;


public class FileAuthorizationServiceFactory implements AuthorizationServiceFactory {

    @Override
    public AuthorizationService init(SystemProperties properties) {
        return new FileAuthorizationService();
    }
}
