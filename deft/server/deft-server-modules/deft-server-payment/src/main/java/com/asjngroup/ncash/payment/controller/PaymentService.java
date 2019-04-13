package com.asjngroup.ncash.payment.controller;

import org.apache.cxf.jaxrs.ext.MessageContext;

import com.asjngroup.deft.common.service.models.ResponseMessageModel;

public interface PaymentService
{

	void verifyPayment( String gatewayPaymentId, Double paidAmount, Long orderId, MessageContext context, ResponseMessageModel responseMessageModel );

}
