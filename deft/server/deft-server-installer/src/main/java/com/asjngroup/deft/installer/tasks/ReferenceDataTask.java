package com.asjngroup.deft.installer.tasks;

import java.util.List;

public abstract interface ReferenceDataTask extends InstallSchemaTask
{
	public abstract void registerSourceFile( String paramString );

	public abstract String getDisplayName();

	public abstract boolean getAllowUpdates();

	public abstract void setAllowUpdates( boolean paramBoolean );

	public abstract void setNoDeletes( boolean paramBoolean );

	public abstract List<String> getRegisteredSourceFiles();

	public abstract void setModuleName( String paramString );

	public abstract String getModuleName();

	public abstract boolean getNoDeletes();

	public abstract boolean getSilent();
}