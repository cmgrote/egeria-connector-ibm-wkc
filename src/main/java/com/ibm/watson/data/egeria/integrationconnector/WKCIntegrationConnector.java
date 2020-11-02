/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright 2020 IBM Corp. All Rights Reserved. */
package com.ibm.watson.data.egeria.integrationconnector;

import com.ibm.watson.data.client.ApiClient;
import com.ibm.watson.data.client.api.*;
import com.ibm.watson.data.client.model.Category;
import com.ibm.watson.data.egeria.auditlog.WKCAuditCode;
import com.ibm.watson.data.egeria.auditlog.WKCErrorCode;
import org.odpi.openmetadata.frameworks.connectors.ffdc.ConnectorCheckedException;
import org.odpi.openmetadata.frameworks.connectors.properties.EndpointProperties;
import org.odpi.openmetadata.governanceservers.integrationdaemonservices.connectors.IntegrationConnectorBase;

/**
 * WKCIntegrationConnectorProvider defines the connector for exchanging metadata between IBM Watson Knowledge
 * Catalog and open metadata.
 */
public class WKCIntegrationConnector extends IntegrationConnectorBase {

    private ApiClient apiClient = null;

    private AccountManagementApi accountManagementApi = null;
    private AssetsApiV2 assetsApiV2 = null;
    private AssetTrashApiV2 assetTrashApiV2 = null;
    private AssetTypesApiV2 assetTypesApiV2 = null;
    private AuthorizationApi authorizationApi = null;
    private BusinessTermsApiV3 businessTermsApiV3 = null;
    private CatalogMembersApiV2 catalogMembersApiV2 = null;
    private CatalogsApiV2 catalogsApiV2 = null;
    private CategoriesApiV3 categoriesApiV3 = null;
    private ClassificationsApiV3 classificationsApiV3 = null;
    private ConnectionsApiV2 connectionsApiV2 = null;
    private DataAssetsApiV2 dataAssetsApiV2 = null;
    private DataClassesApiV3 dataClassesApiV3 = null;
    private DataProtectionRulesApiV3 dataProtectionRulesApiV3 = null;
    private DatasourceTypesApiV2 datasourceTypesApiV2 = null;
    private GovernanceArtifactTypesApiV3 governanceArtifactTypesApiV3 = null;
    private JobRunsApiV2 jobRunsApiV2 = null;
    private JobsApiV2 jobsApiV2 = null;
    private MonitorApi monitorApi = null;
    private PoliciesApiV3 policiesApiV3 = null;
    private ProjectsApiV2 projectsApiV2 = null;
    private ProjectsIntegrationsApiV2 projectsIntegrationsApiV2 = null;
    private ProjectsMembersApiV2 projectsMembersApiV2 = null;
    private ProjectsSettingsApiV2 projectsSettingsApiV2 = null;
    private ProjectsTokensApiV2 projectsTokensApiV2 = null;
    private ReferenceDataSetsApiV3 referenceDataSetsApiV3 = null;
    private RoleManagementApi roleManagementApi = null;
    private RulesApiV3 rulesApiV3 = null;
    private SearchApiV3 searchApiV3 = null;
    private TransactionalProjectsApiV2 transactionalProjectsApiV2 = null;
    private UserManagementApi userManagementApi = null;
    private WorkflowsApiV3 workflowsApiV3 = null;
    private WorkflowUserTasksApiV3 workflowUserTasksApiV3 = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws ConnectorCheckedException {

        super.start();
        final String methodName = "start";

        auditLog.logMessage(methodName, WKCAuditCode.INTEGRATION_SERVICE_STARTING.getMessageDefinition());
        connectToWKC();
        auditLog.logMessage(methodName, WKCAuditCode.INTEGRATION_SERVICE_STARTED.getMessageDefinition());

    }

    /**
     * Free up any resources held since the connector is no longer needed.
     */
    @Override
    public void disconnect() throws ConnectorCheckedException {
        super.disconnect();
        if (auditLog != null) {
            auditLog.logMessage("disconnect", WKCAuditCode.INTEGRATION_SERVICE_SHUTDOWN.getMessageDefinition());
        }
    }

