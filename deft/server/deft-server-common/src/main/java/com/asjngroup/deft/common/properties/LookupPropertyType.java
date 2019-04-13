package com.asjngroup.deft.common.properties;


import java.util.List;

import javax.xml.bind.PropertyException;

import org.hibernate.HibernateException;

import com.asjngroup.deft.common.database.hibernate.HibernateObject;
import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.database.hibernate.util.HibernateQueryCaseConverter;
import com.asjngroup.deft.common.database.hibernate.util.HibernateSession;
import com.asjngroup.deft.common.returntypes.ReturnString;
import com.asjngroup.deft.common.util.StringHelper;

public class LookupPropertyType extends AbstractPropertyType
{
	public Class getReturnClass()
	{
		return Object.class;
	}

	public boolean isValid( PropertyInst priObj, PropertyDfn prdObj, ReturnString message ) throws PropertyException
	{
		try
		{
			message.setString( "" );
			int objectId;
			try
			{
				objectId = Integer.parseInt( priObj.getPriValue() );
			}
			catch ( NumberFormatException e )
			{
				message.setString( "The value specified (" + priObj.getPriValue() + ") for property '" + prdObj.getPrdName() + "' is not a valid object id." );
				return false;
			}

			HibernateQueryCaseConverter hibernateQueryCaseConverter = new HibernateQueryCaseConverter( false );
			String query = hibernateQueryCaseConverter.convertIntialCharCase( buildLookupQuery( prdObj ) );

			List results = HibernateSession.query( query, "objectId", Integer.valueOf( objectId ) );
			if ( results.size() != 1 )
			{
				message.setString( "The id specified (" + objectId + ") for property '" + prdObj.getPrdName() + "' is not valid for this lookup." );
				return false;
			}
		}
		catch ( HibernateException e )
		{
			throw new PropertyException( e );
		}

		return true;
	}

	public String getDefaultValue( PropertyDfn prdObj ) throws PropertyException
	{
		List results = null;
		try
		{
			String query = " from " + prdObj.getPrdExtra1() + " obj " + " where obj." + prdObj.getPrdExtra2() + " = :objectName";

			if ( prdObj.getPrdExtra3().trim().length() > 0 )
			{
				query = query + " and obj." + prdObj.getPrdExtra3() + " = :parentObjectName";
			}

			if ( prdObj.getPrdExtra4().trim().length() > 0 )
			{
				query = query + " and obj." + prdObj.getPrdExtra4();
			}

			if ( prdObj.getPrdExtra5().trim().length() > 0 )
			{
				query = query + " and obj." + prdObj.getPrdExtra5();
			}
			HibernateQueryCaseConverter hibernateQueryCaseConverter = new HibernateQueryCaseConverter( true );
			query = hibernateQueryCaseConverter.convertIntialCharCase( query );

			if ( prdObj.getPrdExtra3().trim().length() > 0 )
			{
				results = HibernateSession.query( query, new String[]
				{ "objectName", "parentObjectName" }, StringHelper.split( prdObj.getPrdDefault(), ";" ) );
			}
			else
			{
				results = HibernateSession.query( query, "objectName", prdObj.getPrdDefault() );
			}

			if ( results.size() != 1 )
			{
				return null;
			}
		}
		catch ( HibernateException e )
		{
			throw new PropertyException( e );
		}

		return Integer.toString( ( ( HibernateObject ) results.get( 0 ) ).getId() );
	}

	public Object getValue( PropertyInst priObj ) throws PropertyException
	{
		if ( PropertyHelper.isValueNull( priObj ) )
		{
			return null;
		}

		int objectId;
		try
		{
			objectId = Integer.parseInt( priObj.getPriValue() );
		}
		catch ( NumberFormatException e )
		{
			return null;
		}

		PropertyDfn prdObj = null;
		List results = null;
		try
		{
			prdObj = ( PropertyDfn ) HibernateSession.get( PropertyDfn.class, Integer.valueOf( priObj.getPrdId() ) );

			HibernateQueryCaseConverter hibernateQueryCaseConverter = new HibernateQueryCaseConverter( false );
			String query = hibernateQueryCaseConverter.convertIntialCharCase( buildLookupQuery( prdObj ) );

			results = HibernateSession.query( query, "objectId", Integer.valueOf( objectId ) );

			if ( results.size() != 1 )
				return null;
		}
		catch ( HibernateException e )
		{
			throw new PropertyException( e );
		}

		return results.get( 0 );
	}

	private String buildLookupQuery( PropertyDfn prdObj )
	{
		String query = " from " + prdObj.getPrdExtra1() + " obj " + " where obj.id = :objectId";

		if ( prdObj.getPrdExtra4().trim().length() > 0 )
		{
			query = query + " and obj." + prdObj.getPrdExtra4();
		}

		if ( prdObj.getPrdExtra5().trim().length() > 0 )
		{
			query = query + " and obj." + prdObj.getPrdExtra5();
		}

		return query;
	}
}