package com.asjngroup.deft.common.xml;

import com.asjngroup.deft.common.exception.DeftException;


public class XMLTreeException extends DeftException
{
    public XMLTreeException( String str )
    {
        super( str );
    }

    public XMLTreeException( Throwable t )
    {
        super( t );
    }

    public XMLTreeException( String str, Object... args )
    {
        super( str, args );
    }

    public XMLTreeException( String str, Throwable t, Object... args )
    {
        super( str, t, args );
    }
}
