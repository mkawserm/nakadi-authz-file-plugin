# Overview
File based implementation of authz plugin https://github.com/zalando-nakadi/nakadi-plugin-api for https://github.com/zalando/nakadi

# Usage
Export path to file with permissions through env var: 
```
export NAKADI_AUTHZ_FILE_PLUGIN_AUTHZ_FILE=authz.json
``` 
from where to read authz permissions. 

Example file `authz.json`:
```
{
  "admins": [
    {
      "data_type": "user",
      "value": "nakadi-mock-admin-user"
    },
    {
      "data_type": "service",
      "value": "nakadi-mock-admin-service"
    }
  ],
  "writers": [
    {
      "data_type": "user",
      "value": "nakadi-mock-writer-user"
    },
    {
      "data_type": "service",
      "value": "nakadi-mock-writer-service"
    }
  ],
  "readers": [
    {
      "data_type": "user",
      "value": "nakadi-mock-reader-user"
    },
    {
      "data_type": "service",
      "value": "nakadi-mock-reader-service"
    }
  ]
}
```

Export Nakadi authz factory:
```
export NAKADI_PLUGINS_AUTHZ_FACTORY=org.crazycoder.nakadiauthzfileplugin.FileAuthorizationServiceFactory
```
