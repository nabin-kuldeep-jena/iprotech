package com.asjngroup.deft.common.properties;


import javax.xml.bind.PropertyException;

import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.returntypes.ReturnString;
import com.asjngroup.deft.common.util.StringHelper;

public class HardLookupPropertyType extends AbstractPropertyType
{
	private String separator;

	public HardLookupPropertyType()
	{
		this.separator = ";";
	}

	public Class getReturnClass()
	{
		return String.class;
	}

	public boolean isValid( PropertyInst priObj, PropertyDfn prdObj, ReturnString message ) throws PropertyException
	{
		message.setString( "" );

		String[] searchStrings = StringHelper.split( prdObj.getPrdExtra1(), this.separator );

		if ( StringHelper.searchStringArray( searchStrings, priObj.getPriValue() ) == -1 )
		{
			message.setString( "The value specified (" + priObj.getPriValue() + ") for property '" + prdObj.getPrdName() + "' is not in the storage value list." );
			return false;
		}

		return true;
	}

	public Object getValue( PropertyDfn prDfn, int count ) throws PropertyException
	{
		String extra = "";

		if ( prDfn != null )
		{
			if ( count == 0 )
			{
				extra = prDfn.getPrdExtra1();
			}
			else
			{
				extra = prDfn.getPrdExtra2();
			}
		}
		return extra;
	}

	public void setValue( PropertyDfn propertyDfn, Object value, int count ) throws PropertyException
	{
		if ( count == 0 )
		{
			propertyDfn.setPrdExtra1( String.valueOf( value ) );
		}
		else
		{
			propertyDfn.setPrdExtra2( String.valueOf( value ) );
		}
	}
}