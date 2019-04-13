package com.asjngroup.deft.common.properties;

import com.asjngroup.deft.common.exception.DeftRuntimeException;
import com.asjngroup.deft.common.util.StringUtil;

public class PropertyHelperException extends DeftRuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PropertyHelperException( String str )
    {
        super( StringUtil.create( str ) );
    }

    public PropertyHelperException( Throwable t )
    {
        super( "", t );
    }

    public PropertyHelperException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

    public PropertyHelperException( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }

}
