package com.asjngroup.deft.common.exception;

import com.asjngroup.deft.common.util.StringUtil;

public class DeftRuntimeException extends RuntimeException 
{

	private static final long serialVersionUID = 1L;

	public DeftRuntimeException( String str )
    {
        super( StringUtil.create( str ) );
    }

    public DeftRuntimeException( Throwable t )
    {
        super( "", t );
    }

    public DeftRuntimeException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

    public DeftRuntimeException( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }
}
