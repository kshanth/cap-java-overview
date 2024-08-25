## SAP Cloud SDK 
 **Detailed Instructions:**  [Add the Consumption of an External Service to Your CAP Application](https://developers.sap.com/tutorials/btp-app-ext-service-add-consumption..html)
1. [Create an Account on the SAP Gateway Demo System](https://developers.sap.com/tutorials/gateway-demo-signup..html)
 
2. From the terminal window, go to the projects directory and run the following
    ```console
    cd projects
    cds init cloud-sdk-sample --add java,tiny-sample
    ```
3. Add the Maven depedency
    ```xml
    <cloud.sdk.version>5.10.0</cloud.sdk.version>
    --------------------------    
    <dependency>
        <groupId>com.sap.cloud.sdk</groupId>
        <artifactId>sdk-modules-bom</artifactId>
        <version>${cloud.sdk.version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
    -------------------------
    <dependency>
        <groupId>com.sap.cloud.sdk</groupId>
        <artifactId>sdk-core</artifactId>
    </dependency>
    ```            


4. Copy the [Approve Purchase Orders](https://sapes5.sapdevcenter.com/sap/opu/odata/sap/EPM_REF_APPS_PO_APV_SRV/$metadata) metadata from the ES5 system and create an EPM_REF_APPS_PO_APV_SRV.edmx file

5. Import the Approve Purchase Orders edmx to your project
    ```console
    cds import EPM_REF_APPS_PO_APV_SRV.edmx
    ```
6. Create an ES5 destination by referencing or importing the **cloud-sdk-sample\ES5** file.

7. Validate that the destinations are working by executing the following
    ```console
    curl -v -i "ES5.dest/sap/opu/odata/sap/EPM_REF_APPS_PO_APV_SRV/PurchaseOrders"
    ```
8. Add the following lines to `CatalogServiceHandler.java` to delegate requests to external services

    ```java
    @Autowired
	@Qualifier(EpmRefAppsPoApvSrv_.CDS_NAME)
	CqnService po;

	@On(entity = PurchaseOrders_.CDS_NAME)
	public void readPurchaseOrders(CdsReadEventContext context) {
		context.setResult(po.run(context.getCqn()));
		context.setCompleted();
	}
    ```
9. Enhanace the `data-model.cds` with your own projection and by this you also avoid requesting data that you are not interested in.
    ```yaml
    entity PurchaseOrders as projection on epm.PurchaseOrders {
        key POId as POId,
        SupplierId as SupplierId,
        SupplierName as SupplierName
    };
    ```
10. Additional steps are needed to consume the service locally.
    - Deploy the approuter to expose the ES5 destination OData service for anonymous access. 
        ```shell
        cd  approuter
        cf push
        ```
    - Create launch.json file under .vscode with the following lines
        ```json
        {
            "type": "java",
            "name": "cloud-sdk-sample",
            "request": "launch",
            "mainClass": "customer.cloud_sdk_sample.Application",
            "projectName": "cloud-sdk-sample",
            "cwd": "${workspaceFolder}/cloud-sdk-sample",
            "env": {
                "destinations": "[{\"name\":\"ES5\",\"url\":\"https://remote-s4-grouchy-lizard-xz.cfapps.us20.hana.ondemand.com/\", \"TrustAll\":true}]"
        }    
        ```
11. Run and test the application using `run and debug` option.