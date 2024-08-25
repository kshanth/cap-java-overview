## Multitenant Application
 **Detailed Instructions:**  [Multitenant Software as a Service application](https://github.com/SAP-samples/btp-cap-multitenant-saas/tree/main
 )
 
1. From the terminal window, go to the projects directory and run the following
    ```console
    cds init bookshop --add java,tiny-sample
    cd bookshop
    ```
2. Enable Multitenancy by running the following

    ```console
    cds add multitenancy --for production
    ```

3. After adding multitenancy, Maven build should be used to generate the model related artifacts:
    ```console
    mvn install
    ```
## Deploy to Cloud
In order to get your multitenant application deployed, follow this

1. Enhance the the project configuration for deployment by running the following commands.
    ```console    
    cds add hana,xsuaa,approuter --for production
   ```
2. Enable the index page with SAP Fiori preview by adding the following configuration in the application.yaml
    ```yml
    ---
    spring:
        config.activate.on-profile: cloud
    cds:
        index-page.enabled: true
    ```
2. **Optional:** If you intend to serve UIs you can easily set up the SAP Cloud Portal service:
    ```shell
    cds add portal
    ```
3. add a deployment descriptor:
    ```shell
    cds add mta
    ```
4. Freeze the npm dependencies for server, MTX sidecar and the UI applications:
    ```shell
    npm update --package-lock-only
    npm update --package-lock-only --prefix mtx/sidecar
    ## Optional
    ## npm update --package-lock-only --prefix app/admin-books
    ```
5. Build and deploy
    ```shell
    mbt build -t gen --mtar mta.tar
    cf deploy gen/mta.tar
    ```
6. Create a BTP subaccount to subscribe your deployed application
    `Note:` This subaccount has to be in the same region as the provider subaccount
7. Create and map a route to your application
    
    **Template:** `cf map-route ‹app› ‹paasDomain› --hostname subscriberSubdomain›-‹saasAppName›`
    
    **Example:** `cf map-route bookshop-mt cfapps.us20.hana.ondemand.com --hostname ci-us20-mt-ci-us20-dev-bookshop-mt`
8. Test the deployed application subscribed to the other subaccount.