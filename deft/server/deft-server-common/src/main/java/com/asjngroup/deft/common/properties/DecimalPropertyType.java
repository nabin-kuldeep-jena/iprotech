package com.asjngroup.deft.common.properties;


import java.math.BigDecimal;

import javax.xml.bind.PropertyException;

import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.returntypes.ReturnString;

public class DecimalPropertyType extends AbstractPropertyType
{
	public Class getReturnClass()
	{
		return BigDecimal.class;
	}

	public boolean isValid( PropertyInst priObj, PropertyDfn prdObj, ReturnString message ) throws PropertyException
	{
		message.setString( "" );
		BigDecimal decimal;
		try
		{
			decimal = new BigDecimal( priObj.getPriValue() );
		}
		catch ( Exception e )
		{
			message.setString( "The value specified (" + priObj.getPriValue() + ") for property '" + prdObj.getPrdName() + "' is not a valid decimal." );
			return false;
		}

		BigDecimal minValue = new BigDecimal( -9223372036854775808L );
		BigDecimal maxValue = new BigDecimal( 9223372036854775807L );
		try
		{
			if ( prdObj.getPrdExtra1().length() > 0 )
				minValue = new BigDecimal( prdObj.getPrdExtra1() );
			if ( prdObj.getPrdExtra2().length() > 0 )
			{
				maxValue = new BigDecimal( prdObj.getPrdExtra2() );
			}
		}
		catch ( Exception e )
		{
			message.setString( "The min or max value specified for property '" + prdObj.getPrdName() + "' is not a valid decimal." );
			return false;
		}

		if ( ( decimal.compareTo( minValue ) < 0 ) || ( decimal.compareTo( maxValue ) > 0 ) )
		{
			message.setString( "The value specified (" + priObj.getPriValue() + ") for property '" + prdObj.getPrdName() + "' is not a decimal in the valid range (" + minValue + " to " + maxValue + ")." );
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

		BigDecimal decimal = null;
		try
		{
			decimal = new BigDecimal( priObj.getPriValue() );
		}
		catch ( Exception e )
		{
			throw new PropertyException( e );
		}
		return decimal;
	}
}