package com.asjngroup.deft.security.service.models;

import com.asjngroup.deft.common.service.models.AbstractResponseModel;

public class EmailAccessModel extends AbstractResponseModel
{
	private String emailId;
	private String evtToken;
	private String evtAccessKey;

	public String getEmailId()
	{
		return emailId;
	}

	public String getEvtToken()
	{
		return evtToken;
	}

	public void setEvtToken( String evtToken )
	{
		this.evtToken = evtToken;
	}

	public String getEvtAccessKey()
	{
		return evtAccessKey;
	}

	public void setEvtAccessKey( String evtAccessKey )
	{
		this.evtAccessKey = evtAccessKey;
	}

	public void setEmailId( String emailId )
	{
		this.emailId = emailId;
	}

}
