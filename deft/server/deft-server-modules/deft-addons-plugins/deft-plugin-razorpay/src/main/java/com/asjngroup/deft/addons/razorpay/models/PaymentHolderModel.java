package com.asjngroup.deft.addons.razorpay.models;

import java.util.List;

public class PaymentHolderModel extends AbstractRazorpayModel
{
	private int count;
	List<RazorpayPaymentModel> items;
	private int fee;
    private int service_tax;
    private String error_code;
    private String error_description;
    private String created_at;

    
	public List<RazorpayPaymentModel> getItems()
	{
		return items;
	}

	public void setItems( List<RazorpayPaymentModel> items )
	{
		this.items = items;
	}

	public int getFee()
	{
		return fee;
	}

	public void setFee( int fee )
	{
		this.fee = fee;
	}

	public int getService_tax()
	{
		return service_tax;
	}

	public void setService_tax( int service_tax )
	{
		this.service_tax = service_tax;
	}

	public String getError_code()
	{
		return error_code;
	}

	public void setError_code( String error_code )
	{
		this.error_code = error_code;
	}

	public String getError_description()
	{
		return error_description;
	}

	public void setError_description( String error_description )
	{
		this.error_description = error_description;
	}

	public String getCreated_at()
	{
		return created_at;
	}

	public void setCreated_at( String created_at )
	{
		this.created_at = created_at;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount( int count )
	{
		this.count = count;
	}
}
