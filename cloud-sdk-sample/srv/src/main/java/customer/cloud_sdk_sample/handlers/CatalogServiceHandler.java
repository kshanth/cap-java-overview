package customer.cloud_sdk_sample.handlers;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.PurchaseOrders_;
import cds.gen.epm_ref_apps_po_apv_srv.EpmRefAppsPoApvSrv_;
import cds.gen.my.bookshop.Books_;
import cds.gen.catalogservice.Books;

@Component
@ServiceName(CatalogService_.CDS_NAME)
public class CatalogServiceHandler implements EventHandler {
	@Autowired
	@Qualifier(EpmRefAppsPoApvSrv_.CDS_NAME)
	CqnService po;

	@On(entity = PurchaseOrders_.CDS_NAME)
	public void readPurchaseOrders(CdsReadEventContext context) {
		context.setResult(po.run(context.getCqn()));
		context.setCompleted();
	}

	@After(event = CqnService.EVENT_READ, entity = Books_.CDS_NAME)
	public void discountBooks(Stream<Books> books) {
		books.filter(b -> b.getTitle() != null && b.getStock() != null)
		.filter(b -> b.getStock() > 200)
		.forEach(b -> b.setTitle(b.getTitle() + " (discounted)"));
	}

}