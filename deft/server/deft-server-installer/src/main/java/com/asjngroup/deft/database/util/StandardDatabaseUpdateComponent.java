package com.asjngroup.deft.database.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import com.asjngroup.deft.common.database.datasource.DataSource;
import com.asjngroup.deft.common.database.datasource.DataSourceException;
import com.asjngroup.deft.common.database.hibernate.HibernateObject;
import com.asjngroup.deft.common.database.hibernate.references.Component;
import com.asjngroup.deft.common.database.hibernate.references.ComponentType;
import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyDfnGroup;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInstGroup;
import com.asjngroup.deft.common.database.hibernate.util.HibernateSession;
import com.asjngroup.deft.common.database.hibernate.util.HibernateTransaction;
import com.asjngroup.deft.common.database.hibernate.util.HibernateUtil;
import com.asjngroup.deft.common.database.hibernate.util.PropertyMapper;
import com.asjngroup.deft.common.database.schema.Entity;
import com.asjngroup.deft.common.database.schema.Schema;
import com.asjngroup.deft.common.database.schema.Table;
import com.asjngroup.deft.common.properties.PropertyHelper;
import com.asjngroup.deft.common.properties.PropertyHelperException;
import com.asjngroup.deft.common.util.StringHelper;
import com.asjngroup.deft.common.xml.ReferenceDataMerger;
import com.asjngroup.deft.common.xml.XMLException;
import com.asjngroup.deft.common.xml.XMLHelper;
import com.asjngroup.deft.installer.exception.ConfigurationException;
import com.asjngroup.deft.installer.exception.HibernateDataAccessException;

/**
 * User: nabin.jena
 * Date: 25-Jan-2018
 * Time: 12:19:03
 *
 * The standard updater/extracter uses a hierarchy of information structures to define what
 * the XML should look like.
 */
public abstract class StandardDatabaseUpdateComponent implements DatabaseUpdateComponent
{
	protected HibernateTransaction transaction;
	protected boolean noDeletes = false;
	protected boolean noUpdates = false;

	protected SessionFactory sessionFactory;
	protected DataSource dataSource;
	protected Schema schema;
	protected Document doc;
	protected List<String> filenames;
	protected boolean ignoreBuisnessConstraintException;
	protected Interceptor interceptor = null;

	protected PropertyMapper propertyMapper;

	protected Map<Class, List> resultsCache;

	// maps for common CmpName/CptTypeCd lookups
	private Map<String, Integer> componentMap;
	private Map<Integer, ComponentType> componentTypeObjMap;
	private Map<Integer, Component> componentObjMap;

	public class StackObject
	{
		String objectName;
		Integer keyValue;
		HibernateObject objectValue;

		public StackObject( String objectName, Integer keyValue, HibernateObject objectValue )
		{
			this.objectName = objectName;
			this.keyValue = keyValue;
			this.objectValue = objectValue;
		}

		public String getObjectName()
		{
			return objectName;
		}

		public void setObjectName( String objectName )
		{
			this.objectName = objectName;
		}

		public Integer getKeyValue()
		{
			return keyValue;
		}

		public void setKeyValue( Integer keyValue )
		{
			this.keyValue = keyValue;
		}

		public Object getObjectValue()
		{
			return objectValue;
		}

		public void setObjectValue( HibernateObject objectValue )
		{
			this.objectValue = objectValue;
		}

	}

	public void initialise( SessionFactory sessionFactory, DataSource dataSource, Schema schema, Document doc ) throws ConfigurationException
	{
		this.sessionFactory = sessionFactory;
		this.dataSource = dataSource;
		this.schema = schema;
		this.doc = doc;

		propertyMapper = new PropertyMapper( sessionFactory );
		try
		{
			propertyMapper.initPropertyMappings( schema );
		}
		catch ( HibernateDataAccessException e )
		{
			throw new ConfigurationException( e );
		}

		transaction = new HibernateTransaction( sessionFactory );

		// create component map
		try
		{
			componentMap = HibernateUtil.buildKeyIdMap( sessionFactory, Component.class, Arrays.asList( new String[]
			{ "componentType.cptTypeCd", "cmpName" } ) );
			componentObjMap = HibernateUtil.buildIdMap( sessionFactory, Component.class );
			componentTypeObjMap = HibernateUtil.buildIdMap( sessionFactory, ComponentType.class );
		}
		catch ( HibernateDataAccessException e )
		{
			throw new HibernateDataAccessException( "Error building component map", e );
		}
	}

	public void initialise( SessionFactory sessionFactory, DataSource dataSource, Schema schema, Document doc, Interceptor interceptor ) throws ConfigurationException
	{
		this.sessionFactory = sessionFactory;
		this.dataSource = dataSource;
		this.schema = schema;
		this.doc = doc;
		this.interceptor = interceptor;

		propertyMapper = new PropertyMapper( sessionFactory, ignoreBuisnessConstraintException );
		try
		{
			propertyMapper.initPropertyMappings( schema );
		}
		catch ( HibernateDataAccessException e )
		{
			throw new ConfigurationException( e );
		}

		transaction = new HibernateTransaction( sessionFactory, interceptor );

		// create component map
		try
		{
			componentMap = HibernateUtil.buildKeyIdMap( sessionFactory, Component.class, Arrays.asList( new String[]
			{ "componentType.cptTypeCd", "cmpName" } ) );
		}
		catch ( HibernateDataAccessException e )
		{
			throw new ConfigurationException( "Error building component map", e );
		}
	}

