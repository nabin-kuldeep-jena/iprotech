package com.asjngroup.deft.common.properties;


import javax.xml.bind.PropertyException;

import org.joda.time.DateTime;

import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.returntypes.ReturnString;
import com.asjngroup.deft.common.util.DateHelper;

public class DatePropertyType extends AbstractPropertyType
{
	public Class getReturnClass()
	{
		return DateTime.class;
	}

	public boolean isValid( PropertyInst priObj, PropertyDfn prdObj, ReturnString message ) throws PropertyException
	{
		message.setString( "" );
		DateTime dttm;
		try
		{
			dttm = ( DateTime ) getValue( priObj );
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
			return DateHelper.parseValidDttmFromStorage( priObj.getPriValue() );
		}
		catch ( Exception e )
		{
			throw new PropertyException( e );
		}
	}
}