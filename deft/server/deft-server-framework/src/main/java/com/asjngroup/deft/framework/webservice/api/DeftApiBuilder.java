package com.asjngroup.deft.framework.webservice.api;

import retrofit2.Retrofit;

public class DeftApiBuilder
{
	Retrofit retrofit;

	private DeftApiBuilder()
	{

	}

	public <T> T create( final Class<T> service )
	{
		return retrofit.create( service );
	}

	public static DeftApiBuilder buildApi( String baseUril )
	{
		DeftApiBuilder api=new DeftApiBuilder();
		api.retrofit = new Retrofit.Builder().baseUrl( baseUril ).build();
		return api;
	}

}
