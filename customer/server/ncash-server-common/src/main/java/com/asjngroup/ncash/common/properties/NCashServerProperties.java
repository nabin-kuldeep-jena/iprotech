package com.asjngroup.ncash.common.properties;

public class NCashServerProperties
{
	private boolean isDevMode;

	public static boolean isDevMode()
	{
		return true;//Boolean.TRUE.equals( System.getenv( "ncash.serverproperty.isDevMode" ) );
	}

}
