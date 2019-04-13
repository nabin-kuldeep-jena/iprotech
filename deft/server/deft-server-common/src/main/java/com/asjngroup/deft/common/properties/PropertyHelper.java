package com.asjngroup.deft.common.properties;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.PropertyException;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.asjngroup.deft.common.component.helper.ComponentHelper;
import com.asjngroup.deft.common.component.helper.ComponentHelperException;
import com.asjngroup.deft.common.database.datasource.DataSourceException;
import com.asjngroup.deft.common.database.datasource.DataSourceHelper;
import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyDfnGroup;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInstGroup;
import com.asjngroup.deft.common.database.hibernate.util.HibernateSession;
import com.asjngroup.deft.common.database.hibernate.util.HibernateTransaction;
import com.asjngroup.deft.common.database.hibernate.util.HibernateUtil;
import com.asjngroup.deft.common.exception.DeftRuntimeException;
import com.asjngroup.deft.common.returntypes.ReturnString;
import com.asjngroup.deft.common.util.StringHelper;

public class PropertyHelper
{
	protected static Map<Integer, PropertyType> propertyTypes = new HashMap();
	protected static boolean initialised = false;

	public static boolean isValueNull( PropertyInst priObj )
	{
		return ( ( priObj.getPriValue() == null ) || ( priObj.getPriValue().trim().length() == 0 ) );
	}

	public static Map<PropertyDfn, PropertyInst> getPropertyMap( int pigId ) throws PropertyHelperException
	{
		Map propertyMap = new HashMap();
		try
		{
			List<Object[]> results = HibernateSession.query( "select prd, pri from PropertyInst pri join pri.PropertyDfn prd where pri.PigId = :pigId", "pigId", Integer.valueOf( pigId ) );

			for ( Object[] row : results )
			{
				propertyMap.put( ( PropertyDfn ) row[0], ( PropertyInst ) row[1] );
			}
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}

		return propertyMap;
	}

	public static PropertyDfnGroup getPropertyDfnGroup( int pigId ) throws PropertyHelperException
	{
		try
		{
			PropertyInstGroup propertyInstGroup = ( PropertyInstGroup ) HibernateSession.get( PropertyInstGroup.class, Integer.valueOf( pigId ) );

			if ( propertyInstGroup == null )
			{
				throw new PropertyHelperException( "Invalid property inst group id %1", new Object[]
				{ Integer.valueOf( pigId ) } );
			}

			return getPropertyDfnGroup( propertyInstGroup );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}
	}

	public static PropertyDfnGroup getPropertyDfnGroup( Session session, String pdgKey ) throws PropertyHelperException
	{
		PropertyDfnGroup retVal = null;
		try
		{
			List propertyDfnGroupList = HibernateUtil.query( session, "from PropertyDfnGroup propertyDfnGroup where propertyDfnGroup.PdgKey=:pdgKey", "pdgKey", pdgKey );

			if ( propertyDfnGroupList.size() == 1 )
				retVal = ( PropertyDfnGroup ) propertyDfnGroupList.get( 0 );
			else
				retVal = null;
		}
		catch ( HibernateException e )
		{
			new PropertyHelperException( e );
		}

		return retVal;
	}

	public static PropertyDfnGroup getPropertyDfnGroup( PropertyInstGroup propertyInstGroup ) throws PropertyHelperException
	{
		PropertyDfnGroup propertyDfnGroup;
		try
		{
			propertyDfnGroup = ( PropertyDfnGroup ) HibernateSession.get( PropertyDfnGroup.class, Integer.valueOf( propertyInstGroup.getPdgId() ) );

			if ( propertyDfnGroup == null )
			{
				throw new PropertyHelperException( "Invalid property dfn group id %1 looking up from property inst group %2", new Object[]
				{ Integer.valueOf( propertyInstGroup.getPdgId() ), Integer.valueOf( propertyInstGroup.getPigId() ) } );
			}
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}

		return propertyDfnGroup;
	}

	public static Map<String, PropertyDfn> getPropertyDfns( PropertyDfnGroup pdgObj ) throws PropertyHelperException
	{
		if ( pdgObj == null )
		{
			throw new IllegalArgumentException( "PropertyDfnGroup == null" );
		}

		Map definitions = null;
		List<PropertyDfn> propertyDfns = null;
		try
		{
			definitions = new HashMap();
			propertyDfns = HibernateSession.query( "from PropertyDfn as prd where prd.pdgId = :pdgId", "pdgId", Integer.valueOf( pdgObj.getPdgId() ) );

			for ( PropertyDfn prdObj : propertyDfns )
			{
				definitions.put( prdObj.getPrdKey(), prdObj );
			}

			if ( pdgObj.getPdgParentPdgId() != null )
			{
				definitions.putAll( getPropertyDfns( ( PropertyDfnGroup ) HibernateSession.get( PropertyDfnGroup.class, pdgObj.getPdgParentPdgId() ) ) );
			}
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}

		return definitions;
	}

