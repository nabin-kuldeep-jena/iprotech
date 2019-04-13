package com.asjngroup.deft.common.database.hibernate;

import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;

public class DeftHikariConfig extends HikariConfig
{
	public DeftHikariConfig()
	{
		super();
	}

	public DeftHikariConfig( Properties properties )
	{
		super( properties );
	}

	public DeftHikariConfig( String propertyFileName )
	{
		super( propertyFileName );
	}

	@Override
	public void setUsername( String username )
	{
		super.setUsername( username );
	}

	@Override
	public void setPassword( String password )
	{
		super.setPassword( password );
	}
}
