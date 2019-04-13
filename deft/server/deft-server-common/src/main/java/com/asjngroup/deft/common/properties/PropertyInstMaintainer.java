package com.asjngroup.deft.common.properties;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

import com.asjngroup.deft.common.database.hibernate.HibernateObject;
import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyDfnGroup;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInstGroup;
import com.asjngroup.deft.common.database.hibernate.util.HibernateSession;
import com.asjngroup.deft.common.database.hibernate.util.HibernateTransaction;
import com.asjngroup.deft.common.database.hibernate.util.HibernateUtil;

public class PropertyInstMaintainer
{
	private HibernateTransaction hibernateTransaction;
	private SessionFactory sessionFactory;
	private Map<String, String> tokenReplacements;

	public PropertyInstMaintainer( SessionFactory sessionFactory )
	{
		this( sessionFactory, new HibernateTransaction( sessionFactory ) );
	}

	public PropertyInstMaintainer( SessionFactory sessionFactory, HibernateTransaction hibernateTransaction )
	{
		this.sessionFactory = sessionFactory;
		this.hibernateTransaction = hibernateTransaction;
	}

	public void setTokenReplacements( Map<String, String> tokenReplacements )
	{
		this.tokenReplacements = tokenReplacements;
	}

	public void maintainPropertyInstGroups() throws PropertyHelperException
	{
		List<PropertyInstGroup> propertyInstGroups = null;
		try
		{
			propertyInstGroups = HibernateUtil.find( this.sessionFactory, "from PropertyInstGroup" );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( "Error loading all property inst groups", e, new Object[0] );
		}

		for ( PropertyInstGroup propertyInstGroup : propertyInstGroups )
		{
			updatePropertyInstGroup( propertyInstGroup );
		}
	}

	public void updatePropertyInstGroup( int pigId ) throws PropertyHelperException
	{
		PropertyInstGroup propertyInstGroup = null;
		try
		{
			propertyInstGroup = ( PropertyInstGroup ) HibernateUtil.get( this.sessionFactory, PropertyInstGroup.class, Integer.valueOf( pigId ) );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( "Error loading property inst group %1", e, new Object[]
			{ Integer.valueOf( pigId ) } );
		}
		updatePropertyInstGroup( propertyInstGroup );
	}