    /**
     * Connect to the IBM Watson Knowledge Catalog host.
     * @throws ConnectorCheckedException on any error connecting to WKC
     */
    protected void connectToWKC() throws ConnectorCheckedException {

        final String methodName = "start";
        EndpointProperties endpointProperties = connectionProperties.getEndpoint();
        if (endpointProperties == null) {
            raiseConnectorCheckedException(WKCErrorCode.REST_CLIENT_FAILURE, methodName, null, "<null>");
        } else {

            String address = endpointProperties.getProtocol() + "://" + endpointProperties.getAddress();

            String username = connectionProperties.getUserId();
            String password = connectionProperties.getClearPassword();

            /*
            boolean disableSSL = false;

            // Retrieve connection details (for any extra properties -- currently none)
            Map<String, Object> configurationProperties = this.connectionBean.getConfigurationProperties();
            if (configurationProperties != null) {
                Object oDisableSSL = configurationProperties.get(WKCIntegrationConnectorProvider.DISABLE_SSL);
                if (oDisableSSL instanceof Boolean) {
                    disableSSL = (Boolean) oDisableSSL;
                }
            }
             */

            boolean successfulInit = false;

            auditLog.logMessage(methodName, WKCAuditCode.CONNECTING_TO_WKC.getMessageDefinition(address));

            // Create new REST API client (opens a new session)
            try {
                this.apiClient = new ApiClient(false);
                apiClient.setBasePath(address);
                apiClient.setCredentials(username, password);
                // Run a basic test that should work in all environments, to ensure we're actually connected
                Category uncategorized = getCategoriesApiV3().getUncategorizedCategory(null).block();
                if (uncategorized != null && uncategorized.getMetadata() != null) {
                    String name = uncategorized.getMetadata().getName();
                    successfulInit = "[uncategorized]".equals(name);
                }
            } catch (Exception e) {
                raiseConnectorCheckedException(WKCErrorCode.REST_CLIENT_FAILURE, methodName, e, "<null>");
            }

            if (!successfulInit) {
                raiseConnectorCheckedException(WKCErrorCode.REST_CLIENT_FAILURE, methodName, null, "<null>");
            }

            auditLog.logMessage(methodName, WKCAuditCode.CONNECTED_TO_WKC.getMessageDefinition(address));

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() throws ConnectorCheckedException {
        // TODO: run the refresh logic...
    }

    /**
     * Retrieve the account management API client.
     * @return AccountManagementApi
     */
    public AccountManagementApi getAccountManagementApi() {
        if (accountManagementApi == null) {
            this.accountManagementApi = new AccountManagementApi(this.apiClient);
        }
        return accountManagementApi;
    }

    /**
     * Retrieve the asset API client.
     * @return AssetsApiV2
     */
    public AssetsApiV2 getAssetsApiV2() {
        if (assetsApiV2 == null) {
            this.assetsApiV2 = new AssetsApiV2(this.apiClient);
        }
        return assetsApiV2;
    }

    /**
     * Retrieve the asset trash API client.
     * @return AssetTrashApiV2
     */
    public AssetTrashApiV2 getAssetTrashApiV2() {
        if (assetTrashApiV2 == null) {
            this.assetTrashApiV2 = new AssetTrashApiV2(this.apiClient);
        }
        return assetTrashApiV2;
    }

    /**
     * Retrieve the asset types API client.
     * @return AssetTypesApiV2
     */
    public AssetTypesApiV2 getAssetTypesApiV2() {
        if (assetTypesApiV2 == null) {
            this.assetTypesApiV2 = new AssetTypesApiV2(this.apiClient);
        }
        return assetTypesApiV2;
    }

    /**
     * Retrieve the authorization API client.
     * @return AuthorizationApi
     */
    public AuthorizationApi getAuthorizationApi() {
        if (authorizationApi == null) {
            this.authorizationApi = new AuthorizationApi(this.apiClient);
        }
        return authorizationApi;
    }

    /**
     * Retrieve the business terms API client.
     * @return BusinessTermsApiV3
     */
    public BusinessTermsApiV3 getBusinessTermsApiV3() {
        if (businessTermsApiV3 == null) {
            this.businessTermsApiV3 = new BusinessTermsApiV3(this.apiClient);
        }
        return businessTermsApiV3;
    }

    /**
     * Retrieve the catalog members API client.
     * @return CatalogMembersApiV2
     */
    public CatalogMembersApiV2 getCatalogMembersApiV2() {
        if (catalogMembersApiV2 == null) {
            this.catalogMembersApiV2 = new CatalogMembersApiV2(this.apiClient);
        }
        return catalogMembersApiV2;
    }

    /**
     * Retrieve the catalogs API client.
     * @return CatalogsApiV2
     */
    public CatalogsApiV2 getCatalogsApiV2() {
        if (catalogsApiV2 == null) {
            this.catalogsApiV2 = new CatalogsApiV2(this.apiClient);
        }
        return catalogsApiV2;
    }

    /**
     * Retrieve the categories API client.
     * @return CategoriesApiV3
     */
    public CategoriesApiV3 getCategoriesApiV3() {
        if (categoriesApiV3 == null) {
            this.categoriesApiV3 = new CategoriesApiV3(this.apiClient);
        }
        return categoriesApiV3;
    }

    /**
     * Retrieve the classifications API client.
     * @return ClassificationsApiV3
     */
    public ClassificationsApiV3 getClassificationsApiV3() {
        if (classificationsApiV3 == null) {
            this.classificationsApiV3 = new ClassificationsApiV3(this.apiClient);
        }
        return classificationsApiV3;
    }

    /**
     * Retrieve the connections API client.
     * @return ConnectionsApiV2
     */
    public ConnectionsApiV2 getConnectionsApiV2() {
        if (connectionsApiV2 == null) {
            this.connectionsApiV2 = new ConnectionsApiV2(this.apiClient);
        }
        return connectionsApiV2;
    }

    /**
     * Retrieve data assets API client.
     * @return DataAssetsApiV2
     */
    public DataAssetsApiV2 getDataAssetsApiV2() {
        if (dataAssetsApiV2 == null) {
            this.dataAssetsApiV2 = new DataAssetsApiV2(this.apiClient);
        }
        return dataAssetsApiV2;
    }

    /**
     * Retrieve data classes API client.
     * @return DataClassesApiV3
     */
    public DataClassesApiV3 getDataClassesApiV3() {
        if (dataClassesApiV3 == null) {
            this.dataClassesApiV3 = new DataClassesApiV3(this.apiClient);
        }
        return dataClassesApiV3;
    }

    /**
     * Retrieve data protection rules API client.
     * @return DataProtectionRulesApiV3
     */
    public DataProtectionRulesApiV3 getDataProtectionRulesApiV3() {
        if (dataProtectionRulesApiV3 == null) {
            this.dataProtectionRulesApiV3 = new DataProtectionRulesApiV3(this.apiClient);
        }
        return dataProtectionRulesApiV3;
    }

    /**
     * Retrieve datasource types API client.
     * @return DatasourceTypesApiV2
     */
    public DatasourceTypesApiV2 getDatasourceTypesApiV2() {
        if (datasourceTypesApiV2 == null) {
            this.datasourceTypesApiV2 = new DatasourceTypesApiV2(this.apiClient);
        }
        return datasourceTypesApiV2;
    }

    /**
     * Retrieve governance artifact types API client.
     * @return GovernanceArtifactTypesApiV3
     */
    public GovernanceArtifactTypesApiV3 getGovernanceArtifactTypesApiV3() {
        if (governanceArtifactTypesApiV3 == null) {
            this.governanceArtifactTypesApiV3 = new GovernanceArtifactTypesApiV3(this.apiClient);
        }
        return governanceArtifactTypesApiV3;
    }

    /**
     * Retrieve job runs API client.
     * @return JobRunsApiV2
     */
    public JobRunsApiV2 getJobRunsApiV2() {
        if (jobRunsApiV2 == null) {
            this.jobRunsApiV2 = new JobRunsApiV2(this.apiClient);
        }
        return jobRunsApiV2;
    }

    /**
     * Retrieve jobs API client.
     * @return JobsApiV2
     */
    public JobsApiV2 getJobsApiV2() {
        if (jobsApiV2 == null) {
            this.jobsApiV2 = new JobsApiV2(this.apiClient);
        }
        return jobsApiV2;
    }

    /**
     * Retrieve monitor API client.
     * @return MonitorApi
     */
    public MonitorApi getMonitorApi() {
        if (monitorApi == null) {
            this.monitorApi = new MonitorApi(this.apiClient);
        }
        return monitorApi;
    }

    /**
     * Retrieve policies API client.
     * @return PoliciesApiV3
     */
    public PoliciesApiV3 getPoliciesApiV3() {
        if (policiesApiV3 == null) {
            this.policiesApiV3 = new PoliciesApiV3(this.apiClient);
        }
        return policiesApiV3;
    }

    /**
     * Retrieve projects API client.
     * @return ProjectsApiV2
     */
    public ProjectsApiV2 getProjectsApiV2() {
        if (projectsApiV2 == null) {
            this.projectsApiV2 = new ProjectsApiV2(this.apiClient);
        }
        return projectsApiV2;
    }

    /**
     * Retrieve projects integrations API client.
     * @return ProjectsIntegrationsApiV2
     */
    public ProjectsIntegrationsApiV2 getProjectsIntegrationsApiV2() {
        if (projectsIntegrationsApiV2 == null) {
            this.projectsIntegrationsApiV2 = new ProjectsIntegrationsApiV2(this.apiClient);
        }
        return projectsIntegrationsApiV2;
    }

    /**
     * Retrieve projects members API client.
     * @return ProjectsMembersApiV2
     */
    public ProjectsMembersApiV2 getProjectsMembersApiV2() {
        if (projectsMembersApiV2 == null) {
            this.projectsMembersApiV2 = new ProjectsMembersApiV2(this.apiClient);
        }
        return projectsMembersApiV2;
    }

    /**
     * Retrieve projects settings API client.
     * @return ProjectsSettingsApiV2
     */
    public ProjectsSettingsApiV2 getProjectsSettingsApiV2() {
        if (projectsSettingsApiV2 == null) {
            this.projectsSettingsApiV2 = new ProjectsSettingsApiV2(this.apiClient);
        }
        return projectsSettingsApiV2;
    }

    /**
     * Retrieve projects tokens API client.
     * @return ProjectsTokensApiV2
     */
    public ProjectsTokensApiV2 getProjectsTokensApiV2() {
        if (projectsTokensApiV2 == null) {
            this.projectsTokensApiV2 = new ProjectsTokensApiV2(this.apiClient);
        }
        return projectsTokensApiV2;
    }

    /**
     * Retrieve reference data sets API client.
     * @return ReferenceDataSetsApiV3
     */
    public ReferenceDataSetsApiV3 getReferenceDataSetsApiV3() {
        if (referenceDataSetsApiV3 == null) {
            this.referenceDataSetsApiV3 = new ReferenceDataSetsApiV3(this.apiClient);
        }
        return referenceDataSetsApiV3;
    }

    /**
     * Retrieve role management API client.
     * @return RoleManagementApi
     */
    public RoleManagementApi getRoleManagementApi() {
        if (roleManagementApi == null) {
            this.roleManagementApi = new RoleManagementApi(this.apiClient);
        }
        return roleManagementApi;
    }

    /**
     * Retrieve rules API client.
     * @return RulesApiV3
     */
    public RulesApiV3 getRulesApiV3() {
        if (rulesApiV3 == null) {
            this.rulesApiV3 = new RulesApiV3(this.apiClient);
        }
        return rulesApiV3;
    }

    /**
     * Retrieve search API client.
     * @return SearchApiV3
     */
    public SearchApiV3 getSearchApiV3() {
        if (searchApiV3 == null) {
            this.searchApiV3 = new SearchApiV3(this.apiClient);
        }
        return searchApiV3;
    }

    /**
     * Retrieve transactional projects API client.
     * @return TransactionalProjectsApiV2
     */
    public TransactionalProjectsApiV2 getTransactionalProjectsApiV2() {
        if (transactionalProjectsApiV2 == null) {
            this.transactionalProjectsApiV2 = new TransactionalProjectsApiV2(this.apiClient);
        }
        return transactionalProjectsApiV2;
    }

    /**
     * Retrieve user management API client.
     * @return UserManagementApi
     */
    public UserManagementApi getUserManagementApi() {
        if (userManagementApi == null) {
            this.userManagementApi = new UserManagementApi(this.apiClient);
        }
        return userManagementApi;
    }

    /**
     * Retrieve workflows API client.
     * @return WorkflowsApiV3
     */
    public WorkflowsApiV3 getWorkflowsApiV3() {
        if (workflowsApiV3 == null) {
            this.workflowsApiV3 = new WorkflowsApiV3(this.apiClient);
        }
        return workflowsApiV3;
    }

    /**
     * Retrieve workflow user tasks API client.
     * @return WorkflowUserTasksApiV3
     */
    public WorkflowUserTasksApiV3 getWorkflowUserTasksApiV3() {
        if (workflowUserTasksApiV3 == null) {
            this.workflowUserTasksApiV3 = new WorkflowUserTasksApiV3(this.apiClient);
        }
        return workflowUserTasksApiV3;
    }

    /**
     * Throws a ConnectorCheckedException using the provided parameters.
     * @param errorCode the error code for the exception
     * @param methodName the name of the method throwing the exception
     * @param cause the underlying cause of the exception (or null if none)
     * @param params any parameters for formatting the error message
     * @throws ConnectorCheckedException always
     */
    protected void raiseConnectorCheckedException(WKCErrorCode errorCode, String methodName, Throwable cause, String ...params) throws ConnectorCheckedException {
        if (cause == null) {
            throw new ConnectorCheckedException(errorCode.getMessageDefinition(params),
                    this.getClass().getName(),
                    methodName);
        } else {
            throw new ConnectorCheckedException(errorCode.getMessageDefinition(params),
                    this.getClass().getName(),
                    methodName,
                    cause);
        }
    }

}
