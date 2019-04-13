package com.asjngroup.deft.common.xml;

import com.asjngroup.deft.common.exception.DeftException;

public class XMLException extends DeftException
{
    public XMLException( String str )
    {
        super( str );
    }

    public XMLException( Throwable t )
    {
        super( t );
    }

    public XMLException( String str, Object... args )
    {
        super( str, args );
    }

    public XMLException( String str, Throwable t, Object... args )
    {
        super( str, t, args );
    }
}
