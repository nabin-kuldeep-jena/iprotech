package com.asjngroup.deft.common.properties;


import java.util.HashMap;
import java.util.Map;

public class AbstractPropertyGroup implements PropertyGroup
{
	private Map<Class< ? extends PropertyGroup>, PropertyGroup> dependencies;

	public AbstractPropertyGroup()
	{
		this.dependencies = new HashMap();
	}

	public void load( int pigId ) throws PropertyHelperException
	{
	}

	public void load( Map<String, Object> propertyMap ) throws PropertyHelperException
	{
	}

	public <T extends PropertyGroup> void registerDependency( T dependency )
	{
		Class clazz = dependency.getClass();

		while ( ( !( clazz.equals( AbstractPropertyGroup.class ) ) ) && ( !( clazz.equals( PropertyGroup.class ) ) ) )
		{
			this.dependencies.put( dependency.getClass(), dependency );

			clazz = clazz.getSuperclass();
		}
	}

	public <T extends PropertyGroup> T getDependency( Class<T> dependencyClass )
	{
		return ( T ) this.dependencies.get( dependencyClass );
	}
}