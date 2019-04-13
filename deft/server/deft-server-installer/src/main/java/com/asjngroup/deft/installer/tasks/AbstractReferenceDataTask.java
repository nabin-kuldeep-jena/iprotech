package com.asjngroup.deft.installer.tasks;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractReferenceDataTask implements ReferenceDataTask
{
	protected List<String> registeredSourceFiles;
	protected boolean allowUpdates;
	protected boolean noDeletes;
	protected boolean isSilentInstallation;
	protected String moduleName;

	public AbstractReferenceDataTask()
	{
		this.registeredSourceFiles = new ArrayList();
		this.allowUpdates = false;
		this.noDeletes = false;
		this.isSilentInstallation = false;
		this.moduleName = null;
	}

	public void registerSourceFile( String filename )
	{
		this.registeredSourceFiles.add( filename );
	}

	public boolean getAllowUpdates()
	{
		return this.allowUpdates;
	}

	public void setAllowUpdates( boolean allowUpdates )
	{
		this.allowUpdates = allowUpdates;
	}

	public void setNoDeletes( boolean noDeletes )
	{
		this.noDeletes = noDeletes;
	}

	public boolean isSilent()
	{
		return this.isSilentInstallation;
	}

	public void setSilent( boolean isSilentInstallation )
	{
		this.isSilentInstallation = isSilentInstallation;
	}

	public List<String> getRegisteredSourceFiles()
	{
		return this.registeredSourceFiles;
	}

	public void setModuleName( String moduleName )
	{
		this.moduleName = moduleName;
	}

	public String getModuleName()
	{
		return this.moduleName;
	}

	public boolean getNoDeletes()
	{
		return this.noDeletes;
	}

	public boolean getSilent()
	{
		return this.isSilentInstallation;
	}
}