package com.asjngroup.deft.common.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.asjngroup.deft.common.exception.DeftRuntimeException;
import com.asjngroup.deft.common.io.util.FileIOResourceHelper;
import com.asjngroup.deft.common.io.util.InputHandler;

public class ResourceHelper
{
    public static InputStream getResourceAsStream( String resource )
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream stream = null;

        String classLoaderResource = resource;
        if ( classLoaderResource.startsWith( "/" ) )
            classLoaderResource = classLoaderResource.substring( 1 );

        try
        {
            if ( classLoader != null )
                stream = classLoader.getResource( classLoaderResource ).openStream();

            if ( stream == null )
                stream = ResourceHelper.class.getResource( resource ).openStream();

            if ( stream == null )
                stream = ResourceHelper.class.getClassLoader().getResource( classLoaderResource ).openStream();
        }
        catch ( IOException e )
        {
            throw new DeftRuntimeException( e );
        }

        return stream;
    }

    public static URL getResourceURL( String resource )
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = null;

        String classLoaderResource = resource;
        if ( classLoaderResource.startsWith( "/" ) )
            classLoaderResource = classLoaderResource.substring( 1 );

        if ( classLoader != null )
            url = classLoader.getResource( classLoaderResource );

        if ( url == null )
            url = ResourceHelper.class.getResource( resource );

        if ( url == null )
            url = ResourceHelper.class.getClassLoader().getResource( classLoaderResource );

        return url;
    }

    public static List< URL > getResourcesURLs( String resourcePath )
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Enumeration<URL> resources = classLoader.getResources( resourcePath );
			return Collections.list( resources );
        }
        catch ( IOException e )
        {
            throw new DeftRuntimeException( e );
        }
    }

    public static void processResources( String resourcePath, InputHandler< URL, InputStream > handler )
    {
        for ( URL url : getResourcesURLs( resourcePath ) )
        {
            InputStream inputStream;
            try
            {
                inputStream = url.openStream();
            }
            catch ( Exception e )
            {
                throw new DeftRuntimeException( e );
            }

            try
            {
                handler.process( url, inputStream );
            }
            catch ( Exception e )
            {
                throw new DeftRuntimeException( "Error processing resource '%1'", e, url.toString() );
            }
            finally
            {
                FileIOResourceHelper.closeSilent( inputStream );
            }
        }
    }

//	public static void processResources( String[] resourcePath, InputHandler< URL, InputStream > handler )
//    {
//		for(String path:resourcePath){
//			processResources( path,handler);
//		}
//	}

	public static void processResources( List<String> resourcePath, InputHandler< URL, InputStream > handler )
    {
		for(String path:resourcePath){
			processResources( path,handler);
		}
	}

	public static boolean isResourceInArchive( URL url )
    {
        String urlPath;
        try
        {
            urlPath = URLDecoder.decode( url.getFile(), "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new DeftRuntimeException( e );
        }

        return urlPath.indexOf( "!" ) >= 0;
    }    
}
