package com.asjngroup.deft.common.database.hibernate.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import com.asjngroup.deft.common.ObjectHelper;
import com.asjngroup.deft.common.database.hibernate.HibernateObject;
import com.asjngroup.deft.common.database.hibernate.exception.HibernateUtilException;
import com.asjngroup.deft.common.database.schema.Entity;
import com.asjngroup.deft.common.database.schema.Schema;
import com.asjngroup.deft.common.exception.DeftRuntimeException;
import com.asjngroup.deft.common.util.StringHelper;

public class PropertyMapper
{
	private Map<String, PropertyMappingInfo> idToPropertyMappings;
	private Map<List<String>, PropertyMappingInfo> propertyToIdMappings;
	private Map<Class, PropertyMappingInfo> classMappings;
	private List<PropertyMappingInfo> mappingInfos;
	private Map<Class, Map<Integer, List<Object>>> propertyLookupCache;
	private Map<Class, Map<List<Object>, HibernateObject>> idLookupCache;
	private List<Class> dirtyList;
	private SessionFactory sessionFactory;
	private boolean partialLoad;

	public PropertyMapper( SessionFactory sessionFactory )
	{
		this( sessionFactory, false );
	}

	public PropertyMapper( SessionFactory sessionFactory, boolean partial )
	{
		this.idToPropertyMappings = new HashMap();
		this.propertyToIdMappings = new HashMap();
		this.classMappings = new HashMap();
		this.mappingInfos = new ArrayList();

		this.propertyLookupCache = new HashMap();
		this.idLookupCache = new HashMap();

		this.dirtyList = new ArrayList();

		this.partialLoad = false;

		this.sessionFactory = sessionFactory;
		this.partialLoad = partial;
	}

	public void initPropertyMappings( Schema schema ) throws HibernateException
	{
		Map<String, Class> classInterfaceMap;
		try
		{
			classInterfaceMap = HibernateConfigurationHelper.buildClassNameInterfaceMap( HibernateSession.getConfiguration() );
		}
		catch ( HibernateUtilException e )
		{
			throw new HibernateException( e );
		}

		for ( Entity entityMapping : schema.EntityMappings )
		{
			if ( entityMapping.getBusinessConstraints().length == 0 )
			{
				continue;
			}
			if ( ( this.partialLoad ) && ( classInterfaceMap.get( entityMapping.EntityName ) == null ) )
			{
				continue;
			}
			try
			{
				this.mappingInfos.add( new PropertyMappingInfo( entityMapping.getPrimaryKey(), entityMapping.getBusinessConstraints(), ( Class ) classInterfaceMap.get( entityMapping.EntityName ) ) );
			}
			catch ( NullPointerException e )
			{
				throw new NullPointerException( "Null Error processing in object-mapping '" + entityMapping.EntityName + "'" );
			}
		}

		for ( PropertyMappingInfo mappingInfo : this.mappingInfos )
		{
			this.idToPropertyMappings.put( mappingInfo.idPropertyName, mappingInfo );
			this.propertyToIdMappings.put( Arrays.asList( mappingInfo.convertPropertyNames ), mappingInfo );
			this.classMappings.put( mappingInfo.clazz, mappingInfo );

			markDirty( mappingInfo.clazz );
		}
	}

	public void markDirty( Class clazz )
	{
		if ( this.dirtyList.contains( clazz ) )
		{
			return;
		}
		this.dirtyList.add( clazz );
	}

	public boolean isDirty( Class clazz )
	{
		return this.dirtyList.contains( clazz );
	}

	private void dirtyCheck( Class clazz ) throws HibernateException
	{
		if ( !( isDirty( clazz ) ) )
			return;
		reset( clazz );
	}

	public void clearDirty( Class clazz )
	{
		if ( !( this.dirtyList.contains( clazz ) ) )
		{
			return;
		}
		this.dirtyList.remove( clazz );
	}

	public void reset( SessionFactory sessionFactory ) throws HibernateException
	{
		for ( PropertyMappingInfo mappingInfo : this.mappingInfos )
		{
			reset( sessionFactory, mappingInfo.clazz );
		}
	}

	public void reset() throws HibernateException
	{
		for ( PropertyMappingInfo mappingInfo : this.mappingInfos )
		{
			reset( this.sessionFactory, mappingInfo.clazz );
		}
	}

	public void reset( Class clazz ) throws HibernateException
	{
		reset( this.sessionFactory, clazz );
	}