	public static Map<String, PropertyInst> getPropertyInsts( Integer pigId ) throws PropertyHelperException
	{
		return getPropertyInsts( pigId, true );
	}

	public static Map<String, PropertyInst> getPropertyInsts( Integer pigId, boolean validate ) throws PropertyHelperException
	{
		return getPropertyInsts( getPropertyInstGroup( pigId ), validate );
	}

	public static Map<String, PropertyInst> getPropertyInsts( PropertyInstGroup pigObj ) throws PropertyHelperException
	{
		return getPropertyInsts( pigObj, true );
	}

	public static Map<String, PropertyInst> getPropertyInsts( PropertyInstGroup pigObj, boolean validate ) throws PropertyHelperException
	{
		Map propertyInstMap = new HashMap();
		populatePropertyInstMap( pigObj, validate, true, propertyInstMap );
		return propertyInstMap;
	}

	public static Map<String, Object> getPropertyInstValues( Integer pigId ) throws PropertyHelperException
	{
		return getPropertyInstValues( pigId, true );
	}

	public static Map<String, Object> getPropertyInstValues( Integer pigId, boolean validate ) throws PropertyHelperException
	{
		return getPropertyInstValues( getPropertyInstGroup( pigId ), validate );
	}

	public static Map<String, Object> getPropertyInstValues( PropertyInstGroup pigObj ) throws PropertyHelperException
	{
		return getPropertyInstValues( pigObj, true );
	}

	public static Map<String, Object> getPropertyInstValues( PropertyInstGroup pigObj, boolean validate ) throws PropertyHelperException
	{
		Map propertyInstMap = new HashMap();
		populatePropertyInstMap( pigObj, validate, false, propertyInstMap );
		return propertyInstMap;
	}

	public static String getPropertyValue( String propertyName ) throws PropertyHelperException
	{
		String query = "select pri.PriValue from PropertyInst pri where pri.PrdId =(select prd.PrdId from PropertyDfn prd where prd.PrdKey = :prdKey)";
		List list = null;
		try
		{
			list = HibernateSession.query( query, "prdKey", propertyName );
		}
		catch ( HibernateException e )
		{
			e.printStackTrace();
		}

		return ( ( String ) list.get( 0 ) );
	}

	private static void populatePropertyInstMap( PropertyInstGroup pigObj, boolean validate, boolean returnPropertyInsts, Map propertyInstMap ) throws PropertyHelperException
	{
		if ( pigObj == null )
		{
			return;
		}
		Map propertyDfns;
		try
		{
			List<PropertyInst> propertyInsts = HibernateSession.query( "from PropertyInst as pri where pri.PigId = :pigId", "pigId", Integer.valueOf( pigObj.getPigId() ) );
			Map<String,PropertyDfn> propertyDfnsByKey = getPropertyDfns( getPropertyDfnGroup( pigObj ) );

			propertyDfns = new HashMap( propertyDfnsByKey.size(), 1.0F );

			for ( PropertyDfn propertyDfn : propertyDfnsByKey.values() )
			{
				propertyDfns.put( Integer.valueOf( propertyDfn.getPrdId() ), propertyDfn );
			}

			for ( PropertyInst priObj : propertyInsts )
			{
				PropertyDfn prdObj = ( PropertyDfn ) propertyDfns.get( Integer.valueOf( priObj.getPrdId() ) );
				PropertyType propertyType = getPropertyType( prdObj );

				propertyType.fixProperty( priObj );

				if ( validate )
				{
					if ( !( propertyType.isSatisfied( prdObj, priObj ) ) )
					{
						throw new PropertyHelperException( "The property '%1' in property group '%2' is mandatory but has no value specified.", new Object[]
						{ prdObj.getPrdName(), pigObj.getPigName() } );
					}

					ReturnString message = new ReturnString();
					if ( ( !( isValueNull( priObj ) ) ) && ( !( propertyType.isValid( priObj, prdObj, message ) ) ) )
					{
						throw new PropertyHelperException( "Invalid property value '%1' detected in property group '%2' (%3).", new Object[]
						{ priObj.getPriValue(), pigObj.getPigName(), message.getString() } );
					}
				}
				String prdKey = prdObj.getPrdKey();
				if ( "SysLicenseKey".equals( prdKey ) )
				{
					String priLicenseKey = propertyType.getValue( priObj ).toString();
					if ( priLicenseKey.length() < 10 )
					{
						try
						{
							Integer.parseInt( priLicenseKey );
							String[] allLicenseKeys = DataSourceHelper.getAllLicenseKeys();
							String licenseKey = allLicenseKeys[( allLicenseKeys.length - 1 )];
							priObj.setPriValue( licenseKey );
						}
						catch ( NumberFormatException nfe )
						{
							throw new DeftRuntimeException(nfe);
						}

					}

				}

				if ( returnPropertyInsts )
				{
					propertyInstMap.put( prdKey, priObj );
				}
				else
				{
					propertyInstMap.put( prdKey, propertyType.getValue( priObj ) );
				}
			}
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}
		catch ( PropertyException e )
		{
			throw new PropertyHelperException( e );
		}
	}

