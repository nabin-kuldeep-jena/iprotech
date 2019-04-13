package com.asjngroup.deft.common.properties;


import javax.xml.bind.PropertyException;

import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.returntypes.ReturnString;

public class BooleanPropertyType extends AbstractPropertyType
{
	public Class getReturnClass()
	{
		return Boolean.class;
	}

	public boolean isValid( PropertyInst priObj, PropertyDfn prdObj, ReturnString message ) throws PropertyException
	{
		message.setString( "" );

		return ( ( priObj.getPriValue().equals( "Y" ) ) || ( priObj.getPriValue().equals( "N" ) ) );
	}

	public Object getValue( PropertyInst priObj ) throws PropertyException
	{
		if ( PropertyHelper.isValueNull( priObj ) )
		{
			return null;
		}

		return Boolean.valueOf( priObj.getPriValue().equals( "Y" ) );
	}
}