	public void initialise( SessionFactory sessionFactory, DataSource dataSource, Schema schema, String filename ) throws ConfigurationException
	{
		initialise( sessionFactory, dataSource, schema, Arrays.asList( filename ) );
	}

	public void initialise( SessionFactory sessionFactory, DataSource dataSource, Schema schema, List<String> filenames ) throws ConfigurationException
	{
		// create the property mapper
		PropertyMapper propertyMapper = new PropertyMapper( sessionFactory );
		try
		{
			propertyMapper.initPropertyMappings( schema );
		}
		catch ( HibernateDataAccessException e )
		{
			throw new ConfigurationException( e );
		}

		// load and merge all the main document
		ReferenceDataMerger referenceDataMerger = new ReferenceDataMerger();
		Map<String, List<String>> searchKeyMap = new HashMap<String, List<String>>();

		searchKeyMap = propertyMapper.getFlattenedEntitySearchKeyMap();

		try
		{
			referenceDataMerger.initialise( searchKeyMap, filenames );
			referenceDataMerger.mergeDocuments();

			doc = referenceDataMerger.getMainDoc();
		}
		catch ( XMLException e )
		{
			throw new ConfigurationException( "Error merging XML files", e );
		}

		initialise( sessionFactory, dataSource, schema, doc );
	}

	public void setNoDeletes( boolean noDeletes )
	{
		this.noDeletes = noDeletes;
	}

	public void setNoUpdates( boolean noUpdates )
	{
		this.noUpdates = noUpdates;
	}

	protected void createDocument( String rootElementName )
	{
		// wipe the doc
		doc = DocumentFactory.getInstance().createDocument();

		// add the root element
		doc.addElement( rootElementName );
	}

	protected void standardProcessObjects() throws ConfigurationException
	{
		// default function for handling configuration objects

		// find the start element
		for ( Iterator<Element> it = doc.getRootElement().elementIterator(); it.hasNext(); )
		{
			Element rootElement = it.next();

			doProcessTopLevelItemGroup( rootElement );
		}
	}

	/// A top-level element is child of the root-node.
	/// Typically we expect this to be a collection of types - ie. elemnts like <ScreenTbls> or <RefTables> etc.
	private void doProcessTopLevelItemGroup( Element itemGroupElement ) throws ConfigurationException
	{
		// ignore anything that doesn't end in 's'
		if ( !itemGroupElement.getName().endsWith( "s" ) )
			return;

		String className = itemGroupElement.getName().substring( 0, itemGroupElement.getName().length() - 1 );
		Entity objectMapping = schema.findEntity( className );

		// this is an unrecognised root element so warn the user
		if ( objectMapping == null )
			throw new ConfigurationException( "Invalid top level element '%1'(s) - not a recognised object", className );

		// allow the subclass to veto this block if it wants to ( used by seed data to only update NEW objects )
		if ( !checkProcessBlock( className, true ) )
			return;

		onBeginTopLevelElement( itemGroupElement );

		// do a quick scan down the element tree to find all element classes in this root block
		List<String> subElementNames = XMLHelper.getAllSubElementNames( itemGroupElement );

		// drop all the indexes to allow unique keys to be swapped without blowing things up, particularly order no's
		// that are sometimes swapped over.
		for ( String subElementName : subElementNames )
		{
			// find the schema table for this object
			Table table = schema.findTableFromEntityName( dataSource.getDatabase(), subElementName );

			if ( table == null )
				continue;

			try
			{
				// drop all the indexes
				dataSource.dropAllIndexes( table );
			}
			catch ( DataSourceException e )
			{
				throw new ConfigurationException( e );
			}
		}

		Stack<StackObject> objectIdStack = new Stack<StackObject>();

		resultsCache = new HashMap<Class, List>();

		// do processing from the first level
		doProcessItemElement( itemGroupElement, itemGroupElement, objectIdStack );

		// add all the objects that existed in the db that DID NOT match any in the schema
		if ( !noDeletes )
		{
			Map<Class, List> newResultsCache = onPreDeleteVeto( resultsCache );

			for ( Map.Entry<Class, List> entry : newResultsCache.entrySet() )
			{
				try
				{
					transaction.deleteObjects( entry.getValue() );
				}
				catch ( HibernateDataAccessException e )
				{
					throw new ConfigurationException( e );
				}
			}
		}

		// save the db changes on each root iteration for dependencies
		saveDatabaseChanges();

		resultsCache.clear();

		// reapply all the indexes
		for ( String subElementName : subElementNames )
		{
			// find the schema table for this object
			Table table = schema.findTableFromEntityName( dataSource.getDatabase(), subElementName );

			if ( table == null )
				continue;

			try
			{
				// create all the indexes
				dataSource.applyIndexes( table, "" );
			}
			catch ( DataSourceException e )
			{
				throw new ConfigurationException( e );
			}
		}

		onEndTopLevelElement( itemGroupElement );
	}

	/// Process an individual object described in the seed-data
	/// <SeedDataRoot>
	///    <UserTbls>
	///       <UserTbl ... HERE ... >

