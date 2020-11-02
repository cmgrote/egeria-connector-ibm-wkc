/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright 2020 IBM Corp. All Rights Reserved. */
package com.ibm.watson.data.egeria.integrationconnector;

import org.odpi.openmetadata.frameworks.connectors.properties.beans.ConnectorType;
import org.odpi.openmetadata.governanceservers.integrationdaemonservices.connectors.IntegrationConnectorProvider;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditingComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * WKCIntegrationConnectorProvider defines the setup of the connector for exchanging metadata between IBM Watson
 * Knowledge Catalog and open metadata.
 * <br><br>
 * The WKCIntegrationConnectorProvider must initialize ConnectorProviderBase with the Java class
 * name of the OMRS Connector implementation (by calling super.setConnectorClassName(className)).
 * Then the connector provider will work.
 * <br><br>
 * The permitted configuration options include:
 * <ul>
 *     <li>disableSSL - a boolean indicating whether to disable SSL certificate verification (true) or not (false)
 *     for communication with the Watson Knowledge Catalog host. By default, SSL certificate verification will be done
 *     (this will default to 'false'), but if you are using self-signed certificates in Cloud Pak for Data you likely
 *     want to set this to 'true', at least for quick testing purposes.</li>
 * </ul>
 */
public class WKCIntegrationConnectorProvider extends IntegrationConnectorProvider {

    static final String  connectorTypeGUID = "fc021d3e-11b6-4f10-8b14-9bc4281f6a45";
    static final String  connectorTypeName = "WKC Integration Connector";
    static final String  connectorTypeDescription = "Connector supports the exchange of metadata between IBM Watson Knowledge Catalog and open metadata.";

    public static final String DISABLE_SSL = "disableSSL";

    /**
     * Constructor used to initialize the ConnectorProviderBase with the Java class name of the specific
     * integration service implementation.
     */
    public WKCIntegrationConnectorProvider()
    {
        Class<?> connectorClass = WKCIntegrationConnector.class;

        super.setConnectorClassName(connectorClass.getName());

        ConnectorType connectorType = new ConnectorType();
        connectorType.setType(ConnectorType.getConnectorTypeType());
        connectorType.setGUID(connectorTypeGUID);
        connectorType.setQualifiedName(connectorTypeName);
        connectorType.setDisplayName(connectorTypeName);
        connectorType.setDescription(connectorTypeDescription);
        connectorType.setConnectorProviderClassName(this.getClass().getName());

        List<String> recognizedConfigurationProperties = new ArrayList<>();
        recognizedConfigurationProperties.add(DISABLE_SSL);
        connectorType.setRecognizedConfigurationProperties(recognizedConfigurationProperties);

        super.connectorTypeBean = connectorType;
        super.setConnectorComponentDescription(OMRSAuditingComponent.INTEGRATION_CONNECTOR);
    }

}
