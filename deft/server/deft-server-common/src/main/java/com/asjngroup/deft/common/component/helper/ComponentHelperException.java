package com.asjngroup.deft.common.component.helper;

import com.asjngroup.deft.common.exception.DeftException;

public class ComponentHelperException extends DeftException
{
	public ComponentHelperException( String str )
	{
		super( str );
	}

	public ComponentHelperException( Throwable t )
	{
		super( t );
	}

	public ComponentHelperException( String str, Object[] args )
	{
		super( str, args );
	}

	public ComponentHelperException( String str, Throwable t, Object[] args )
	{
		super( str, t, args );
	}
}
