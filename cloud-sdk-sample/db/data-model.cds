namespace my.bookshop;
using {  EPM_REF_APPS_PO_APV_SRV as epm } from '../srv/external/EPM_REF_APPS_PO_APV_SRV';

entity Books {
  key ID : Integer;
  title  : localized String;
  stock  : Integer;
}

entity PurchaseOrders as projection on epm.PurchaseOrders {
  key POId as POId,
  SupplierId as SupplierId,
  SupplierName as SupplierName
};