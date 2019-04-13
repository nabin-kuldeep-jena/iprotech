package com.asjngroup.deft.common.properties;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.PropertyException;

import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.returntypes.ReturnString;
import com.asjngroup.deft.common.util.StringHelper;

public class StringPropertyType extends AbstractPropertyType
{
	public Class getReturnClass()
	{
		return String.class;
	}

	public Object getValue( PropertyInst priObj ) throws PropertyException
	{
		if ( PropertyHelper.isValueNull( priObj ) )
		{
			return "";
		}
		return super.getValue( priObj );
	}

	public boolean isValid( PropertyInst priObj, PropertyDfn prdObj, ReturnString message ) throws PropertyException
	{
		String regexPattern = prdObj.getPrdExtra2();
		if ( ( !( StringHelper.isEmpty( regexPattern ) ) ) && ( !( StringHelper.isEmpty( priObj.getPriValue() ) ) ) )
		{
			Pattern pattern = StringHelper.getReplaceAllPattern( regexPattern );
			Matcher matcher = pattern.matcher( priObj.getPriValue() );

			if ( matcher.matches() )
			{
				message.setString( "" );
				return true;
			}

			message.setString( priObj.getPriValue() + "doesn't match the pattern  " + pattern );
			return false;
		}

		return true;
	}
}
