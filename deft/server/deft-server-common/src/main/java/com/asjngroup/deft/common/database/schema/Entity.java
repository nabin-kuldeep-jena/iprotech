package com.asjngroup.deft.common.database.schema;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Root;

import com.asjngroup.deft.common.util.StringHelper;

/**
 * User: nabin.jena
 * Date: 07-Apr-2017
 * Time: 10:13:22
 */

@Root( name = "entity", strict = false )
public class Entity implements Cloneable
{
	public String EntityName;
	public String DatabaseName = "";
	public String TableName = "";
	public String ServerPackageName = "";
	public String ClientAssemblyName = "";
	public String ClientNamespace = "";
	public String EntityPrefix;
	public String DisplayProperty;
	public String DisplayString;
	public String AuditingDisplayString;

	public List<Field> FieldMappings = new ArrayList<Field>();
	public List<NestedObject> NestedObjects = new ArrayList<NestedObject>();
	public List<NestedCollection> NestedCollections = new ArrayList<NestedCollection>();

	public List<Field> fields = FieldMappings;
	
	public List<Constraint> constraints = new ArrayList<Constraint>();

	public String primaryKey;
	public String versionKey;
	public String businessConstraints="";

	public transient Schema schema;

	public Object clone() throws CloneNotSupportedException
	{
		Entity entityMapping = new Entity();

		entityMapping.EntityName = EntityName;
		entityMapping.DatabaseName = DatabaseName;
		entityMapping.TableName = TableName;

		entityMapping.primaryKey = primaryKey;
		entityMapping.versionKey = versionKey;

		for ( Field fieldMapping : FieldMappings )
		{
			entityMapping.FieldMappings.add( ( Field ) fieldMapping.clone() );
		}

		for ( NestedObject nestedObject : NestedObjects )
		{
			entityMapping.NestedObjects.add( ( NestedObject ) nestedObject.clone() );
		}

		for ( NestedCollection nestedCollection : NestedCollections )
		{
			entityMapping.NestedCollections.add( ( NestedCollection ) nestedCollection.clone() );
		}

		return entityMapping;
	}

	public String[] getBusinessConstraints()
	{
		if("".equals(businessConstraints ))
		{
			for(Constraint constraint:constraints)
			{
				if(constraint.isBusinessConstraint())
					businessConstraints=constraint.getPropertyList();
			}
		}
		if ( businessConstraints.trim().length() == 0 )
			return new String[0];

		return StringHelper.split( businessConstraints, ',' );
	}

	public Field findFieldMappingByColumn( String columnName )
	{
		for ( Field fieldMapping : FieldMappings )
		{
			if ( fieldMapping.ColumnName.equals( columnName ) )
			{
				return fieldMapping;
			}
		}

		return null;
	}

	public Field findFieldMapping( String propertyName )
	{
		for ( Field propertyMapping : FieldMappings )
		{
			if ( propertyMapping.FieldName.equals( propertyName ) )
			{
				return propertyMapping;
			}
		}

		return null;
	}

	public NestedObject findNestedObject( String propertyName )
	{
		for ( NestedObject nestedObject : NestedObjects )
		{
			if ( nestedObject.PropertyName.equals( propertyName ) )
			{
				return nestedObject;
			}
		}

		return null;
	}

	public NestedCollection findNestedCollection( String propertyName )
	{
		for ( NestedCollection nestedCollection : NestedCollections )
		{
			if ( nestedCollection.PropertyName.equals( propertyName ) )
			{
				return nestedCollection;
			}
		}

		return null;
	}

	public Table getTable()
	{
		return schema.Databases.get( 0 ).findTable( TableName );
	}
	
	public String getPrimaryKey()
	{

		if(primaryKey==null)
		{
			for(Constraint constraint:constraints)
			{
				if(constraint.getConstraintSuffix().equals( "pk" ))
					primaryKey=constraint.getPropertyList();
			}
		}
		return primaryKey;
	}

}
