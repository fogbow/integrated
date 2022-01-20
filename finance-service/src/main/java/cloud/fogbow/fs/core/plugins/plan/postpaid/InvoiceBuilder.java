package cloud.fogbow.fs.core.plugins.plan.postpaid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cloud.fogbow.fs.core.models.Invoice;
import cloud.fogbow.fs.core.models.InvoiceItem;
import cloud.fogbow.fs.core.models.ResourceItem;
import cloud.fogbow.ras.core.models.orders.OrderState;

public class InvoiceBuilder {
	private List<InvoiceItem> items;
	private String userId;
	private String providerId;
	private Double invoiceTotal;
	private Long startTime;
	private Long endTime;
	
	public InvoiceBuilder() {
		this.items = new ArrayList<InvoiceItem>();
		invoiceTotal = 0.0;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	
	public void setStartTime(Long startTime) {
	    this.startTime = startTime;
	}
	
	public void setEndTime(Long endTime) {
	    this.endTime = endTime;
	}
	
	public void addItem(String orderId, ResourceItem resourceItem, OrderState state, Double valueToPayPerTimeUnit, Double timeUsed) {
		Double itemValue = valueToPayPerTimeUnit * timeUsed;
		items.add(new InvoiceItem(orderId, resourceItem, state, itemValue));
		invoiceTotal += itemValue;
	}

	public Invoice buildInvoice() {
		return new Invoice(UUID.randomUUID().toString(), userId, providerId, startTime, endTime, 
		        items, invoiceTotal);
	}

	public void reset() {
		this.userId = null;
		this.providerId = null;
		this.items = new ArrayList<InvoiceItem>();
		this.invoiceTotal = 0.0;
	}
}
