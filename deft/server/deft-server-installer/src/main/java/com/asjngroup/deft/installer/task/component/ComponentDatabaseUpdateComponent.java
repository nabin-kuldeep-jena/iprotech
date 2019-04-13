package com.asjngroup.deft.installer.task.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

import com.asjngroup.deft.common.database.datasource.DataSource;
import com.asjngroup.deft.common.database.hibernate.HibernateObject;
import com.asjngroup.deft.common.database.hibernate.references.Component;
import com.asjngroup.deft.common.database.hibernate.references.PartitionTbl;
import com.asjngroup.deft.common.database.hibernate.references.PropertyDfnGroup;
import com.asjngroup.deft.common.database.hibernate.util.HibernateUtil;
import com.asjngroup.deft.common.database.schema.Schema;
import com.asjngroup.deft.database.util.StandardDatabaseUpdateComponent;
import com.asjngroup.deft.installer.exception.ConfigurationException;

public class ComponentDatabaseUpdateComponent extends StandardDatabaseUpdateComponent
{
	private static final Log log = LogFactory.getLog( ComponentDatabaseUpdateComponent.class );
	private Map<String, Integer> propertyDfnGroupNameMap;
	private Map<String, Integer> propertyDfnGroupKeyMap;
	private Map<Integer, PropertyDfnGroup> propertyDfnGroupIdMap;
	private Map<String, Integer> partitionMap;

	public ComponentDatabaseUpdateComponent()
	{
		this.partitionMap = new HashMap();
	}

	public void initialise( SessionFactory sessionFactory, DataSource dataSource, Schema schema, List<String> filenames, String onlyComponentType ) throws ConfigurationException
	{
		initialise( sessionFactory, dataSource, schema, filenames );

		setNoDeletes( true );

		List<Element> elementsToRemove = new ArrayList();

		Element componentTypesRoot = this.doc.getRootElement().element( "componentTypes" );
		for ( Iterator it = componentTypesRoot.elementIterator(); it.hasNext(); )
		{
			Element element = ( Element ) it.next();

			if ( !( element.attribute( "cptTypeCd" ).getValue().equals( onlyComponentType ) ) )
			{
				elementsToRemove.add( element );
			}
		}

		for ( Element element : elementsToRemove )
		{
			componentTypesRoot.remove( element );
		}
	}

	public void initialise( SessionFactory sessionFactory, DataSource dataSource, Schema schema, List<String> filenames ) throws ConfigurationException
	{
		super.initialise( sessionFactory, dataSource, schema, filenames );

		initialisePartitionMap();
		try
		{
			this.propertyDfnGroupNameMap = HibernateUtil.buildKeyIdMap( sessionFactory, PropertyDfnGroup.class, "pdgName" );
			this.propertyDfnGroupKeyMap = HibernateUtil.buildKeyIdMap( sessionFactory, PropertyDfnGroup.class, "pdgKey" );
		}
		catch ( HibernateException e )
		{
			throw new ConfigurationException( "Error building property dfn group map", e, new Object[0] );
		}

		try
		{
			this.propertyDfnGroupIdMap = HibernateUtil.buildIdMap( sessionFactory, PropertyDfnGroup.class );
		}
		catch ( HibernateException e )
		{
			throw new ConfigurationException( "Error building property dfn group map", e, new Object[0] );
		}
	}

	public void updateDatabase() throws ConfigurationException
	{
		standardProcessObjects();

		if ( this.noDeletes )
		{
			return;
		}
		try
		{
			HibernateUtil.delete( this.sessionFactory, "from Component cmp where not exists ( from ComponentType cpt where cmp.cptId = cpt.cptId )" );
		}
		catch ( Exception e )
		{
			throw new ConfigurationException( e );
		}
	}

	public void extractFromDatabase() throws ConfigurationException
	{
	}

