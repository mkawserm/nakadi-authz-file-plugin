package org.crazycoder.nakadiauthzfileplugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.zalando.nakadi.plugin.api.PluginException;
import org.zalando.nakadi.plugin.api.authz.AuthorizationAttribute;
import org.zalando.nakadi.plugin.api.authz.AuthorizationService;
import org.zalando.nakadi.plugin.api.authz.Resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Parses provided configuration and prepares the data to be retrieved from the map by authz operations.
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
        Map<String, String> dataTypeName = mockedData.get(operation);
        if (dataTypeName == null) {
            return false;
        }

        return dataTypeName.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(resource.getType()) && entry.getValue().equals(resource.getName()));
    }

    @Override
    public boolean isAuthorizationAttributeValid(AuthorizationAttribute attribute) throws PluginException {
        return mockedData.values().stream()
                .flatMap(stringStringMap -> stringStringMap.entrySet().stream())
                .filter(entry -> entry.getKey().equals(attribute.getDataType()))
                .anyMatch(entry -> entry.getValue().equals(attribute.getValue()));
    }
}
