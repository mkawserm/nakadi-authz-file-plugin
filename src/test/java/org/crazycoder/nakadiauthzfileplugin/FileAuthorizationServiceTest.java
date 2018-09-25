package org.crazycoder.nakadiauthzfileplugin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zalando.nakadi.plugin.api.authz.AuthorizationAttribute;
import org.zalando.nakadi.plugin.api.authz.AuthorizationService;
import org.zalando.nakadi.plugin.api.authz.Resource;

import java.util.List;
import java.util.Optional;

public class FileAuthorizationServiceTest {

    @Test
    public void testShouldAuthorizeDefinedPermissions() {
        FileAuthorizationService service = (FileAuthorizationService) new FileAuthorizationServiceFactory().init(name -> "testdata/authz.json");
        Assertions.assertTrue(service.isAuthorized(AuthorizationService.Operation.ADMIN,
                new TestResource("service", "nakadi-mock-admin-service")));
        Assertions.assertTrue(service.isAuthorized(AuthorizationService.Operation.ADMIN,
                new TestResource("user", "nakadi-mock-admin-user")));

        Assertions.assertTrue(service.isAuthorized(AuthorizationService.Operation.WRITE,
                new TestResource("service", "nakadi-mock-writer-service")));
        Assertions.assertTrue(service.isAuthorized(AuthorizationService.Operation.WRITE,
                new TestResource("user", "nakadi-mock-writer-user")));

        Assertions.assertTrue(service.isAuthorized(AuthorizationService.Operation.READ,
                new TestResource("service", "nakadi-mock-reader-service")));
        Assertions.assertTrue(service.isAuthorized(AuthorizationService.Operation.READ,
                new TestResource("user", "nakadi-mock-reader-user")));
    }

    @Test
    public void testShouldFailIfNotDefined() {
        FileAuthorizationService service = (FileAuthorizationService) new FileAuthorizationServiceFactory().init(name -> "testdata/authz.json");
        Assertions.assertFalse(service.isAuthorized(AuthorizationService.Operation.WRITE,
                new TestResource("service", "some-other-service")));
    }

    @Test
    public void testShouldLoadFromEnvVarAndAuthorizeAdmin() {
        FileAuthorizationService service = (FileAuthorizationService) new FileAuthorizationServiceFactory().init(name -> "testdata/authz-2.json");
        Assertions.assertTrue(service.isAuthorized(AuthorizationService.Operation.ADMIN,
                new TestResource("service", "nakadi-mock-service")));
    }

    @Test
    public void testShouldLoadConfigEvenWithNoWritersAndReaders() {
        FileAuthorizationService service = (FileAuthorizationService) new FileAuthorizationServiceFactory().init(name -> "testdata/authz-3.json");
        Assertions.assertTrue(service.isAuthorized(AuthorizationService.Operation.ADMIN,
                new TestResource("user", "nakadi-mock-admin-user")));
    }

    private class TestResource implements Resource {

        private final String type;
        private final String name;

        public TestResource(String type, String name) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public Optional<List<AuthorizationAttribute>> getAttributesForOperation(AuthorizationService.Operation operation) {
            return Optional.empty();
        }
    }
}
