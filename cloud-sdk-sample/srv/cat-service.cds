using my.bookshop as my from '../db/data-model';

service CatalogService {
    @readonly entity Books as projection on my.Books;
    @readonly entity PurchaseOrders as projection on my.PurchaseOrders;

}