	public void updatePropertyInstGroup( PropertyInstGroup propertyInstGroup ) throws PropertyHelperException
	{
		Collection<PropertyDfn> propertyDfns = null;
		PropertyDfnGroup propertyDfnGroup = null;
		try
		{
			propertyDfnGroup = ( PropertyDfnGroup ) HibernateSession.get( PropertyDfnGroup.class, Integer.valueOf( propertyInstGroup.getPdgId() ) );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}

		List<PropertyInst> propertyInsts = null;
		try
		{
			propertyInsts = HibernateUtil.query( this.sessionFactory, "from PropertyInst pri where pri.PigId = :pigId", "pigId", Integer.valueOf( propertyInstGroup.getPigId() ) );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( "Error loading all property insts for group %1", e, new Object[]
			{ Integer.valueOf( propertyInstGroup.getPigId() ) } );
		}

		if ( propertyDfnGroup == null )
		{
			try
			{
				this.hibernateTransaction.delete( propertyInstGroup );
				this.hibernateTransaction.deleteObjects( propertyInsts );
			}
			catch ( HibernateException e )
			{
				throw new PropertyHelperException( e );
			}
			return;
		}

		propertyDfns = PropertyHelper.getPropertyDfns( propertyDfnGroup ).values();

		if ( propertyDfns.size() == 0 )
		{
			PropertyDfnGroup dfnGroup = null;
			try
			{
				dfnGroup = ( PropertyDfnGroup ) HibernateUtil.get( this.sessionFactory, PropertyDfnGroup.class, Integer.valueOf( propertyInstGroup.getPdgId() ) );
			}
			catch ( HibernateException e )
			{
				throw new PropertyHelperException( "Error loading property dfn group for group %1", e, new Object[]
				{ Integer.valueOf( propertyInstGroup.getPdgId() ) } );
			}

			if ( dfnGroup == null )
			{
				try
				{
					this.hibernateTransaction.delete( propertyInstGroup );
				}
				catch ( HibernateException e )
				{
					throw new PropertyHelperException( "Error deleting property inst group %1", e, new Object[]
					{ Integer.valueOf( propertyInstGroup.getPigId() ) } );
				}
			}
		}
		else
		{
			for ( PropertyDfn propertyDfn : propertyDfns )
			{
				boolean found = false;

				for ( PropertyInst propertyInst : propertyInsts )
				{
					if ( propertyDfn.getPrdId() == propertyInst.getPrdId() )
					{
						found = true;
						break;
					}
				}

				if ( !( found ) )
				{
					PropertyInst newPropertyInst = null;
					try
					{
						newPropertyInst = ( PropertyInst ) HibernateSession.createObject( PropertyInst.class );
					}
					catch ( HibernateException e )
					{
						throw new PropertyHelperException( "Error creating new property inst", e, new Object[0] );
					}
					newPropertyInst.setPigId( propertyInstGroup.getPigId() );
					newPropertyInst.setPrdId( propertyDfn.getPrdId() );

					newPropertyInst.setPriValue( PropertyHelper.getDefaultValue( propertyDfn, this.tokenReplacements ) );
					try
					{
						this.hibernateTransaction.save( newPropertyInst );
					}
					catch ( HibernateException e )
					{
						throw new PropertyHelperException( "Error saving new property inst %1", e, new Object[]
						{ Integer.valueOf( newPropertyInst.getId() ) } );
					}
				}
			}
		}

		for ( PropertyInst propertyInst : propertyInsts )
		{
			boolean found = false;

			for ( PropertyDfn propertyDfn : propertyDfns )
			{
				if ( propertyDfn.getPrdId() == propertyInst.getPrdId() )
				{
					found = true;
					break;
				}

			}

			if ( !( found ) )
			{
				try
				{
					this.hibernateTransaction.delete( propertyInst );
				}
				catch ( HibernateException e )
				{
					throw new PropertyHelperException( "Error deleting property inst %1", e, new Object[]
					{ Integer.valueOf( propertyInst.getId() ) } );
				}
			}
		}
	}

	public PropertyInstGroup createPropertyInstGroupObject( String pdgKey, String pigName ) throws PropertyHelperException
	{
		List propertyDfnGroups = null;
		try
		{
			propertyDfnGroups = HibernateUtil.query( this.sessionFactory, "from PropertyDfnGroup pdg where pdg.PdgKey = :pdgKey", "pdgKey", pdgKey );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}

		if ( propertyDfnGroups.size() == 0 )
		{
			throw new PropertyHelperException( "Unable to find property dfn group. PdgKey: %1", new Object[]
			{ pdgKey } );
		}
		if ( propertyDfnGroups.size() > 1 )
		{
			throw new PropertyHelperException( "Found multiple property dfn groups. PdgKey: %1", new Object[]
			{ pdgKey } );
		}

		return createPropertyInstGroupObject( ( ( PropertyDfnGroup ) propertyDfnGroups.get( 0 ) ).getPdgId(), pigName );
	}

	public int createPropertyInstGroup( String pdgKey, String pigName ) throws PropertyHelperException
	{
		List propertyDfnGroups = null;
		try
		{
			propertyDfnGroups = HibernateUtil.query( this.sessionFactory, "from PropertyDfnGroup pdg where pdg.PdgKey = :pdgKey", "pdgKey", pdgKey );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}

		if ( propertyDfnGroups.size() == 0 )
		{
			throw new PropertyHelperException( "Unable to find property dfn group. PdgKey: %1", new Object[]
			{ pdgKey } );
		}
		if ( propertyDfnGroups.size() > 1 )
		{
			throw new PropertyHelperException( "Found multiple property dfn groups. PdgKey: %1", new Object[]
			{ pdgKey } );
		}

		return createPropertyInstGroup( ( ( PropertyDfnGroup ) propertyDfnGroups.get( 0 ) ).getPdgId(), pigName );
	}

