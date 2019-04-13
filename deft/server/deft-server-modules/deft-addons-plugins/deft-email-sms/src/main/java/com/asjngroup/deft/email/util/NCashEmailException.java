package com.asjngroup.deft.email.util;

import com.asjngroup.deft.common.exception.NCashException;
import com.asjngroup.deft.common.util.StringUtil;

public class NCashEmailException extends NCashException
{
	private static final long serialVersionUID = 1L;

	public NCashEmailException( String str )
    {
        super( StringUtil.create( str ) );
    }

	public NCashEmailException( Throwable t )
    {
        super( "", t );
    }

	public NCashEmailException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

	public NCashEmailException( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }
}
