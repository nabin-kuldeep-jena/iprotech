package com.asjngroup.deft.common.database.datasource;

import com.asjngroup.deft.common.exception.DeftException;
import com.asjngroup.deft.common.util.StringUtil;

public class DataSourceException extends DeftException
{

	private static final long serialVersionUID = 1L;

	public DataSourceException( String str )
    {
        super( StringUtil.create( str ) );
    }

    public DataSourceException( Throwable t )
    {
        super( "", t );
    }

    public DataSourceException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

    public DataSourceException( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }
}
