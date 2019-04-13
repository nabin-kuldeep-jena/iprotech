package com.asjngroup.deft.common.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FolderZiper
{

	public static void main( String[] a ) throws Exception
	{
		List<String> test = new ArrayList<String>();
		test.add( "c:\\a" );
		test.add( "c:\\b" );
		zipMultipleFolder( test, "c:\\a.zip" );
	}

	public static void zipFolder( String srcFolder, String destZipFile ) throws IOException
	{
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		try
		{
			fileWriter = new FileOutputStream( destZipFile );
			zip = new ZipOutputStream( fileWriter );
			addFolderToZip( "", srcFolder, zip );
		}
		finally
		{
			if ( zip != null )
			{
				zip.flush();
				zip.close();
			}
			if ( fileWriter != null )
				fileWriter.close();
		}

	}

	public static void zipMultipleFolder( List<String> srcFolders, String destZipFile ) throws IOException
	{
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		try
		{
			fileWriter = new FileOutputStream( destZipFile );
			zip = new ZipOutputStream( fileWriter );
			for ( String srcFolder : srcFolders )
				addFolderToZip( "", srcFolder, zip );
		}
		finally
		{
			if ( zip != null )
			{
				zip.flush();
				zip.close();
			}
			if ( fileWriter != null )
				fileWriter.close();
		}

	}

	public static void addFileToZip( String path, String srcFile, ZipOutputStream zip ) throws IOException
	{

		File folder = new File( srcFile );
		if ( folder.isDirectory() )
		{
			addFolderToZip( path, srcFile, zip );
		}
		else
		{
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = null;
			try
			{
				in = new FileInputStream( srcFile );
				zip.putNextEntry( new ZipEntry( path + "/" + folder.getName() ) );
				while ( ( len = in.read( buf ) ) > 0 )
				{
					zip.write( buf, 0, len );
				}
			}
			finally
			{
				if ( in != null )
					in.close();
			}
		}
	}

	public static void addFolderToZip( String path, String srcFolder, ZipOutputStream zip ) throws IOException
	{
		File folder = new File( srcFolder );

		for ( String fileName : folder.list() )
		{
			if ( path.equals( "" ) )
			{
				addFileToZip( folder.getName(), srcFolder + "/" + fileName, zip );
			}
			else
			{
				addFileToZip( path + "/" + folder.getName(), srcFolder + "/" + fileName, zip );
			}
		}
	}
}
