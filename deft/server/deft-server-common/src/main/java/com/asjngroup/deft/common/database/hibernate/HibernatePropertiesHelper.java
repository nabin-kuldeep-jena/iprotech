package com.asjngroup.deft.common.database.hibernate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.asjngroup.deft.common.database.hibernate.util.HibernateUtil;
import com.asjngroup.deft.common.exception.DeftException;
import com.asjngroup.deft.common.io.util.FileHelper;

public class HibernatePropertiesHelper
{
	private static final Log log = LogFactory.getLog( HibernateUtil.class );
	protected static String programName = "Deft Server Service";

	public static void writeHibernatePropertiesFile( File file, boolean append, Properties propertiesMap ) throws DeftException
	{
		writeHibernatePropertiesFile( file, append, propertiesMap, false );
	}

	public static void writeHibernatePropertiesFile( File file, boolean append, Properties propertiesMap, boolean isHikariCp ) throws DeftException
	{
		try
		{
			FileOutputStream outputStream = new FileOutputStream( file, append );
			propertiesMap.store( outputStream, "" );
			outputStream.close();

			BufferedReader reader = new BufferedReader( new FileReader( file ) );
			StringBuffer buffer = new StringBuffer();
			while ( true )
			{
				String nextLine = reader.readLine();
				if ( nextLine == null )
				{
					break;
				}

				if ( !( isHikariCp ) )
					buffer.append( nextLine.replaceFirst( "=", " " ) + "\n" );
				else
					buffer.append( nextLine + "\n" );
			}
			reader.close();

			FileWriter writer = new FileWriter( file, false );
			writer.write( buffer.toString() );
			writer.close();
		}
		catch ( FileNotFoundException e )
		{
			if ( isHikariCp )
				throw new DeftException( "Error while writing to c3p0 properties" );
			throw new DeftException( "Error while writing to hibernate properties" );
		}
		catch ( IOException e )
		{
			if ( isHikariCp )
				throw new DeftException( "Error while writing to c3p0 properties" );
			throw new DeftException( "Error while writing to hibernate properties" );
		}
	}

	public static File getHibernateFile()
	{
		File currDir = new File( System.getProperty( "user.dir" ) );
		return new File( currDir.getParent() + FileHelper.fileSeperator + "config" + FileHelper.fileSeperator + "hibernate.properties" );
	}

	public static File getHibernateCfgXmlFile()
	{
		File currDir = new File( System.getProperty( "user.dir" ) );
		return new File( currDir.getParent() + FileHelper.fileSeperator + "config" + FileHelper.fileSeperator + "hibernate.cfg.xml" );
	}

	public static File getDistCacheHibernateCfgXmlFile()
	{
		File currDir = new File( System.getProperty( "user.dir" ) );
		return new File( currDir.getParent() + FileHelper.fileSeperator + "config" + FileHelper.fileSeperator + "DistributedCacheManagerResources" + FileHelper.fileSeperator + "hibernate_dc.cfg.xml" );
	}

	public static File getC3P0File()
	{
		File currDir = new File( System.getProperty( "user.dir" ) );
		return new File( currDir.getParent() + FileHelper.fileSeperator + "config" + FileHelper.fileSeperator + "c3p0.properties" );
	}

	public static Properties getC3poProperties()
	{
		InputStream propertyStream = DeftHibernateConfiguration.class.getResourceAsStream( "/hikari.properties" );

		if ( propertyStream == null )
		{
			return null;
		}
		Properties props = new Properties();
		try
		{
			props.load( propertyStream );
		}
		catch ( IOException e )
		{
			log.info( "unable to load: " + e.getMessage() );

			return null;
		}
		finally
		{
			try
			{
				propertyStream.close();
			}
			catch ( IOException e )
			{
				log.error( e );
				e.printStackTrace();
			}
		}

		return props;
	}

