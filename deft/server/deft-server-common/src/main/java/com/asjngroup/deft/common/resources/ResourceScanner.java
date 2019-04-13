package com.asjngroup.deft.common.resources;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;



public abstract class ResourceScanner
{
    private ClassLoader classLoader;

    public ResourceScanner()
    {
        this( Thread.currentThread().getContextClassLoader() );
    }

    public ResourceScanner( ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    /**
     * Override this to handle each item that is identified by the scan.
     *
     * @param name the scanned item
     * @throws IOException
     */
    protected abstract void handleItem( String name ) throws IOException;

    /**
     * Helper method to retrieve the class loader for this scanner.
     *
     * @return the class loader
     */
    protected ClassLoader getClassLoader()
    {
        if ( classLoader == null )
            classLoader = this.getClass().getClassLoader();

        return classLoader;
    }

    /**
     * Helper method to convert a resource filename to the corresponding classname.
     * 
     * @param filename name of resource to convert
     * @return the classname
     */
    protected static String filenameToClassname( String filename )
    {
       return filename.substring( 0, filename.lastIndexOf( ".class" ) )
               .replace( '/', '.' )
               .replace( '\\', '.' );
    }

    /**
     * Helper method to get the Java class file details.
     *
     * @param filename name of resource to get class details for
     * @return the Java class details
     * @throws IOException
     */
    protected ClassFile getClassFile( String filename ) throws IOException
    {
        InputStream inputStream = null;
        DataInputStream dataInputStream = null;
        try
        {
            inputStream = getClassLoader().getResourceAsStream( filename );
            dataInputStream = new DataInputStream( inputStream );

            return new ClassFile( dataInputStream );
        }
        finally
        {
            if ( dataInputStream != null )
                dataInputStream.close();
            if ( inputStream != null )
                inputStream.close();
        }
    }

    /**
     * Helper method to check a Java class has the annotation specified.
     *
     * @param classFile the Java class to check
     * @param annotationType the annotation to check for
     * @return true if the Java class contains the annotation specified, false otherwise
     */
    protected boolean hasAnnotation( ClassFile classFile, Class< ? extends Annotation > annotationType )
    {
        AnnotationsAttribute visible = (AnnotationsAttribute)classFile.getAttribute( AnnotationsAttribute.visibleTag );
        return ( visible != null && visible.getAnnotation( annotationType.getName() ) != null );
    }

    /**
     * Scan all files, jars, folders for the specified resource and process individually.
     *
     * @param resourceName the resource to scan for
     * @throws IOException
     */
    protected void scan( String resourceName ) throws IOException
    {
        Set< UrlPath > urlPaths = new HashSet< UrlPath >();

        if ( resourceName == null )
        {
            // scan the whole class path
            for ( URL url : ((URLClassLoader)getClassLoader()).getURLs() )
            {
                String urlPath = url.getFile();
                if ( urlPath.endsWith( "/" ) )
                {
                   urlPath = urlPath.substring( 0, urlPath.length() - 1 );
                }

                urlPaths.add( new UrlPath( urlPath, null ) );
            }
        }
        else
        {
            // only scan the resource specified
            Enumeration< URL > urlEnum = getClassLoader().getResources( resourceName );

            while ( urlEnum.hasMoreElements() )
            {
                String urlPath = urlEnum.nextElement().getFile();
                urlPath = URLDecoder.decode( urlPath, "UTF-8" );

                String parent = null;

                // check for a file URL
                if ( urlPath.startsWith( "file:" ) )
                {
                    // urlPath looks like file:/C: for Windows and file:/home for Linux
                    // substring(5) works for both
                    urlPath = urlPath.substring( 5 );
                }

                // check for a Jar URL
                if ( urlPath.indexOf( '!' ) > 0 )
                {
                    urlPath = urlPath.substring( 0, urlPath.indexOf( '!' ) );
                }
                // check for the metadata resource URL
                else if ( resourceName.startsWith( "META-INF" ) )
                {
                    File dirOrArchive = new File( urlPath );
                    if ( resourceName.lastIndexOf( '/' ) > 0 )
                    {
                        // for META-INF/components.xml
                        dirOrArchive = dirOrArchive.getParentFile();
                    }
                    urlPath = dirOrArchive.getParent();
                }
                // any other resource location URL
                else
                {
                    parent = resourceName;
                }

                urlPaths.add( new UrlPath( urlPath, parent ) );
            }
        }

        // process each of the paths located for the specified resource
        for ( UrlPath urlPath: urlPaths )
        {
            File file = new File( urlPath.getPath() );

            if ( file.isDirectory() )
            {
                handleDirectory( file, urlPath.getParent() );
            }
            else
            {
                handleArchive( file );
            }
        }
    }

    private void handleArchive( File file ) throws IOException
    {
        ZipFile archive = new ZipFile( file );

        Enumeration< ? extends ZipEntry > entries = archive.entries();

        while ( entries.hasMoreElements() )
        {
            ZipEntry entry = entries.nextElement();

            handleItem( entry.getName() );
        }
    }

    private void handleDirectory( File file, String path ) throws IOException
    {
        // recursively process each file/sub-directory
        for ( File child : file.listFiles() )
        {
            String newPath = ( path == null ? child.getName() : path + '/' + child.getName() );

            if ( child.isDirectory() )
            {
                handleDirectory( child, newPath );
            }
            else
            {
                handleItem( newPath );
            }
        }
    }

    class UrlPath
    {
        private String path;
        private String parent;

        public UrlPath( String path, String parent )
        {
            this.path = normalisePath( path );
            this.parent = normalisePath( parent );
        }

        public String getPath()
        {
            return path;
        }

        public String getParent()
        {
            return parent;
        }

        private String normalisePath( String strPath )
        {
            if ( strPath == null )
                return null;

            if ( !strPath.endsWith( "/" ) )
                return strPath;

            return strPath.substring( 0, strPath.length() - 1 );
        }
    }
}