	private void doProcessItemElement( Element itemElement, Element parentElement, Stack<StackObject> objectIdStack ) throws ConfigurationException
	{
		// list of parent/child ids to keep
		List<Integer> parentIds = new ArrayList<Integer>();

		String rootClassName = parentElement.getName();
		Entity rootObjectMapping = schema.findEntity( rootClassName );

		String foreignKey = null;
		if ( rootObjectMapping != null )
		{
			foreignKey = rootObjectMapping.getPrimaryKey();
		}

		// allow the subclass to veto this block if it wants to ( used by seed data to only update NEW objects )
		if ( !checkProcessBlock( itemElement.getName(), false ) )
			return;

		// if we're on an object mapping
		if ( schema.findEntity( itemElement.getName() ) != null )
		{
			// iterate the sub elements and process them
			for ( Iterator it = itemElement.elementIterator(); it.hasNext(); )
			{
				Element onlyElement = ( Element ) it.next();

				Class clazz = HibernateSession.getInterfaceFromName( onlyElement.getName() );

				if ( clazz == null )
				{
					clazz = HibernateSession.getInterfaceFromName( onlyElement.getName().substring( 0, onlyElement.getName().length() - 1 ) );

					if ( clazz == null )
					{
						throw new ConfigurationException( "Error processing unknown element %1 is an unknown class and not a multiple wrapper ( eg PropertyDfnGroups )", onlyElement.getName() );
					}

					doProcessItemElement( onlyElement, itemElement, objectIdStack );
				}
			}

			return;
		}

		// iterate over the elements
		for ( Iterator<Element> it = ( Iterator<Element> ) itemElement.elementIterator(); it.hasNext(); )
		{
			Element currentElement = it.next();

			String parentClassName = currentElement.getName();
			Class clazz = HibernateSession.getInterfaceFromName( currentElement.getName() );

			if ( clazz == null )
			{
				throw new ConfigurationException( "Error finding class %1", currentElement.getName() );
			}

			// call event handler
			onBeginElement( clazz, currentElement );

			ClassMetadata parentMetadata;
			try
			{
				parentMetadata = HibernateUtil.getClassMetadata( sessionFactory, clazz );
			}
			catch ( HibernateDataAccessException e )
			{
				throw new ConfigurationException( e );
			}

			if ( parentMetadata == null )
			{
				throw new ConfigurationException( "Parent metadata not found for class %1. Class is not loaded in session factory.", clazz.getName() );
			}

			// load the business constraint key(s) for the object
			Entity objectMapping = schema.findEntity( parentClassName );

			if ( objectMapping == null )
			{
				throw new ConfigurationException( "Unable to find object mapping defined in schema for %1", parentClassName );
			}

			if ( !ignoreBuisnessConstraintException && ( objectMapping.businessConstraints == null || objectMapping.businessConstraints.trim().length() == 0 ) )
			{
				throw new ConfigurationException( "No business constraint defined for %1", parentClassName );
			}

			String[] businessConstraints = objectMapping.getBusinessConstraints();

			// load the current parent objects
			List currentParentObjects = null;
			try
			{
				// load the results if there are none in the cache
				if ( !resultsCache.containsKey( clazz ) )
				{
					resultsCache.put( clazz, HibernateUtil.getAllObjects( sessionFactory, clazz ) );
				}

				currentParentObjects = resultsCache.get( clazz );
			}
			catch ( HibernateDataAccessException e )
			{
				throw new ConfigurationException( e );
			}

			// build the search key map
			Map<String, Object> searchKeys = new HashMap<String, Object>();

			for ( String searchKey : businessConstraints )
			{
				// if the business constraint is the primary key then skip/shortcut
				// CRIT - Is this right? Needed when importing Address rows (in Interconnect)
				// CRIT - via the installer - didn't have any meaningful business constraint
				// CRIT - so it was set to the primary key
				if ( searchKey.equals( objectMapping.getPrimaryKey() ) )
					continue;

				// for the case of a search key being the parent foreign key use the
				// actual parent id
				if ( foreignKey != null && searchKey.endsWith( foreignKey ) )
				{
					searchKeys.put( searchKey, objectIdStack.peek().keyValue );
					continue;
				}

				// let the override if there is one have a crack at this search key value
				Object searchKeyValue = onGetSearchKeyValue( currentElement, searchKey, objectIdStack );

				if ( searchKeyValue != null )
				{
					searchKeys.put( searchKey, searchKeyValue );
					continue;
				}

				Attribute attribute = currentElement.attribute( searchKey );

				if ( attribute == null )
				{
					// no attribute found so try to find the object with this foreign key
					Entity foreignObjectMapping = schema.findEntityWithPrimaryKey( searchKey );

					if ( foreignObjectMapping == null )
					{
						throw new ConfigurationException( "Error in element %1. Mandatory attribute %2 not found.", currentElement.getName(), searchKey );
					}

					// check up the object id stack for a matching key
					boolean matched = false;
					for ( StackObject stackObject : objectIdStack )
					{
						if ( stackObject.objectName.equals( foreignObjectMapping.EntityName ) )
						{
							searchKeys.put( searchKey, stackObject.keyValue );
							matched = true;
						}
					}

					if ( matched )
						continue;

					// get the prefix of this foreign key if it has one
					String prefix = searchKey.substring( 0, searchKey.length() - foreignObjectMapping.getPrimaryKey().length() );

					// get the foreign business constraint for the foreign object type
					String[] foreignBusinessConstraints = foreignObjectMapping.getBusinessConstraints();

					if ( foreignBusinessConstraints.length == 0 )
					{
						throw new ConfigurationException( "Error in element %1. Mandatory attribute %2 not found. Unable to use property mapper as the probable matching object type %3 has no business constraint", currentElement.getName(), searchKey, foreignObjectMapping.EntityName );
					}

					if ( foreignBusinessConstraints.length > 1 )
					{
						throw new ConfigurationException( "Error in element %1. Mandatory attribute %2 not found. Unable to use property mapper as the probable matching object type %3 has more than one business constraint", currentElement.getName(), searchKey, foreignObjectMapping.EntityName );
					}

					// load the attribute ( EstName rather than EstId )
					attribute = currentElement.attribute( prefix + foreignBusinessConstraints[0] );

					if ( attribute == null )
					{
						throw new ConfigurationException( "Error in element %1. Mandatory attribute %2 not found. Property mapper lookup on probable matching object type %3 failed as there is no matching id for the attribute %4", currentElement.getName(), searchKey, foreignObjectMapping.EntityName, foreignBusinessConstraints[0] );
					}

					// find the relevant id
					Integer id = null;
					try
					{
						id = propertyMapper.attributeToId( foreignBusinessConstraints[0], attribute.getValue() );
					}
					catch ( HibernateDataAccessException e )
					{
						throw new ConfigurationException( "Error in element %1. Mandatory attribute %2 value %5 not found. Property mapper lookup on probable matching object type %3 failed as there is no matching id for the attribute %4", currentElement.getName(), searchKey, foreignObjectMapping.EntityName, foreignBusinessConstraints[0], attribute.getValue() );
					}

					// check the id is valid
					if ( id == null )
					{
						throw new ConfigurationException( "Error in element %1. Mandatory attribute %2 value %5 not found. Property mapper lookup on probable matching object type %3 failed as there is no matching id for the attribute %4", currentElement.getName(), searchKey, foreignObjectMapping.EntityName, foreignBusinessConstraints[0], attribute.getValue() );
					}

					// add to the search key map
					searchKeys.put( searchKey, id );
					continue;
				}

				// find the class of this property
				Class propertyClass = null;
				try
				{
					propertyClass = parentMetadata.getPropertyType( searchKey ).getReturnedClass();
				}
				catch ( HibernateDataAccessException e )
				{
					throw new ConfigurationException( e );
				}

				// convert the xml string to the object
				Object object = XMLHelper.stringToType( propertyClass, attribute.getValue() );

				// add the search key to the map
				searchKeys.put( searchKey, object );
			}

			// see if the parent object already exists
			List<Object> parentObjects = null;
			if ( !ignoreBuisnessConstraintException )
			{
				try
				{
					parentObjects = HibernateUtil.findInResults( sessionFactory, currentParentObjects, searchKeys );
				}
				catch ( HibernateDataAccessException e )
				{
					throw new ConfigurationException( e );
				}

				// can't have more than one!
				if ( parentObjects.size() > 1 )
				{
					throw new ConfigurationException( "Found multiple rows matching search object key searching object %1", clazz.getName() );
				}
			}

			HibernateObject parentObject;

			// null or not null
			if ( parentObjects == null || parentObjects.size() == 0 )
			{
				parentObject = null;
			}
			else
			{
				parentObject = ( HibernateObject ) parentObjects.get( 0 );

				// remove this object from the parent object list
				currentParentObjects.remove( parentObject );
			}

			// if it doesn't exist create a new one
			if ( parentObject == null )
			{
				try
				{
					parentObject = ( HibernateObject ) HibernateUtil.createObject( clazz, true );
				}
				catch ( HibernateDataAccessException e )
				{
					throw new ConfigurationException( e );
				}

				try
				{
					transaction.save( parentObject );
				}
				catch ( HibernateDataAccessException e )
				{
					throw new ConfigurationException( e );
				}
			}
			else
			{
				if ( !noUpdates )
				{
					try
					{
						transaction.update( parentObject );
					}
					catch ( HibernateDataAccessException e )
					{
						throw new ConfigurationException( e );
					}
				}
			}

			// save the id in a list
			parentIds.add( parentObject.getId() );

			// copy the attributes into the object overwriting old ones
			populateObjectFromElement( parentObject, currentElement, objectIdStack );

			if ( foreignKey != null )
			{
				if ( Arrays.asList( parentMetadata.getPropertyNames() ).contains( foreignKey ) )
				{
					try
					{
						parentMetadata.setPropertyValue( parentObject, foreignKey, objectIdStack.peek().keyValue );
					}
					catch ( HibernateDataAccessException e )
					{
						throw new ConfigurationException( "Error setting foreign key of child type %1, parent type %2, key %3", e, parentClassName, rootClassName, foreignKey );
					}
				}
			}

			// tell the property mapper this class is dirty
			propertyMapper.markDirty( clazz );

			// push this objects id onto the stack
			objectIdStack.push( new StackObject( parentClassName, parentObject.getId(), parentObject ) );

			// process the next level down, handle the multiple wrapper elements by passing in the parent
			// element if the current element is one of these. The real parent is needed for object mapping
			// lookups
			if ( isElementRecognisedClass( currentElement ) )
			{
				doProcessItemElement( currentElement, currentElement, objectIdStack );
			}
			else
			{
				doProcessItemElement( currentElement, currentElement.getParent(), objectIdStack );
			}

			// pop id back off stack
			objectIdStack.pop();
		}
	}