	public void reset( SessionFactory sessionFactory, Class clazz ) throws HibernateException
	{
		Session session = sessionFactory.openSession();
		if ( !( this.classMappings.containsKey( clazz ) ) )
		{
			return;
		}
		PropertyMappingInfo mappingInfo = ( PropertyMappingInfo ) this.classMappings.get( clazz );
		ClassMetadata metadata;
		Map<Integer, List<Object>> idCache;
		Map<List<Object>, HibernateObject> propertyCache;
		Iterator i$;
		try
		{
			List results = HibernateUtil.query( session, "from " + mappingInfo.clazz.getName() );
			metadata = HibernateUtil.getClassMetadata( sessionFactory, mappingInfo.clazz );
			idCache = new HashMap<Integer, List<Object>>();
			propertyCache = new HashMap<List<Object>, HibernateObject>();
			this.propertyLookupCache.put( mappingInfo.clazz, idCache );
			this.idLookupCache.put( mappingInfo.clazz, propertyCache );
			for ( i$ = results.iterator(); i$.hasNext(); )
			{
				Object obj = i$.next();

				Integer id = ( Integer ) metadata.getIdentifier( obj );

				List propertyValues = new ArrayList();

				for ( int i = 0; i < mappingInfo.convertPropertyNames.length; ++i )
				{
					Object propertyValue = metadata.getPropertyValue( obj, mappingInfo.convertPropertyNames[i] );
					propertyValues.add( propertyValue );
				}
				propertyCache.put( propertyValues, ( HibernateObject ) obj );
				idCache.put( id, propertyValues );
			}
		}
		finally
		{
			session.close();
		}

		clearDirty( clazz );
	}

	private PropertyMappingInfo findIdPropertyMappingInfo( String propertyName )
	{
		return ( ( PropertyMappingInfo ) this.idToPropertyMappings.get( propertyName ) );
	}

	private PropertyMappingInfo findPropertyMappingInfo( String propertyName )
	{
		return findPropertyMappingInfo( Arrays.asList( new String[]
		{ StringHelper.lowerFirstChar( propertyName ) } ) );
	}

	private PropertyMappingInfo findPropertyMappingInfo( List<String> propertyNames )
	{
		return ( ( PropertyMappingInfo ) this.propertyToIdMappings.get( propertyNames ) );
	}

	public Map<String, List<String>> getEntitySearchKeyMap()
	{
		Map entitySearchKeyMap = new HashMap();

		for ( PropertyMappingInfo propertyMappingInfo : this.mappingInfos )
		{
			entitySearchKeyMap.put( ObjectHelper.getClassOnlyName( propertyMappingInfo.clazz ), Arrays.asList( propertyMappingInfo.convertPropertyNames ) );
		}

		return entitySearchKeyMap;
	}

	public Map<String, List<String>> getFlattenedEntitySearchKeyMap()
	{
		Map entitySearchKeyMap = new HashMap();

		for ( PropertyMappingInfo propertyMappingInfo : this.mappingInfos )
		{
			entitySearchKeyMap.put( ObjectHelper.getClassOnlyName( propertyMappingInfo.clazz ), flattenKeys( Arrays.asList( propertyMappingInfo.convertPropertyNames ) ) );
		}

		return entitySearchKeyMap;
	}

	private List<String> flattenKeys( List<String> keys )
	{
		List flattenedKeys = new ArrayList();

		for ( String key : keys )
		{
			flattenedKeys.addAll( flattenKey( key ) );
		}

		return flattenedKeys;
	}

	private List<String> flattenKey( String key )
	{
		List flattenedKeys = new ArrayList();
		if ( this.idToPropertyMappings.containsKey( key ) )
		{
			PropertyMappingInfo propertyMappingInfo = ( PropertyMappingInfo ) this.idToPropertyMappings.get( key );

			for ( String subKey : propertyMappingInfo.convertPropertyNames )
			{
				if ( subKey.equals( key ) )
				{
					continue;
				}
				flattenedKeys.addAll( flattenKey( subKey ) );
			}
		}
		else
		{
			flattenedKeys.add( key );
		}

		return flattenedKeys;
	}

	public boolean mapPropertyToId( Object obj, ClassMetadata metadata, Map<String, String> properties, String propertyName, Object propertyValue ) throws HibernateException
	{
		String suffix = StringHelper.getCamelCaseSuffix( StringHelper.upperFirstChar( propertyName ), 2 );
		PropertyMappingInfo propertyMappingInfo = findPropertyMappingInfo( suffix );
		if ( propertyMappingInfo == null )
		{
			suffix = StringHelper.getCamelCaseSuffix( StringHelper.upperFirstChar( propertyName ), 3 );

			propertyMappingInfo = findPropertyMappingInfo( suffix );
		}

		if ( propertyMappingInfo == null )
		{
			return false;
		}

		if ( ( propertyMappingInfo.clazz.isAssignableFrom( obj.getClass() ) ) && ( propertyName.length() == suffix.length() ) )
		{
			return false;
		}

		dirtyCheck( propertyMappingInfo.clazz );

		HibernateObject nextedObj = ( HibernateObject ) ( ( Map ) this.idLookupCache.get( propertyMappingInfo.clazz ) ).get( Arrays.asList( new Object[]
		{ propertyValue } ) );

		if ( nextedObj != null )
		{
			String idPropertyName = propertyName.substring( 0, propertyName.length() - suffix.length() );
			idPropertyName = ( idPropertyName == null || idPropertyName.isEmpty() ) ? propertyMappingInfo.idPropertyName : idPropertyName + StringHelper.upperFirstChar( propertyMappingInfo.idPropertyName );
			metadata.setPropertyValue( obj, idPropertyName, nextedObj.getId() );
			setNextedObject( obj, metadata, propertyName, nextedObj,idPropertyName );
		}
		return true;
	}