	public static PropertyType getPropertyType( PropertyDfn prdObj ) throws PropertyHelperException
	{
		int cmpId = prdObj.getCmpId();
		PropertyType propertyType = null;

		synchronized (propertyTypes)
		{
			propertyType = ( PropertyType ) propertyTypes.get( Integer.valueOf( cmpId ) );

			if ( null == propertyType )
			{
				try
				{
					propertyType = ( PropertyType ) ComponentHelper.createInstance( cmpId, new Object[0] );
				}
				catch ( ComponentHelperException e )
				{
					throw new PropertyHelperException( e );
				}

				propertyTypes.put( Integer.valueOf( cmpId ), propertyType );
			}

		}

		if ( propertyType == null )
		{
			throw new PropertyHelperException( "No property type exists for the property definition '" + prdObj.getPrdName() + "' (CmpId " + cmpId + ")." );
		}
		return propertyType;
	}

	public static PropertyInst getPropertyInst( Map<String, PropertyInst> properties, String key ) throws PropertyHelperException
	{
		if ( !( properties.containsKey( key ) ) )
		{
			return null;
		}

		return ( ( PropertyInst ) properties.get( key ) );
	}

	public static String getDefaultValue( Map<String, PropertyDfn> definitions, String key ) throws PropertyHelperException
	{
		if ( !( definitions.containsKey( key ) ) )
		{
			return "";
		}

		return getDefaultValue( ( PropertyDfn ) definitions.get( key ) );
	}

	public static String getDefaultValue( PropertyDfn prdObj ) throws PropertyHelperException
	{
		try
		{
			PropertyType propertyType = getPropertyType( prdObj );

			return propertyType.getDefaultValue( prdObj );
		}
		catch ( PropertyException e )
		{
			throw new PropertyHelperException( e );
		}
	}

	public static String getDefaultValue( PropertyDfn prdObj, Map<String, String> tokens ) throws PropertyHelperException
	{
		try
		{
			PropertyType propertyType = getPropertyType( prdObj );

			if ( StringHelper.isEmpty( prdObj.getPrdDefault() ) )
			{
				return "";
			}

			String defaultString = propertyType.getDefaultValue( prdObj );

			if ( tokens != null )
			{
				for ( Map.Entry entry : tokens.entrySet() )
				{
					defaultString = defaultString.replace( ( CharSequence ) entry.getKey(), ( CharSequence ) entry.getValue() );
				}
			}

			return defaultString;
		}
		catch ( PropertyException e )
		{
			throw new PropertyHelperException( e );
		}
	}

	public static Object getPropertyValueFromPig( Integer pigId, String key ) throws PropertyHelperException
	{
		return getPropertyValueFromPig( getPropertyInstGroup( pigId ), key );
	}

	public static Object getPropertyValueFromPig( PropertyInstGroup pigObj, String key ) throws PropertyHelperException
	{
		Map values = getPropertyInstValues( pigObj );

		return getPropertyValueFromValues( values, key );
	}

	public static Object getPropertyValueFromValues( Map<String, Object> values, String key ) throws PropertyHelperException
	{
		if ( !( values.containsKey( key ) ) )
		{
			return null;
		}

		return values.get( key );
	}

	public static Object getPropertyValueFromInsts( Map<String, PropertyInst> properties, String key ) throws PropertyHelperException
	{
		PropertyInst priObj = null;
		PropertyDfn prdObj = null;
		PropertyType propertyType = null;
		Object value = null;
		try
		{
			priObj = getPropertyInst( properties, key );
			if ( priObj == null )
			{
				return null;
			}

			prdObj = ( PropertyDfn ) HibernateSession.get( PropertyDfn.class, Integer.valueOf( priObj.getPrdId() ) );

			propertyType = getPropertyType( prdObj );
			value = propertyType.getValue( priObj );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}
		catch ( PropertyException e )
		{
			throw new PropertyHelperException( e );
		}

		return value;
	}

