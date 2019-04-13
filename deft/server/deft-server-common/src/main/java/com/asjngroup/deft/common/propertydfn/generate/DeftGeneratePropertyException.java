package com.asjngroup.deft.common.propertydfn.generate;

import com.asjngroup.deft.common.exception.DeftException;
import com.asjngroup.deft.common.util.StringUtil;

public class DeftGeneratePropertyException extends DeftException
{
	private static final long serialVersionUID = 1L;

	public DeftGeneratePropertyException( String str )
    {
        super( StringUtil.create( str ) );
    }

    public DeftGeneratePropertyException( Throwable t )
    {
        super( "", t );
    }

    public DeftGeneratePropertyException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

    public DeftGeneratePropertyException( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }
}