	private void setNextedObject( Object obj, ClassMetadata metadata, String propertyName, HibernateObject nextedObj, String idPropertyName )
	{

		for ( String classPropertyName : metadata.getPropertyNames() )
		{
			
			Type propertyType = metadata.getPropertyType( classPropertyName );
			try
			{
				Class< ? > entityClassName = Class.forName( propertyType.getReturnedClass().getName() );
				String propertyNamePrefix=classPropertyName.replaceAll( entityClassName.getSimpleName() , "");
				if ( nextedObj.getClass().isAssignableFrom( entityClassName ) && idPropertyName.toUpperCase().contains( propertyNamePrefix.toUpperCase() ) )
				{
					metadata.setPropertyValue( obj, classPropertyName, nextedObj );
					return;
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace();
				throw new DeftRuntimeException( e );
			}
		}

	}

	public boolean mapIdToAttribute( Element element, Object obj, ClassMetadata metadata, String propertyName ) throws HibernateException
	{
		String suffix = StringHelper.getCamelCaseSuffix( propertyName, 2 );

		if ( this.idToPropertyMappings.containsKey( suffix ) )
		{
			PropertyMappingInfo propertyMappingInfo = ( PropertyMappingInfo ) this.idToPropertyMappings.get( suffix );

			dirtyCheck( propertyMappingInfo.clazz );

			Object propertyValue = metadata.getPropertyValue( obj, propertyName );
			List propertyObjects = ( List ) ( ( Map ) this.propertyLookupCache.get( propertyMappingInfo.clazz ) ).get( propertyValue );

			for ( int i = 0; i < propertyObjects.size(); ++i )
			{
				String suffixedPropertyName = StringHelper.getCamelCaseSuffix( propertyName, 2 ) + propertyMappingInfo.convertPropertyNames[i];

				if ( propertyObjects.get( i ) == null )
				{
					element.addAttribute( suffixedPropertyName, "" );
				}
				else
				{
					element.addAttribute( suffixedPropertyName, propertyObjects.get( i ).toString() );
				}
			}
			return true;
		}

		return false;
	}

	public HibernateObject attributeToId( String attributeName, Object attributeValue ) throws HibernateException
	{
		PropertyMappingInfo propertyMappingInfo = findPropertyMappingInfo( attributeName );

		if ( propertyMappingInfo == null )
		{
			return null;
		}
		dirtyCheck( propertyMappingInfo.clazz );

		return ( ( HibernateObject ) ( ( Map ) this.idLookupCache.get( propertyMappingInfo.clazz ) ).get( Arrays.asList( new Object[]
		{ attributeValue } ) ) );
	}

	public Object idToAttribute( String attributeName, Integer attributeValue ) throws HibernateException
	{
		if ( this.idToPropertyMappings.containsKey( attributeName ) )
		{
			PropertyMappingInfo propertyMappingInfo = ( PropertyMappingInfo ) this.idToPropertyMappings.get( attributeName );

			dirtyCheck( propertyMappingInfo.clazz );

			return ( ( Map ) this.propertyLookupCache.get( propertyMappingInfo.clazz ) ).get( attributeValue );
		}

		return null;
	}

	class PropertyMappingInfo
	{
		String idPropertyName;
		String[] convertPropertyNames;
		Class clazz;

		public PropertyMappingInfo( String paramString, String[] paramArrayOfString, Class paramClass )
		{
			if ( paramString == null )
			{
				throw new NullPointerException( "id property is null" );
			}
			if ( paramArrayOfString == null )
			{
				throw new NullPointerException( "converted-property-names is null" );
			}
			if ( paramClass == null )
			{
				throw new NullPointerException( "null Class definition" );
			}
			this.idPropertyName = paramString;
			this.convertPropertyNames = paramArrayOfString;

			Arrays.sort( this.convertPropertyNames );
			this.clazz = paramClass;
		}

		public PropertyMappingInfo( String paramString1, String paramString2, Class paramClass )
		{
			this( paramString1, new String[]
			{ paramString2 }, paramClass );
		}
	}
}