	public static Properties getHibernateProperties()
	{
		InputStream propertyStream = HibernatePropertiesHelper.class.getResourceAsStream( "/hibernate.properties" );

		if ( propertyStream == null )
		{
			return null;
		}
		Properties props = new Properties();
		try
		{
			props.load( propertyStream );
		}
		catch ( IOException e )
		{
			log.info( "unable to migrate: " + e.getMessage() );
			return null;
		}

		return props;
	}

	public static void setProgramName( String prName )
	{
		programName = prName;
	}

	public static String getProgramName()
	{
		return programName;
	}

	public static void addProgramName( Properties properties )
	{
		if ( !( properties.getProperty( "hibernate.connection.driver_class" ).equals( "oracle.jdbc.OracleDriver" ) ) )
			return;
		properties.setProperty( "hibernate.connection.v$session.program", programName );
	}

	public static void setDecryptedPasswordToProperties( Properties hibernateProperties )
	{
		/*if ( hibernateProperties == null )
		{
			return;
		}
		String password = hibernateProperties.getProperty( "hibernate.connection.password" );
		String username = hibernateProperties.getProperty( "hibernate.connection.username" );
		
		if ( ( password != null ) && ( password.length() > 0 ) && ( username != null ) && ( username.length() > 0 ) )
		{
			try
			{
				password = RSAEncryptionHelper.decryptPassword( password );
				username = RSAEncryptionHelper.decrypt( username );
			}
			catch ( RSAEncryptionException e )
			{
				try
				{
					password = RSAEncryptionHelper.oldDecryptPassword( password );
					username = RSAEncryptionHelper.oldDecryptPassword( username );
					Properties properties = getHibernateProperties();
					properties.setProperty( "hibernate.connection.password", password );
					properties.setProperty( "hibernate.connection.username", username );
					setEncryptedPasswordToProperties( properties );
					writeHibernatePropertiesFile( getHibernateFile(), false, properties );
				}
				catch ( Exception exception )
				{
					log.info( "unable to decrypt username or password : " + exception.getMessage() );
		
					password = "";
					username = "";
				}
			}
		
			hibernateProperties.setProperty( "hibernate.connection.password", password );
			hibernateProperties.setProperty( "hibernate.connection.username", username );
		}
		*/
		addProgramName( hibernateProperties );
	}

	public static void setEncryptedPasswordToProperties( Properties hibernateProperties )
	{/*
		if ( hibernateProperties == null )
		{
			return;
		}
		String password = hibernateProperties.getProperty( "hibernate.connection.password" );
		String username = hibernateProperties.getProperty( "hibernate.connection.username" );
		
		if ( ( password == null ) || ( password.length() <= 0 ) || ( username == null ) || ( username.length() <= 0 ) )
			return;
		try
		{
			password = RSAEncryptionHelper.encryptPassword( password );
			username = RSAEncryptionHelper.encrypt( username );
		}
		catch ( RSAEncryptionException e )
		{
			log.info( "unable to decrypt username or password : " + e.getMessage() );
		
			password = "";
		
			username = "";
		}
		
		hibernateProperties.setProperty( "hibernate.connection.password", password );
		hibernateProperties.setProperty( "hibernate.connection.username", username );*/
	}

	public static Properties getDecryptedPasswordForDistCache( Properties hibernateProperties )
	{
		/*if ( hibernateProperties == null )
		{
			return hibernateProperties;
		}
		String password = hibernateProperties.getProperty( "hibernate.connection.password" );
		String username = hibernateProperties.getProperty( "hibernate.connection.username" );
		if ( ( password != null ) && ( password.length() > 0 ) && ( username != null ) && ( username.length() > 0 ) )
		{
			try
			{
				password = RSAEncryptionHelper.decryptPassword( password );
				username = RSAEncryptionHelper.decrypt( username );
			}
			catch ( RSAEncryptionException e )
			{
				try
				{
					password = RSAEncryptionHelper.oldDecryptPassword( password );
					username = RSAEncryptionHelper.oldDecryptPassword( username );
					hibernateProperties.setProperty( "hibernate.connection.password", password );
					hibernateProperties.setProperty( "hibernate.connection.username", username );
					hibernateProperties = getEncryptedPasswordForDistCache( hibernateProperties );
				}
				catch ( Exception exception )
				{
					log.info( "unable to decrypt username or password : " + exception.getMessage() );
		
					password = "";
					username = "";
				}
			}
		
			hibernateProperties.setProperty( "hibernate.connection.password", password );
			hibernateProperties.setProperty( "hibernate.connection.username", username );
			return hibernateProperties;
		}
		*/ return hibernateProperties;
	}

