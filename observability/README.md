## Observability 
 **Detailed Instructions:**  [Enable Logging Service for Your Application](https://developers.sap.com/tutorials/btp-app-logging.html)

1. From the terminal window, go to the projects directory and run the following
    ```console
    cd projects
    cds init observability --add java,tiny-sample
    cds add hana,mta,xsuaa,approuter --for production
    ```
2. Build and deploy 
    ```shell
        mbt build -t gen --mtar mta.mtar
        cf deploy gen/mta.mtar
    ```
3. Enable SSH
   ```shell
   cf enable-ssh observability
   cf restage observability
   cf ssh -N -T -L 8000:localhost:8000 observability
   ```
4. Attach vscode to your application and debug
    ```json
    {
      "type": "java",
      "name": "observability",
      "request": "attach",
      "hostName": "localhost",
      "port": 8000     
    }
    ```
## Logging
[Spring Boot Logging ](https://cap.cloud.sap/docs/java/operating-applications/observability#logging-configuration)

