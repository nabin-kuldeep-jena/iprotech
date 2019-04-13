package com.asjngroup.deft.common.database.hibernate.exception;

import com.asjngroup.deft.common.exception.DeftException;
import com.asjngroup.deft.common.util.StringUtil;

public class HibernateUtilException extends DeftException
{
	private static final long serialVersionUID = 1L;

	public HibernateUtilException( String str )
    {
        super( StringUtil.create( str ) );
    }

    public HibernateUtilException( Throwable t )
    {
        super( "", t );
    }

    public HibernateUtilException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

    public HibernateUtilException( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }
}
