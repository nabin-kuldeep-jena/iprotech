package com.asjngroup.deft.common.properties;


import javax.xml.bind.PropertyException;

import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.returntypes.ReturnString;

public class IntegerPropertyType extends AbstractPropertyType
{
	public Class getReturnClass()
	{
		return Integer.class;
	}

	public boolean isValid( PropertyInst priObj, PropertyDfn prdObj, ReturnString message ) throws PropertyException
	{
		message.setString( "" );
		int intValue;
		try
		{
			intValue = Integer.parseInt( priObj.getPriValue() );
		}
		catch ( NumberFormatException e )
		{
			message.setString( "The value specified (" + priObj.getPriValue() + ") for property '" + prdObj.getPrdName() + "' is not a valid number." );
			return false;
		}

		int minValue = -2147483648;
		int maxValue = 2147483647;
		try
		{
			if ( prdObj.getPrdExtra1().length() > 0 )
				minValue = Integer.parseInt( prdObj.getPrdExtra1() );
			if ( prdObj.getPrdExtra2().length() > 0 )
			{
				maxValue = Integer.parseInt( prdObj.getPrdExtra2() );
			}
		}
		catch ( NumberFormatException e )
		{
			message.setString( "The min or max value specified for property '" + prdObj.getPrdName() + "' is not a valid number." );
			return false;
		}

		if ( ( intValue < minValue ) || ( intValue > maxValue ) )
		{
			message.setString( "The value specified (" + priObj.getPriValue() + ") for property '" + prdObj.getPrdName() + "' is not a number in the valid range (" + minValue + " to " + maxValue + ")." );
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

		Integer integer = null;
		try
		{
			integer = Integer.valueOf( Integer.parseInt( priObj.getPriValue() ) );
		}
		catch ( NumberFormatException e )
		{
			throw new PropertyException( e );
		}
		return integer;
	}
}