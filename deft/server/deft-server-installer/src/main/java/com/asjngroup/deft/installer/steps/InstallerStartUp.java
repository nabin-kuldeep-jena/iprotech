package com.asjngroup.deft.installer.steps;

import com.asjngroup.deft.installer.task.component.UpdateComponentDefinitions;
import com.asjngroup.deft.installer.task.propertydefination.UpdatePropertyDefinitions;
import com.asjngroup.deft.installer.task.seeddata.UpdateSeedData;
import com.asjngroup.deft.installer.tasks.schema.UpdateSchemaTask;

public class InstallerStartUp
{
	public static void main( String[] args )
	{
		try
		{
			//UpdateSchemaTask.start();
			//UpdateComponentDefinitions.start();
			//UpdatePropertyDefinitions.start();
			UpdateSeedData.start();
		}
		catch ( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
