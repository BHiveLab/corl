# CORL

Corl enables access to resources protected by AZURE AD using OAuth

Currently corl only supports password authentication and configuration is maintained in a file.

### Building
```sh
sbt clean compile stage
```

### Running
```sh
bin/corl -Dconfig.file=application.conf "https://to.some.protected.resource"
```
