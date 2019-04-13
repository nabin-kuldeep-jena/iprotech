package com.asjngroup.ncash.framework.webservice.api;

import retrofit2.Retrofit;

public class NcashApiBuilder
{
	Retrofit retrofit;

	private NcashApiBuilder()
	{

	}

	public <T> T create( final Class<T> service )
	{
		return retrofit.create( service );
	}

	public static NcashApiBuilder buildApi( String baseUril )
	{
		NcashApiBuilder api=new NcashApiBuilder();
		api.retrofit = new Retrofit.Builder().baseUrl( baseUril ).build();
		return api;
	}

}
