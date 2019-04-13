package com.asjngroup.deft.common.returntypes;


import org.joda.time.DateTime;

public class ReturnDateTime
{
	private DateTime dateTime;

	public ReturnDateTime()
	{
		this.dateTime = null;
	}

	public DateTime getDateTime()
	{
		return this.dateTime;
	}

	public void setDateTime( DateTime dateTime )
	{
		this.dateTime = dateTime;
	}
}