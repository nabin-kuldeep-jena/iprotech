package com.asjngroup.deft.common.properties;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.PropertyException;

import org.hibernate.HibernateException;

import com.asjngroup.deft.common.database.datasource.DataSourceException;
import com.asjngroup.deft.common.database.datasource.DataSourceHelper;
import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.database.hibernate.references.TableColumn;
import com.asjngroup.deft.common.database.hibernate.util.HibernateSession;
import com.asjngroup.deft.common.database.schema.Column;
import com.asjngroup.deft.common.database.schema.ColumnDataType;
import com.asjngroup.deft.common.database.schema.ColumnDataTypeHelper;
import com.asjngroup.deft.common.returntypes.ReturnString;
import com.asjngroup.deft.common.util.DateHelper;
import com.asjngroup.deft.common.util.StringHelper;

public class SQLLookupPropertyType extends AbstractPropertyType
{
	public Class getReturnClass()
	{
		return SQLLookupPropertyType.class;
	}

	public boolean isValid( PropertyInst priObj, PropertyDfn prdObj, ReturnString message ) throws PropertyException
	{
		try
		{
			String tableName = prdObj.getPrdExtra1();
			String column = prdObj.getPrdExtra2();

			String query = "select tcl from TableColumn tcl, TableInst ti where ti.TinTableName = :tinName and ti.TbdId = tcl.TbdId and tcl.TclName = :tclName";
			TableColumn tableColumn = ( TableColumn ) HibernateSession.query( query, new String[]
			{ "tinName", "tclName" }, new Object[]
			{ tableName, column } ).get( 0 );

			List selectColumns = new ArrayList();
			List whereColumns = new ArrayList();

			ColumnDataType columnDataType = ColumnDataTypeHelper.typeStringToColumnDataType( tableColumn.getTclType() );
			Column selectCol = new Column( tableColumn.getTclName(), columnDataType, null );

			Object value = doDateConvertion( columnDataType, priObj.getPriValue() );
			Column whereCol = new Column( tableColumn.getTclName(), columnDataType, value );

			selectColumns.add( selectCol );
			whereColumns.add( whereCol );
			String queryString = null;
			//			String queryString = HibernateSession.getDataSource().createSQLSelectQuery( tableName, selectColumns, whereColumns );
			//		queryString = getAdditionalConstraints( prdObj, queryString );

			List list = null;//DataSourceHelper.executeQuery( HibernateSession.getSessionFactory(), queryString, new ColumnDataType[]{ columnDataType } );
			if ( list.isEmpty() )
			{
				message.setString( getErrorMessage( prdObj, priObj ) );
				return false;
			}
			return true;
		}
		catch ( HibernateException e )
		{
			throw new PropertyException( e );
		}
	}

	private Object doDateConvertion( ColumnDataType type, String priValue )
	{
		if ( ( priValue != null ) && ( type.equals( ColumnDataType.DateTime ) ) )
		{
			return DateHelper.parseValidDttmFromStorage( priValue );
		}
		return priValue;
	}

	protected String getAdditionalConstraints( PropertyDfn prdObj, String queryString ) throws NumberFormatException, HibernateException
	{
		String whereClause =null;// LongStringHelper.getString( Integer.parseInt( prdObj.getPrdExtra3() ) );

		if ( ( whereClause != null ) && ( !( whereClause.equals( "" ) ) ) )
		{
			queryString = StringHelper.concatStrings( new String[]
			{ queryString, " and ", whereClause } );
		}

		return queryString;
	}

	protected String getErrorMessage( PropertyDfn prdObj, PropertyInst priObj )
	{
		return "The value specified (" + priObj.getPriValue() + ") for property '" + prdObj.getPrdName() + "' is invalid.";
	}
}
