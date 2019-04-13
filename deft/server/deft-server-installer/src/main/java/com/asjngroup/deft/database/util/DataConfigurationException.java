package com.asjngroup.deft.database.util;

import com.asjngroup.deft.common.exception.DeftException;
import com.asjngroup.deft.common.util.StringUtil;

public class DataConfigurationException extends DeftException
{

	private static final long serialVersionUID = 1L;

	public DataConfigurationException( String str )
    {
        super( StringUtil.create( str ) );
    }

    public DataConfigurationException( Throwable t )
    {
        super( "", t );
    }

    public DataConfigurationException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

    public DataConfigurationException( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }
}
