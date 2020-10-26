package org.crazycoder.nakadiauthzfileplugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.zalando.nakadi.plugin.api.authz.AuthorizationAttribute;
import org.zalando.nakadi.plugin.api.authz.AuthorizationService;
import org.zalando.nakadi.plugin.api.authz.Resource;
import org.zalando.nakadi.plugin.api.authz.Subject;
import org.zalando.nakadi.plugin.api.exceptions.AuthorizationInvalidException;
import org.zalando.nakadi.plugin.api.exceptions.OperationOnResourceNotPermittedException;
import org.zalando.nakadi.plugin.api.exceptions.PluginException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;


class ServiceName implements Subject {
    public ServiceName() {

    }
    
    public String getName() {
       return "nakadi-authz-plugin";
   }
}


/**
 * Parses provided configuration and prepares the data to be retrieved from the
 * map by authz operations.
 *
 * @author Andrey Dyachkov
 */
public class FileAuthorizationService implements AuthorizationService {

    public FileAuthorizationService() {
    }

    @Override
    public boolean isAuthorized(Operation operation, Resource resource) throws PluginException {
        return true;
        // if (operation == Operation.VIEW) {
        //     return true;
        // }

        // if (resource == null || resource.getAuthorization() == null) {
        //     return true;
        // }

        // Map<String, List<AuthorizationAttribute>> authorization = resource.getAuthorization();
        // for (Map.Entry<String, List<AuthorizationAttribute>> entry : authorization.entrySet()) {
        //     for (AuthorizationAttribute aa : entry.getValue()) {
        //         // Map<String, String> dataTypeValue = mockedData.get(Operation.valueOf(entry.getKey().toUpperCase()));
        //         // if (dataTypeValue.get(aa.getDataType()).equals(aa.getValue())) {
        //         //     return true;
        //         // }
        //     }
        // }

        // return false;
    }

    @Override
    public void isAuthorizationForResourceValid(Resource resource)
            throws PluginException, AuthorizationInvalidException, OperationOnResourceNotPermittedException {

        // if (resource == null || resource.getAuthorization() == null) {
        //     return;
        // }

        // Map<String, List<AuthorizationAttribute>> authorization = resource.getAuthorization();
        // for (Map.Entry<String, List<AuthorizationAttribute>> entry : authorization.entrySet()) {
        //     for (AuthorizationAttribute aa : entry.getValue()) {

        //         // Map<String, String> dataTypeValue = mockedData.get(Operation.valueOf(entry.getKey().toUpperCase()));
        //         // if (!dataTypeValue.get(aa.getDataType()).equals(aa.getValue())) {
        //         //     throw new OperationOnResourceNotPermittedException(
        //         //             "Operation is not permitted " + resource.getName());
        //         // }

        //     }
        // }

    }

    @Override
    public List<Resource> filter(List<Resource> input) throws PluginException {
        return Collections.emptyList();
    }

    @Override
    public Optional<Subject> getSubject() throws PluginException {
        Optional<Subject> opt = Optional.of(new ServiceName());
        return opt;
    }

    public String getToken() {
        String token = null;
        OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            token = ((OAuth2AuthenticationDetails) authentication.getDetails()).getTokenValue();
        }
        return token;
    }


    public String getUsername() {
        try {
            return Optional.of(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication)
                    .map(authentication -> (OAuth2Authentication) authentication)
                    .map(OAuth2Authentication::getUserAuthentication)
                    .map(Authentication::getDetails)
                    .map(details -> (Map) details)
                    .map(details -> details.get("username"))
                    .map(username -> (String) username)
                    .orElse("");
        } catch (final ClassCastException e) {
            return "";
        }
    }
}
