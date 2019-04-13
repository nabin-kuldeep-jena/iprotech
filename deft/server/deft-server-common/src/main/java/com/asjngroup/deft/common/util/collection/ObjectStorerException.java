package com.asjngroup.deft.common.util.collection;

import com.asjngroup.deft.common.exception.DeftRuntimeException;

public class ObjectStorerException extends DeftRuntimeException
{
    public ObjectStorerException( String str )
    {
        super( str );
    }

    public ObjectStorerException( Throwable t )
    {
        super( t );
    }

    public ObjectStorerException( String str, Object... args )
    {
        super( str, args );
    }

    public ObjectStorerException( String str, Throwable t, Object... args )
    {
        super( str, t, args );
    }
}
