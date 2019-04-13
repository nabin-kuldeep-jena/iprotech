package com.asjngroup.ncash.database.util;

import com.asjngroup.ncash.common.exception.NCashException;
import com.asjngroup.ncash.common.util.StringUtil;

public class DataConfigurationException extends NCashException
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
