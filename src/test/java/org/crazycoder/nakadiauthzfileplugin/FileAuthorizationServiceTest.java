package org.crazycoder.nakadiauthzfileplugin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zalando.nakadi.plugin.api.authz.AuthorizationAttribute;
import org.zalando.nakadi.plugin.api.authz.AuthorizationService;
import org.zalando.nakadi.plugin.api.authz.Resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FileAuthorizationServiceTest {

    private final AuthorizationAttribute admin = new AuthorizationAttribute() {
        @Override
        public String getDataType() {
            return "service";
        }

        @Override
        public String getValue() {
            return "nakadi-mock-admin-service";
        }

    };
    private final AuthorizationAttribute write = new AuthorizationAttribute() {
        @Override
        public String getDataType() {
            return "service";
        }

        @Override
        public String getValue() {
            return "nakadi-mock-writer-service";
        }

    };
    private final AuthorizationAttribute read = new AuthorizationAttribute() {
        @Override
        public String getDataType() {
            return "service";
        }

        @Override
        public String getValue() {
            return "nakadi-mock-reader-service";
        }

    };
    private final Map<String, List<AuthorizationAttribute>> authorization = new HashMap<>();

    public FileAuthorizationServiceTest() {
        authorization.put("admin", Collections.singletonList(admin));
        authorization.put("write", Collections.singletonList(write));
        authorization.put("read", Collections.singletonList(read));
    }

    @Test
    public void testShouldAuthorizeDefinedPermissions() {
        FileAuthorizationService service = (FileAuthorizationService) new FileAuthorizationServiceFactory()
                .init(name -> "testdata/authz.json");
        Assertions.assertTrue(
                service.isAuthorized(AuthorizationService.Operation.ADMIN, new TestResource(authorization)));
        Assertions.assertTrue(
                service.isAuthorized(AuthorizationService.Operation.ADMIN, new TestResource(authorization)));

        Assertions.assertTrue(
                service.isAuthorized(AuthorizationService.Operation.WRITE, new TestResource(authorization)));
        Assertions.assertTrue(
                service.isAuthorized(AuthorizationService.Operation.WRITE, new TestResource(authorization)));

        Assertions
                .assertTrue(service.isAuthorized(AuthorizationService.Operation.READ, new TestResource(authorization)));
        Assertions
                .assertTrue(service.isAuthorized(AuthorizationService.Operation.READ, new TestResource(authorization)));
    }

    @Test
    public void testIsAuthorizationForResourceValid() {
        FileAuthorizationService service = (FileAuthorizationService) new FileAuthorizationServiceFactory()
                .init(name -> "testdata/authz.json");
        service.isAuthorizationForResourceValid(new TestResource(authorization));
    }

    @Test
    public void testShouldFailIfNotDefined() {
        FileAuthorizationService service = (FileAuthorizationService) new FileAuthorizationServiceFactory()
                .init(name -> "testdata/authz.json");
        AuthorizationAttribute aa = new AuthorizationAttribute() {
            @Override
            public String getDataType() {
                return "service";
            }

            @Override
            public String getValue() {
                return "some-other-service";
            }

        };
        Map<String, List<AuthorizationAttribute>> authorization = new HashMap<>();
        authorization.put("write", Collections.singletonList(aa));
        Assertions.assertFalse(
                service.isAuthorized(AuthorizationService.Operation.WRITE, new TestResource(authorization)));
    }

    @Test
    public void testShouldLoadFromEnvVarAndAuthorizeAdmin() {
        FileAuthorizationService service = (FileAuthorizationService) new FileAuthorizationServiceFactory()
                .init(name -> "testdata/authz-2.json");
        AuthorizationAttribute aa = new AuthorizationAttribute() {
            @Override
            public String getDataType() {
                return "service";
            }

            @Override
            public String getValue() {
                return "nakadi-mock-service";
            }

        };
        Map<String, List<AuthorizationAttribute>> authorization = new HashMap<>();
        authorization.put("admin", Collections.singletonList(aa));
        Assertions.assertTrue(
                service.isAuthorized(AuthorizationService.Operation.ADMIN, new TestResource(authorization)));
    }

    @Test
    public void testShouldLoadConfigEvenWithNoWritersAndReaders() {
        FileAuthorizationService service = (FileAuthorizationService) new FileAuthorizationServiceFactory()
                .init(name -> "testdata/authz-3.json");
        AuthorizationAttribute aa = new AuthorizationAttribute() {
            @Override
            public String getDataType() {
                return "user";
            }

            @Override
            public String getValue() {
                return "nakadi-mock-admin-user";
            }

        };
        Map<String, List<AuthorizationAttribute>> authorization = new HashMap<>();
        authorization.put("admin", Collections.singletonList(aa));
        Assertions.assertTrue(
                service.isAuthorized(AuthorizationService.Operation.ADMIN, new TestResource(authorization)));
    }

    private class TestResource implements Resource<String> {

        private final Map<String, List<AuthorizationAttribute>> authorization;

        public TestResource(Map<String, List<AuthorizationAttribute>> authorization) {
            this.authorization = authorization;
        }

        @Override
        public String getName() {
            return "name-test";
        }

        @Override
        public String getType() {
            return "type-test";
        }

        @Override
        public Optional<List<AuthorizationAttribute>> getAttributesForOperation(
                AuthorizationService.Operation operation) {
            return Optional.empty();
        }

        @Override
        public String get() {
            return null;
        }

        @Override
        public Map<String, List<AuthorizationAttribute>> getAuthorization() {
            return authorization;
        }
    }
}