	private boolean isElementRecognisedClass( Element element )
	{
		Class clazz = HibernateSession.getInterfaceFromName( element.getName() );

		return clazz != null;
	}

	protected void populateObjectFromElement( HibernateObject obj, Element element, Stack<StackObject> objectIdStack ) throws ConfigurationException
	{
		ClassMetadata classMetadata;
		try
		{
			classMetadata = HibernateUtil.getClassMetadata( sessionFactory, obj.getClass() );
		}
		catch ( HibernateDataAccessException e )
		{
			throw new ConfigurationException( e );
		}

		for ( String propertyName : classMetadata.getPropertyNames() )
		{
			Entity entityMapping = schema.findEntityWithPrimaryKey( propertyName );
			Type propertyType = classMetadata.getPropertyType( propertyName );
			try
			{
				Class< ? > entityClassName = Class.forName( propertyType.getReturnedClass().getName() );
				if ( HibernateObject.class.isAssignableFrom( entityClassName ) )
				{
					for ( StackObject stackObject : objectIdStack )
					{
						if ( stackObject.objectName.equals( entityClassName.getSimpleName() ) )
						{
							try
							{
								classMetadata.setPropertyValue( obj, propertyName, stackObject.objectValue );
								break;
							}
							catch ( HibernateDataAccessException e )
							{
								throw new ConfigurationException( e );
							}
						}
					}
					continue;
				}
				else if ( entityMapping == null )
					continue;

			}
			catch ( ClassNotFoundException e1 )
			{
				throw new ConfigurationException( e1 );
			}

			boolean doStackMatch = true;
			for ( String propertyName2 : classMetadata.getPropertyNames() )
			{
				if ( propertyName.equals( propertyName2 ) )
					continue;

				Entity objectMapping2 = schema.findEntityWithPrimaryKey( propertyName2 );

				if ( objectMapping2 == null )
					continue;

				if ( objectMapping2.EntityName.equals( entityMapping.EntityName ) )
				{
					doStackMatch = false;
					break;
				}
			}

			if ( doStackMatch )
			{
				for ( StackObject stackObject : objectIdStack )
				{
					if ( stackObject.objectName.equals( entityMapping.EntityName ) )
					{
						try
						{
							classMetadata.setPropertyValue( obj, propertyName, stackObject.keyValue );
						}
						catch ( HibernateDataAccessException e )
						{
							throw new ConfigurationException( e );
						}
					}
				}
			}
		}

		Map<String, String> elementAttributes = new LinkedHashMap<String, String>();
		List<String> attributeNames = new ArrayList<String>();

		for ( Iterator<Attribute> it = ( Iterator<Attribute> ) element.attributeIterator(); it.hasNext(); )
		{
			Attribute attribute = it.next();

			elementAttributes.put( attribute.getName(), attribute.getValue() );
			attributeNames.add( attribute.getName() );
		}

		for ( String attributeName : attributeNames )
		{
			// check to see if this attribute has been consumed elsewhere..

			if ( !elementAttributes.containsKey( attributeName ) )
				continue;

			String attributeValue = elementAttributes.get( attributeName );

			// try the custom converter
			if ( !onCopyAttributeToProperty( obj, element, classMetadata, elementAttributes, attributeName, attributeValue, objectIdStack ) )
			{
				try
				{
					// try the standard cache converter
					if ( !propertyMapper.mapPropertyToId( obj, classMetadata, elementAttributes, attributeName, attributeValue ) )
					{
						// do the default copy
						Class propertyClass = classMetadata.getPropertyType( attributeName ).getReturnedClass();
						classMetadata.setPropertyValue( obj, attributeName, XMLHelper.stringToType( propertyClass, attributeValue ) );
					}
				}
				catch ( HibernateDataAccessException e )
				{
					throw new ConfigurationException( "Error processing %1", e, obj.getClass().getName() );
				}
			}
		}
	}

