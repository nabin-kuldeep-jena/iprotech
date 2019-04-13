package com.asjngroup.ncash.payment.service;

import org.joda.time.DateTime;

public interface NCashPaymentService
{
	void getAllPayments( DateTime from, DateTime to, int count, int skip );
	void getPaymentById(int paymentId);
	void capturePaymentById(int paymentId);
}