	private static PropertyInstGroup getPropertyInstGroup( Integer pigId ) throws PropertyHelperException
	{
		PropertyInstGroup pigObj = null;
		if ( pigId == null )
			return null;
		try
		{
			pigObj = ( PropertyInstGroup ) HibernateSession.get( PropertyInstGroup.class, pigId );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}
		return pigObj;
	}

	public static <T extends PropertyGroup> T loadPropertyGroup( Class<T> propertyGroupClass, Map<String, Object> propertyMap ) throws PropertyHelperException
	{
		PropertyGroup propertyGroup;
		try
		{
			propertyGroup = ( PropertyGroup ) propertyGroupClass.newInstance();
		}
		catch ( InstantiationException e )
		{
			throw new PropertyHelperException( e );
		}
		catch ( IllegalAccessException e )
		{
			throw new PropertyHelperException( e );
		}

		propertyGroup.load( propertyMap );

		return ( T ) propertyGroup;
	}

	public static <T extends PropertyGroup> T loadPropertyGroup( Class<T> propertyGroupClass, int pigId ) throws PropertyHelperException
	{
		PropertyGroup propertyGroup;
		try
		{
			propertyGroup = ( PropertyGroup ) propertyGroupClass.newInstance();
		}
		catch ( InstantiationException e )
		{
			throw new PropertyHelperException( e );
		}
		catch ( IllegalAccessException e )
		{
			throw new PropertyHelperException( e );
		}

		propertyGroup.load( pigId );

		return ( T ) propertyGroup;
	}

	public static PropertyGroup loadPropertyGroup( int pigId ) throws PropertyHelperException
	{
		PropertyDfnGroup propertyDfnGroup = getPropertyDfnGroup( pigId );
		Class propertyGroupClass = getPropertyGroupClassFromDfn( propertyDfnGroup );

		return loadPropertyGroup( propertyGroupClass, pigId );
	}

	public static <T extends PropertyGroup> Class<T> getPropertyGroupClassFromDfn( PropertyDfnGroup propertyDfnGroup ) throws PropertyHelperException
	{
		String pdgKey = propertyDfnGroup.getPdgKey();
		try
		{
			Class clazz = Class.forName( propertyDfnGroup.getPdgSystemServerPackage() + "." + pdgKey );
			return clazz.asSubclass( PropertyGroup.class );
		}
		catch ( ClassNotFoundException e )
		{
			throw new PropertyHelperException( "Unable to find class corresponding to property dfn group key %1", e, new Object[]
			{ pdgKey } );
		}
	}

	public static void copyPropertyInstGroup( int fromPigId, int toPigId ) throws PropertyHelperException
	{
		Map<String,PropertyInst> fromPropertyInsts = getPropertyInsts( Integer.valueOf( fromPigId ), true );
		Map<String,PropertyInst> toPropertyInsts = getPropertyInsts( Integer.valueOf( toPigId ), false );

		HibernateTransaction transaction = new HibernateTransaction( HibernateSession.getSessionFactory() );
		try
		{
			for ( Map.Entry entry : fromPropertyInsts.entrySet() )
			{
				if ( !( toPropertyInsts.containsKey( entry.getKey() ) ) )
				{
					throw new PropertyHelperException( "Could not find existing property inst in 'to' group for dfn '%1'", new Object[]
					{ entry.getKey() } );
				}
				PropertyInst propertyInst = ( PropertyInst ) toPropertyInsts.get( entry.getKey() );

				propertyInst.setPriValue( ( ( PropertyInst ) entry.getValue() ).getPriValue() );
				transaction.update( propertyInst );
			}

			transaction.commit();
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}
	}

	public static List<PropertyInst> getPropertyInst( String pdgKey, String prdKey ) throws PropertyHelperException
	{
		String query = "select pri from PropertyInst pri where pri.PropertyDfn.PropertyDfnGroup.PdgKey = :pdgKey and pri.PropertyDfn.PrdKey = :prdKey ";

		List propertyInsts = null;
		try
		{
			propertyInsts = HibernateSession.query( query, new String[]
			{ "pdgKey", "prdKey" }, new Object[]
			{ pdgKey, prdKey } );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}

		return propertyInsts;
	}

	public static List<String> getPriValues( String pdgKey, String prdKey ) throws PropertyHelperException
	{
		String query = "select pri.PriValue from PropertyInst pri where pri.PropertyDfn.PropertyDfnGroup.PdgKey = :pdgKey and pri.PropertyDfn.PrdKey = :prdKey ";

		List results = null;
		try
		{
			results = HibernateSession.query( query, new String[]
			{ "pdgKey", "prdKey" }, new Object[]
			{ pdgKey, prdKey } );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}

		return results;
	}
}