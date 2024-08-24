## Reuse a CAP Java Service
 **Detailed Instructions:**  [Reuse a CAP Java Service](https://developers.sap.com/tutorials/cp-cap-java-service-reuse.html)
 

1. From the terminal window, go to the projects directory and run the following
    ```console
    cd projects
    cds init bookstore --add java
    ```
2. Install the reusable service project as npm dependency
    ```console
    npm install $(npm pack ../products-service -s)
    ```

    Navigate to your db folder and create a new file named `schema.cds`. Then, insert the following code into the file.
    ```yml
    namespace sap.capire.bookstore;

    using { Currency, cuid, managed }      from '@sap/cds/common';
    using { sap.capire.products.Products } from '@sap/capire-products';

    entity Books as projection on Products; extend Products with {
        // Note: we map Books to Products to allow reusing AdminService as is
        author : Association to Authors;
    }

    entity Authors : cuid {
        firstname : String(111);
        lastname  : String(111);
        books     : Association to many Books on books.author = $self;
    }

    @Capabilities.Updatable: false
    entity Orders : cuid, managed {
        items    : Composition of many OrderItems on items.parent = $self;
        total    : Decimal(9,2) @readonly;
        currency : Currency;
    }

    @Capabilities.Updatable: false
    entity OrderItems : cuid {
        parent    : Association to Orders not null;
        book_ID   : UUID;
        amount    : Integer;
        netAmount : Decimal(9,2) @readonly;
    }

    ```
3. Go to your srv folder and create a file called `services.cds`. Then, insert the following code into the file.
    ```java
    using { sap.capire.bookstore as db } from '../db/schema';

    // Define Books Service
    service BooksService {
        @readonly entity Books as projection   on db.Books { *, category as genre } excluding { category, createdBy, createdAt, modifiedBy, modifiedAt };
        @readonly entity Authors as projection on db.Authors;
    }

    // Define Orders Service
    service OrdersService {
        entity Orders as projection on db.Orders;
        entity OrderItems as projection on db.OrderItems;
    }

    // Reuse Admin Service
    using { AdminService } from '@sap/capire-products';
    extend service AdminService with {
        entity Authors as projection on db.Authors;
    }

    ```
4. Go to the `data` folder and load the sample data using CSV files.
    ```shell
    cd ~/projects/bookstore/db/data

    curl https://raw.githubusercontent.com/SAP-samples/cloud-cap-samples/CAA160-final/bookstore/db/data/sap.capire.bookstore-Authors.csv -O

    curl https://raw.githubusercontent.com/SAP-samples/cloud-cap-samples/CAA160-final/bookstore/db/data/sap.capire.bookstore-Books.csv -O

    mv sap.capire.bookstore-Books.csv sap.capire.products-Products.csv

    curl https://raw.githubusercontent.com/SAP-samples/cloud-cap-samples/CAA160-final/bookstore/db/data/sap.capire.bookstore-Books_texts.csv -O

    
    mv sap.capire.bookstore-Books_texts.csv sap.capire.products-Products_texts.csv

    curl https://raw.githubusercontent.com/SAP-samples/cloud-cap-samples/CAA160-final/bookstore/db/data/sap.capire.products-Categories.csv -O

    ```

5. Now start your application by running `cd srv && mvn cds:watch` in the terminal and open it in a new tab   

6. Enhance the Bookstore by incorporating custom code, specifically referring to the `OrdersService.java` handler 
7. Restart and test the application using HTTP requests from `requests.http` file
    
## Authentication 

1. Edit the pom.xml in the srv directory and add the cds-starter-cloudfoundry dependency 
    ```xml
    <dependency>
        <groupId>com.sap.cds</groupId>
        <artifactId>cds-starter-cloudfoundry</artifactId>
    </dependency>
    ```
2. Restart and test the create order http request with and without authentication
3. Discover more possibilities by refering the documentation  [Add Authentication and Authorization to the Application](https://developers.sap.com/tutorials/cp-cap-java-security-local.html)

## Deployment
1. Enhance the the project configuration for deployment by running the following commands.
    ```shell
    cds add hana,mta,xsuaa,approuter --for production
    ```

2. Enable the index page with SAP Fiori preview by adding the following configuration in the application.yaml
    ```yml
    ---
    spring:
    config.activate.on-profile: cloud
    cds:
    index-page.enabled: true
    ```
3. Update `xs-security.json` with following
    ```json
    {
        "xsappname": "bookstore",
        "tenant-mode": "dedicated",
        "scopes": [
            {
            "name": "$XSAPPNAME.Administrators",
            "description": "Administrators"
            }
        ],
        "attributes": [],
        "role-templates": [
            {
            "name": "Administrators",
            "description": "generated",
            "scope-references": [
                "$XSAPPNAME.Administrators"
            ],
            "attribute-references": []
            }
        ],
        "role-collections": [
            {
            "name": "BookStore_Administrators",
            "description": "BookStore Administrators",
            "role-template-references": ["$XSAPPNAME.Administrators"]
            }
        ],
        "oauth2-configuration": {
            "redirect-uris": ["https://*.cfapps.us10-001.hana.ondemand.com/**"]
        }
    }
    ```
4. Build and deploy 
    ```shell
        mbt build -t gen --mtar mta.mtar
        cf deploy gen/mta.mtar
    ```
5. Implement authentication and authorization on SAP BTP, followed by testing the application.

In the upcoming tutorial, you will create a Fiori UI using this service
