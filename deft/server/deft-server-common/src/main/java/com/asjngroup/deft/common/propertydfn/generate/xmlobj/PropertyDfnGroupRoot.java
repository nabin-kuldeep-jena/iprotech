package com.asjngroup.deft.common.propertydfn.generate.xmlobj;

import java.util.ArrayList;
import java.util.List;

public class PropertyDfnGroupRoot
{
	public String clientNamespace;
	public String serverNamespace;
	public String parentClientNamespace1;
	public String parentClientNamespace2;
	public String parentClientNamespace3;
	public String parentClientNamespace4;
	public String parentClientNamespace5;
	public String parentServerNamespace1;
	public String parentServerNamespace2;
	public String parentServerNamespace3;
	public String parentServerNamespace4;
	public String parentServerNamespace5;
	public List<PropertyDfnGroup> PropertyDfnGroups = new ArrayList();

	public String getClientNamespace()
	{
		return clientNamespace;
	}

	public void setClientNamespace( String clientNamespace )
	{
		this.clientNamespace = clientNamespace;
	}

	public String getServerNamespace()
	{
		return serverNamespace;
	}

	public void setServerNamespace( String serverNamespace )
	{
		this.serverNamespace = serverNamespace;
	}

	public String getParentClientNamespace1()
	{
		return parentClientNamespace1;
	}

	public void setParentClientNamespace1( String parentClientNamespace1 )
	{
		this.parentClientNamespace1 = parentClientNamespace1;
	}

	public String getParentClientNamespace2()
	{
		return parentClientNamespace2;
	}

	public void setParentClientNamespace2( String parentClientNamespace2 )
	{
		this.parentClientNamespace2 = parentClientNamespace2;
	}

	public String getParentClientNamespace3()
	{
		return parentClientNamespace3;
	}

	public void setParentClientNamespace3( String parentClientNamespace3 )
	{
		this.parentClientNamespace3 = parentClientNamespace3;
	}

	public String getParentClientNamespace4()
	{
		return parentClientNamespace4;
	}

	public void setParentClientNamespace4( String parentClientNamespace4 )
	{
		this.parentClientNamespace4 = parentClientNamespace4;
	}

	public String getParentClientNamespace5()
	{
		return parentClientNamespace5;
	}

	public void setParentClientNamespace5( String parentClientNamespace5 )
	{
		this.parentClientNamespace5 = parentClientNamespace5;
	}

	public String getParentServerNamespace1()
	{
		return parentServerNamespace1;
	}

	public void setParentServerNamespace1( String parentServerNamespace1 )
	{
		this.parentServerNamespace1 = parentServerNamespace1;
	}

	public String getParentServerNamespace2()
	{
		return parentServerNamespace2;
	}

	public void setParentServerNamespace2( String parentServerNamespace2 )
	{
		this.parentServerNamespace2 = parentServerNamespace2;
	}

	public String getParentServerNamespace3()
	{
		return parentServerNamespace3;
	}

	public void setParentServerNamespace3( String parentServerNamespace3 )
	{
		this.parentServerNamespace3 = parentServerNamespace3;
	}

	public String getParentServerNamespace4()
	{
		return parentServerNamespace4;
	}

	public void setParentServerNamespace4( String parentServerNamespace4 )
	{
		this.parentServerNamespace4 = parentServerNamespace4;
	}

	public String getParentServerNamespace5()
	{
		return parentServerNamespace5;
	}

	public void setParentServerNamespace5( String parentServerNamespace5 )
	{
		this.parentServerNamespace5 = parentServerNamespace5;
	}

	public List<PropertyDfnGroup> getPropertyDfnGroups()
	{
		return PropertyDfnGroups;
	}

	public void setPropertyDfnGroups( List<PropertyDfnGroup> propertyDfnGroups )
	{
		PropertyDfnGroups = propertyDfnGroups;
	}

}