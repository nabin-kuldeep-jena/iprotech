package com.asjngroup.deft.common;



import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.asjngroup.deft.common.util.StringHelper;
import com.asjngroup.deft.common.util.StringUtil;

public class ReflectionHelper
{
	public static List<Method> findMethodWithName( Class clazz, String name )
	{
		Method[] methods = clazz.getMethods();

		List matchMethods = new ArrayList();

		for ( int i = 0; i < methods.length; ++i )
		{
			if ( !( methods[i].getName().equals( name ) ) )
				continue;
			matchMethods.add( methods[i] );
		}

		return matchMethods;
	}

	public static Object newInstance( Class< ? > clazz, Object[] args ) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException
	{
		if ( args == null )
		{
			args = new Object[0];
		}
		for ( Constructor constructor : clazz.getConstructors() )
		{
			if ( constructor.getParameterTypes().length != args.length )
			{
				continue;
			}
			boolean possibleMatch = true;

			for ( int i = 0; i < constructor.getParameterTypes().length; ++i )
			{
				Class parameterClass = constructor.getParameterTypes()[i];
				Object arg = args[i];

				if ( arg == null )
				{
					if ( !( parameterClass.isPrimitive() ) )
						continue;
					possibleMatch = false;
					break;
				}

				if ( parameterClass.isInstance( arg ) )
					continue;
				possibleMatch = false;
				break;
			}

			if ( possibleMatch )
			{
				return constructor.newInstance( args );
			}
		}
		throw new NoSuchMethodException( StringUtil.create( "No matching constructor in class '%1' for args '%2'", new Object[]
		{ clazz.getName(), Arrays.toString( args ) } ) );
	}
}