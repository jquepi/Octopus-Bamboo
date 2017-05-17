# Octopus-Bamboo
Octopus plugin for Bamboo

https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK

# Testing
Run integration test against a mock Octopus Deploy REST API with the command:
```
atlas-integration-test --jvmargs "-Dspring.profiles.active=test"
```

To run the integration tests against a local copy of Octopus Deploy (http://localhost:8065), 
use the following command:
```
atlas-integration-test --jvmargs "-DapiKey=OctopusDeployAPIKey"
```
