package com.asjngroup.deft.common.database.schema;

import com.asjngroup.deft.common.exception.DeftException;
import com.asjngroup.deft.common.util.StringUtil;

public class SchemaException extends DeftException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public SchemaException( String str )
    {
        super( StringUtil.create( str ) );
    }

    public SchemaException( Throwable t )
    {
        super( "", t );
    }

    public SchemaException( String str, Object... args )
    {
        super( StringUtil.create( str, args ) );
    }

    public SchemaException( String str, Throwable t, Object... args )
    {
        super( StringUtil.create( str, args ), t );
    }

}
