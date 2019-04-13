package com.asjngroup.deft.common.exception;

import com.asjngroup.deft.common.util.StringUtil;

public class DeftException extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DeftException( String str )
    {
        super( StringUtil.create( str ) );
    }

    public DeftException( Throwable t )
    {
        super( "", t );
    }

    public DeftException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

    public DeftException( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }
}
