package com.asjngroup.deft.common.properties;

import java.util.Map;

public abstract interface PropertyGroup
{
	public abstract void load( int paramInt ) throws PropertyHelperException;

	public abstract void load( Map<String, Object> paramMap ) throws PropertyHelperException;

	public abstract <T extends PropertyGroup> void registerDependency( T paramT );

	public abstract <T extends PropertyGroup> T getDependency( Class<T> paramClass );
}