	protected void saveDatabaseChanges() throws ConfigurationException
	{
		try
		{
			transaction.commit();
		}
		catch ( HibernateDataAccessException e )
		{
			throw new ConfigurationException( e );
		}
	}

	protected boolean onCopyAttributeToProperty( HibernateObject toObject, Element element, ClassMetadata metadata, Map<String, String> elementAttributes, String attributeName, String attributeValue, Stack<StackObject> objectIdStack ) throws ConfigurationException
	{
		// check for the common Component attribute (must have an associated CptTypeCd attribute)
		if ( attributeName.endsWith( "cmpName" ) )
		{
			// strip off the prefix so we can identify the CptTypeCd associated with this key
			String prefix = StringHelper.removeCamelCaseSuffix( attributeName, 2 );

			// lookup the component id using the CptTypeCd and CmpName
			Object componentId = null;
			if ( attributeValue.length() > 0 )
			{
				if ( !elementAttributes.containsKey( prefix + "cptTypeCd" ) )
				{
					throw new ConfigurationException( prefix + "cptTypeCd not specified for component name %1 in object type %2", attributeValue, toObject.getClass().getName() );
				}

				componentId = componentMap.get( elementAttributes.get( prefix + "cptTypeCd" ) + "\t" + attributeValue );

				if ( componentId == null )
				{
					throw new ConfigurationException( "Unknown component name %1 found in attribute %2 of object type %3", attributeValue, attributeName, toObject.getClass().getName() );
				}
			}

			// set the value
			try
			{
				metadata.setPropertyValue( toObject, prefix + "cmpId", componentId );
				metadata.setPropertyValue( toObject, "component", componentObjMap.get( componentId ) );
			}
			catch ( HibernateDataAccessException e )
			{
				throw new ConfigurationException( "Error setting " + prefix + " component id from name %1 found in object type %2", e, attributeValue, toObject.getClass().getName() );
			}

			// consume the CptTypeCd attribute as it has no place in the actual object
			elementAttributes.remove( prefix + "cptTypeCd" );

			return true;
		}
		else if ( attributeName.endsWith( "pigName" ) )
		{
			// strip off the prefix so we can identify the PigPdgKey associated with this key
			String prefix = StringHelper.removeCamelCaseSuffix( StringHelper.upperFirstChar( attributeName ), 2 );

			// lookup/create the property instance group using the PigPdgKey and PigName
			Integer pigId = null;
			if ( attributeValue.length() > 0 )
			{
				// first check if the PigPdgKey attribute is present
				String pigPdgKey = elementAttributes.remove( prefix + "pigPdgKey" );

				// get the property inst groups wrapper
				Element propertyInstGroupsElement = element.element( "PropertyInstGroups" );

				Element matchedPropertyInstGroupElement = null;

				if ( propertyInstGroupsElement != null )
				{
					// get the property inst groups

					// try to match a property inst group with the same PigName
					for ( Element propertyInstGroupElement : ( List<Element> ) propertyInstGroupsElement.elements() )
					{
						if ( !propertyInstGroupElement.getName().equals( "PropertyInstGroup" ) )
						{
							throw new ConfigurationException( "Found non-PropertyInstGroup element under PropertyInstGroups under " + element.getName() );
						}

						// valid and compare pig name
						Attribute attribute = propertyInstGroupElement.attribute( "pigName" );

						if ( attribute == null )
						{
							throw new ConfigurationException( "Found PropertyInstGroup without PigName specified under " + element.getName() );
						}

						if ( attribute.getValue().equals( attributeValue ) )
						{
							matchedPropertyInstGroupElement = propertyInstGroupElement;
							break;
						}
					}
				}

				if ( matchedPropertyInstGroupElement == null && pigPdgKey == null )
				{
					throw new ConfigurationException( "Did not find PropertyInstGroup with PigName " + attributeValue + " nor a PigPdgKey attribute under " + element.getName() );
				}

				if ( matchedPropertyInstGroupElement != null && pigPdgKey != null )
				{
					throw new ConfigurationException( "Found both a PropertyInstGroup with PigName " + attributeValue + " and a PigPdgKey attribute under " + element.getName() );
				}

				// matched a property inst group

				// now lookup for the pdg key

				// valid pdg key exists
				String pdgKey = null;
				if ( pigPdgKey != null )
				{
					pdgKey = pigPdgKey;
				}
				else
				{
					Attribute attribute = matchedPropertyInstGroupElement.attribute( "pdgKey" );

					if ( attribute == null )
					{
						throw new ConfigurationException( "Found PropertyInstGroup without PdgKey specified with PigName " + attributeValue + " under " + element.getName() );
					}

					pdgKey = attribute.getValue();
				}

				PropertyInstGroup propertyInstGroup = null;
				try
				{
					HibernateTransaction transaction = new HibernateTransaction( sessionFactory, interceptor );

					PropertyDfnGroup propertyDfnGroup = ( PropertyDfnGroup ) HibernateSession.queryExpectOneRow( "from PropertyDfnGroup pdg where pdg.pdgKey = :pdgKey", "pdgKey", pdgKey );

					// List<PropertyInstGroup> propertyInsts =  HibernateSession.query( "from PropertyInstGroup pig where pig.PigName = :pigName and pig.DeleteFl = 'N'", "pigName", attributeValue );

					if ( metadata.getPropertyValue( toObject, prefix + "pigId" ) == null || ( Integer ) metadata.getPropertyValue( toObject, prefix + "pigId" ) == 0 )
					{
						propertyInstGroup = HibernateSession.createObject( PropertyInstGroup.class );
						propertyInstGroup.setPdgId( propertyDfnGroup.getPdgId() );
						propertyInstGroup.setPropertyDfnGroup( propertyDfnGroup );
						propertyInstGroup.setPigName( attributeValue );

						transaction.save( propertyInstGroup );
						transaction.commit();
						Map<String, PropertyDfn> propertyDfns = PropertyHelper.getPropertyDfns( propertyDfnGroup );

						// if we found a custom property inst group then use it's definitions to override defaults
						if ( matchedPropertyInstGroupElement != null )
						{
							// get the property inst groups wrapper
							Element propertyInstsElement = matchedPropertyInstGroupElement.element( "PropertyInsts" );

							if ( propertyInstsElement != null )
							{
								// instantiate and populate all the property insts
								for ( Element propertyInstElement : ( List<Element> ) propertyInstsElement.elements() )
								{
									// first find the corresponding prd by matching the key
									Attribute attribute = propertyInstElement.attribute( "prdKey" );

									if ( attribute == null )
									{
										throw new ConfigurationException( "Found a property inst without PrdKey attribute specified in PigName " + attributeValue );
									}

									String prdKey = attribute.getValue();

									PropertyDfn matchedPropertyDfn = null;

									// find the matching property dfn
									if ( propertyDfns.containsKey( prdKey ) )
									{
										matchedPropertyDfn = propertyDfns.remove( prdKey );
									}
									else
									{
										throw new ConfigurationException( "Could not match property inst (property inst group name %1) PrdKey %2 with to a valid property dfn for the specified property dfn group %3", attributeValue, prdKey, propertyDfnGroup.getPdgKey() );
									}

									// get the pri value
									String priValue = null;

									Attribute priValueAttribute = propertyInstElement.attribute( "PriValue" );

									if ( priValueAttribute != null )
									{
										matchedPropertyDfn.setPrdDefault( priValueAttribute.getValue() );
										priValue = PropertyHelper.getDefaultValue( matchedPropertyDfn );
									}

									PropertyInst propertyInst = HibernateSession.createObject( PropertyInst.class );

									// set property inst values
									propertyInst.setPigId( propertyInstGroup.getPigId() );
									propertyInst.setPropertyInstGroup( propertyInstGroup );
									propertyInst.setPrdId( matchedPropertyDfn.getPrdId() );
									propertyInst.setPropertyDfn( matchedPropertyDfn );
									propertyInst.setPriValue( priValue );

									transaction.save( propertyInst );
								}
							}
						}

						// add the defaults for any missing property dfns
						for ( PropertyDfn propertyDfn : propertyDfns.values() )
						{
							PropertyInst propertyInst = HibernateSession.createObject( PropertyInst.class);
							propertyInst.setPigId( propertyInstGroup.getPigId() );
							propertyInst.setPropertyInstGroup( propertyInstGroup );
							propertyInst.setPrdId( propertyDfn.getPrdId() );
							propertyInst.setPropertyDfn( propertyDfn );
							propertyInst.setPriValue( PropertyHelper.getDefaultValue( propertyDfn ) );
							transaction.save( propertyInst );
						}
						transaction.commit();
					}
				}
				catch ( HibernateDataAccessException e )
				{
					throw new ConfigurationException( e );
				}
				catch ( PropertyHelperException e )
				{
					throw new ConfigurationException( e );
				}

				if ( propertyInstGroup != null )
				{
					pigId = propertyInstGroup.getPigId();
					metadata.setPropertyValue( toObject, "propertyInstGroup", propertyInstGroup );
				}
			}

			// set the value
			try
			{
				if ( pigId != null )
				{
					metadata.setPropertyValue( toObject, prefix + "pigId", pigId );
				}
			}
			catch ( HibernateDataAccessException e )
			{
				throw new ConfigurationException( "Error setting " + prefix + " property inst group id from name %1 found in object type %2", e, attributeValue, toObject.getClass().getName() );
			}

			return true;
		}
		else
		{
			// default
			return false;
		}
	}

