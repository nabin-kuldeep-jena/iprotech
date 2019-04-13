package com.asjngroup.deft.common.database.hibernate.exception;

import com.asjngroup.deft.common.exception.DeftException;
import com.asjngroup.deft.common.util.StringUtil;

public class NCashDataAccessException extends DeftException
{
	private static final long serialVersionUID = 1L;

	public NCashDataAccessException( String str )
    {
        super( StringUtil.create( str ) );
    }

    public NCashDataAccessException( Throwable t )
    {
        super( "", t );
    }

    public NCashDataAccessException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

    public NCashDataAccessException( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }
}
