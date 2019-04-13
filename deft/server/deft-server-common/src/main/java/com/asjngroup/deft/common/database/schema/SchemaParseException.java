package com.asjngroup.deft.common.database.schema;

import com.asjngroup.deft.common.exception.DeftException;


public class SchemaParseException extends DeftException
{
    public SchemaParseException( String str )
    {
        super( str );
    }

    public SchemaParseException( Throwable t )
    {
        super( t );
    }

    public SchemaParseException( String str, Object... args )
    {
        super( str, args );
    }

    public SchemaParseException( String str, Throwable t, Object... args )
    {
        super( str, t, args );
    }
}