	public int createPropertyInstGroup( int pdgId, String pigName ) throws PropertyHelperException
	{
		PropertyInstGroup newPropertyInstGroup = null;
		try
		{
			newPropertyInstGroup = ( PropertyInstGroup ) HibernateSession.createObject( PropertyInstGroup.class );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( "Error creating new property inst", e, new Object[0] );
		}

		newPropertyInstGroup.setPigName( pigName );
		newPropertyInstGroup.setPdgId( pdgId );
		try
		{
			this.hibernateTransaction.save( newPropertyInstGroup );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( "Error saving property inst group %1", e, new Object[]
			{ Integer.valueOf( newPropertyInstGroup.getId() ) } );
		}

		updatePropertyInstGroup( newPropertyInstGroup );

		return newPropertyInstGroup.getPigId();
	}

	public PropertyInstGroup createPropertyInstGroupObject( int pdgId, String pigName ) throws PropertyHelperException
	{
		PropertyInstGroup newPropertyInstGroup = null;
		try
		{
			newPropertyInstGroup = ( PropertyInstGroup ) HibernateSession.createObject( PropertyInstGroup.class );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( "Error creating new property inst", e, new Object[0] );
		}

		newPropertyInstGroup.setPigName( pigName );
		newPropertyInstGroup.setPdgId( pdgId );
		try
		{
			this.hibernateTransaction.save( newPropertyInstGroup );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( "Error saving property inst group %1", e, new Object[]
			{ Integer.valueOf( newPropertyInstGroup.getId() ) } );
		}

		updatePropertyInstGroup( newPropertyInstGroup );

		return newPropertyInstGroup;
	}

	public void assignPropertyInstGroupToProperty( Object obj, String propertyName, String pdgName, String pigName ) throws PropertyHelperException
	{
		int pigId = createPropertyInstGroup( pdgName, pigName );

		ClassMetadata metadata = null;
		try
		{
			metadata = HibernateUtil.getClassMetadata( this.sessionFactory, obj.getClass() );
			metadata.setPropertyValue( obj, propertyName, Integer.valueOf( pigId ) );
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( "Error setting pig id to generated pig. Class %1, id %2", e, new Object[]
			{ obj.getClass(), Integer.valueOf( ( ( HibernateObject ) obj ).getId() ) } );
		}
	}

	public int clonePropertyInstGroup( int pigId ) throws PropertyHelperException
	{
		try
		{
			PropertyInstGroup pig = ( PropertyInstGroup ) HibernateUtil.get( this.sessionFactory, PropertyInstGroup.class, Integer.valueOf( pigId ) );
			List<PropertyInst> propertyInsts = HibernateUtil.query( this.sessionFactory, "from PropertyInst pri where pri.PigId = :pigId", "pigId", Integer.valueOf( pigId ) );

			pig.setPigId( HibernateSession.generateId( PropertyInstGroup.class, 1 ).intValue() );
			pig.setVersionId( 1 );

			this.hibernateTransaction.save( pig );

			for ( PropertyInst propertyInst : propertyInsts )
			{
				propertyInst.setPriId( HibernateSession.generateId( PropertyInst.class, 1 ).intValue() );
				propertyInst.setVersionId( 1 );
				propertyInst.setPigId( pig.getPigId() );

				this.hibernateTransaction.save( propertyInst );
			}

			return pig.getPigId();
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( e );
		}
	}

	public void saveChanges() throws PropertyHelperException
	{
		try
		{
			this.hibernateTransaction.commit();
		}
		catch ( HibernateException e )
		{
			throw new PropertyHelperException( "Error updating new property insts" );
		}

		reset();
	}

	public void resetToDefault( PropertyInstGroup propertyInstGroup ) throws HibernateException
	{
		List<Object[]> propertyInsts = HibernateUtil.query( this.sessionFactory, "select pri,pri.PropertyDfn from PropertyInst pri where pri.PigId=:pigId", new String[]
		{ "pigId" }, new Object[]
		{ Integer.valueOf( propertyInstGroup.getPigId() ) } );
		for ( Object[] item : propertyInsts )
		{
			PropertyInst propertyInst = ( PropertyInst ) item[0];
			PropertyDfn propertyDfn = ( PropertyDfn ) item[1];
			propertyInst.setPriValue( propertyDfn.getPrdDefault() );

			this.hibernateTransaction.update( propertyInst );
		}
	}

	public void reset()
	{
		this.hibernateTransaction.rollback();
	}
}
