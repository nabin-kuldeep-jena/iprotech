package com.asjngroup.deft.common.component.helper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;

import com.asjngroup.deft.common.ReflectionHelper;
import com.asjngroup.deft.common.database.hibernate.references.Component;
import com.asjngroup.deft.common.database.hibernate.util.HibernateSession;
import com.asjngroup.deft.common.exception.DeftRuntimeException;

public class ComponentHelper
{
	private static String buildFullClassName( Component component ) throws ComponentHelperException
	{
		if ( ( component.getCmpServerComponent().length() == 0 ) && ( component.getCmpClientComponent().length() > 0 ) )
		{
			return null;
		}

		if ( component.getCmpServerComponent().length() == 0 )
		{
			throw new ComponentHelperException( "Error creating component instance as server package was not specified. Component name: %1", new Object[]
			{ component.getCmpName() } );
		}

		return component.getCmpServerComponent();
	}

	public static Class getComponentClass( Component component ) throws ComponentHelperException
	{
		String fullClassName = buildFullClassName( component );
		Class clazz;
		try
		{
			clazz = Class.forName( fullClassName );
		}
		catch ( ClassNotFoundException e )
		{
			throw new ComponentHelperException( e );
		}

		return clazz;
	}

	public static <T> T createInstance( String fullClassName, Object[] args ) throws ComponentHelperException
	{
		try
		{
			Class clazz = Class.forName( fullClassName );

			return ( T ) ReflectionHelper.newInstance( clazz, args );
		}
		catch ( ClassNotFoundException e )
		{
			throw new ComponentHelperException( e );
		}
		catch ( IllegalAccessException e )
		{
			throw new ComponentHelperException( e );
		}
		catch ( InstantiationException e )
		{
			throw new ComponentHelperException( e );
		}
		catch ( NoSuchMethodException e )
		{
			throw new ComponentHelperException( e );
		}
		catch ( InvocationTargetException e )
		{
			throw new ComponentHelperException( e );
		}
	}

	public static <T> T createInstance( Component component, Object[] args ) throws ComponentHelperException
	{
		String fullClassName = buildFullClassName( component );

		if ( fullClassName == null )
		{
			return null;
		}

		return createInstance( fullClassName, args );
	}

	public static <T> T createInstance( int cmpId, Object[] args ) throws ComponentHelperException
	{
		Component component = null;
		try
		{
			component = ( Component ) HibernateSession.get( Component.class, Integer.valueOf( cmpId ) );
		}
		catch ( HibernateException e )
		{
			throw new ComponentHelperException( e );
		}

		if ( component == null )
		{
			throw new ComponentHelperException( "Unable to find component with cmp id %1", new Object[]
			{ Integer.valueOf( cmpId ) } );
		}

		return createInstance( component, args );
	}

	public static Component[] getComponents( String cptTypeCd ) throws ComponentHelperException
	{
		List components = null;
		Component[] results = null;
		try
		{
			components = HibernateSession.query( "from Component c where c.ComponentType.CptTypeCd = :cptTypeCd", "cptTypeCd", cptTypeCd );
		}
		catch ( HibernateException e )
		{
			throw new ComponentHelperException( e );
		}

		results = new Component[components.size()];
		for ( int i = 0; i < components.size(); ++i )
			results[i] = ( ( Component ) components.get( i ) );
		return results;
	}

	public static List<Object> getComponentInstances( String componentType )
	{
		return getComponentInstances( componentType, Object.class );
	}

	public static <T> List<T> getComponentInstances( String componentType, Class<T> clazz )
	{
		try
		{
			Component[] components = getComponents( componentType );

			List componentList = new ArrayList();

			for ( Component component : components )
			{
				Object componentInstance = createInstance( component, new Object[0] );

				if ( !( clazz.isInstance( componentInstance ) ) )
				{
					throw new DeftRuntimeException( "Returned component of type '%1' that is not an instance of the required class '%2'", new Object[]
					{ componentInstance.getClass().getName(), clazz.getName() } );
				}

				componentList.add( clazz.cast( componentInstance ) );
			}

			return componentList;
		}
		catch ( ComponentHelperException e )
		{
			throw new DeftRuntimeException( e );
		}
	}
}