package com.asjngroup.deft.common.installer.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InstallerSessionBuilder
{
	public static ApplicationContext load()
	{
		ApplicationContext context = new ClassPathXmlApplicationContext( new String[]
		{ "deft-common.xml", "deft-common-datasource.xml" } );
		return context;
	}

}
