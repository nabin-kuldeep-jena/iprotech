package com.asjngroup.deft.common.properties;


import javax.xml.bind.PropertyException;

import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.returntypes.ReturnString;

public abstract class AbstractPropertyType implements PropertyType
{
	public static final String STRING_PROPERTY = "String Property";
	public static final String FLAG_PROPERTY = "Flag Property";
	public static final String DATE_PROPERTY = "Date Property";
	public static final String INTEGER_PROPERTY = "Integer Property";
	public static final String LONG_PROPERTY = "Long Property";
	public static final String DECIMAL_PROPERTY = "Decimal Property";
	public static final String HARD_LOOKUP_PROPERTY = "Hard Lookup Property";
	public static final String SQL_LOOKUP_PROPERTY = "SQL Lookup Property";

	public boolean isSatisfied( PropertyDfn prdObj, PropertyInst priObj ) throws PropertyException
	{
		return ( ( !( prdObj.getPrdMandatoryFl() ) ) || ( !( PropertyHelper.isValueNull( priObj ) ) ) );
	}

	public boolean isValid( PropertyInst priObj, PropertyDfn prdObj, ReturnString message ) throws PropertyException
	{
		message.setString( "" );
		return true;
	}

	public String getDefaultValue( PropertyDfn prdObj ) throws PropertyException
	{
		return prdObj.getPrdDefault();
	}

	public Object getValue( PropertyInst priObj ) throws PropertyException
	{
		if ( PropertyHelper.isValueNull( priObj ) )
		{
			return null;
		}

		return priObj.getPriValue();
	}

	public void fixProperty( PropertyInst priObj ) throws PropertyException
	{
	}
}