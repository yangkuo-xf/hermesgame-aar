package com.hermesgamesdk.entity;

public class QGOrderInfo {

	private String payType;
	private String orderSubject;
	private String productOrderId;
	private String amount;
	private int count;
	private String extrasParams;
	private String payParam;

	public void changeType(int payType) {
		this.payType = payType + "";
	}

	public String getPayType() {
		return payType;
	}

	public String getOrderSubject() {
		return orderSubject;
	}

	public void setOrderSubject(String orderSubject) {
		this.orderSubject = orderSubject;
	}

	public String getProductOrderId() {
		return productOrderId;
	}

	public void setProductOrderId(String productOrderId) {
		this.productOrderId = productOrderId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getExtrasParams() {
		return extrasParams;
	}

	public void setExtrasParams(String extrasParams) {
		this.extrasParams = extrasParams;
	}

	public String getPayParam() {
		return payParam;
	}

	public void setPayParam(String payParam) {
		this.payParam = payParam;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
