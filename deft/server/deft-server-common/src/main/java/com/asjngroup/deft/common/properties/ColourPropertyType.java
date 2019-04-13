package com.asjngroup.deft.common.properties;


import javax.xml.bind.PropertyException;

import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.returntypes.ReturnString;

public class ColourPropertyType extends AbstractPropertyType
{
	public Class getReturnClass()
	{
		return String.class;
	}

	public boolean isValid( PropertyInst priObj, PropertyDfn prdObj, ReturnString message ) throws PropertyException
	{
		message.setString( "" );
		String color;
		try
		{
			color = ( String ) getValue( priObj );
		}
		catch ( Exception e )
		{
			message.setString( e.toString() );
			return false;
		}
		return true;
	}

	public Object getValue( PropertyInst priObj ) throws PropertyException
	{
		if ( PropertyHelper.isValueNull( priObj ) )
		{
			return null;
		}
		try
		{
			return priObj.getPriValue();
		}
		catch ( Exception e )
		{
			throw new PropertyException( e );
		}
	}
}