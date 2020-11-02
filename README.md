<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright 2020 IBM Corp. All Rights Reserved. -->

[![GitHub](https://img.shields.io/github/license/IBM/egeria-connector-ibm-wkc)](LICENSE)

# IBM Watson Knowledge Catalog Connector

[IBM Watson Knowledge Catalog](https://www.ibm.com/uk-en/cloud/watson-knowledge-catalog) is a
commercially-available metadata catalogue, commonly referred to simply as "WKC". This repository contains an Egeria
connector for that catalogue.

[Only a subset of the overall Open Metadata Types are currently implemented](docs/mappings/README.md).

## How it works

The IBM WKC Connector works through a combination of the following:

- IBM WKC's REST API, itself abstracted through the [IBM Watson Data API clients](https://github.com/IBM/watson-data-api-clients)
- Egeria's Integration Daemon API and services

## Getting started

### TL;DR

The quick version:

1. Download the latest IBM WKC connector from: https://github.com/IBM/egeria-connector-ibm-wkc/packages
1. Download the latest Egeria core from: https://odpi.jfrog.io/odpi/egeria-snapshot-local/org/odpi/egeria/server-chassis-spring/2.5-SNAPSHOT/server-chassis-spring-2.5-SNAPSHOT.jar
1. Rename the downloaded Egeria core file to `egeria-server-chassis-spring.jar`.
1. Run the following command to start Egeria from the command-line, waiting for the final line of output indicating the
    server is running and ready for configuration:
    ```bash
    $ export STRICT_SSL=false
    $ java -Dloader.path=. -jar egeria-server-chassis-spring.jar
     ODPi Egeria
        ____   __  ___ ___    ______   _____                                 ____   _         _     ___
       / __ \ /  |/  //   |  / ____/  / ___/ ___   ____ _   __ ___   ____   / _  \ / / __    / /  / _ /__   ____ _  _
      / / / // /|_/ // /| | / / __    \__ \ / _ \ / __/| | / // _ \ / __/  / /_/ // //   |  / _\ / /_ /  | /  _// || |
     / /_/ // /  / // ___ |/ /_/ /   ___/ //  __// /   | |/ //  __// /    /  __ // // /  \ / /_ /  _// / // /  / / / /
     \____//_/  /_//_/  |_|\____/   /____/ \___//_/    |___/ \___//_/    /_/    /_/ \__/\//___//_/   \__//_/  /_/ /_/
    
     :: Powered by Spring Boot (v2.2.2.RELEASE) ::
    
    
    No OMAG servers listed in startup configuration
    Thu Jan 02 11:30:10 GMT 2020 OMAG server platform ready for more configuration
    ```
1. In another shell / command-line window, run the following commands to configure Egeria and startup its services --
    making sure to replace the hostnames and port numbers with those relevant to your own environment (`localhost:9092`
    for your own Kafka bus, `wkc` with the hostname of your WKC or Cloud Pak for Data ingress server, `user1` with
    the username for your WKC environment, and `pass1` with the password for your WKC environment):
    ```bash
    $ curl -k -X POST -H "Content-Type: application/json" --data '{"producer":{"bootstrap.servers":"localhost:9092"},"consumer":{"bootstrap.servers":"localhost:9092"}}' "https://localhost:9443/open-metadata/admin-services/users/admin/servers/myserver/event-bus?connectorProvider=org.odpi.openmetadata.adapters.eventbus.topic.kafka.KafkaOpenMetadataTopicProvider&topicURLRoot=OMRSTopic"
    $ curl -k -X POST "https://localhost:9443/open-metadata/admin-services/users/admin/servers/myserver/cohorts/mycohort"
    $ curl -k -X POST -H "Content-Type: application/json" --data '{"class":"Connection","connectorType":{"class":"ConnectorType","connectorProviderClassName":"org.odpi.egeria.connectors.ibm.igc.repositoryconnector.IGCOMRSRepositoryConnectorProvider"},"endpoint":{"class":"Endpoint","address":"infosvr:9446","protocol":"https"},"userId":"isadmin","clearPassword":"isadmin","configurationProperties":{"defaultZones":["default"]}}' "https://localhost:9443/open-metadata/admin-services/users/admin/servers/myserver/local-repository/mode/repository-proxy/connection"
    $ curl -k -X POST "https://localhost:9443/open-metadata/admin-services/users/admin/servers/myserver/instance"
    ```

### Obtain the connector

You can either download the latest released or snapshot version of the connector directly from GitHub, or build the
connector yourself. In both cases, once you have the jar file for the connector
(`egeria-connector-ibm-wkc-VERSION-jar-with-dependencies.jar`) this needs to be copied to a
location where it can be run alongside the OMAG Server Platform from Egeria core itself. For example, this could be
placing the file into the `/lib` directory as `/lib/egeria-connector-ibm-wkc-VERSION-jar-with-dependencies.jar`.

#### Download from GitHub

To download a pre-built version of the connector, use either of the following URLs (depending on whether you want an
officially-released version or the latest snapshot):

- Release: https://github.com/IBM/egeria-connector-ibm-wkc/releases
- Snapshot: https://github.com/IBM/egeria-connector-ibm-wkc/packages

#### Building the connector yourself

Alternatively, you can build the connector yourself. To do this, you'll need to first clone this repository and then
build through Maven using `mvn clean install`. After building, the connector is available as:

```text
target/egeria-connector-ibm-wkc-VERSION-jar-with-dependencies.jar
```

### Configure security

There are [multiple options to configure the security of your environment](docs/security/README.md) for this connector,
but this must be done prior to starting up the connector itself (step below).

If you simply want to test things out, and are not concerned about security, the simplest (but most insecure) option
is to set the environment variable `STRICT_SSL` to `false` using something like the following prior to starting
up the OMAG Server Platform:

```bash
export STRICT_SSL=false
```

Note that this will disable all certificate validation for SSL connections made between Egeria and your WKC
environment, so is inherently insecure.

### Startup the OMAG Server Platform

You can startup the OMAG Server Platform with this connector ready-to-be-configured by running the following
(this example assumes you've placed the connector jar file in the `/lib` directory, if you are using a different
location simply change the `-Dloader.path=` to point to the location you have used):

```bash
$ java -Dloader.path=/lib -jar server-chassis-spring-VERSION.jar
```

(This command will startup the OMAG Server Platform, including all libraries
in the `/lib` directory as part of the classpath of the OMAG Server Platform.)

### Configure the WKC connector

You will need to configure the OMAG Server Platform as follows (order is important) to make use of the WKC connector.
For example payloads and endpoints, see the [Postman samples](samples).

1. Configure your event bus for Egeria, by POSTing a payload like the following (replace the `localhost:9092` with the
    hostname and port number where your Kafka bus is running, and assuming you are running the OMAG Server Platform
    locally at its default port of `9443`):

    ```json
    {
        "producer": {
            "bootstrap.servers":"localhost:9092"
        },
        "consumer": {
            "bootstrap.servers":"localhost:9092"
        }
    }
    ```

    to:

    ```
    POST https://localhost:9443/open-metadata/admin-services/users/admin/servers/myserver/event-bus?connectorProvider=org.odpi.openmetadata.adapters.eventbus.topic.kafka.KafkaOpenMetadataTopicProvider&topicURLRoot=OMRSTopic
    ```

1. Configure the cohort, by POSTing something like the following:

    ```
    POST https://localhost:9443/open-metadata/admin-services/users/admin/servers/myserver/cohorts/mycohort
    ```

1. Configure the WKC connector, by POSTing a payload like the following, replacing the `{{wkc_host}}` with the hostname
    of your WKC instance, `{{wkc_user}}` with the username of a user able to access the REST API, and `{{wkc_password}}`
    with the password for that user:

    ```json
    {
        "class": "Connection",
        "connectorType": {
            "class": "ConnectorType",
            "connectorProviderClassName": "com.ibm.watson.data.egeria.integrationconnector.WKCIntegrationConnectorProvider"
        },
        "endpoint": {
            "class": "Endpoint",
            "address": "{{wkc_host}}",
            "protocol": "https"
        },
        "userId": "{{wkc_user}}",
        "clearPassword": "{{wkc_password}}"
    }
    ```

    to:

    ```
    POST https://localhost:9443/open-metadata/admin-services/users/admin/servers/myserver/local-repository/mode/repository-proxy/connection
    ```

    To operate, the WKC user credentials must have (at a minimum) the following roles within Watson Knowledge Catalog:
    `Suite User` and `Information Governance Catalog User`. (These are both read-only, non-administrative roles.)

    You can optionally also provide a list of zone names that will be used as default zones for all Assets retrieved
    from IGC through the proxy (in the example above this is a single zone called `default`).

    Note that you also need to provide the `connectorProvider` parameter, set to the name of the IGC
    connectorProvider class (value as given above).

1. The connector should now be configured, and you should now be able to start the instance by POSTing something like
    the following:

   ```
   POST https://localhost:9443/open-metadata/admin-services/users/admin/servers/myserver/instance
   ```

After following these instructions, your WKC instance will be participating in the Egeria cohort. For those objects
supported by the connector, any new instances or updates to existing instances should result in that metadata
automatically being communicated out to the rest of the cohort.

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright 2020 IBM Corp. All Rights Reserved.

