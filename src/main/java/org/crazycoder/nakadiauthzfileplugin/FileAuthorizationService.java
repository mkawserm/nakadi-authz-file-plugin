package org.crazycoder.nakadiauthzfileplugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.zalando.nakadi.plugin.api.authz.AuthorizationAttribute;
import org.zalando.nakadi.plugin.api.authz.AuthorizationService;
import org.zalando.nakadi.plugin.api.authz.Resource;
import org.zalando.nakadi.plugin.api.authz.Subject;
import org.zalando.nakadi.plugin.api.exceptions.AuthorizationInvalidException;
import org.zalando.nakadi.plugin.api.exceptions.OperationOnResourceNotPermittedException;
import org.zalando.nakadi.plugin.api.exceptions.PluginException;

/**
 * Parses provided configuration and prepares the data to be retrieved from the
 * map by authz operations.
 *
 * @author Andrey Dyachkov
 */
public class FileAuthorizationService implements AuthorizationService {

    private final Map<Operation, Map<String, String>> mockedData;

    public FileAuthorizationService(JsonNode mockedAuthz) {
        mockedData = new HashMap<>();
        buildMockedAuthorization(mockedAuthz, "admins", Operation.ADMIN);
        buildMockedAuthorization(mockedAuthz, "writers", Operation.WRITE);
        buildMockedAuthorization(mockedAuthz, "readers", Operation.READ);
    }

    private void buildMockedAuthorization(JsonNode mockedAuthz, String permission, Operation operation) {
        ArrayNode arrayNode = (ArrayNode) mockedAuthz.get(permission);
        if (arrayNode == null) {
            return;
        }
        Map<String, String> dataTypeValue = new HashMap<>();
        Iterator<JsonNode> it = arrayNode.elements();
        while (it.hasNext()) {
            JsonNode jsonNode = it.next();
            String dataType = jsonNode.get("data_type").asText(null);
            if (dataType != null) {
                dataTypeValue.put(dataType, jsonNode.get("value").asText(null));
            }
        }
        if (!dataTypeValue.isEmpty()) {
            mockedData.put(operation, dataTypeValue);
        }
    }

    @Override
    public boolean isAuthorized(Operation operation, Resource resource) throws PluginException {
        if (operation == Operation.VIEW) {
            return true;
        }

        Map<String, String> dataTypeName = mockedData.get(operation);
        if (dataTypeName == null) {
            return false;
        }

        if (resource == null || resource.getAuthorization() == null) {
            return true;
        }

        Map<String, List<AuthorizationAttribute>> authorization = resource.getAuthorization();
        for (Map.Entry<String, List<AuthorizationAttribute>> entry : authorization.entrySet()) {
            for (AuthorizationAttribute aa : entry.getValue()) {
                Map<String, String> dataTypeValue = mockedData.get(Operation.valueOf(entry.getKey().toUpperCase()));
                if (dataTypeValue.get(aa.getDataType()).equals(aa.getValue())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void isAuthorizationForResourceValid(Resource resource)
            throws PluginException, AuthorizationInvalidException, OperationOnResourceNotPermittedException {
        if (resource == null || resource.getAuthorization() == null) {
            return;
        }

        Map<String, List<AuthorizationAttribute>> authorization = resource.getAuthorization();
        for (Map.Entry<String, List<AuthorizationAttribute>> entry : authorization.entrySet()) {
            for (AuthorizationAttribute aa : entry.getValue()) {
                Map<String, String> dataTypeValue = mockedData.get(Operation.valueOf(entry.getKey().toUpperCase()));
                if (!dataTypeValue.get(aa.getDataType()).equals(aa.getValue())) {
                    throw new OperationOnResourceNotPermittedException(
                            "Operation is not permitted " + resource.getName());
                }
            }
        }
    }

    @Override
    public List<Resource> filter(List<Resource> input) throws PluginException {
        return Collections.emptyList();
    }

    @Override
    public Optional<Subject> getSubject() throws PluginException {
        return Optional.empty();
    }
}
