package com.asjngroup.deft.installer.tasks;

public abstract interface InstallSchemaTask
{
	public abstract void run( InstallerData paramInstallerData ) throws Exception;

	public abstract boolean isSilent();

	public abstract void setSilent( boolean paramBoolean );
}
