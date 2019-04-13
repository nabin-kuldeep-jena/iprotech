package com.asjngroup.deft.common.properties;

import javax.xml.bind.PropertyException;

import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.returntypes.ReturnString;

public class LongPropertyType extends AbstractPropertyType
{
	public Class getReturnClass()
	{
		return Long.class;
	}

	public boolean isValid( PropertyInst priObj, PropertyDfn prdObj, ReturnString message ) throws PropertyException
	{
		message.setString( "" );
		long longValue;
		try
		{
			longValue = Long.parseLong( priObj.getPriValue() );
		}
		catch ( NumberFormatException e )
		{
			message.setString( "The value specified (" + priObj.getPriValue() + ") for property '" + prdObj.getPrdName() + "' is not a valid number." );
			return false;
		}

		long minValue = -9223372036854775808L;
		long maxValue = 9223372036854775807L;
		try
		{
			if ( prdObj.getPrdExtra1().length() > 0 )
				minValue = Long.parseLong( prdObj.getPrdExtra1() );
			if ( prdObj.getPrdExtra2().length() > 0 )
			{
				maxValue = Long.parseLong( prdObj.getPrdExtra2() );
			}
		}
		catch ( NumberFormatException e )
		{
			message.setString( "The min or max value specified for property '" + prdObj.getPrdName() + "' is not a valid number." );
			return false;
		}

		if ( ( longValue < minValue ) || ( longValue > maxValue ) )
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

		Long longValue = null;
		try
		{
			longValue = Long.valueOf( Long.parseLong( priObj.getPriValue() ) );
		}
		catch ( NumberFormatException e )
		{
			throw new PropertyException( e );
		}
		return longValue;
	}
}