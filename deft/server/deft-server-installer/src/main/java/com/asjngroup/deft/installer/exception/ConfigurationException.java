package com.asjngroup.deft.installer.exception;

import com.asjngroup.deft.common.exception.DeftRuntimeException;
import com.asjngroup.deft.common.util.StringUtil;

public class ConfigurationException extends DeftRuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConfigurationException( String str )
    {
        super( StringUtil.create( str ) );
    }

    public ConfigurationException( Throwable t )
    {
        super( "", t );
    }

    public ConfigurationException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

    public ConfigurationException( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }
}