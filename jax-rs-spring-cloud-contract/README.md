# What?

This example project will help you generate and run integration tests for JAX-RS REST resources 
using SpringCloudContract.


# How

- add spring cloud contract and rest-easy dependencies in pom.xml
- add and configure spring-cloud-contract-maven-plugin configuration to the pom.xml
- add a base test class (or more when required) 
- add the contracts


# Pros

- your customers/clients can help define the behavior of your service
- you don't have to write integration tests
- stub generation

# Cons

- might not be using the same application container as in prod

- @CompressData : just one resource compressed the data so we need a nother WebTarget -> another base class
  - different base classes are required to support the different http client configurations