	protected void onPreDatabaseFlush() throws ConfigurationException
	{
	}

	protected Map<Class, List> onPreDeleteVeto( Map<Class, List> deleteObjects )
	{
		// do nothing by default
		return deleteObjects;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////                                                                                         ///////////
	///////////                DB EXTRACTION CODE                                                       ///////////
	///////////                                                                                         ///////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//    protected void standardExtractObjects( String rootElementName, Class parentClass, String searchParentKey, Class childClass, String searchChildKey, String parentPropertyForeignKey ) throws ConfigurationException
	//    {
	//        standardExtractObjects( rootElementName, parentClass, Arrays.asList( new String[] { searchParentKey } ), childClass, Arrays.asList( new String[] { searchChildKey } ), parentPropertyForeignKey );
	//    }
	//
	//    protected void standardExtractObjects( String rootElementName, Class parentClass, String searchParentKey, Class childClass, List< String > searchChildKey, String parentPropertyForeignKey ) throws ConfigurationException
	//    {
	//        standardExtractObjects( rootElementName, parentClass, Arrays.asList( new String[] { searchParentKey } ), childClass, searchChildKey, parentPropertyForeignKey );
	//    }
	//
	//    protected void standardExtractObjects( String rootElementName, StandardDatabaseUpdateInfo updateInfo ) throws ConfigurationException
	//    {
	//        standardExtractObjects( rootElementName, Arrays.asList( updateInfo ) );
	//    }
	//
	//    protected void standardExtractObjects( String rootElementName, List< StandardDatabaseUpdateInfo > updateInfos ) throws ConfigurationException
	//    {
	//        // add the root element
	//        Element rootElement = doc.getRootElement().addElement( rootElementName );
	//
	//        Stack< Integer > objectStack = new Stack< Integer >();
	//
	//        for ( StandardDatabaseUpdateInfo updateInfo : updateInfos )
	//        {
	//            doExtractProcessLevel( rootElement, updateInfo, objectStack );
	//        }
	//    }
	//
	//    protected void doExtractProcessLevel( Element currentElement, StandardDatabaseUpdateInfo updateInfo, Stack< Integer > objectIdStack ) throws ConfigurationException
	//    {
	//        String parentClassName = ObjectHelper.getClassOnlyName( updateInfo.clazz );
	//
	//        ClassMetadata parentMetadata;
	//        try
	//        {
	//            parentMetadata = sessionFactory.getClassMetadata( updateInfo.clazz );
	//        }
	//        catch ( HibernateDataAccessException e )
	//        {
	//            throw new ConfigurationException( e );
	//        }
	//
	//        if ( parentMetadata == null )
	//        {
	//            int i = 1;
	//        }
	//
	//        // extract all parent objects
	//        List< HibernateObject > parentObjects = null;
	//        try
	//        {
	//            if ( updateInfo.parentForeignKey != null )
	//            {
	//                parentObjects = (List< HibernateObject >)HibernateUtil.query( sessionFactory, "from " + parentClassName + " obj where obj." + updateInfo.parentForeignKey + " = :parentId", "parentId", objectIdStack.peek() );
	//            }
	//            else
	//            {
	//                parentObjects = (List< HibernateObject >)HibernateUtil.find( sessionFactory, "from " + parentClassName );
	//            }
	//        }
	//        catch ( HibernateDataAccessException e )
	//        {
	//            throw new ConfigurationException( e );
	//        }
	//
	//        for ( HibernateObject parentObject : parentObjects )
	//        {
	//            // add a parent element
	//            Element parentElement = currentElement.addElement( parentClassName );
	//
	//            for ( String propertyName : parentMetadata.getPropertyNames() )
	//            {
	//                if ( propertyName.equals( "VersionId" ) ) continue;
	//                if ( propertyName.equals( "DeleteFl" ) ) continue;
	//                if ( propertyName.equals( "PartitionId" ) ) continue;
	//
	//                // skip if its the parent key
	//                if ( propertyName.equals( updateInfo.parentForeignKey ) ) continue;
	//
	//                try
	//                {
	//                    if ( !parentMetadata.getPropertyType( propertyName ).isAssociationType() )
	//                    {
	//                        if ( !onCopyPropertyToAttribute( parentElement, parentObject, parentMetadata, propertyName ) )
	//                        {
	//                            if ( !propertyMapper.mapIdToAttribute( parentElement, parentObject, parentMetadata, propertyName ) )
	//                            {
	//                                Object obj = parentMetadata.getPropertyValue( parentObject, propertyName );
	//
	//                                parentElement.addAttribute( propertyName, XMLHelper.typeToString( obj ) );
	//                            }
	//                        }
	//                    }
	//                }
	//                catch ( HibernateDataAccessException e )
	//                {
	//                    throw new ConfigurationException( e );
	//                }
	//            }
	//
	//            objectIdStack.push( parentObject.getId() );
	//
	//            for ( StandardDatabaseUpdateInfo childUpdateInfo : updateInfo.updateInfos )
	//            {
	//                doExtractProcessLevel( parentElement, childUpdateInfo, objectIdStack );
	//            }
	//
	//            objectIdStack.pop();
	//        }
	//
	//    }

	protected boolean onCopyPropertyToAttribute( Element element, Object obj, ClassMetadata metadata, String propertyName ) throws ConfigurationException
	{
		try
		{
			if ( propertyName.endsWith( "cmpId" ) )
			{
				Integer propertyValue = ( Integer ) metadata.getPropertyValue( obj, propertyName );
				String prefix = StringHelper.removeCamelCaseSuffix( propertyName, 2 );

				if ( propertyValue == null )
				{
					element.addAttribute( prefix + "cmpName", "" );
					element.addAttribute( prefix + "cptTypeCd", "" );
					return true;
				}

				if ( !componentObjMap.containsKey( propertyValue ) )
				{
					throw new HibernateDataAccessException( "Unable to find component matching cmp id of %1", propertyValue );
				}

				Component comp = componentObjMap.get( propertyValue );

				element.addAttribute( prefix + "cmpName", comp.getCmpName() );

				if ( !componentTypeObjMap.containsKey( comp.getCptId() ) )
				{
					throw new ConfigurationException( "Unable to find component type matching cpt id of %1 for component %2", comp.getCptId(), comp.getCmpName() );
				}

				ComponentType componentType = componentTypeObjMap.get( comp.getCptId() );
				element.addAttribute( prefix + "cptTypeCd", componentType.getCptTypeCd() );

				return true;
			}

			return false;
		}
		catch ( HibernateDataAccessException e )
		{
			throw new ConfigurationException( e );
		}
	}

	protected Object onGetSearchKeyValue( Element element, String searchKey, Stack<StackObject> objectIdStack ) throws ConfigurationException
	{
		return null;
	}

	protected void onBeginTopLevelElement( Element element ) throws ConfigurationException
	{

	}

	protected void onEndTopLevelElement( Element element ) throws ConfigurationException
	{

	}

	protected void onBeginElement( Class clazz, Element element ) throws ConfigurationException
	{

	}

	protected boolean checkProcessBlock( String rootBlockName, boolean isRoot ) throws ConfigurationException
	{
		// default don't skip
		return true;
	}
}
