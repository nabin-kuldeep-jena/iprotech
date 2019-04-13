package com.asjngroup.deft.common.database.schema;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import com.asjngroup.deft.common.exception.DeftRuntimeException;
import com.asjngroup.deft.common.util.StringHelper;

@Root( name = "constraint" )
public class Constraint implements Cloneable
{

	@Attribute( name = "constraintSuffix" )
	public String constraintSuffix;

	@Attribute( name = "isUnique" )
	public boolean isUnique;

	@Attribute( name = "isClustered" )
	public boolean isClustered;

	@Attribute( name = "isBusinessConstraint" )
	public boolean isBusinessConstraint;

	@Attribute( name = "isDisplayName" )
	public boolean isDisplayName;

	@Attribute( name = "propertyList" )
	public String propertyList;

	public Constraint()
	{
	}

	public Constraint( String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4 )
	{
		this.constraintSuffix = paramString;
		this.isUnique = paramBoolean1;
		this.isClustered = paramBoolean2;
		this.isBusinessConstraint = paramBoolean3;
		this.isDisplayName = paramBoolean4;
	}

	public void addProperty( String paramString )
	{
		if ( StringHelper.isEmpty( this.propertyList ) )
		{
			this.propertyList = paramString;
		}
		else
		{
			Constraint tmp26_25 = this;
			tmp26_25.propertyList = tmp26_25.propertyList + "," + paramString;
		}
	}

	public boolean equals( Object paramObject )
	{
		if ( paramObject == null )
			return false;
		if ( !( paramObject instanceof Constraint ) )
			return false;
		Constraint localConstraint = ( Constraint ) paramObject;
		if ( !( this.constraintSuffix.equalsIgnoreCase( localConstraint.constraintSuffix ) ) )
			return false;
		if ( this.isUnique != localConstraint.isUnique )
			return false;
		if ( this.isClustered != localConstraint.isClustered )
			return false;
		return ( this.propertyList.equals( localConstraint.propertyList ) );
	}

	public String getConstraintSuffix()
	{
		return this.constraintSuffix;
	}

	public void setConstraintSuffix( String paramString )
	{
		this.constraintSuffix = paramString;
	}

	public boolean isUnique()
	{
		return this.isUnique;
	}

	public void setUnique( boolean paramBoolean )
	{
		this.isUnique = paramBoolean;
	}

	public boolean isClustered()
	{
		return this.isClustered;
	}

	public void setClustered( boolean paramBoolean )
	{
		this.isClustered = paramBoolean;
	}

	public boolean isBusinessConstraint()
	{
		return this.isBusinessConstraint;
	}

	public void setBusinessConstraint( boolean paramBoolean )
	{
		this.isBusinessConstraint = paramBoolean;
	}

	public boolean isDisplayName()
	{
		return this.isDisplayName;
	}

	public void setDisplayName( boolean paramBoolean )
	{
		this.isDisplayName = paramBoolean;
	}

	public String getPropertyList()
	{
		return this.propertyList;
	}

	public void setPropertyList( String paramString )
	{
		this.propertyList = paramString;
	}

	public List<Entity> getEntitys( Entity paramObjectMapping )
	{
		ArrayList localArrayList = new ArrayList();
		String[] arrayOfString1 = StringHelper.split( getPropertyList(), "," );
		for ( String str : arrayOfString1 )
		{
			Field localPropertyMapping = paramObjectMapping.findFieldMapping( str );
			if ( localPropertyMapping == null )
				throw new DeftRuntimeException( "The constraint %1 on object mapping %2 is invalid - the property '%3' is not in the object mapping", new Object[]
				{ getConstraintSuffix(), paramObjectMapping.EntityName, str } );
			localArrayList.add( localPropertyMapping );
		}
		return localArrayList;
	}

	public Index getIndex( Entity paramObjectMapping )
	{
		Index localIndex = new Index();
		localIndex.IsBusinessConstraint = isBusinessConstraint();
		localIndex.IsUnique= isUnique();
		localIndex.IsDisplayName= isDisplayName();
		localIndex.IndexName= SchemaHelper.entityNameToDatabaseName( paramObjectMapping.EntityName + "_" + getConstraintSuffix() ) ;
		localIndex.ColumnList= SchemaHelper.entityNameToDatabaseName( getPropertyList() ) ;
		return localIndex;
	}

	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}