	public static Properties getEncryptedPasswordForDistCache( Properties hibernateProperties )
	{
		if ( hibernateProperties == null )
		{
			return hibernateProperties;
		}
		String password = hibernateProperties.getProperty( "hibernate.connection.password" );
		String username = hibernateProperties.getProperty( "hibernate.connection.username" );

		/*	if ( ( password != null ) && ( password.length() > 0 ) && ( username != null ) && ( username.length() > 0 ) )
			{
				try
				{
					password = RSAEncryptionHelper.encryptPassword( password );
					username = RSAEncryptionHelper.encrypt( username );
				}
				catch ( RSAEncryptionException e )
				{
					log.info( "unable to decrypt username or password : " + e.getMessage() );
		
					password = "";
		
					username = "";
				}
		
				hibernateProperties.setProperty( "hibernate.connection.password", password );
				hibernateProperties.setProperty( "hibernate.connection.username", username );
				return hibernateProperties;
			}*/
		return hibernateProperties;
	}

	public static void writeHibernateCfgXmlFile( LinkedHashMap<String, String> hibernatePropertiesMap )
	{
		File cfgXml = getHibernateCfgXmlFile();
		FileOutputStream fileoutputStream = null;
		try
		{
			if ( !( cfgXml.exists() ) )
			{
				cfgXml.createNewFile();
			}
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentbuilder = dbf.newDocumentBuilder();
			Document document = documentbuilder.newDocument();
			Element sessionFactoryNode = ( Element ) ( ( Element ) document.appendChild( document.createElement( "hibernate-configuration" ) ) ).appendChild( document.createElement( "session-factory" ) );

			Set keys = hibernatePropertiesMap.keySet();
			Iterator keyIterator = keys.iterator();
			while ( keyIterator.hasNext() )
			{
				String key = ( String ) keyIterator.next();
				Element property = ( Element ) sessionFactoryNode.appendChild( document.createElement( "property" ) );
				property.setAttribute( "name", key );
				property.appendChild( document.createTextNode( ( String ) hibernatePropertiesMap.get( key ) ) );
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty( "doctype-public", "-//Hibernate/Hibernate Configuration DTD 3.0//EN" );
			transformer.setOutputProperty( "doctype-system", "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd" );
			transformer.setOutputProperty( "indent", "yes" );
			transformer.setOutputProperty( "method", "xml" );

			DOMSource source = new DOMSource( document );
			fileoutputStream = new FileOutputStream( cfgXml, false );
			StreamResult outputstream = new StreamResult( fileoutputStream );
			transformer.transform( source, outputstream );
		}
		catch ( ParserConfigurationException e )
		{
			log.error( "Failed to create hibernate.properties - Failed to get document builder instance \n " + e.getStackTrace() );
		}
		catch ( FileNotFoundException e )
		{
			log.error( "Failed to create hibernate.properties - Unable to locate hibernate.cfg.xml file : " + e.getStackTrace() );
		}
		catch ( IOException e )
		{
			log.error( "Failed to create hibernate.properties :" + e.getStackTrace() );
		}
		catch ( TransformerConfigurationException e )
		{
			log.error( "Failed to create hibernate.properties :" + e.getStackTrace() );
		}
		catch ( TransformerException e )
		{
			log.error( "Failed to create hibernate.properties :" + e.getStackTrace() );
		}
		finally
		{
			try
			{
				if ( fileoutputStream != null )
					fileoutputStream.close();
			}
			catch ( IOException e )
			{
				log.error( "Error while closing output stream : " + e.getMessage() );
			}
		}
	}
}