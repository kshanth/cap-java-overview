## Create a Reusable Service
1. From the terminal window, go to the projects directory and run the following
    ```
    cd projects
    cds init products-service --add java
    ```
2. Navigate to your db folder and create a new file named `schema.cds`. Then, insert the following code into the file.
    ```
    namespace sap.capire.products;

    using { Currency, cuid, managed, sap.common.CodeList } from '@sap/cds/common';

    entity Products : cuid, managed {
        title    : localized String(111);
        descr    : localized String(1111);
        stock    : Integer;
        price    : Decimal(9,2);
        currency : Currency;
        category : Association to Categories;
    }

    entity Categories : CodeList {
        key ID   : Integer;
        parent   : Association to Categories;
        children : Composition of many Categories on children.parent = $self;
    }
    ```
3. Go to your srv folder and create a file called `admin-service.cds`. Then, insert the following code into the file.
    ```
    using { sap.capire.products as db } from '../db/schema';

    service AdminService {
        entity Products   as projection on db.Products;
        entity Categories as projection on db.Categories;
    }

    ```
4. Now start your application by running `mvn spring-boot:run` in the terminal and open it in a new tab
5. This application will be reused by a bookstore application. The reuse of models can be achieved by publishing NPM modules with the models
    -   Open the package.json file and change the value of name field from `products-service-cds` to `@sap/capire-products`
    -   Create a new file `index.cds` in the `~/projects/products-service` folder and place the following content inside this file
        ```
        using from './db/schema';
        using from './srv/admin-service';
        ```
In the next tutorial, you will build a bookstore application, reusing the products service application.
