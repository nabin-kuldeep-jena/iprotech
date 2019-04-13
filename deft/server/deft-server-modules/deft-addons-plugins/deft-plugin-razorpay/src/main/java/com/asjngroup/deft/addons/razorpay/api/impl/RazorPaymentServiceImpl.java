package com.asjngroup.deft.addons.razorpay.api.impl;

import org.joda.time.DateTime;

import com.asjngroup.deft.addons.razorpay.models.RazorpayPaymentModel;
import com.asjngroup.deft.common.NCashDateFormatter;
import com.asjngroup.deft.framework.webservice.api.NcashApiBuilder;
import com.asjngroup.ncash.payment.service.NCashPaymentService;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RazorPaymentServiceImpl implements NCashPaymentService
{

	NcashApiBuilder ncashApiBuilder;

	public RazorPaymentServiceImpl( String prefix, String key, String passCode, String baseUrl )
	{
		ncashApiBuilder = NcashApiBuilder.buildApi( prefix + key + passCode + baseUrl );

	}

	@Override
	public void getAllPayments( DateTime from, DateTime to, int count, int skip )
	{
		RazorPayPaymentApi paymentApi = ncashApiBuilder.create( RazorPayPaymentApi.class );
		//Object paymentHolder = paymentApi.getAllPaymentsApi(from.getS, to, 0, 0 );
	}

	@Override
	public void getPaymentById( int paymentId )
	{

	}

	@Override
	public void capturePaymentById( int paymentId )
	{
	}

	interface RazorPayPaymentApi
	{
		@GET( "/payments" )
		public RazorpayPaymentModel getAllPaymentsApi( @Query( "from" ) String from, @Query( "to" ) String to, @Query( "count" ) String count, @Query( "skip" ) String skip );

		@GET( "/payments/:id" )
		public void getPaymentByIdApi( @Path( "id" ) int id );

		@GET( "/payments/:id/capture" )
		public void capturePaymentByIdApi( @Path( "id" ) int id );
	}

}