	protected boolean onCopyAttributeToProperty( HibernateObject toObject, Element element, ClassMetadata metadata, Map<String, String> elementAttributes, String attributeName, String attributeValue, Stack<StandardDatabaseUpdateComponent.StackObject> objectIdStack ) throws ConfigurationException
	{
		if ( ( toObject.getClass().equals( Component.class ) ) && ( attributeName.equals( "pdgKey" ) ) )
		{
			Object propertyDfnGroupId;
			if ( attributeValue.length() != 0 )
			{
				propertyDfnGroupId = this.propertyDfnGroupKeyMap.get( attributeValue );

				if ( propertyDfnGroupId == null )
				{
					throw new ConfigurationException( "Unknown property dfn group name %1 found in object type %2", new Object[]
					{ attributeValue, toObject.getClass().getName() } );
				}
			}
			else
			{
				propertyDfnGroupId = null;
			}

			try
			{
				metadata.setPropertyValue( toObject, "pdgId", propertyDfnGroupId );
				metadata.setPropertyValue( toObject, "propertyDfnGroup", this.propertyDfnGroupIdMap.get( propertyDfnGroupId ) );
			}
			catch ( HibernateException e )
			{
				throw new ConfigurationException( "Error setting property dfn group id from name %1 found in object type %2", new Object[]
				{ attributeValue, toObject.getClass().getName() } );
			}

			return true;
		}
		else if ( ( toObject.getClass().equals( Component.class ) ) && ( attributeName.equals( "pdgName" ) ) )
		{
			Object propertyDfnGroupId;
			if ( attributeValue.length() != 0 )
			{
				propertyDfnGroupId = this.propertyDfnGroupNameMap.get( attributeValue );

				if ( propertyDfnGroupId == null )
				{
					throw new ConfigurationException( "Unknown property dfn group name %1 found in object type %2", new Object[]
					{ attributeValue, toObject.getClass().getName() } );
				}
			}
			else
			{
				propertyDfnGroupId = null;
			}

			try
			{
				metadata.setPropertyValue( toObject, "pdgId", propertyDfnGroupId );
			}
			catch ( HibernateException e )
			{
				throw new ConfigurationException( "Error setting property dfn group id from name %1 found in object type %2", new Object[]
				{ attributeValue, toObject.getClass().getName() } );
			}

			return true;
		}
		if ( ( !( toObject instanceof PartitionTbl ) ) && ( attributeName.endsWith( "ptnName" ) ) )
		{
			toObject.setPartitionId( ( ( Integer ) this.partitionMap.get( attributeValue ) ).intValue() );

			return true;
		}

		return false;
	}

	protected boolean onCopyPropertyToAttribute( Element element, Object obj, ClassMetadata metadata, String propertyName ) throws ConfigurationException
	{
		if ( ( obj instanceof Component ) && ( propertyName.equals( "pdgId" ) ) )
		{
			Object propertyValue = null;
			try
			{
				propertyValue = metadata.getPropertyValue( obj, propertyName );
			}
			catch ( HibernateException e )
			{
				throw new ConfigurationException( "Error loading property dfn group value %1 in object %2", new Object[]
				{ propertyValue, obj.getClass().getName() } );
			}

			if ( propertyValue == null )
			{
				element.addAttribute( "pdgName", "" );
			}
			else
			{
				PropertyDfnGroup propertyDfnGroup = ( PropertyDfnGroup ) this.propertyDfnGroupIdMap.get( propertyValue );

				if ( propertyDfnGroup == null )
				{
					throw new ConfigurationException( "Unknown property dfn group id %1 found in object type %2", new Object[]
					{ propertyValue, obj.getClass().getName() } );
				}

				element.addAttribute( "pdgName", propertyDfnGroup.getPdgName() );
			}

			return true;
		}

		return false;
	}

	private void initialisePartitionMap() throws ConfigurationException
	{
		try
		{
			this.partitionMap = HibernateUtil.buildKeyIdMap( this.sessionFactory, PartitionTbl.class, Arrays.asList( new String[]
			{ "ptnName" } ) );
			partitionMap.put( "System", 0 );
			partitionMap.put( "Common", 1 );
		}
		catch ( HibernateException e )
		{
			throw new ConfigurationException( "Error building stream stage map", e, new Object[0] );
		}
	}
}