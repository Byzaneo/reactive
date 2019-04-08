# TEST
[![Build Status](https://travis-ci.com/my-org/my-repo.svg?branch=master)](https://travis-ci.com/my-org/my-repo)
[![codecov](https://codecov.io/gh/my-org/my-repo/branch/master/graph/badge.svg)](https://codecov.io/gh/my-org/my-repo)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 
See deployment for notes on how to deploy the project on a live system.

### Prerequisites

Things you need to build and run this service:
- Java 12+
- Maven 3.6+

### Installing

A step by step series of examples that tell you how to get a development env running

Say what the step will be

```
mvn clean install
```

## Running the tests

```
mvn clean test
```

with Jacoco coverage

```
mvn clean test -P coverage
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

### Profiles

* dev: development profile. 
* prod: production packaging et runtime.

## Authors

* Tester 

## License

* [Apache 2.0](./LICENSE)