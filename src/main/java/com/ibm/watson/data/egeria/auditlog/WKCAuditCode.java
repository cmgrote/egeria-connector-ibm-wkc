/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright 2020 IBM Corp. All Rights Reserved. */
package com.ibm.watson.data.egeria.auditlog;

import org.odpi.openmetadata.frameworks.auditlog.messagesets.AuditLogMessageDefinition;
import org.odpi.openmetadata.frameworks.auditlog.messagesets.AuditLogMessageSet;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLogRecordSeverity;

/**
 * The WKCAuditCode is used to define the message content for the OMRS Audit Log.
 *
 * The 5 fields in the enum are:
 * <ul>
 *     <li>Log Message Id - to uniquely identify the message</li>
 *     <li>Severity - is this an event, decision, action, error or exception</li>
 *     <li>Log Message Text - includes placeholder to allow additional values to be captured</li>
 *     <li>Additional Information - further parameters and data relating to the audit message (optional)</li>
 *     <li>SystemAction - describes the result of the situation</li>
 *     <li>UserAction - describes how a user should correct the situation</li>
 * </ul>
 */
public enum WKCAuditCode implements AuditLogMessageSet {

    INTEGRATION_SERVICE_STARTING("INTG-SVC-WKC-0001",
            OMRSAuditLogRecordSeverity.INFO,
            "The IBM Watson Knowledge Catalog integration service is starting a new server instance",
            "The local server has started up a new instance of the IBM Watson Knowledge Catalog integration service.",
            "No action is required.  This is part of the normal operation of the service."),
    CONNECTING_TO_WKC("INTG-SVC-WKC-0002",
            OMRSAuditLogRecordSeverity.INFO,
            "The IBM Watson Knowledge Catalog integration service is attempting to connect to WKC at {0}",
            "The local server is attempting to connect to the IBM Watson Knowledge Catalog server.",
            "No action is required.  This is part of the normal operation of the service."),
    CONNECTED_TO_WKC("INTG-SVC-WKC-0003",
            OMRSAuditLogRecordSeverity.INFO,
            "The IBM Watson Knowledge Catalog integration service has successfully connected to WKC at {0}",
            "The local server has successfully connected to the IBM Watson Knowledge Catalog server.",
            "No action is required.  This is part of the normal operation of the service."),
    INTEGRATION_SERVICE_STARTED("INTG-SVC-WKC-0004",
            OMRSAuditLogRecordSeverity.INFO,
            "The IBM Watson Knowledge Catalog service has started a new instance.",
            "The local server has completed startup of a new instance.",
            "No action is required.  This is part of the normal operation of the service."),
    INTEGRATION_SERVICE_SHUTDOWN("INTG-SVC-WKC-0005",
            OMRSAuditLogRecordSeverity.INFO,
            "The IBM Watson Knowledge Catalog integration service has shutdown its instance.",
            "The local server has requested shut down of a IBM Watson Knowledge Catalog integration service instance.",
            "No action is required.  This is part of the normal operation of the service."),
    FAILED_DISCONNECT("INTG-SVC-WKC-0006",
            OMRSAuditLogRecordSeverity.EXCEPTION,
            "The IBM Watson Knowledge Catalog connector failed to disconnect",
            "The local server failed to disconnect from IBM Watson Knowledge Catalog.",
            "Investigate the logs for additional information and clear session manually in WKC if needed.")
    ;

    private String logMessageId;
    private OMRSAuditLogRecordSeverity severity;
    private String logMessage;
    private String systemAction;
    private String userAction;


    /**
     * The constructor for OMRSAuditCode expects to be passed one of the enumeration rows defined in
     * OMRSAuditCode above.   For example:
     * <p>
     * OMRSAuditCode   auditCode = OMRSAuditCode.SERVER_NOT_AVAILABLE;
     * <p>
     * This will expand out to the 4 parameters shown below.
     *
     * @param messageId    - unique Id for the message
     * @param severity     - the severity of the message
     * @param message      - text for the message
     * @param systemAction - description of the action taken by the system when the condition happened
     * @param userAction   - instructions for resolving the situation, if any
     */
    WKCAuditCode(String messageId, OMRSAuditLogRecordSeverity severity, String message,
                     String systemAction, String userAction) {
        this.logMessageId = messageId;
        this.severity = severity;
        this.logMessage = message;
        this.systemAction = systemAction;
        this.userAction = userAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditLogMessageDefinition getMessageDefinition() {
        return new AuditLogMessageDefinition(logMessageId,
                severity,
                logMessage,
                systemAction,
                userAction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditLogMessageDefinition getMessageDefinition(String ...params) {
        AuditLogMessageDefinition messageDefinition = new AuditLogMessageDefinition(logMessageId,
                severity,
                logMessage,
                systemAction,
                userAction);
        messageDefinition.setMessageParameters(params);
        return messageDefinition;
    }

}
