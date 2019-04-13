package com.asjngroup.deft.common.properties;


import java.util.List;

import javax.xml.bind.PropertyException;

import org.hibernate.HibernateException;

import com.asjngroup.deft.common.component.helper.ComponentHelper;
import com.asjngroup.deft.common.component.helper.ComponentHelperException;
import com.asjngroup.deft.common.database.hibernate.references.Component;
import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.database.hibernate.util.HibernateSession;
import com.asjngroup.deft.common.returntypes.ReturnString;

public class ComponentPropertyType extends AbstractPropertyType
{
	public Class getReturnClass()
	{
		return Object.class;
	}

	public boolean isValid( PropertyInst priObj, PropertyDfn prdObj, ReturnString message ) throws PropertyException
	{
		message.setString( "" );
		int cmpId;
		try
		{
			cmpId = Integer.parseInt( priObj.getPriValue() );
		}
		catch ( NumberFormatException e )
		{
			message.setString( "The value specified (" + priObj.getPriValue() + ") for property '" + prdObj.getPrdName() + "' is not a valid object id." );
			return false;
		}

		if ( getComponent( Integer.valueOf( cmpId ) ) == null )
		{
			message.setString( "The component specified (CptTypeCd '" + prdObj.getPrdExtra1() + "' CmpId " + cmpId + ") for property '" + prdObj.getPrdName() + "' does not exist." );
			return false;
		}

		return true;
	}

	public String getDefaultValue( PropertyDfn prdObj ) throws PropertyException
	{
		List results = null;
		try
		{
			String query = "from Component cmp  where cmp.ComponentType.CptTypeCd = :cptTypeCd  and cmp.CmpName = :cmpName ";

			results = HibernateSession.query( query, new String[]
			{ "cptTypeCd", "cmpName" }, new Object[]
			{ prdObj.getPrdExtra1(), prdObj.getPrdDefault() } );

			if ( results.size() != 1 )
			{
				throw new PropertyException( "Unable to find component lookup. CmpName: %1, CptTypeCd: %2", new Object[]
				{ prdObj.getPrdDefault(), prdObj.getPrdExtra1() } );
			}
		}
		catch ( HibernateException e )
		{
			throw new PropertyException( e );
		}

		return Integer.toString( ( ( Component ) results.get( 0 ) ).getCmpId() );
	}

	public Object getValue( PropertyInst priObj ) throws PropertyException
	{
		if ( PropertyHelper.isValueNull( priObj ) )
		{
			return null;
		}

		return createInstance( priObj );
	}

	private Component getComponent( Integer cmpId ) throws PropertyException
	{
		Component component = null;
		try
		{
			component = ( Component ) HibernateSession.get( Component.class, cmpId );
		}
		catch ( HibernateException e )
		{
			throw new PropertyException( e );
		}
		return component;
	}

	private Object createInstance( PropertyInst priObj ) throws PropertyException
	{
		int cmpId;
		try
		{
			cmpId = Integer.parseInt( priObj.getPriValue() );
		}
		catch ( NumberFormatException e )
		{
			return null;
		}

		try
		{
			return ComponentHelper.createInstance( getComponent( Integer.valueOf( cmpId ) ), new Object[0] );
		}
		catch ( ComponentHelperException e )
		{
			throw new PropertyException( e );
		}
	}
}