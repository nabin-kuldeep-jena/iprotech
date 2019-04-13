package com.asjngroup.deft.framework.security.component;

import com.asjngroup.deft.common.exception.DeftException;
import com.asjngroup.deft.common.util.StringUtil;

public class UserPasswordValidationException extends DeftException
{
	private static final long serialVersionUID = 1L;

	public UserPasswordValidationException( String str )
    {
        super( StringUtil.create( str ) );
    }

	public UserPasswordValidationException( Throwable t )
    {
        super( "", t );
    }

	public UserPasswordValidationException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

	public UserPasswordValidationException	( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }

}
