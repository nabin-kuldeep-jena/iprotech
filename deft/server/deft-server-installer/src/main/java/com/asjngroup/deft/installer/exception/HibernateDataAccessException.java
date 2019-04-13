package com.asjngroup.deft.installer.exception;

import com.asjngroup.deft.common.exception.DeftRuntimeException;
import com.asjngroup.deft.common.util.StringUtil;

public class HibernateDataAccessException extends DeftRuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HibernateDataAccessException( String str )
    {
        super( StringUtil.create( str ) );
    }

    public HibernateDataAccessException( Throwable t )
    {
        super( "", t );
    }

    public HibernateDataAccessException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

    public HibernateDataAccessException( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }
}